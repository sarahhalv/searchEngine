import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {

	//change is a single comment
	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		/*
		 * TODO Modify this method as necessary. 
		 */
		
		// store initial start time
		Instant start = Instant.now();

		// output arguments
		System.out.println(Arrays.toString(args));

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	/*
	 * Generally, "driver" classes are responsible for setting up and calling
	 * other classes, usually from a main() method that parses command-line
	 * parameters. If the driver were only responsible for a single class, we use
	 * that class name. For example, "TaxiDriver" is what we would name a driver
	 * class that just sets up and calls the "Taxi" class.
	 */
}
