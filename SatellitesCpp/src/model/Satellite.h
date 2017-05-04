/*
 * Satellite.h
 *
 */

#ifndef MODEL_SATELLITE_H_
#define MODEL_SATELLITE_H_

#include <string>

#include "Spherical.h"

struct Satellite {
	std::string name = "";
	Spherical spherical_coordinates = Spherical();
};

#endif /* MODEL_SATELLITE_H_ */
