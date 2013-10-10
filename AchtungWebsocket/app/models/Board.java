package models;

import java.util.*;

import models.iface.Collidable;

public class Board
{
	private final Map<String, Player> players = new HashMap<String, Player>();

	private Integer sizeX = Integer.MAX_VALUE;
	private Integer sizeY = Integer.MAX_VALUE;
	private BoardState state = BoardState.WAITING;

	public enum BoardState
	{
		WAITING, RUNNING, PAUSED, ENDED
	}

	public void start(Date nowTime)
	{
		Random randomGenerator = new Random();

		for (Player player : players.values())
		{
			Integer x = Math.abs(randomGenerator.nextInt()) % this.getSizeX();
			Integer y = Math.abs(randomGenerator.nextInt()) % this.getSizeY();

			player.setX(x.doubleValue());
			player.setY(y.doubleValue());
			player.setA(0D);
			player.setV(0.1D);
			player.setTime(nowTime);
		}

		state = BoardState.RUNNING;
	}

	public void tick()
	{
		if (!state.equals(BoardState.RUNNING))
		{
			return;
		}

		Date time = new Date();

		for (Player player : players.values())
		{
			PlayerState playerState = extrapolate(player, time);

			System.out.println(playerState);
		}
	}

	public PlayerState extrapolate(Player player, Date time)
	{
        PlayerState state = new PlayerState();

        if (player.getTime() == null) {
            state.setA(player.getA());
            state.setX(player.getX());
            state.setY(player.getY());

            return state;
        }

		Long dT = time.getTime() - player.getTime().getTime();

		if (player.getDirection() == 0)
		{
			state.setX(Math.cos(player.getA()) * player.getV() * dT + player.getX());
			state.setY(Math.sin(player.getA()) * player.getV() * dT + player.getY());
			state.setA(player.getA());
		}
		else
		{
			Double fi = (player.getDirection() * Math.PI * player.getV() * dT) / (2 * player.getR());

			state.setA((player.getA() + fi) % (2 * Math.PI));
			state.setX(-1 * player.getR() * Math.cos(player.getA() + (player.getDirection() * Math.PI / 2) + fi) + player.getR() * Math.cos(player.getA() + player.getDirection() * Math.PI / 2) + player.getX());
			state.setY(-1 * player.getR() * Math.sin(player.getA() + (player.getDirection() * Math.PI / 2) + fi) + player.getR() * Math.sin(player.getA() + player.getDirection() * Math.PI / 2) + player.getY());
		}

		return state;
	}

	public void update(List<Player> deadPlayers, List<Player> teleportedPlayers, Date time)
	{
        if (state != BoardState.RUNNING) {
            return; // collided;
        }

		// TODO: Start (?) with checking for collision with the "walls" of the board
		for (Player player : this.players.values())
		{
            PlayerState state = extrapolate(player, time);
            if ( state.getX() < 0)
            {
                player.setX((double)this.getSizeX());
                player.setTime(time);
                teleportedPlayers.add(player);
            }
            if (state.getX() > this.getSizeX())
            {
                player.setX(0.0);
                player.setTime(time);
                teleportedPlayers.add(player);
            }
            if (state.getY() < 0)
            {
                player.setY((double)this.getSizeY());
                player.setTime(time);
                teleportedPlayers.add(player);
            }
            if (state.getY() > this.getSizeY())
            {
                player.setY(0.0);
                player.setTime(time);
                teleportedPlayers.add(player);
            }

			/*if ((player.getX() < 0) || (player.getX() > this.getSizeX()))
			{
				collided.add(player);
			}
			else if ((player.getY() < 0) || (player.getY() > this.getSizeY()))
			{
				collided.add(player);
			}*/
		}
           /*
		List<Collidable> allParts = new ArrayList<Collidable>();
		for (Player player : this.players.values())
		{
			allParts.addAll(player.getParts());
		}

		for (Player player : this.players.values())
		{
			for (Collidable collidable : allParts)
			{
				if (collidable.isCollision(player.getX(), player.getY()))
				{
					collided.add(player);
				}
			}
		}*/

		return; // collided;
	}

	public Map<String, Player> getPlayers()
	{
		return players;
	}

	public Player getPlayer(String name)
	{
		return players.get(name);
	}

	public void addPlayer(Player player)
	{
		players.put(player.getName(), player);
	}

	public void removePlayer(String name)
	{
		players.remove(name);
	}

	public Integer getSizeX()
	{
		return sizeX;
	}

	public void setSizeX(Integer sizeX)
	{
		this.sizeX = sizeX;
	}

	public Integer getSizeY()
	{
		return sizeY;
	}

	public void setSizeY(Integer sizeY)
	{
		this.sizeY = sizeY;
	}

	public boolean allPlayersAreReady()
	{
		for (Player readyPlayers : this.getPlayers().values())
		{
			if (!readyPlayers.isReady())
			{
				return false;
			}
		}

		return true;
	}

	public static class PlayerState
	{
		private Double x, y, a;

		public PlayerState()
		{
			x = y = a = 0D;
		}

		public Double getX()
		{
			return x;
		}

		public void setX(Double x)
		{
			this.x = x;
		}

		public Double getY()
		{
			return y;
		}

		public void setY(Double y)
		{
			this.y = y;
		}

		public Double getA()
		{
			return a;
		}

		public void setA(Double a)
		{
			this.a = a;
		}

		@Override
		public String toString()
		{
			return "PlayerState{" +
					"x=" + x +
					", y=" + y +
					", a=" + a +
					'}';
		}
	}
}
