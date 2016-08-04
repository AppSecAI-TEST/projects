package cz.radek.satellitesreactor.model;

/**
 * Cartesian coordinates with basic operations
 * @author Tomsu Radek
 */
public class Cartesian {

	public static Cartesian CENTRE = new Cartesian( 0, 0, 0 );

	private final double x;
	private final double y;
	private final double z;

	public Cartesian( double x, double y, double z ) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	/**
	 * Subtraction 
	 * @param c Cartesian
	 * @return Cartesian
	 */
	public Cartesian minus( Cartesian c ) {
		return new Cartesian( x - c.x, y - c.y, z - c.z );
	}

	/**
	 * Addition
	 * @param c Cartesian
	 * @return Cartesian
	 */
	public Cartesian plus( Cartesian c ) {
		return new Cartesian( x + c.x, y + c.y, z + c.z );
	}

	/**
	 * Scalar multiplication
	 * @param s Scalar
	 * @return Cartesian
	 */
	public Cartesian multiply( double s ) {
		return new Cartesian( x * s, y * s, z * s );
	}

	/**
	 * Vector product
	 * @param c Cartesian
	 * @return Cartesian
	 */
	public Cartesian vectorProduct( Cartesian c ) {
		return new Cartesian(
				y * c.z - c.y * z,
				x * c.z - c.x * z,
				x * c.y - c.x * y );
	}

	/**
	 * Dot product
	 * @param c Cartesian
	 * @return scalar
	 */
	public double dotProduct( Cartesian c ) {
		return x * c.x + y * c.y + z * c.z;
	}

	/**
	 * Distance to other point
	 * @param c Cartesian
	 * @return distance
	 */
	public double distanceTo( Cartesian c ) {
		return c.minus( this ).size();
	}

	/**
	 * Size of vector represented in Cartesian coordinates
	 * @return 
	 */
	public double size() {
		return Math.sqrt( Math.pow( x, 2 ) + Math.pow( y, 2 ) + Math.pow( z, 2 ) );
	}

	/**
	 * Squared size of vector represented in Cartesian coordinates
	 * @return 
	 */
	public double sizeSquared() {
		return Math.pow( x, 2 ) + Math.pow( y, 2 ) + Math.pow( z, 2 );
	}
}
