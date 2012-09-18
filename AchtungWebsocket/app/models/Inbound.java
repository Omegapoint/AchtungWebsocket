package models;


import java.util.Date;

public class Inbound
{
	public static abstract class In
	{
		private Integer id;
		private Date time;
		private Connection sender;
		
		public Integer getId()
		{
			return id;
		}

		public void setId(Integer id)
		{
			this.id = id;
		}

		public Date getTime()
		{
			return time;
		}

		public void setTime(Date time)
		{
			this.time = time;
		}

		public Connection getSender()
		{
			return sender;
		}

		public void setSender(Connection sender)
		{
			this.sender = sender;
		}
	}
	
	public static class Join extends In
	{
		public Join(Connection sender)
		{
			this.setSender(sender);
		}
	}
	
	public static class Quit extends In
	{
		public Quit(Connection sender)
		{
			this.setSender(sender);
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
		private Integer direction;

		public Integer getDirection()
		{
			return direction;
		}

		public void setDirection(Integer direction)
		{
			this.direction = direction;
		}
	}

	public static class Tick extends In
	{

	}
}
