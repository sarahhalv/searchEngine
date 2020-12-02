import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

/**
 * QueryParser class made thread safe for multithreading
 * 
 * @author sarah
 */
public class ThreadSafeQueryParser implements QueryParserInterface {
	/** Logger to use for this class. */
	// private static final Logger log = LogManager.getLogger();
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
	 * @param index     the safe index to use for the constructor
	 * @param workQueue the workqueue to use
	 */
	public ThreadSafeQueryParser(ThreadSafeInvertedIndex index, WorkQueue workQueue) {
		this.index = index;
		this.searchResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
		this.workQueue = workQueue;
	}

	@Override
	public void parseQueryFile(Path path, boolean exact) throws IOException {
		QueryParserInterface.super.parseQueryFile(path, exact);
		workQueue.finish();
	}

	@Override
	public void parseQueryLine(String line, boolean exact) {
		workQueue.execute(new Task(line, exact));
	}

	@Override
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
		@Override
		public void run() {
			// log.debug("starting to run query task of: " + line);
			// parseQueryLine(line, exact);
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
			// log.debug("finished running query task of:" + line);
		}
	}

}
