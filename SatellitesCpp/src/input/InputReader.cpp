/*
 * InputReader.cpp
 *
 */

#include "InputReader.h"

#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <boost/algorithm/string.hpp>

InputData InputReader::parseInput(const char* file_name) {
	if (file_name == nullptr) {
		throw std::invalid_argument("file_name is null");
	}

	std::ifstream ifs(file_name, std::ifstream::in);

	if (!ifs.is_open()) {
		throw std::runtime_error("file not opened");
	}

	InputData result;
	for (std::string line; std::getline(ifs, line);) {
		std::vector<std::string> split_line, split_tmp;
		boost::split(split_line, line, boost::is_any_of(","));

		if (split_line[0].find("#SEED") != std::string::npos) {
			boost::split(split_tmp, split_line[0], boost::is_any_of(":"));
			boost::trim(split_tmp[1]);
			result.seed = std::stod(split_tmp[1]);
		} else if (split_line[0].find("SAT") != std::string::npos) {
			Satellite satellite;
			satellite.spherical_coordinates.latitude = std::stod(split_line[1]);
			satellite.spherical_coordinates.longitude = std::stod(
					split_line[2]);
			satellite.spherical_coordinates.altitude = std::stod(split_line[3]);
			satellite.name = split_line[0];
			result.satellites.push_back(satellite);
		} else if (split_line[0].find("ROUTE") != std::string::npos) {
			Spherical start, end;
			start.latitude = std::stod(split_line[1]);
			start.longitude = std::stod(split_line[2]);
			end.latitude = std::stod(split_line[3]);
			end.longitude = std::stod(split_line[4]);
			result.start = start;
			result.end = end;
		}
	}
	return result;
}
