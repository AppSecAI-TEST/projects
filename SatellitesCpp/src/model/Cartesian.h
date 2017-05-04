/*
 * Cartesian.h
 *
 */

#ifndef MODEL_CARTESIAN_H_
#define MODEL_CARTESIAN_H_

class Cartesian {
public:
	Cartesian(double x, double y, double z) : x_(x), y_(y), z_(z) {};
	virtual ~Cartesian() {};

	double getX() const;
	double getY() const;
	double getZ() const;

	Cartesian minus(const Cartesian& c) const;
	Cartesian plus(const Cartesian& c) const;
	Cartesian multiply(double s) const;
	Cartesian vectorProduct(const Cartesian& c) const;
	double dotProduct(const Cartesian& c) const;
	double distanceTo(const Cartesian& c) const;
	double size() const;
	double sizeSquared() const;

private:
	double x_ = 0.0, y_ = 0.0, z_ = 0.0;

};

#endif /* MODEL_CARTESIAN_H_ */
