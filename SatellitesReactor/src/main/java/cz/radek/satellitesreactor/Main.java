package cz.radek.satellitesreactor;

import cz.radek.satellitesreactor.input.InputReader;
import cz.radek.satellitesreactor.model.Graph;
import cz.radek.satellitesreactor.model.InputData;
import cz.radek.satellitesreactor.tools.Converter;
import cz.radek.satellitesreactor.tools.GeometryTool;
import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomsu Radek
 */
public class Main {

	private final Logger logger = LoggerFactory.getLogger( getClass() );

	public static final double EPSILON = Math.pow( 10, -6 );
	public static final double RADIUS = 6371;

	private final InputReader inputReader;
	private final GeometryTool geometryTool;
	private final Converter converter;
	private final double radius;

	public Main( InputReader inputReader, GeometryTool geometryTool, Converter converter, double radius ) {
		this.inputReader = inputReader;
		this.geometryTool = geometryTool;
		this.converter = converter;
		this.radius = radius;
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main( String[] args ) {
		Main main = new Main( new InputReader(), new GeometryTool(), new Converter(), Main.RADIUS );

		if( args.length != 1 ){
			System.out.println( "Give input file path!" );
			return;
		}
		File input = new File( args[0] );
		try{
			main.inputReader.parseInput( input );
		} catch ( IOException e ){
			main.logger.error( "Reading file failed", e );
		} catch ( NumberFormatException e ){
			main.logger.error( "File has unknown format", e );
		}

		InputData inputData = main.inputReader.getInputData();
		Graph g = new Graph( main.geometryTool, main.converter, main.radius );
		g.initGraph( inputData );

		boolean result = g.calculateShortestPath();
		if( result ){
			System.out.println( g.getPath() );
		} else {
			System.out.println( "No path found" );
		}

	}

}
