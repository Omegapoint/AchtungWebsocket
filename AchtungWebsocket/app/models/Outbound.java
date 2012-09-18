package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Outbound
{
	private static final Random random = new Random();
	
	public static Integer generateOutboundId()
	{
		return random.nextInt();
	}
	
	public static class Out<T>
	{
		private Integer id;
		private Date time;
		private final T message;

		public Out(T message)
		{
			this.message = message;
		}

		public String getAction()
		{
			return this.message.getClass().getSimpleName();
		}
		
		public Integer getId()
		{
			return id;
		}

		public void setId(Integer id)
		{
			this.id = id;
		}

		public T getMessage()
		{
			return message;
		}

		public Date getTime()
		{
			return time;
		}

		public void setTime(Date time)
		{
			this.time = time;
		}
	}
	
	public static class Ping
	{
	}
	
	public static class Pong
	{
		final List<Player> players = new ArrayList<Player>();

		public List<Player> getPlayers()
		{
			return players;
		}
		
	}

	public static class Join
	{
		private Player player;

		public Player getPlayer()
		{
			return player;
		}

		public void setPlayer(Player player)
		{
			this.player = player;
		}
	}

	public static class Welcome
	{
		private final List<Player> players = new ArrayList<Player>();

		public List<Player> getPlayers()
		{
			return players;
		}
	}

	public static class Direction
	{
		private Player player;

		public Player getPlayer()
		{
			return player;
		}

		public void setPlayer(Player player)
		{
			this.player = player;
		}
	}

	public static class Leave
	{
		private Player player;

		public Player getPlayer()
		{
			return player;
		}

		public void setPlayer(Player player)
		{
			this.player = player;
		}
	}

	public static class Death
	{
		private final List<Player> players = new ArrayList<Player>();

		public List<Player> getPlayers()
		{
			return players;
		}

		public void setPlayers(List<Player> players)
		{
			this.players.addAll(players);
		}
	}
	
	public static class Error
	{
		String message;
		
		public Error(String message)
		{
			this.message = message;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}
}
