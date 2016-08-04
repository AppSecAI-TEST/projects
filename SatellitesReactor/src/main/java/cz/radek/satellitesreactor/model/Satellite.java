package cz.radek.satellitesreactor.model;

/**
 * Satellite represented by name and spheric coordinates
 * @author Tomsu Radek
 */
public class Satellite {

	private final String name;
	private final Spherical sphericalCoordinates;

	public Satellite( String name, Spherical sphericalCoordinates ) {
		this.name = name;
		this.sphericalCoordinates = sphericalCoordinates;
	}

	public String getName() {
		return name;
	}

	public Spherical getSphericalCoordinates() {
		return sphericalCoordinates;
	}

}
