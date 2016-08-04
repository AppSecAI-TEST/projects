package cz.radek.satellitesreactor.tools;

import cz.radek.satellitesreactor.Main;
import cz.radek.satellitesreactor.model.Cartesian;
import cz.radek.satellitesreactor.model.Spherical;

/**
 * Converter. Converts between radian/degrees and Spheric/Cartesian coordinates
 * @author Tomsu Radek
 */
public class Converter {

	/**
	 * Uses earth radius to shift all coordinates
	 * @param input spherical coordinates
	 * @param radius earth radius
	 * @return 
	 */
	public Cartesian fromSphericalToCartesian( Spherical input, double radius ) {
		double x, y, z;

		double latRad = fromDegreeToRadian( input.getLatitude() );
		double longRad = fromDegreeToRadian( input.getLongitude() );

		x = (radius + input.getAltitude()) * Math.cos( latRad ) * Math.cos( longRad );
		y = (radius + input.getAltitude()) * Math.cos( latRad ) * Math.sin( longRad );
		z = (radius + input.getAltitude()) * Math.sin( latRad );
		return new Cartesian( x, y, z );
	}

	/**
	 * Input is expected to be shifted by earth radius, same for all application
	 * @param input Cartesian coordinates
	 * @param radius earth radius
	 * @return 
	 */
	public Spherical fromCartesianToSpherical( Cartesian input, double radius ) {
		double latitude, longitude, altitude, size;

		size = input.size();
		altitude = size - radius;
		if( altitude < -Main.EPSILON ){
			return null;
		}

		latitude = fromRadianToDegree( Math.asin( input.getZ() / size ) );
		longitude = fromRadianToDegree( Math.atan2( input.getY(), input.getX() ) );
		return new Spherical( latitude, longitude, altitude );
	}

	public double fromDegreeToRadian( double degree ) {
		return degree * Math.PI / 180.0;
	}

	public double fromRadianToDegree( double radian ) {
		return radian * 180.0 / Math.PI;
	}

}
