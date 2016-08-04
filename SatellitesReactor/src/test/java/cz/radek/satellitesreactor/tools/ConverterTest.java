package cz.radek.satellitesreactor.tools;

import cz.radek.satellitesreactor.Main;
import cz.radek.satellitesreactor.model.Cartesian;
import cz.radek.satellitesreactor.model.Spherical;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Tomsu Radek
 */
public class ConverterTest {

	private Converter converter;
	private double radius = 66;
	
	@Before
	public void setup() {
		converter = new Converter( );
	}

	@Test
	public void degRadDeg() {
		double degree = 45;

		double radian = converter.fromDegreeToRadian( degree );
		double convertedDegree = converter.fromRadianToDegree( radian );

		Assert.assertEquals( degree, convertedDegree, Main.EPSILON );
	}

	@Test
	public void radDegRad() {
		double radian = 1;

		double degree = converter.fromRadianToDegree( radian );
		double convertedRadian = converter.fromDegreeToRadian( degree );

		Assert.assertEquals( radian, convertedRadian, Main.EPSILON );
	}

	@Test
	public void radToDeg() {
		double radian = Math.PI / 6.0;
		double expDegree = 30.0;

		double res = converter.fromRadianToDegree( radian );

		Assert.assertEquals( expDegree, res, Main.EPSILON );
	}

	@Test
	public void degToRad() {
		double degree = 90.0;
		double expRadian = Math.PI / 2.0;

		double res = converter.fromDegreeToRadian( degree );

		Assert.assertEquals( expRadian, res, Main.EPSILON );
	}

	@Test
	public void CartesianSphericalCartesian() {
		Cartesian cartesian = new Cartesian( 45, 45, 700 );

		Spherical spherical = converter.fromCartesianToSpherical( cartesian, radius );
		Cartesian convertedCartesian = converter.fromSphericalToCartesian( spherical, radius );

		Assert.assertEquals( cartesian.getX(), convertedCartesian.getX(), Main.EPSILON );
		Assert.assertEquals( cartesian.getY(), convertedCartesian.getY(), Main.EPSILON );
		Assert.assertEquals( cartesian.getZ(), convertedCartesian.getZ(), Main.EPSILON );
	}

	@Test
	public void sphericalCartesianSpherical() {
		Spherical spherical = new Spherical( 30, 45, 500 );

		Cartesian cartesian = converter.fromSphericalToCartesian( spherical, radius );
		Spherical convertedSpherical = converter.fromCartesianToSpherical( cartesian, radius );

		Assert.assertEquals( spherical.getAltitude(), convertedSpherical.getAltitude(), Main.EPSILON );
		Assert.assertEquals( spherical.getLatitude(), convertedSpherical.getLatitude(), Main.EPSILON );
		Assert.assertEquals( spherical.getLongitude(), convertedSpherical.getLongitude(), Main.EPSILON );
	}

	@Test
	public void invalidCartesian() {
		Cartesian cartesian = new Cartesian( 0, 0, 0 );
		Spherical convertedSpherical = converter.fromCartesianToSpherical( cartesian, radius );

		Assert.assertEquals( null, convertedSpherical );
	}
}
