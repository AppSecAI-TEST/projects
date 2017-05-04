/*
 * Converting.h
 *
 */

#ifndef TOOLS_CONVERTING_H_
#define TOOLS_CONVERTING_H_

#include <math.h>

#include "../model/Cartesian.h"
#include "../model/Spherical.h"
#include "../model/Constants.h"

namespace converting {
double fromDegreeToRadian(double degree) {
	return degree * M_PI / 180.0;
}

double fromRadianToDegree(double radian)  {
	return radian * 180.0 / M_PI;
}

Cartesian fromSphericalToCartesian(const Spherical& input, double radius) {
	double x, y, z;

	double latRad = fromDegreeToRadian(input.latitude);
	double longRad = fromDegreeToRadian(input.longitude);

	x = (radius + input.altitude) * cos(latRad) * cos(longRad);
	y = (radius + input.altitude) * cos(latRad) * sin(longRad);
	z = (radius + input.altitude) * sin(latRad);

	return Cartesian(x, y, z);
}

Spherical fromCartesianToSpherical(const Cartesian& input, double radius) {
	double size;
	Spherical result;

	size = input.size();
	result.altitude = size - radius;
	if (result.altitude < constants::kEPSILON) {
		return Spherical();
	}

	result.latitude = fromRadianToDegree(asin(input.getZ() / size));
	result.longitude = fromRadianToDegree(atan2(input.getY(), input.getX()));
	return result;
}
}

#endif /* TOOLS_CONVERTING_H_ */
