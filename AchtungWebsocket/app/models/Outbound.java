package models;

import java.util.Date;
import java.util.Random;

public class Outbound
{
	private static final Random random = new Random();
	
	public static Integer generateOutboundId()
	{
		return random.nextInt();
	}
	
	public static abstract class Out
	{
		private Integer id;
		private Date time;
		
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
	}
	
	public static class Ping extends Out
	{
		
	}
	
	public static class Pong extends Out
	{
		
	}
	
	public static class Error extends Out
	{
		final String message;
		
		public Error(String message)
		{
			this.message = message;
		}

		public String getMessage()
		{
			return message;
		}
	}
}
