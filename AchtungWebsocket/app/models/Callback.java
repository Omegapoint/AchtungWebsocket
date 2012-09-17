package models;

public interface Callback<T>
{
	public T execute(Collector collector);
}
