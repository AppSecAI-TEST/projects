/*
 * InputData.h
 *
 */

#ifndef MODEL_INPUTDATA_H_
#define MODEL_INPUTDATA_H_

#include <list>

#include "Satellite.h"
#include "Spherical.h"

struct InputData {
	double seed;
	std::list<Satellite> satellites;
	Spherical start, end;
};

#endif /* MODEL_INPUTDATA_H_ */
