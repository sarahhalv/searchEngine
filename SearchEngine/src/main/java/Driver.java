import java.io.IOException;
import java.nio.file.Files;
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
		QueryParser queryParser = new QueryParser(index);

		if (map.hasFlag("-path")) {
			Path path = map.getPath("-path");
			try {
				InvertedIndexBuilder.build(path, index);
			} catch (NullPointerException e) {
				System.out.println("The -path flag is missing a value.");
				return;
			} catch (IOException e) {
				System.out.println("Unable to build index from path: " + path);
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

		// if counts flag, output locations and their word count to provided path
		if (map.hasFlag("-counts")) {

			// if path not provided, use default
			Path path = map.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asMap(index.returnCountMap(), path);
			} catch (IOException e) {
				System.out.println("unable to write counts to file: " + path.toString());
			}
		}

		// if queries, use path to a text file of queries to perform search
		if (map.hasFlag("-queries")) {

			// check for no query path provided or if query is empty
			if (map.getString("-queries") == null) {
				System.out.println("query path is missing");
				return;
			}
			// check for invalid query path
			if (!Files.isDirectory(map.getPath("-queries")) && !Files.exists(map.getPath("-queries"))) {
				System.out.println("invalid query path");
				return;
			}

			try {
				queryParser.parseQueryFile(map.getPath("-queries"), map.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("no file found or buffered reader unable to work with file for search");
			}

		}

		// if results, use provided path for the search results output file
		if (map.hasFlag("-results")) {
			// if no file path provided, use default

			Path path = map.getPath("-results", Path.of("results.json"));
			try {
				queryParser.writeJson(path);
			} catch (IOException e) {
				System.out.println("unable to write results to file: " + map.getPath("-results"));
			}

		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
