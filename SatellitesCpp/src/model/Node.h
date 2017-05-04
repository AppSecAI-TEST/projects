/*
 * Node.h
 *
 */

#ifndef MODEL_NODE_H_
#define MODEL_NODE_H_

#include <string>
#include <list>
#include <limits>

#include "Cartesian.h"
#include "State.h"

class Node {
public:
	Node(std::string name, const Cartesian& coordinates) :
			name_(name), coordinates_(coordinates) {
		this->state_ = OPEN;
		this->distance_ = std::numeric_limits<double>::max();
		this->prev_node_ = nullptr;
	}
	virtual ~Node() {
	}
	;

	void close() {
		state_ = CLOSED;
	}
	;
	bool isOpen() const {
		return state_ == OPEN;
	}
	;

	const Cartesian& getCoordinates() const {
		return coordinates_;
	}

	double getDistance() const {
		return distance_;
	}

	void setDistance(double distance) {
		this->distance_ = distance;
	}

	const std::string& getName() const {
		return name_;
	}

	std::list<Node*>& getNeighbours() {
		return neighbours_;
	}

	Node* getPrevNode() const {
		return prev_node_;
	}

	void setPrevNode(Node* prevNode) {
		this->prev_node_ = prevNode;
	}

private:
	State state_;
	std::string name_;
	Cartesian coordinates_;
	double distance_;
	std::list<Node*> neighbours_;
	Node* prev_node_;
};

#endif /* MODEL_NODE_H_ */
