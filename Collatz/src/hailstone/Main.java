package hailstone;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tomsu Radek
 */
public class Main {

	public static final int MIN_INPUT = 2;
	public static final int MAX_INPUT = 999999;
	public static final String INPUT_TEXT = "Please give a starting number in "
			+ "("+ MIN_INPUT +" <= x <= "+MAX_INPUT+"):";
	
	/**
	 * Main function. Handles all computations
	 *
	 * @param args main args not used
	 */
	public static void main( String[] args ) {

		//Read from standard input and validate it fits 1 < x < 1000000
		System.out.println( INPUT_TEXT );
		Scanner in = new Scanner( System.in );
		int input = -1;
		while( true ){
			input = in.nextInt();
			if( input < MIN_INPUT || input > MAX_INPUT ){
				System.out.println( INPUT_TEXT );
			} else {
				break;
			}
		}
		
		//save all requiered parameters
		long lastNo = input;
		long noSteps = 0;
		long largestNo = 1;
		long largestNo2 = 1;

		while( true ){
			//end at one or at underlow
			if( lastNo <= 1 ){
				break;
			}

			if( (lastNo & 1) == 0 ){
				//even lastNo, x = x /2
				lastNo = lastNo / 2;
			} else {
				//odd lastNo, x = 3x + 1
				lastNo = 3 * lastNo + 1;
			}

			//update largest numbers
			if( lastNo >= largestNo ){
				largestNo2 = largestNo;
				largestNo = lastNo;
			} else if( lastNo >= largestNo2 ){
				largestNo2 = lastNo;
			}
			noSteps++;
		}

		outputToHtml(input, noSteps, largestNo2 );
	}

	private static boolean outputToHtml( int input, long steps, long largestNo2 ) {

		StringBuilder sb = new StringBuilder();
		sb.append( "<html>" );
		sb.append( "<head>" );
		sb.append( "<title>Result</title>" );
		sb.append( "</head>" );
		sb.append( "<body>" );
		sb.append( "Input: " );
		sb.append( input );
		sb.append( "<br>" );
		sb.append( "Steps:" );
		sb.append( steps );
		sb.append( "<br>" );
		sb.append( "Second largest number in a sequence:" );
		sb.append( largestNo2 );
		sb.append( "<br>" );
		sb.append( "</body>" );
		sb.append( "</html>" );
		try ( Writer writer = new BufferedWriter( new OutputStreamWriter(
				new FileOutputStream( "MyHtml.html" ), "utf-8" ) ) ){
			writer.write( sb.toString() );
		} catch ( IOException ex ){
			Logger.getLogger( Main.class.getName() ).log( Level.SEVERE, null, ex );
			return false;
		}
		return true;
	}

	private static boolean outputToOut( int input, long steps, long largestNo2 ) {

		System.out.println( "Input: " + input );
		System.out.println( "Steps: " + steps );
		System.out.println( "2nd largest: " + largestNo2 );
		return true;
	}
}
