package cz.radek.satellitesreactor.input;

import cz.radek.satellitesreactor.model.InputData;
import cz.radek.satellitesreactor.model.Satellite;
import cz.radek.satellitesreactor.model.Spherical;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 * Parses and stores input from defined format given in .csv file
 * @author Tomsu Radek
 */
public class InputReader {

	private InputData inputData;

	public void parseInput( File inputFile ) throws FileNotFoundException, IOException {
		inputData = new InputData();

		if( inputFile == null ){
			throw new NullPointerException();
		}
		if( !inputFile.isFile() ){
			throw new FileNotFoundException();
		}

		try ( BufferedReader br = new BufferedReader( new FileReader( inputFile ) ) ){
			String line;
			while( (line = br.readLine()) != null ){
				String[] split = line.split( "," );
				if( split[0].startsWith( "#SEED" ) ){
					double seed = Double.parseDouble( split[0].split( ":" )[1].trim() );

					inputData.setSeed( seed );
				} else if( split[0].startsWith( "SAT" ) ){
					double latitude = Double.parseDouble( split[1] );
					double longitude = Double.parseDouble( split[2] );
					double altitude = Double.parseDouble( split[3] );

					inputData.getSatellites().add( new Satellite( split[0], new Spherical( latitude, longitude, altitude ) ) );
				} else if( split[0].startsWith( "ROUTE" ) ){
					double startLatitude = Double.parseDouble( split[1] );
					double startLongitude = Double.parseDouble( split[2] );
					double endLatitude = Double.parseDouble( split[3] );
					double endLongitude = Double.parseDouble( split[4] );

					inputData.setStart( new Spherical( startLatitude, startLongitude, 0 ) );
					inputData.setEnd( new Spherical( endLatitude, endLongitude, 0 ) );
				}
			}
		}
	}

	public InputData getInputData() {
		return inputData;
	}
	
	
}
