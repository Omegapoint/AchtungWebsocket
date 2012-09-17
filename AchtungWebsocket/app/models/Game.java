package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import play.libs.Akka;
import play.libs.Json;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class Game extends UntypedActor
{
	private static ActorRef defaultGame = Akka.system().actorOf(new Props(Game.class));

	public static ActorRef getActor()
	{
		return defaultGame;
	}
	
	private GameState state = GameState.WAITING;
	private Map<String, Player> players = new HashMap<String, Player>();
	private Map<Integer, Collector> collectors = new HashMap<Integer, Collector>();
	
	public enum GameState
	{
		WAITING, RUNNING, PAUSED, ENDED
	}
	
	// Pre constructor
	{
		this.context().watch(defaultGame);
	}
	
	@Override
	public void onReceive(Object object) throws Exception
	{
		Inbound.In message = (Inbound.In) object;
		Player player = message.getPlayer();
		Collector collector = collectors.get(message.getId());
		
		System.out.println(message.getClass().getName());
		
		if (collector != null)
		{
			if (collector.registerResponse(message))
			{
				if (collector.isFinished())
				{
					send(collector.getPlayer(), (Outbound.Out) collector.getCallback().execute(collector));
				}
				
				collectors.remove(message.getId());
			}
			
			return;
		}
		
		if (message instanceof Inbound.Join)
		{
			players.put(player.getName(), player);
		}
		else if (message instanceof Inbound.Quit)
		{
			players.remove(player.getName());
		}
		else if (message instanceof Inbound.Ready)
		{
			
		}
		else if (message instanceof Inbound.Ping)
		{
			final Inbound.Ping inPing = (Inbound.Ping) message; 
			final Outbound.Ping outPing = new Outbound.Ping();
			
			broadcastAndCollect(outPing, inPing.getPlayer(), new Callback<Outbound.Pong>()
			{
				@Override
				public Outbound.Pong execute(Collector collector)
				{
					Outbound.Pong outPong = new Outbound.Pong();
					
					outPong.setId(inPing.getId());
					//outPong.s
					
					return outPong;
				}
			});
		}
	}
	
	public void broadcast(Outbound.Out message)
	{
		for(Player player: players.values())
		{
			send(player, message);
		}
	}
	
	public void broadcastAndCollect(Outbound.Out message, Player reportTo, Callback callback)
	{
		Collector collector = new Collector(reportTo, callback);
		
		Integer outId;
		Date nowDate = new Date(System.currentTimeMillis());
		
		for(Player player: players.values())
		{
			outId = Outbound.generateOutboundId();
			
			message.setId(outId);
			message.setTime(nowDate);
			collector.addExpected(outId);
			collectors.put(outId, collector);
			
			send(player, message);
		}
	}
	
	public void send(Player receiver, Outbound.Out message)
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

	public Player getPlayer(String name)
	{
		return players.get(name);
	}
}
