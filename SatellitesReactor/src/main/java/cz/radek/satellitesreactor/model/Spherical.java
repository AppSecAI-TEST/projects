package cz.radek.satellitesreactor.model;

/**
 * Spherical coordinates
 * @author Tomsu Radek
 */
public class Spherical {

	private final double latitude;
	private final double longitude;
	private final double altitude;

	public Spherical( double latitude, double longitude, double altitude ) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getAltitude() {
		return altitude;
	}

}
