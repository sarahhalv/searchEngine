import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author sarah
 *
 */
public class ThreadSafeBuilder extends InvertedIndexBuilder { // access to all inverted index builder

	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();

	/**
	 * work queue to use for building
	 */
	private final WorkQueue workQueue;

	/**
	 * thread safe builder constructor
	 * 
	 * @param index   the thread safe index
	 * @param threads the num of threads to use in wq
	 */
	public ThreadSafeBuilder(InvertedIndex index, int threads) {
		this.workQueue = new WorkQueue(threads);
		log.debug("a thread safe builder was made");

	}

	/**
	 * thread safe build method
	 * 
	 * @param path  the path to use
	 * @param index the index to populate
	 * @throws IOException if IO error encountered
	 */
	@Override
	public void build(Path path, InvertedIndex index) throws IOException { // make sure u pass thread safe
		log.debug("inside thread safe build");
		if (Files.isDirectory(path)) {
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its sub directories.
			List<Path> files = TextFileFinder.list(path);
			// storing a word, file path, and location into an inverted index data structure
			for (Path file : files) { // iterate through the files
				addFile(file, index);
			}
		} else { // if single file, add it
			addFile(path, index);
		}
		workQueue.join();
	}

	/**
	 * thread safe add file method
	 * 
	 * @param file  the file to add into index
	 * @param index thread sae index to populate
	 * @throws IOException if IO error occurs
	 */
	@Override
	public void addFile(Path file, InvertedIndex index) throws IOException {
		workQueue.execute(new Task(file, index));
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
		private InvertedIndex safeIndex;

		/**
		 * Initializes this task.
		 * 
		 * @param path  the path
		 * @param index the index to use
		 *
		 */
		public Task(Path path, InvertedIndex index) {
			this.path = path;
			this.safeIndex = index;
			//log.debug("builder task just created : task of path " + path.toString());

		}

		public void run() { // just addFile method
			//log.debug("starting to run builder task of path: " + path.toString());
			Stemmer stemmer = new SnowballStemmer(DEFAULT);

			try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);) {
				String line = null;
				int location = 1;

				String fileLocation = path.toString();

				while ((line = reader.readLine()) != null) {
					String[] words = TextParser.parse(line);

					for (String word : words) {
						safeIndex.add((stemmer.stem(word)).toString(), fileLocation, location);
						log.debug("just added stemmed word " + (stemmer.stem(word)).toString() + " to " + fileLocation);
						location++;
					}
				}
			} catch (IOException e) {
				log.debug("IO exception ");
			}
			//log.debug("finished running builder task of path: " + path.toString());
		}
	}
}