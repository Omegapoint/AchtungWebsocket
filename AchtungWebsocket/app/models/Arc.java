package models;

import models.iface.Collidable;

public class Arc implements Collidable
{
	private Long x;
	private Long y;
	private Long r;
	private Double amc;
	private Double amcc;

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

	public Long getR()
	{
		return r;
	}

	public void setR(Long r)
	{
		this.r = r;
	}

	public Double getAmc()
	{
		return amc;
	}

	public void setAmc(Double amc)
	{
		this.amc = amc;
	}

	public Double getAmcc()
	{
		return amcc;
	}

	public void setAmcc(Double amcc)
	{
		this.amcc = amcc;
	}

	public boolean isCollision(Long x, Long y) {
		return false;
	}
	
	
}
