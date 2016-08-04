package cz.radek.satellitesreactor.tools;

import cz.radek.satellitesreactor.Main;
import cz.radek.satellitesreactor.model.Cartesian;

/**
 *
 * @author Tomsu Radek
 */
public class GeometryTool {

	/**
	 * Calculates distance from line segment (start,end) to point centre and 
	 * compares it to threshold limit
	 * @param centre
	 * @param start
	 * @param end
	 * @param limit
	 * @return true if distance from centre point to line segment is larger than limit
	 */
	public boolean isCentreFarFromLineSegment( Cartesian centre, Cartesian start, Cartesian end, double limit ) {
		Cartesian startEndLine = end.minus( start );
		Cartesian startCentreLine = centre.minus( start );

		double lengthSquared = startEndLine.sizeSquared();
		if( Math.abs( lengthSquared ) < Main.EPSILON ){
			return centre.distanceTo( start ) > limit - Main.EPSILON;
		}

		double t = Math.max( 0, Math.min( 1, startCentreLine.dotProduct( startEndLine ) / lengthSquared ) );
		Cartesian projection = start.plus( startEndLine.multiply( t ) );
		return projection.distanceTo( centre ) > limit - Main.EPSILON;
	}

}
