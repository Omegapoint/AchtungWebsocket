package models.iface;

import java.util.Date;

public interface Collidable {
	public boolean isCollision(Double x, Double y, Date now);
}
