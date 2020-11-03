import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.TreeMap;

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
		//InvertedIndex index = new InvertedIndex();
		InvertedIndex index;
		InvertedIndexBuilder builder;
		//InvertedIndexBuilder builder = new InvertedIndexBuilder();
		SearchResult searchResult1; 
		//SearchResult searchResult1 = new SearchResult();
		TreeMap<String, List<SearchResult>> searchResults = new TreeMap<String, List<SearchResult>>();

		// check if program should be multithreaded
		if (map.hasFlag("-threads")) {
			// get number of worker threads to use, or 5 if no number provided
			int workerThreads;
			if(map.getInteger("-threads", 5) <= 0) {
				workerThreads = 5;
			}else {
				workerThreads = map.getInteger("-threads", 5);
			}
			//do something with work queue in here?
			
			// change all references that should be thread safe
			index = new ThreadSafeInvertedIndex(workerThreads);
			builder = new ThreadSafeBuilder((ThreadSafeInvertedIndex)index, workerThreads);
			searchResult1 = new ThreadSafeSearchResult();
		}else {
			//no multithreading
			index =  new InvertedIndex(); // create index
			builder =  new InvertedIndexBuilder();
			searchResult1 = new SearchResult();
		}

		if (map.hasFlag("-path")) {
			Path path = map.getPath("-path");
			try {
				builder.build(path, index);
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
			if (map.getString("-counts") == null) {

				Path path = Paths.get("counts.json"); // default path
				try {
					SimpleJsonWriter.asMap(InvertedIndexBuilder.returnCountMap(), path);
				} catch (IOException e) {
					System.out.println("unable to write counts to file: " + path.toString());
				}
			} else { // path provided
				try {
					SimpleJsonWriter.asMap(InvertedIndexBuilder.returnCountMap(), map.getPath("-counts"));
				} catch (IOException e) {
					System.out.println("unable to write counts to file: " + map.getPath("-counts"));
				}
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

			if (map.hasFlag("-exact")) { // perform exact searching

				searchResults = index.completeExactSearch(searchResult1.getAllFiles(map.getPath("-queries")));

			} else { // perform partial searching
				searchResults = index.completePartialSearch(searchResult1.getAllFiles(map.getPath("-queries")));
			}
		}

		// if results, use provided path for the search results output file
		if (map.hasFlag("-results")) {
			// if no file path provided, use default
			if (map.getString("-results") == null) {
				Path path = Paths.get("results.json");
				try {
					SimpleJsonWriter.asFullResults(searchResults, path);
				} catch (IOException e) {
					System.out.println("unable to write results to file: results.json");
				}
			} else { // path provided
				try {
					SimpleJsonWriter.asFullResults(searchResults, map.getPath("-results"));
				} catch (IOException e) {
					System.out.println("unable to write results to file: " + map.getPath("-results"));
				}
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
