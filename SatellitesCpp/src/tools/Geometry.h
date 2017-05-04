/*
 * Geometry.h
 *
 */

#ifndef TOOLS_GEOMETRY_H_
#define TOOLS_GEOMETRY_H_

#include <math.h>

#include "../model/Cartesian.h"
#include "../model/Constants.h"

namespace geometry {

bool isCentreFarFromLineSegment(const Cartesian& centre, const Cartesian& start, const Cartesian& end, double limit) {
	Cartesian startEndLine = end.minus(start);
	Cartesian startCentreLine = centre.minus(start);

	double lengthSquared = startEndLine.sizeSquared();
	if (abs(lengthSquared) < constants::kEPSILON) {
		return centre.distanceTo(start) > limit - constants::kEPSILON;
	}

	double t = std::max(0.0, std::min(1.0, startCentreLine.dotProduct(startEndLine) / lengthSquared));
	Cartesian projection = start.plus(startEndLine.multiply(t));
	return projection.distanceTo(centre) > limit - constants::kEPSILON;
}
}

#endif /* TOOLS_GEOMETRY_H_ */
