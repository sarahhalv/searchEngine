import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author sarah
 *
 */
public class ThreadSafeBuilder { 

	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

	/**
	 * thread safe build method
	 * 
	 * @param path      the path to use
	 * @param index     the index to populate
	 * @param workQueue the workqueue to use
	 * @throws IOException if IO error encountered
	 */

	public static void build(Path path, ThreadSafeInvertedIndex index, WorkQueue workQueue) throws IOException {
		log.debug("inside thread safe build");
		if (Files.isDirectory(path)) {
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its sub directories.
			List<Path> files = TextFileFinder.list(path);
			// storing a word, file path, and location into an inverted index data structure
			for (Path file : files) { // iterate through the files
				// addFile(file, index, workQueue);
				workQueue.execute(new Task(file, index));
			}
		} else { // if single file, add it
			// addFile(path, index, workQueue);
			workQueue.execute(new Task(path, index));
		}

		// potential deadlock fixed?
		workQueue.finish();
	}

	/**
	 * The non-static task class (runnable interclass with run method)
	 */
	private static class Task implements Runnable {
		/** The path to add to the index? */
		private final Path path;
		/**
		 * thread safe index to add to
		 */
		private ThreadSafeInvertedIndex safeIndex;

		/**
		 * Initializes this task.
		 * 
		 * @param path  the path
		 * @param index the index to use
		 *
		 */
		public Task(Path path, ThreadSafeInvertedIndex index) {
			this.path = path;
			this.safeIndex = index;
			// log.debug("builder task just created : task of path " + path.toString());

		}

		@Override
		public void run() { // just addFile method
			 log.debug("starting to run builder task of path: " + path.toString());
			InvertedIndex local = new InvertedIndex();

			try {
				InvertedIndexBuilder.addFile(path, local);
			} catch (IOException e) {
				System.out.println("addfile within builder task run failed");
			}
			//System.out.println("add file ovr");
			// merge the shared data with the local data
			safeIndex.addAll(local);
			log.debug("finished running builder task of path: " + path.toString());
		}
	}
}