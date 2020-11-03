import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Inverted Index class that is thread safe
 * 
 * @author sarah
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** Logger to use for this class. */
	//private static final Logger log = LogManager.getLogger();
	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;
	/**
	 * work queue to use for building
	 */
	private final WorkQueue workQueue;

	/**
	 * Initializes a thread-safe index
	 * 
	 * @param threads the amount of threads to use
	 *
	 */
	public ThreadSafeInvertedIndex(int threads) {
		super();
		lock = new ReadWriteLock();
		this.workQueue = new WorkQueue(threads);
	}

	/** method overriding */
	@Override
	public void add(String word, String file, Integer position) {
		lock.writeLock().lock();
		try {
			super.add(word, file, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size(String word) {
		lock.readLock().lock();
		try {
			return super.size(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int size(String word, String location) {
		lock.readLock().lock();
		try {
			return super.size(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String stem) {
		lock.readLock().lock();
		try {
			return super.contains(stem);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.contains(word, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void toJson(Path path) throws IOException {
		lock.writeLock().lock();
		try {
			super.toJson(path);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Set<String> partialFileGetter(String word) {
		lock.readLock().lock();
		try {
			return super.partialFileGetter(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int wordGetter(String word, String file) {
		lock.readLock().lock();
		try {
			return super.wordGetter(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int partialWordGetter(String word, String file) {
		lock.readLock().lock();
		try {
			return super.partialWordGetter(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * new method because override wouldn't work
	 * 
	 * @param words the query line
	 * @return the list of results
	 */
	// @Override
	public List<ThreadSafeSearchResult> threadSafeExactSearch(TreeSet<String> words) {
		// lock.readLock().lock();
		// try {
		// return super.exactSearch(words);
		// } finally {
		// lock.readLock().unlock();
		// }
		List<ThreadSafeSearchResult> resultList = new ArrayList<>();
		workQueue.execute(new Task(words, resultList, "exact"));
		return resultList;
	}

	// @Override
	/**
	 * override doesn't work
	 * 
	 * @param p list of files to use
	 * @return full exact search results as a map
	 */
	public TreeMap<String, List<ThreadSafeSearchResult>> CompleteExactSearch(List<Path> p) {

		TreeMap<String, List<ThreadSafeSearchResult>> fullExactResults = new TreeMap<String, List<ThreadSafeSearchResult>>();
		// parse query file by line
		for (Path file : p) { // loop through all files
			try (BufferedReader buff = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
				String line;
				while ((line = buff.readLine()) != null) { // while still lines in query file, parse

					if (TextFileStemmer.uniqueStems(line) != null && TextFileStemmer.uniqueStems(line).size() != 0) {
						// fullExactResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
						// exactSearch(TextFileStemmer.uniqueStems(line)));
						fullExactResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
								threadSafeExactSearch(TextFileStemmer.uniqueStems(line)));
					}
				}
			} catch (IOException e) {
				System.out.println("no file found or buffered reader unable to work with file");
			}
		}

		workQueue.finish();
		return fullExactResults;

	}

	/**
	 * override wasn't working
	 * 
	 * @param words the query line
	 * @return list of search results
	 */
	// @Override
	public List<ThreadSafeSearchResult> threadSafePartialSearch(TreeSet<String> words) {
		List<ThreadSafeSearchResult> resultList = new ArrayList<>();
		workQueue.execute(new Task(words, resultList, "partial"));
		return resultList;
	}

	// @Override
	/**
	 * Override wouldn't work
	 * 
	 * @param files the files to use
	 * @return the search results as a map
	 */
	public TreeMap<String, List<ThreadSafeSearchResult>> CompletePartialSearch(List<Path> files) {
		TreeMap<String, List<ThreadSafeSearchResult>> fullPartialResults = new TreeMap<String, List<ThreadSafeSearchResult>>();
		// parse query file by line

		for (Path file : files) { // loop through all files
			try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
				String line;
				while ((line = reader.readLine()) != null) { // while still lines in query file, parse

					if (TextFileStemmer.uniqueStems(line) != null && TextFileStemmer.uniqueStems(line).size() != 0) {

						fullPartialResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
								threadSafePartialSearch(TextFileStemmer.uniqueStems(line)));
					}
				}
			} catch (IOException e) {
				System.out.print("buffered reader was unable to work with file");
			}
		}
		workQueue.finish();
		return fullPartialResults;
	}

	/**
	 * The non-static task class (runnable interclass with run method)
	 */
	private class Task implements Runnable {
		
		/**
		 * the list to add to
		 */
		List<ThreadSafeSearchResult> resultList;

		/**
		 * the entire string into lil queries
		 */
		private TreeSet<String> words;

		/**
		 *the type of search the task must perform
		 */
		private String searchType;

		/**
		 * Initializes this task.
		 * 
		 * @param words      the query string
		 * @param resultList the results to add the task run results to
		 * @param searchType the type of search to perform
		 * 
		 */
		public Task(TreeSet<String> words, List<ThreadSafeSearchResult> resultList, String searchType) {
			this.words = words;
			this.resultList = resultList;
			this.searchType = searchType;

		}

		public void run() { // just add search or partial search
			List<ThreadSafeSearchResult> results = resultList; // add to passed in list?
			ArrayList<String> parsedWords = new ArrayList<String>(words);
			ArrayList<String> usedFiles = new ArrayList<String>();

			// run method based on which type of search
			if (searchType.equals("partial")) {
				// run partial search method
				for (String word : parsedWords) {
					if (partialFileGetter(word) != null) {

						for (String file : partialFileGetter(word)) {
							// if file is not already been used
							if (!usedFiles.contains(file)) {
								usedFiles.add(file);

								ThreadSafeSearchResult nextResult = new ThreadSafeSearchResult();
								nextResult.where = file;
								int count1 = 0;

								for (int x = 0; x < parsedWords.size(); x++) {
									count1 += partialWordGetter(parsedWords.get(x), file);
								}

								nextResult.count = count1;
								nextResult.score = ((double) count1 / (double) (builder.wordCountGetter(file)));
								results.add(nextResult);
							}

						}
					}
				}
				Collections.sort(results);
				// return results;
			} else {
				// run exact search
				for (String i : parsedWords) {
					if (getLocations(i) != null) {

						for (String file : getLocations(i)) {
							// if file is not already been used
							if (!usedFiles.contains(file)) {
								usedFiles.add(file);

								ThreadSafeSearchResult nextResult = new ThreadSafeSearchResult();
								nextResult.where = file;
								int count1 = 0;
								for (int x = 0; x < parsedWords.size(); x++) {
									count1 += wordGetter(parsedWords.get(x), file);
								}
								nextResult.count = count1;
								nextResult.score = ((double) count1 / (double) (builder.wordCountGetter(file)));
								results.add(nextResult);
							}

						}
					}
				}
				Collections.sort(results);
				// return results;
			}
		}
	}

}
