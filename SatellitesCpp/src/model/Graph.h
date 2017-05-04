/*
 * Graph.h
 *
 */

#ifndef MODEL_GRAPH_H_
#define MODEL_GRAPH_H_

#include <list>

#include "Node.h"
#include "../model/InputData.h"

class Graph {
public:
	virtual ~Graph();
	Graph(double radius) :
			start_(nullptr), end_(nullptr), initialized_(false), calculated_(false), path_(""), radius_(radius) {

	}

	bool initGraph(const InputData& inputData);
	bool calculateShortestPath();

	bool isInitialized() const {
		return initialized_;
	}

	bool isCalculated() const {
		return calculated_;
	}

	std::string getPath() const {
		return path_;
	}

private:
	Node* start_, *end_;
	std::list<Node*> satellite_nodes_;
	bool initialized_;
	bool calculated_;
	std::string path_;
	double radius_;

	void calculateEdges();
	void reconstructPath();
	void printCoordinates();
};

#endif /* MODEL_GRAPH_H_ */
