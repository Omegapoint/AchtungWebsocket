package controllers;

import java.util.Random;

import models.Game;
import models.Inbound;
import models.Player;
import models.exception.WebsocketException;

import org.codehaus.jackson.JsonNode;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.WebSocket;

public class WebsocketRouter extends Controller
{
	private static final String ID_NODE = "Id";
	private static final String TYPE_NODE = "Action";
	private static final String SENDER_NODE = "Name";
	private static final String MESSAGE_NODE = "Message";
	private static final String MESSAGE_PACKAGE = "models.Message.";
	
	public static WebSocket<JsonNode> connect(final String name)
	{
		return new WebSocket<JsonNode>() 
		{
			@Override
			public void onReady(WebSocket.In<JsonNode> inStream,  WebSocket.Out<JsonNode> outStream)
			{
				final Player player = new Player(name, outStream);
								
				Game.getActor().tell(new Inbound.Join(player));
				
				inStream.onMessage(new Callback<JsonNode>()
				{
					@Override
					public void invoke(JsonNode message) throws Throwable
					{
						try
						{
							System.out.println("MSG");
							System.out.println(message.toString());
							Game.getActor().tell(jsonToMessage(message, player));
						}
						catch (WebsocketException ex)
						{
							player.getOut().write(Json.toJson(ex.getErrorMessage()));
						}
						catch (Exception ex)
						{
							player.getOut().write(Json.toJson(WebsocketException.exceptionToMessage(ex)));
						}
					}
			
				});
				
				inStream.onClose(new Callback0()
				{
					@Override
					public void invoke() throws Throwable
					{
						try
						{
							System.out.println("QUIT");
							Game.getActor().tell(new Inbound.Quit(player));
						}
						catch (Exception ex)
						{
							player.getOut().write(Json.toJson(WebsocketException.exceptionToMessage(ex)));
						}
					}
				});
			}
			
		};
	}
	
	private static Inbound.In jsonToMessage(JsonNode message, Player player) throws WebsocketException
	{
		Inbound.In messageInstance = null;
				
		try
		{
			Integer messageId = message.get(ID_NODE).asInt();
			String messageType = message.get(TYPE_NODE).asText();
			String senderName = message.get(SENDER_NODE).asText();
			JsonNode messageData = message.get(MESSAGE_NODE);
			Class<Inbound.In> messageClass = (Class<Inbound.In>) Class.forName(new StringBuilder().append(MESSAGE_PACKAGE).append(messageType).toString());
			
			if ((messageId == null) || (messageId == 0))
			{
				messageId = new Random().nextInt();
			}
			
			messageInstance = Json.fromJson(messageData, messageClass);
			messageInstance.setPlayer(player);
			messageInstance.setId(messageId);
		}
		catch (ClassNotFoundException ex)
		{
			throw new WebsocketException(ex);
		}
		
		return messageInstance;
	}

}
