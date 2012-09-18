package models;

public interface ICallback<T>
{
	public T execute(Collector collector);
}
