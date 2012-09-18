package models;

import java.util.Date;

public class Player
{
	private static Integer PLAYER_INDEX = 0;
	private static final Integer PLAYER_RADIUS = 20;
	private static final String[] PLAYER_COLOR = new String[]{ "#ff0000", "#00ff00", "#0000ff", "#0ff000", "#ffff00" };

	private final String name;
	private final String color;

	private Date time;
	private Double a;
	private Double v;
	private Long x;
	private Long y;
	private Integer direction;
	
	public Player(String name)
	{
		this.name = name;
		this.color = PLAYER_COLOR[PLAYER_INDEX];
		this.direction = 0;

		PLAYER_INDEX++;
	}

	public String getColor()
	{
		return color;
	}

	public String getName()
	{
		return name;
	}

	public Date getTime()
	{
		return time;
	}

	public void setTime(Date time)
	{
		this.time = time;
	}

	public Double getA()
	{
		return a;
	}

	public void setA(Double a)
	{
		this.a = a;
	}

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

	public Integer getR()
	{
		return PLAYER_RADIUS;
	}

	public Double getV()
	{
		return v;
	}

	public void setV(Double v)
	{
		this.v = v;
	}

	public Integer getDirection()
	{
		return direction;
	}

	public void setDirection(Integer direction)
	{
		this.direction = direction;
	}

	public void flush(Board.PlayerState extrapolated)
	{
		this.setA(extrapolated.getA());
		this.setX(Math.round(extrapolated.getX()));
		this.setY(Math.round(extrapolated.getY()));
	}
}
