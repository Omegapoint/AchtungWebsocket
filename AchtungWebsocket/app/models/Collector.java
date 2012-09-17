package models;

import java.util.ArrayList;
import java.util.Collection;

public class Collector
{
	private final Player player;
	private final Callback callback;
	private final Collection<Integer> expected;
	private final Collection<Inbound.In> responses;
	
	public Collector(Player player, Callback callback)
	{
		this.player = player;
		this.callback = callback;
		
		this.expected = new ArrayList<Integer>();
		this.responses = new ArrayList<Inbound.In>();
	}

	public Callback getCallback()
	{
		return callback;
	}

	public Player getPlayer()
	{
		return player;
	}

	public void addExpected(Integer id)
	{
		this.expected.add(id);
	}

	public Boolean registerResponse(Inbound.In response)
	{
		if (this.expected.contains(response.getId()))
		{
			this.expected.remove(response.getId());
			this.responses.add(response);
			
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	public Boolean isFinished()
	{
		return expected.isEmpty();
	}
}
