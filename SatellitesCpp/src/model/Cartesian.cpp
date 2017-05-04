/*
 * Cartesian.cpp
 *
 */

#include "Cartesian.h"

#include <math.h>

double Cartesian::getX() const {
	return x_;
}

double Cartesian::getY() const {
	return y_;
}

double Cartesian::getZ() const {
	return z_;
}

Cartesian Cartesian::minus(const Cartesian& c) const {
	return Cartesian(x_ - c.x_, y_ - c.y_, z_ - c.z_);
}

Cartesian Cartesian::plus(const Cartesian& c) const {
	return Cartesian(x_ + c.x_, y_ + c.y_, z_ + c.z_);
}

Cartesian Cartesian::multiply(double s) const {
	return Cartesian(x_ * s, y_ * s, z_ * s);
}

Cartesian Cartesian::vectorProduct(const Cartesian& c) const {
	return Cartesian(y_ * c.z_ - c.y_ * z_, x_ * c.z_ - c.x_ * z_,
			x_ * c.y_ - c.x_ * y_);
}

double Cartesian::dotProduct(const Cartesian& c) const {
	return x_ * c.x_ + y_ * c.y_ + z_ * c.z_;
}

double Cartesian::distanceTo(const Cartesian& c) const {
	return c.minus(*this).size();
}

double Cartesian::size() const {
	return sqrt(pow(x_, 2) + pow(y_, 2) + pow(z_, 2));
}

double Cartesian::sizeSquared() const {
	return pow(x_, 2) + pow(y_, 2) + pow(z_, 2);
}


