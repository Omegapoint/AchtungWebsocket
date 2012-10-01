package models;

import models.iface.Collidable;

public class Line implements Collidable
{
	private Double x;
	private Double y;
	private Double dx;
	private Double dy;

	public Line()
	{
		
	}
	
	public Line(Double x, Double y, Double dx, Double dy)
	{
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
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
		/* Försökte skapa en rektangel runt linjen, för att få med en felmarginal, och sedan se om punkten ligger i rektangeln.
		 * förmodligen onödigt komplicerat...

		Double b = 4.0;
		Double microY = b/2 * Math.asin(Math.tan(dy/dx));
		Double microX = b/2 * Math.acos(Math.tan(dy/dx));
		
		Double p1x = x + microX;
		Double p1y = y + microY;
		Double p2x = x - microX;
		Double p2y = y - microY;
		Double p3x = x + dx + microX;
		Double p3y = y + dy + microY;
		Double p4x = x + dx - microX;
		Double p4y = y + dy - microY;
	
		*/

		Double k1 = this.dx/this.dy;
		Double k2 = (x-this.x)/(y-this.y);
		
		// Att kolla om de båda lutningarna är exakt den samma kommer bli problem. Det måste till en felmarginal.
		// Felmarginalen måste i sin tur vara rätt avvägd så att man inte krockar för att man bara är nära linjen. 
		if(k1.equals(k2))
		{
			return this.x <= x && x <= this.x+this.dx && this.y <= y && y <= this.y+this.dy;
		}
		return false;
	}
}
