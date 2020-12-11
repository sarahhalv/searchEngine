import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

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
		QueryParserInterface queryParser;
		ThreadSafeInvertedIndex threadSafe = null;
		WorkQueue workQueue = null;

		int workerThreads = 5;
		// check if program should be multithreaded
		if (map.hasFlag("-threads") || map.hasFlag("-url") || map.hasFlag("-server")) {
			// log.debug("threads flag found, beginning of threads section");
					
			// threads flag instead
			// get number of worker threads to use, or 5 if no number provided
			if (map.getInteger("-threads", 5) <= 0) {
				workerThreads = 5;
			} else {
				workerThreads = map.getInteger("-threads", 5);
			}
			// }

			workQueue = new WorkQueue(workerThreads);
			threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			queryParser = new ThreadSafeQueryParser(threadSafe, workQueue);

		} else {
			// no multithreading
			index = new InvertedIndex(); // create index
			queryParser = new QueryParser(index);
		}
		// log.debug("done with threads section");

		if (map.hasFlag("-path")) {
			log.debug("path flag found, beginning of path section");

			Path path = map.getPath("-path");
			try {
				// bc static, check if multithread or regular function
				if (threadSafe != null) {
					ThreadSafeBuilder.build(path, threadSafe, workQueue);
				} else {
					InvertedIndexBuilder.build(path, index);
				}

			} catch (NullPointerException e) {
				System.out.println("The -path flag is missing a value.");
				return;
			} catch (IOException e) {
				System.out.println("Unable to build index from path: " + path);
				return;
			}
		}
		// log.debug("done with path section");

		// if url flag, build index from seed url
		if (map.hasFlag("-url")) {

			// get number of URLs to crawl when building index
			int total = 1;
			if (map.hasFlag("-max")) {
				if (map.getInteger("-max", 1) <= 0) {
					total = 1;
				} else {
					total = map.getInteger("-max", 1);
				}
			}
			// make new webcrawler instance and pass in workqueue
			WebCrawler webCrawler = new WebCrawler(workQueue, total, threadSafe);
			URL seed;
			try {
				seed = new URL(map.getString("-url"));
			} catch (MalformedURLException e) {
				System.out.println("unable to grab/create url from -url flag");
				return;
			}
			System.out.println("url given :" + seed);
			webCrawler.crawl(seed);
		}

		//launch a server (after build has occured)
		if( map.hasFlag("-server")) {
			int port = 8080;
			if(map.getInteger("-server", 8080) >= 0) {
				port = map.getInteger("-server", 8080);
			}
			Server server = new Server(port); //setting up a socket connector
			ServletHandler handler = new ServletHandler();
			try {
				handler.addServletWithMapping(new ServletHolder(new Servlet(threadSafe)), "/");
				handler.addServletWithMapping(new ServletHolder(new LocationBrowserServlet(threadSafe)), "/locations");
				handler.addServletWithMapping(new ServletHolder(new IndexBrowserServlet(threadSafe)), "/index");
			} catch (IOException e2) {
				System.out.println("unable to create new servlet");
			}
			server.setHandler(handler); //class used for handling requests
			
			try {
				server.start();
			} catch (Exception e1) {
				System.out.println("unable to start server");
			}
			try {
				server.join();
			} catch (InterruptedException e) {
				System.out.println("issues with join");
			}
		}
		
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

		if (workQueue != null) {
			workQueue.shutdown();
		}
	}

}
