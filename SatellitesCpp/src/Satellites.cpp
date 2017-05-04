//============================================================================
// Name        : Satellites.cpp
// Author      : Radek
//============================================================================

#include <iostream>
#include <boost/log/trivial.hpp>
#include <iostream>

#include "input/InputReader.h"
#include "model/Graph.h"
#include "model/Constants.h"

int main(int argc, const char * argv[]) {
	if (argc != 2) {
		BOOST_LOG_TRIVIAL(error)<< "Not enough arguments";
		return 1;
	}

	InputReader input_reader;
	try {
		InputData input_data = input_reader.parseInput(argv[1]);

		Graph g {constants::kRADIUS};
		g.initGraph( input_data );

		bool result = g.calculateShortestPath();
		if( result ) {
			std::cout << g.getPath() << std::endl;
		} else {
			std::cout << "No path found" << std::endl;
		}

	} catch(std::invalid_argument& ex) {
		BOOST_LOG_TRIVIAL(error) << ex.what();
		return 2;
	} catch(std::runtime_error& ex) {
		BOOST_LOG_TRIVIAL(error) << ex.what();
		return 3;
	}

}
