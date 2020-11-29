import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


/* TODO Design
In cases where there are common methods, but you aren't reusing very much code
and have to either break encapsulation or create new private data, the extends
relationship doesn't really end up helping very much.

Create an interface with the common methods in your single and multithreaded
classes. Instead of extending, have both implement that interface. Each class
will have its own data and implementations. (There will be some opportunity still
for code reuse, which becomes more apparent after you have the rest optimized.)

public class ThreadSafeQueryParser implements QueryParserInterface
public class QueryParser implements QueryParserInterface
*/

/**
 * QueryParser class made thread safe for multithreading
 * 
 * @author sarah
 */
public class ThreadSafeQueryParser extends QueryParser {
	/** Logger to use for this class. */
	//private static final Logger log = LogManager.getLogger();
	/**
	 * work queue to use for building
	 */
	private final WorkQueue workQueue;
	/**
	 * index to use
	 */
	private final ThreadSafeInvertedIndex index;
	/**
	 * map containing search results from query
	 */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> searchResults;

	/**
	 * thread safe query parser constructor
	 * 
	 * @param index   the safe index to use for the constructor
	 * @param threads the amount of threads to use in the multithreading
	 */
	public ThreadSafeQueryParser(ThreadSafeInvertedIndex index, int threads) {
		super(index);
		this.index = index;
		this.searchResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
		this.workQueue = new WorkQueue(threads);

	}

	/**
	 * threadsafe override for parsequeryfile. parses the query file line by line
	 * and calls parseQueryLine for the results
	 * 
	 * @param path  the path to the query file
	 * @param exact whether to perform exact or partial search
	 * @throws IOException if IO error occurs
	 */
	public void parseQueryFile(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			String line;
			while ((line = reader.readLine()) != null) { // while still lines in query file, parse
				workQueue.execute(new Task(line, exact));
			}
		}
		
		/* TODO Deadlock
		If this method is called twice, the second time there will be no worker threads
		active and your code will deadlock. Do not shutdown your work queue in a scope
		different from where it was created.
		*/
		workQueue.join();
	}

	/**
	 * thread safe override for parsequeryline: parses a single line of queries and
	 * adds the appropriate type of search result to the result map
	 * 
	 * @param line  the query line of text
	 * @param exact whether its an exact search or not
	 */
	public void parseQueryLine(String line, boolean exact) {

		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		String query = String.join(" ", stems);

		if (stems.size() != 0) {
			synchronized (searchResults) {
				if (searchResults.containsKey(query)) {
					return;
				}
			}
			List<InvertedIndex.SearchResult> result = index.search(stems, exact);
			synchronized (searchResults) {
				searchResults.put(query, result);
			}
		}
	}

	/**
	 * threadsafe version of writeJSON. outputs the searchResults map to file
	 * 
	 * @param path the file to output/write results to
	 * @throws IOException if IO error encountered
	 */
	public void writeJson(Path path) throws IOException {
		synchronized (searchResults) {
			SimpleJsonWriter.asFullResults(searchResults, path);
		}
	}

	/**
	 * The non-static task class (runnable interclass with run method)
	 */
	private class Task implements Runnable {

		/**
		 * whether search is exact or partial
		 */
		private boolean exact;

		/**
		 * the query line
		 */
		private String line;

		/**
		 * Initializes this task.
		 * 
		 * @param line  the query line
		 * @param exact whether exact or partial search
		 * 
		 */
		public Task(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
			// log.debug("query parser task just created : query line of task = " + line);
		}

		/*
		 * run function for the task (performs the search on each query line)
		 */
		public void run() {
			// log.debug("starting to run query task of: " + line);
			parseQueryLine(line, exact);
			// log.debug("finished running query task of:" + line);
		}
	}

}
