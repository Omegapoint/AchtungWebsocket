package models;

import models.iface.Collidable;

public class Line implements Collidable
{
	private Double x;
	private Double y;
	private Double dx;
	private Double dy;

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

	public Double getDx()
	{
		return dx;
	}

	public void setDx(Double dx)
	{
		this.dx = dx;
	}

	public Double getDy()
	{
		return dy;
	}

	public void setDy(Double dy)
	{
		this.dy = dy;
	}
	
	public boolean isCollision(Double x, Double y)
	{
		double k1 = this.dx/this.dy;
		double k2 = (x-this.x)/(y-this.y);
		if(k1 == k2)
		{
			return this.x <= x && x <= this.x+this.dx && this.y <= y && y <= this.y+this.dy;
		}
		return false;
		
		//return this.x <= x && x <= this.x+this.dx && this.y <= y && y <= this.y+this.dy && (this.dx/this.dy == (x-this.x)/(y-this.y));
	}
}
