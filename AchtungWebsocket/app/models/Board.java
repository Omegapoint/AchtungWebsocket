package models;

import java.util.*;

public class Board
{
	private final Map<String, Player> players = new HashMap<String, Player>();

	private BoardState state = BoardState.WAITING;

	public enum BoardState
	{
		WAITING, RUNNING, PAUSED, ENDED
	}

	public void start()
	{
		Date nowTime = new Date();

		for (Player player : players.values())
		{
			player.setX(0L);
			player.setY(0L);
			player.setA(0D);
			player.setV(0.0001D);
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
		Long msDiff = time.getTime() - player.getTime().getTime();

		if (player.getDirection() == 0)
		{
			state.setX(player.getX().doubleValue() + Math.cos(player.getA()) * player.getV() * msDiff);
			state.setY(player.getY().doubleValue() + Math.sin(player.getA()) * player.getV() * msDiff);
			state.setA(player.getA());
		}
		else
		{
			Double vx, vy, cx, cy;

			vx = Math.cos(player.getA() + player.getDirection().doubleValue() * Math.PI / 2D) * player.getR();
			vy = Math.sin(player.getA() + player.getDirection().doubleValue() * Math.PI / 2D) * player.getR();
			cx = player.getX().doubleValue() - vx;
			cy = player.getY().doubleValue() - vy;

			state.setA(player.getA() + player.getV() * msDiff / (2D * Math.PI * player.getR()));
			state.setX(player.getX().doubleValue() + Math.cos(state.getA()) * player.getR());
			state.setY(player.getY().doubleValue() + Math.sin(state.getA()) * player.getR());
		}

		return state;
	}

	public List<Player> update()
	{
		List<Player> collided = new ArrayList<Player>();

		// TODO: Detect collisions and add to list

		return collided;
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

	public static class PlayerState
	{
		private Double x, y, a;

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
