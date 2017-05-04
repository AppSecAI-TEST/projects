/*
 * Graph.cpp
 *
 */

#include "Graph.h"

#include <math.h>
#include <functional>
#include <vector>
#include <algorithm>
#include <iostream>

#include "../tools/Converting.h"
#include "../model/InputData.h"
#include "../tools/Geometry.h"
#include "../model/Cartesian.h"

Graph::~Graph() {
	delete start_;
	delete end_;
	for (Node* satellite_node : satellite_nodes_)
		delete satellite_node;
}

bool Graph::initGraph(const InputData& inputData) {
	start_ = new Node("START",
			converting::fromSphericalToCartesian(inputData.start, radius_));
	end_ = new Node("END",
			converting::fromSphericalToCartesian(inputData.end, radius_));
	for (Satellite satellite : inputData.satellites) {
		satellite_nodes_.push_back(
				new Node(satellite.name, converting::fromSphericalToCartesian(satellite.spherical_coordinates, radius_)));
	}
	calculateEdges();
	initialized_ = true;
	return true;
}

bool Graph::calculateShortestPath() {
	if (!initialized_) {
		return false;
	}

	auto my_comp = [](Node* a, Node* b) { return a->getDistance() < b->getDistance(); };

	std::vector<Node*> node_vector;

	for(Node* node: satellite_nodes_){
		node_vector.push_back(node);
	}
	node_vector.push_back(end_);

	start_->setDistance(0);
	node_vector.push_back(start_);
	while (!node_vector.empty()) {
		Node* n = *std::min_element(node_vector.begin(), node_vector.end(), my_comp);
		node_vector.erase(std::remove(node_vector.begin(), node_vector.end(), n), node_vector.end());

		n->close();
		if (n == end_) {
			reconstructPath();
			calculated_ = true;
			return true;
		}
		for (Node* neighbour : n->getNeighbours()) {
			if (neighbour->getDistance() > n->getDistance() + n->getCoordinates().distanceTo(neighbour->getCoordinates())) {
				neighbour->setDistance(n->getDistance()	+ n->getCoordinates().distanceTo(neighbour->getCoordinates()));
				neighbour->setPrevNode(n);
			}
		}
	}
	return false;
}

void Graph::calculateEdges() {
	std::vector<Node*> temp_vector;
	temp_vector.push_back( start_ );
	temp_vector.push_back( end_ );
	for(Node* node: satellite_nodes_){
		temp_vector.push_back(node);
		}
	for( unsigned int i = 0; i < temp_vector.size(); i++ ){
		for( unsigned int j = i + 1; j < temp_vector.size(); j++ ){
			if( geometry::isCentreFarFromLineSegment( {0, 0, 0}, temp_vector[i]->getCoordinates(), temp_vector[j]->getCoordinates(), radius_ ) ){
				temp_vector[i]->getNeighbours().push_back(temp_vector[j]);
				temp_vector[j]->getNeighbours().push_back(temp_vector[i]);
			}
		}
	}
}

void Graph::reconstructPath() {
	std::list<std::string> list_path;
	Node* n = end_->getPrevNode();
	while (n != start_) {
		list_path.push_front(n->getName());
		n = n->getPrevNode();
	}
	path_ = "";
	for (auto s : list_path){
		if(!path_.empty())
			path_ += ",";
		path_ += s;
	}
}

void Graph::printCoordinates() {
	for (Node* satellite_node : satellite_nodes_) {
		std::cout << satellite_node->getCoordinates().getX() << "; " << std::endl;
	}
	std::cout << std::endl;
	for (Node* satellite_node : satellite_nodes_) {
		std::cout << satellite_node->getCoordinates().getY() << "; " << std::endl;
	}
	std::cout << std::endl;
	for (Node* satellite_node : satellite_nodes_) {
		std::cout << satellite_node->getCoordinates().getZ() << "; " << std::endl;
	}
}
