package models.exception;

import models.Outbound;

public class WebsocketException extends Exception
{
	private static final long serialVersionUID = 1L;
	
	private Outbound.Error error = null;
	
	public WebsocketException()
	{
		super();
	}

	// Removed to make JDK6 compatible
	/*public WebsocketException(String arg0, Throwable arg1, boolean arg2, boolean arg3)
	{
		super(arg0, arg1, arg2, arg3);
	}*/

	public WebsocketException(String arg0, Throwable arg1)
	{
		super(arg0, arg1);
	}

	public WebsocketException(String arg0)
	{
		super(arg0);
	}

	public WebsocketException(Throwable arg0)
	{
		super(arg0);
	}

	public Outbound.Error getErrorMessage()
	{
		if (error == null)
		{
			error = WebsocketException.exceptionToMessage(this);
		}
		
		return error;
	}
	
	public static Outbound.Error exceptionToMessage(Exception ex)
	{
		return new Outbound.Error(ex.getMessage());
	}
}
