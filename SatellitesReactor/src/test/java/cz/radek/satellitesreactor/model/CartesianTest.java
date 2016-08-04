package cz.radek.satellitesreactor.model;

import cz.radek.satellitesreactor.Main;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Tomsu Radek
 */
public class CartesianTest {

	@Test
	public void zeroVectorSize() {
		Cartesian c = new Cartesian( 0, 0, 0 );

		double vectorSize = c.size();

		Assert.assertEquals( 0, vectorSize, Main.EPSILON );
	}
	
	@Test
	public void unitVectorSize() {
		Cartesian c = new Cartesian( 1, 0, 0 );

		double vectorSize = c.size();

		Assert.assertEquals( 1, vectorSize, Main.EPSILON );
	}
	
	@Test
	public void someVectorSize() {
		Cartesian c = new Cartesian( 4, 5, 7 );

		double vectorSize = c.size();

		Assert.assertEquals( Math.sqrt( 90 ), vectorSize, Main.EPSILON );
	}
	
	@Test
	public void someVectorSizeSquare() {
		Cartesian c = new Cartesian( 4, 5, 7 );

		double vectorSize = c.sizeSquared();

		Assert.assertEquals( 90, vectorSize, Main.EPSILON );
	}
	
	public void addVector(){
		Cartesian c = new Cartesian( 4, 5, 7 );
		Cartesian res = c.plus( new Cartesian(3, 4, 7));
		
		Assert.assertEquals(7, res.getX(), Main.EPSILON);
		Assert.assertEquals(9, res.getY(), Main.EPSILON);
		Assert.assertEquals(14, res.getZ(), Main.EPSILON);
	}
	
	public void subtractVector(){
		Cartesian c = new Cartesian( 4, 5, 7 );
		Cartesian res = c.plus( new Cartesian(3, 4, 7));
		
		Assert.assertEquals(1, res.getX(), Main.EPSILON);
		Assert.assertEquals(1, res.getY(), Main.EPSILON);
		Assert.assertEquals(0, res.getZ(), Main.EPSILON);
	}
	
	public void scaleVector(){
		Cartesian c = new Cartesian( 4, 5, 7 );
		Cartesian res = c.multiply( 3 );
		
		Assert.assertEquals(12, res.getX(), Main.EPSILON);
		Assert.assertEquals(15, res.getY(), Main.EPSILON);
		Assert.assertEquals(21, res.getZ(), Main.EPSILON);
	}
	
	public void dotProductTest(){
		Cartesian c = new Cartesian( 4, 5, 7 );
		double res = c.dotProduct(new Cartesian(3, 4, 7));
		
		Assert.assertEquals(81, res, Main.EPSILON);
	}
}
