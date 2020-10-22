import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {

		if (args.length == 0) { // no arguments provided
			System.out.println("no arguments!");
			return;
		}

		// store initial start time
		Instant start = Instant.now();
		ArgumentMap map = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex(); // create index

		if (map.hasFlag("-path")) {
			Path path = map.getPath("-path");
			try {
				InvertedIndexBuilder.build(path, index);
			} catch (NullPointerException e) {
				System.out.println("The -path flag is missing a value.");
				return;
			} catch (IOException e) {
				System.out.println("Sunable to build index from path.");
				return;
			}
		}

		/*
		 * writing a nested data structure (matching your inverted index data structure)
		 * to a file in JSON format (SimpleJSONWriter)
		 */
		if (map.hasFlag("-index")) {
			Path path = map.getPath("-index", Path.of("index.json"));

			try {
				index.toJson(path);
			} catch (IOException e) {
				System.out.println("unable to write inverted index to file: " + path.toString());
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
