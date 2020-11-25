import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {
	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

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
		InvertedIndex index; // create index
		QueryParser queryParser;
		InvertedIndexBuilder builder;

		int workerThreads = 5;
		// check if program should be multithreaded
		if (map.hasFlag("-threads")) {
			// log.debug("threads flag found, beginning of threads section");
			// get number of worker threads to use, or 5 if no number provided
			if (map.getInteger("-threads", 5) <= 0) {
				workerThreads = 5;
			} else {
				workerThreads = map.getInteger("-threads", 5);
			}
			index = new ThreadSafeInvertedIndex(workerThreads);
			queryParser = new ThreadSafeQueryParser((ThreadSafeInvertedIndex) index, workerThreads);
			builder = new ThreadSafeBuilder((ThreadSafeInvertedIndex) index, workerThreads);

			/* TODO Don't downcast. Do this instead:
			ThreadSafeInvertedIndex threadSafe = new ThreadSafeInvertedIndex(workerThreads);
			index = threadSafe;
			queryParser = new ThreadSafeQueryParser(threadSafe, workerThreads);
			builder = new ThreadSafeBuilder(threadSafe, workerThreads);
			*/
		
		} else {
			// no multithreading
			index = new InvertedIndex(); // create index
			queryParser = new QueryParser(index);
			builder = new InvertedIndexBuilder();
		}
		// log.debug("done with threads section");

		if (map.hasFlag("-path")) {
			log.debug("path flag found, beginning of path section");

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
		// log.debug("done with path section");
		/*
		 * writing a nested data structure (matching your inverted index data structure)
		 * to a file in JSON format (SimpleJSONWriter)
		 */
		if (map.hasFlag("-index")) {
			// log.debug("index flag found, beginning of index section");
			Path path = map.getPath("-index", Path.of("index.json"));

			try {
				index.toJson(path);
			} catch (IOException e) {
				System.out.println("unable to write inverted index to file: " + path.toString());
			}
		}
		// log.debug("done with index section");

		// if counts flag, output locations and their word count to provided path
		if (map.hasFlag("-counts")) {
			// log.debug("counts flag found, beginning of counts section");
			// if path not provided, use default
			Path path = map.getPath("-counts", Path.of("counts.json"));
			try {
				SimpleJsonWriter.asMap(index.returnCountMap(), path);
			} catch (IOException e) {
				System.out.println("unable to write counts to file: " + path.toString());
			}
			// log.debug("done with counts section");
		}

		// if queries, use path to a text file of queries to perform search
		if (map.hasFlag("-queries")) {
			// log.debug("found queries .. beginning of query section");
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
				// log.debug("index keys: "+ index.getWords().toString());
				queryParser.parseQueryFile(map.getPath("-queries"), map.hasFlag("-exact"));
			} catch (IOException e) {
				System.out.println("no file found or buffered reader unable to work with file for search");
			}

		}
		// log.debug("done with queries section");

		// if results, use provided path for the search results output file

		if (map.hasFlag("-results")) {
			// log.debug("results flag found, beginning of results section");
			// if no file path provided, use default

			Path path = map.getPath("-results", Path.of("results.json"));
			try {
				queryParser.writeJson(path);
			} catch (IOException e) {
				System.out.println("unable to write results to file: " + map.getPath("-results"));
			}

		}
		// log.debug("done with results section");

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);

	}

}
