package models;

import org.junit.Assert;
import org.junit.Test;

public class LineTest {
	
	@Test
	public void testCollision()
	{
		Line l = new Line(1.0, 0.0, 0.0, 5.0);
		Assert.assertTrue(l.isCollision(1.0, 3.0));
		Assert.assertFalse(l.isCollision(2.0, 3.0));
		
		Line l2 = new Line(1.0, 1.0, 2.0, 2.0);
		Assert.assertTrue(l2.isCollision(2.0, 2.0));
		
	}

}
