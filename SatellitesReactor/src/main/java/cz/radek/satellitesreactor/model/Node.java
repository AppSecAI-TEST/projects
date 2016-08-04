package cz.radek.satellitesreactor.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Tomsu Radek
 */
public class Node implements Comparable<Node> {

	@Override
	public int compareTo( Node o ) {
		return Double.compare( this.distance, o.distance );
	}

	public enum STATE {
		OPEN, CLOSED
	}

	private STATE state;
	private final String name;
	private final Cartesian coordinates;
	private double distance;
	private final List<Node> neighbours;
	private Node prevNode; //used for calculating shortest route

	public Node( String name, Cartesian coordinates ) {
		this.name = name;
		this.coordinates = coordinates;
		this.state = STATE.OPEN;
		this.distance = Double.POSITIVE_INFINITY;
		this.neighbours = new LinkedList<>();
		this.prevNode = null;
	}

	public void close() {
		state = STATE.CLOSED;
	}

	public boolean isOpen() {
		return state == STATE.OPEN;
	}

	public String getName() {
		return name;
	}

	public Cartesian getCoordinates() {
		return coordinates;
	}

	public double getDistance() {
		return distance;
	}

	public List<Node> getNeighbours() {
		return neighbours;
	}

	public void setDistance( double distance ) {
		this.distance = distance;
	}

	public Node getPrevNode() {
		return prevNode;
	}

	public void setPrevNode( Node prevNode ) {
		this.prevNode = prevNode;
	}

}
