package controllers;

import models.Connection;
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

import java.util.Date;
import java.util.Random;

public class WebsocketRouter extends Controller
{
	private static final String ID_NODE = "id";
	private static final String TYPE_NODE = "action";
	private static final String SENDER_NODE = "name";
	private static final String MESSAGE_NODE = "message";
	private static final String MESSAGE_PACKAGE = "models.Inbound$";
	
	public static WebSocket<JsonNode> connect(final String name)
	{
		return new WebSocket<JsonNode>()
		{
			@Override
			public void onReady(WebSocket.In<JsonNode> inStream,  WebSocket.Out<JsonNode> outStream)
			{
				final Player player = new Player(name);
				final Connection connection = new Connection(player, inStream, outStream);
								
				Game.getActor().tell(new Inbound.Join(connection));

				inStream.onMessage(new Callback<JsonNode>()
				{
					@Override
					public void invoke(JsonNode message) throws Throwable
					{
						try
						{
							System.out.println("MSG");
							System.out.println(message.toString());
							Game.getActor().tell(jsonToMessage(message, connection));
						}
						catch (WebsocketException ex)
						{
							ex.printStackTrace();
							connection.getOut().write(Json.toJson(ex.getErrorMessage()));
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
							connection.getOut().write(Json.toJson(WebsocketException.exceptionToMessage(ex)));
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
							Game.getActor().tell(new Inbound.Quit(connection));
						}
						catch (Exception ex)
						{
							connection.getOut().write(Json.toJson(WebsocketException.exceptionToMessage(ex)));
						}
					}
				});
			}
			
		};
	}
	
	private static Inbound.In jsonToMessage(JsonNode message, Connection sender) throws WebsocketException
	{
		Inbound.In messageInstance = null;
				
		try
		{
			JsonNode idNode = message.get(ID_NODE);
			JsonNode typeNode = message.get(TYPE_NODE);
			JsonNode senderNode = message.get(SENDER_NODE);
			JsonNode messageData = message.get(MESSAGE_NODE);

			Integer messageId = (idNode == null) ? null : idNode.asInt();
			String messageType = (typeNode == null) ? null : typeNode.asText();
			String senderName = (senderNode == null) ? null : senderNode.asText();

			Class<Inbound.In> messageClass = (Class<Inbound.In>) Class.forName(new StringBuilder().append(MESSAGE_PACKAGE).append(messageType).toString());

			if ((messageId == null) || (messageId == 0))
			{
				messageId = new Random().nextInt();
			}

			messageInstance = Json.fromJson(messageData, messageClass);
			messageInstance.setSender(sender);
			messageInstance.setTime(new Date());
			messageInstance.setId(messageId);
		}
		catch (ClassNotFoundException ex)
		{
			throw new WebsocketException(ex);
		}

		return messageInstance;
	}

}
