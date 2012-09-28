package models;

import models.iface.Collidable;

public class Line implements Collidable
{
	private Long x;
	private Long y;
	private Long dx;
	private Long dy;

	public Long getX()
	{
		return x;
	}

	public void setX(Long x)
	{
		this.x = x;
	}

	public Long getY()
	{
		return y;
	}

	public void setY(Long y)
	{
		this.y = y;
	}

	public Long getDx()
	{
		return dx;
	}

	public void setDx(Long dx)
	{
		this.dx = dx;
	}

	public Long getDy()
	{
		return dy;
	}

	public void setDy(Long dy)
	{
		this.dy = dy;
	}
	
	public boolean isCollision(Long x, Long y)
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
