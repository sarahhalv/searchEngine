import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

			// check for no path provided
			if (map.getString("-path") == null) {
				System.out.println("path is missing");
				return;
			}
			// check for invalid path
			if (!Files.isDirectory(map.getPath("-path")) && !Files.exists(map.getPath("-path"))) {
				System.out.println("invalid path");
				return;
			}

			Path path = map.getPath("-path");
			InvertedIndexBuilder.build(path, index);

		} else { // if no path flag/bad arguments
			System.out.println("bad arguments !");
			// write empty inverted index to default file
			Path p = Paths.get("index.json");
			try {

				index.toJson(p);

			} catch (IOException e) {
				System.out.println(
						"unable to output nested inverted index in simple JSON format to path: " + p.toString());
			}
			return;
		}

		/*
		 * writing a nested data structure (matching your inverted index data structure)
		 * to a file in JSON format (SimpleJSONWriter)
		 */
		if (map.hasFlag("-index")) { // write JSON to a file because index flag present
			if (map.getPath("-index") != null) { // if has path value, use it
				try {

					index.toJson(map.getPath("-index"));

				} catch (IOException e) {
					System.out.println("unable to write inverted index to file: " + map.getPath("-index").toString());
				}
			} else { // use default value
				Path p = Paths.get("index.json");
				try {

					index.toJson(p);

				} catch (IOException e) {
					System.out.println("unable to write inverted index to file: " + p.toString());
				}
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
