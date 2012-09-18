package models;

import org.codehaus.jackson.JsonNode;
import play.mvc.WebSocket;

public class Connection
{
	private final Player player;
	private final WebSocket.In<JsonNode> in;
	private final WebSocket.Out<JsonNode> out;

	public Connection(Player player, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out)
	{
		this.player = player;
		this.in = in;
		this.out = out;
	}

	public Player getPlayer()
	{
		return player;
	}

	public WebSocket.In<JsonNode> getIn()
	{
		return in;
	}

	public WebSocket.Out<JsonNode> getOut()
	{
		return out;
	}
}
