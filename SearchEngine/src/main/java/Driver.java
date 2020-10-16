import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

/*
 * TODO Code style and variable names
 *
 * Decide on a consistent code style that uses spaces consistently Use the
 * built-in formatter in Eclipse
 *
 * Java has a strict naming convention. Usually without abbreviation and always
 * using camelCase.
 */

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

		InvertedIndexBuilder nestedInvertedIndex1 = new InvertedIndexBuilder();

		if (args.length == 0) { // no arguments provided
			System.out.println("no arguments!");
			return;
		}

		// store initial start time
		Instant start = Instant.now();

		// create index
		TreeMap<String, TreeMap<Path, List<Integer>>> index = new TreeMap<String, TreeMap<Path, List<Integer>>>();
		// parsing command-line arguments into flag/value pairs, and supports default
		ArgumentMap map = new ArgumentMap(args);
		// TODO InvertedIndex index = new InvertedIndex();

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
			index = nestedInvertedIndex1.createNestedInvertedIndex(path, map);

		} else { // if no path flag/bad arguments
			System.out.println("bad arguments !");
			// write empty inverted index to default file
			Path p = Paths.get("index.json");
			try {
				SimpleJsonWriter.asDoubleNestedArray(index, p);
			} catch (IOException e) {
				System.out.println(
						"unable to output nested inverted index in simple JSON format to path: " + p.toString());
			}
			return;
		}

		// writing a nested data structure (matching your inverted index data structure)
		// to a file in JSON format (SimpleJSONWriter)
		if (map.hasFlag("-index")) { // write JSON to a file because index flag present
			if (map.getPath("-index") != null) { // if has path value, use it
				try {
					// TODO index.toJson(map.getPath("-index"));
					SimpleJsonWriter.asDoubleNestedArray(index, map.getPath("-index"));
					//System.out.println(SimpleJsonWriter.asDoubleNestedArray(index));
		
				} catch (IOException e) {
					System.out.println("unable to write inverted index to file: " + map.getPath("-index").toString());
				}
			} else { // use default value
				Path p = Paths.get("index.json");
				try {
					SimpleJsonWriter.asDoubleNestedArray(index, p);
					
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
