import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * @throws IOException if IO error occurs
	 */
	public static void main(String[] args) throws IOException {

		NestedInvertedIndex nestedInvertedIndex1 = new NestedInvertedIndex();
		SearchResult searchResult1 = new SearchResult();

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
					SimpleJsonWriter.asDoubleNestedArray(index, map.getPath("-index"));
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

		// if counts flag, output locations and their word count to provided path
		if (map.hasFlag("-counts")) {

			// if path not provided, use default
			if (map.getString("-counts") == null) {

				Path p = Paths.get("counts.json"); // default path
				SimpleJsonWriter.asMap(nestedInvertedIndex1.returnCountMap(), p);
				System.out.println("word count was written to: counts.json");
			} else { // path provided
				System.out.println("word count written to: " + map.getString("-counts"));
				SimpleJsonWriter.asMap(nestedInvertedIndex1.returnCountMap(), map.getPath("-counts"));
			}
		}

		// if queries, use path to a text file of queries to perform search
		if (map.hasFlag("-query")) {
			// check what type of search
			if (map.hasFlag("-exact")) { // perform exact searching
				System.out.println("exact searching");
				searchResult1.completeExactSearch(map.getPath("-query"));
				
			} else { // perform partial searching
				System.out.println("partial searching");
				// parse query file, perform partial search and make results tree map
			}
		}

		// if results, use provided path for the search results output file
		if (map.hasFlag("-results")) {
			// if no file path provided, use default
			if (map.getString("-results") == null) {
				System.out.println("using default output search path");
				Path p = Paths.get("results.json");
				SimpleJsonWriter.asFullResults(searchResult1.completeExactSearch(map.getPath("-query")), p);
				System.out.println(SimpleJsonWriter.asFullResults(searchResult1.completeExactSearch(map.getPath("-query"))));

			} else { // path provided
				System.out.println("using provided output search path: " + map.getString("-results"));
				SimpleJsonWriter.asFullResults(searchResult1.completeExactSearch(map.getPath("-query")), map.getPath("-results"));
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
