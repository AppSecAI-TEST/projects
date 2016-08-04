/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.radek.satellitesreactor.model;

import cz.radek.satellitesreactor.Main;
import cz.radek.satellitesreactor.tools.Converter;
import cz.radek.satellitesreactor.tools.GeometryTool;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

/**
 *
 * @author Tomsu Radek
 */
public class Graph {

	private Node start, end;
	private final List<Node> satelliteNodes;
	private boolean initialized = false;
	private boolean calculated = false;
	private String path = "";
	private final GeometryTool geometryTool;
	private final Converter converter;
	private final double radius;

	public Graph( GeometryTool geometryTool, Converter converter, double radius ) {
		this.geometryTool = geometryTool;
		this.converter = converter;
		this.satelliteNodes = new LinkedList<>();
		this.radius = radius;
	}

	public boolean initGraph( InputData inputData ) {
		this.start = new Node( "START", converter.fromSphericalToCartesian( inputData.getStart(), radius ) );
		this.end = new Node( "END", converter.fromSphericalToCartesian( inputData.getEnd(), radius ) );
		for( Satellite satellite : inputData.getSatellites() ){
			this.satelliteNodes.add( new Node( satellite.getName(), converter.fromSphericalToCartesian( satellite.getSphericalCoordinates(), radius ) ) );
		}
		calculateEdges();
		initialized = true;
		return true;
	}

	//Use 'dijktra's' algorithm for finding shortest path as graph has positive constant valued edges
	public boolean calculateShortestPath() {
		if( !initialized ){
			return false;
		}
		PriorityQueue<Node> queue = new PriorityQueue();

		start.setDistance( 0 );
		queue.add( start );
		while( !queue.isEmpty() ){
			Node n = queue.poll();
			n.close();
			if( n.equals( end ) ){
				reconstructPath();
				calculated = true;
				return true;
			}
			for( Node neighbour : n.getNeighbours() ){
				if( neighbour.getDistance() > n.getDistance() + n.getCoordinates().distanceTo( neighbour.getCoordinates() ) ){
					queue.remove( neighbour );
					neighbour.setDistance( n.getDistance() + n.getCoordinates().distanceTo( neighbour.getCoordinates() ) );
					neighbour.setPrevNode( n );
					queue.add( neighbour );
				}
			}
		}
		return false;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public String getPath() {
		return path;
	}

	private void calculateEdges() {
		ArrayList<Node> temp = new ArrayList<>();
		temp.add( start );
		temp.add( end );
		temp.addAll( satelliteNodes );

		for( int i = 0; i < temp.size(); i++ ){
			for( int j = i + 1; j < temp.size(); j++ ){
				if( geometryTool.isCentreFarFromLineSegment( Cartesian.CENTRE, temp.get( i ).getCoordinates(), temp.get( j ).getCoordinates(), Main.RADIUS ) ){
					temp.get( i ).getNeighbours().add( temp.get( j ) );
					temp.get( j ).getNeighbours().add( temp.get( i ) );
				}
			}
		}
	}

	private void reconstructPath() {
		LinkedList<String> listPath = new LinkedList<>();
		Node n = end.getPrevNode();
		while( !n.equals( start ) ){
			listPath.addFirst( n.getName() );
			n = n.getPrevNode();
		}
		this.path = listPath.stream().collect( Collectors.joining( "," ) );
	}
	
	public void printCoordinates(){
		for( Node satelliteNode : satelliteNodes ){
			System.out.print( satelliteNode.getCoordinates().getX() + "; ");
		}
		System.out.println( "" );
		for( Node satelliteNode : satelliteNodes ){
			System.out.print( satelliteNode.getCoordinates().getY() + "; ");
		}
		System.out.println( "" );
		for( Node satelliteNode : satelliteNodes ){
			System.out.print( satelliteNode.getCoordinates().getZ() + "; ");
		}
	}
}
