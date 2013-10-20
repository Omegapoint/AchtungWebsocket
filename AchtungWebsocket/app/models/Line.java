package models;

import models.iface.Collidable;
import java.util.Date;

public class Line implements Collidable
{
	private Double x;
	private Double y;
	private Double dx;
	private Double dy;
    private Date created;

	public Line()
	{
        this.created = new Date();
    }
	
	public Line(Double x, Double y, Double dx, Double dy)
	{
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
        this.created = new Date();
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

    public Date getCreationDate()
    {
        return created;
    }
	
	public boolean isCollision(Double x, Double y, Date now)
	{
        final Integer timeThreshold = 200;
        final Double distanceThreshold = 6.0;

        if (now.getTime() - created.getTime() < timeThreshold)
            return false;

        // http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
        Double sq_sum = this.getDx() * this.getDx() + this.getDy() * this.getDy();
        Double u = ((x-this.getX())*this.getDx() + (y-this.getY())*this.getDy())/sq_sum;
        u = clamp(u, 0.0, 1.0);

        Double xx = this.getX() + u * this.getDx();
        Double yy = this.getY() + u * this.getDy();

        Double dx = xx - x;
        Double dy = yy - y;
        Double distance = Math.sqrt(dx*dx + dy*dy);

        return (distance < distanceThreshold);
	}

    private Double clamp(Double input, Double min, Double max)
    {
        return Math.max(Math.min(input, max), min);
    }
}
