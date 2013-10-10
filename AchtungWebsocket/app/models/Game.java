package models;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.util.Duration;
import play.libs.Akka;
import play.libs.Json;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game extends UntypedActor
{
	private static ActorRef defaultGame = Akka.system().actorOf(new Props(Game.class));

	public static ActorRef getActor()
	{
		return defaultGame;
	}

	private Cancellable scheduledTick;
	private final Board board = new Board();
	private final Map<String, Connection> connections = new HashMap<String, Connection>();
	private final Map<Integer, Collector> collectors = new HashMap<Integer, Collector>();

	// Pre constructor
	{
		this.context().watch(defaultGame);
	}

	@Override
	public void preStart()
	{
		scheduledTick = Akka.system().scheduler().schedule(Duration.create(20, TimeUnit.MILLISECONDS), Duration.create(20, TimeUnit.MILLISECONDS), Game.getActor(), new Inbound.Tick());
	}

	@Override
	public void postStop()
	{
		scheduledTick.cancel();
	}

	@Override
	public void onReceive(Object object) throws Exception
	{
		final Inbound.In message = (Inbound.In) object;
		final Connection sender = message.getSender();
		final Player player = (sender == null) ? null : sender.getPlayer();
		final Collector collector = collectors.get(message.getId());

		if (!(message instanceof Inbound.Tick))
		{
			System.out.println(message.getClass().getName());
		}

		if (collector != null)
		{
			if (collector.registerResponse(message))
			{
				if (collector.isFinished())
				{
					send(collector.getCollector(), (Outbound.Out) collector.getCallback().execute(collector));
				}
				
				collectors.remove(message.getId());
			}
			
			return;
		}
		
		if (message instanceof Inbound.Join)
		{
			Outbound.Out<Outbound.Welcome> outWelcome = new Outbound.Out<Outbound.Welcome>(new Outbound.Welcome());
			Outbound.Out<Outbound.Join> outJoin = new Outbound.Out<Outbound.Join>(new Outbound.Join());

			for (Player existingPlayer : board.getPlayers().values())
			{
				outWelcome.getMessage().getPlayers().add(existingPlayer);
			}

			send(sender, outWelcome);

			addConnection(sender);

			outJoin.getMessage().setPlayer(player);

			broadcast(outJoin);
		}
		else if (message instanceof Inbound.Quit)
		{
			Outbound.Out<Outbound.Leave> outLeave = new Outbound.Out<Outbound.Leave>(new Outbound.Leave());

			outLeave.getMessage().setPlayer(player);

			broadcast(outLeave);

			removeConnection(player.getName());
		}
		else if (message instanceof Inbound.Ready)
		{
			Inbound.Ready inReady = (Inbound.Ready) message;
			Outbound.Out<Outbound.Ready> outReady = new Outbound.Out<Outbound.Ready>(new Outbound.Ready());

            board.setSizeX(Math.min(inReady.getSizeX(), board.getSizeX()));
			board.setSizeY(Math.min(inReady.getSizeY(), board.getSizeY()));

			outReady.getMessage().setSizeX(board.getSizeX());
			outReady.getMessage().setSizeY(board.getSizeY());

			send(sender, outReady);

			player.setReady(true);

            if(board.allPlayersAreReady())
            {
				Outbound.Out<Outbound.Start> outStart = new Outbound.Out<Outbound.Start>(new Outbound.Start());
				Date now = new Date();

			    board.start(now);
				outStart.setTime(now);
                outStart.getMessage().setSizeX(board.getSizeX());
                outStart.getMessage().setSizeY(board.getSizeY());

				broadcast(outStart);
            }
		}
		else if (message instanceof Inbound.Tick)
		{
            board.tick();
            Date d = new Date();

            List<Player> deadPlayers = new ArrayList<Player>();
            List<Player> teleportedPlayers = new ArrayList<Player>();
            board.update(deadPlayers, teleportedPlayers, d);

            for(Player p : teleportedPlayers)
            {
                Outbound.Out<Outbound.Teleport> outTeleport = new Outbound.Out<Outbound.Teleport>(new Outbound.Teleport());
                outTeleport.getMessage().setPart(p.flush(board.extrapolate(p,d)));
                outTeleport.getMessage().setPlayer(p);
                p.setTime(d);
                broadcast(outTeleport);

                p.teleport();
            }

            if (!deadPlayers.isEmpty())
            {
                Outbound.Out<Outbound.Death> outDeath = new Outbound.Out<Outbound.Death>(new Outbound.Death());
                outDeath.getMessage().setPlayers(deadPlayers);
                broadcast(outDeath);
            }
		}
		else if (message instanceof Inbound.Direction)
		{
			Inbound.Direction inDirection = (Inbound.Direction) message;
			Outbound.Out<Outbound.Direction> outDirection = new Outbound.Out<Outbound.Direction>(new Outbound.Direction());

			outDirection.getMessage().setPart(player.flush(board.extrapolate(player, inDirection.getTime())));
			player.setDirection(inDirection.getDirection());
			player.setTime(inDirection.getTime());

			outDirection.getMessage().setPlayer(player);

			broadcast(outDirection);
		}
		else if (message instanceof Inbound.Ping)
		{
			final Inbound.Ping inPing = (Inbound.Ping) message; 
			final Outbound.Out<Outbound.Ping> outPing = new Outbound.Out<Outbound.Ping>(new Outbound.Ping());
			
			broadcastAndCollect(outPing, inPing.getSender(), new ICallback<Outbound.Out>()
			{
				@Override
				public Outbound.Out execute(Collector collector)
				{
					Outbound.Out<Outbound.Pong> outPong = new Outbound.Out<Outbound.Pong>(new Outbound.Pong());
					
					outPong.setId(inPing.getId());

					for (Inbound.In in : collector.getResponses())
					{
						outPong.getMessage().getPlayers().add(in.getSender().getPlayer());
					}
					
					return outPong;
				}
			});
		}
	}
	
	public void broadcast(Outbound.Out message)
	{
		for(Connection connection: connections.values())
		{
			send(connection, message);
		}
	}
	
	public void broadcastAndCollect(Outbound.Out message, Connection reportTo, ICallback callback)
	{
		Collector collector = new Collector(reportTo, callback);
		
		Integer outId;
		Date nowDate = new Date(System.currentTimeMillis());
		
		for(Connection connection: connections.values())
		{
			outId = Outbound.generateOutboundId();
			
			message.setId(outId);
			message.setTime(nowDate);
			collector.addExpected(outId);
			collectors.put(outId, collector);
			
			send(connection, message);
		}
	}
	
	public void send(Connection receiver, Outbound.Out message)
	{
		if (message.getId() == null)
		{
			message.setId(Outbound.generateOutboundId());
		}
		if (message.getTime() == null)
		{
			message.setTime(new Date(System.currentTimeMillis()));
		}
		
		receiver.getOut().write(Json.toJson(message));
	}

	public Connection getConnection(String name)
	{
		return connections.get(name);
	}

	public void addConnection(Connection connection)
	{
		Player player = connection.getPlayer();

		board.addPlayer(player);
		connections.put(player.getName(), connection);
	}

	public void removeConnection(String name)
	{
		board.removePlayer(name);
		connections.remove(name);
	}
}
