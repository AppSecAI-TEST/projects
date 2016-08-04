package cz.radek.satellitesreactor.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tomsu Radek
 */
public class InputData {

	private double seed;
	private List<Satellite> satellites;
	private Spherical start, end;

	public InputData() {
		satellites = new LinkedList<>();
	}

	public double getSeed() {
		return seed;
	}

	public void setSeed( double seed ) {
		this.seed = seed;
	}

	public List<Satellite> getSatellites() {
		return satellites;
	}

	public void setSatellites( List<Satellite> satellites ) {
		this.satellites = satellites;
	}

	public Spherical getStart() {
		return start;
	}

	public void setStart( Spherical start ) {
		this.start = start;
	}

	public Spherical getEnd() {
		return end;
	}

	public void setEnd( Spherical end ) {
		this.end = end;
	}

}
