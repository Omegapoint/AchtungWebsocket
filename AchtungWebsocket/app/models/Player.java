package models;

import org.codehaus.jackson.JsonNode;

import play.mvc.WebSocket;
import play.mvc.WebSocket.Out;

public class Player
{
	private final String name;
	private final WebSocket.Out<JsonNode> out;
	
	public Player(String name, Out<JsonNode> out)
	{
		this.name = name;
		this.out = out;
	}

	public String getName()
	{
		return name;
	}

	public WebSocket.Out<JsonNode> getOut()
	{
		return out;
	}
}
