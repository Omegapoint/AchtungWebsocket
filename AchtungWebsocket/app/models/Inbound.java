package models;


public class Inbound
{
	public static abstract class In
	{
		private Integer id;
		private Player player;
		
		public Integer getId()
		{
			return id;
		}

		public void setId(Integer id)
		{
			this.id = id;
		}

		public Player getPlayer()
		{
			return player;
		}
		
		public void setPlayer(Player player)
		{
			this.player = player;
		}
	}
	
	public static class Join extends In
	{
		public Join(Player player)
		{
			this.setPlayer(player);
		}
	}
	
	public static class Quit extends In
	{
		public Quit(Player player)
		{
			this.setPlayer(player);
		}
	}
	
	public static class Ping extends In
	{
		
	}
	
	public static class Pong extends In
	{
		
	}
	
	public static class Pause extends In
	{
		
	}
	
	public static class Ready extends In
	{
		
	}
	
	public static class Direction extends In
	{
		
	}
}
