package models;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	private List<Object> parts;
	
	public Player(String name)
	{
		this.name = name;
		this.color = PLAYER_COLOR[PLAYER_INDEX];
		this.direction = 0;
		this.parts = new ArrayList<Object>();

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

	@Transient
	public List<Object> getParts()
	{
		return parts;
	}

	public void addPart(Object part)
	{
		if ((part instanceof Arc) || (part instanceof Line))
		{
			parts.add(part);
		}
		else
		{
			throw new IllegalArgumentException("Can only add Line or Arc to part list.");
		}
	}

	public Object flush(Board.PlayerState extrapolated)
	{
		Object retval =  null;

		if (this.direction == 0) // Line
		{
			Line line = new Line();
			line.setX(this.x);
			line.setY(this.y);
			line.setDx(Math.round(extrapolated.getX()) - this.x);
			line.setDy(Math.round(extrapolated.getY()) - this.y);
			retval = line;
		}
		else
		{
			Arc arc = new Arc();

			// TODO: Generate arc

			retval = arc;
		}

		this.setA(extrapolated.getA());
		this.setX(Math.round(extrapolated.getX()));
		this.setY(Math.round(extrapolated.getY()));

		return retval;
	}
}
