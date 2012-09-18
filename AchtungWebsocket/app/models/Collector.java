package models;

import java.util.ArrayList;
import java.util.Collection;

public class Collector
{
	private final Connection collector;
	private final ICallback callback;
	private final Collection<Integer> expected;
	private final Collection<Inbound.In> responses;
	
	public Collector(Connection collector, ICallback callback)
	{
		this.collector = collector;
		this.callback = callback;
		
		this.expected = new ArrayList<Integer>();
		this.responses = new ArrayList<Inbound.In>();
	}

	public ICallback getCallback()
	{
		return callback;
	}

	public Connection getCollector()
	{
		return collector;
	}

	public Collection<Inbound.In> getResponses()
	{
		return responses;
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
