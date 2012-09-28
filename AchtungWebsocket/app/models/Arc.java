package models;

import models.iface.Collidable;

public class Arc implements Collidable
{
	private Double x;
	private Double y;
	private Double r;
	private Double as;
	private Double ae;
	private Integer direction;

	public Double getX()
	{
		return x;
	}

	public void setX(Double x)
	{
		this.x = x;
	}

	public Double getY()
	{
		return y;
	}

	public void setY(Double y)
	{
		this.y = y;
	}

	public Double getR()
	{
		return r;
	}

	public void setR(Double r)
	{
		this.r = r;
	}

	public Double getAe()
	{
		return ae;
	}

	public void setAe(Double ae)
	{
		this.ae = ae;
	}

	public Double getAs()
	{
		return as;
	}

	public void setAs(Double as)
	{
		this.as = as;
	}

	public Integer getDirection()
	{
		return direction;
	}

	public void setDirection(Integer direction)
	{
		this.direction = direction;
	}

	public boolean isCollision(Double x, Double y)
	{
		return false;
	}

}
