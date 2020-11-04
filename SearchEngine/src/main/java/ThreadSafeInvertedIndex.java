import java.io.IOException;
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
	 * search results map
	 */
	TreeMap<String, List<SearchResult>> fullResults = new TreeMap<String, List<SearchResult>>();
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
	@Override
	public List<SearchResult> exactSearch(TreeSet<String> words) {
		
		List<SearchResult> resultList = new ArrayList<>();
		workQueue.execute(new Task(words, resultList, "exact"));
		return resultList;
	}

	/**
	 * override of non-thread safe method
	 * 
	 * @param p list of files to use
	 * @return full exact search results as a map
	 */
	@Override
	public TreeMap<String, List<SearchResult>> completeExactSearch(List<Path> p) {
	
		fullResults = super.completeExactSearch(p);
		workQueue.join();
		return fullResults;
	}

	/**
	 * override wasn't working
	 * 
	 * @param words the query line
	 * @return list of search results
	 */
	// @Override
	public List<SearchResult> partialSearch(TreeSet<String> words) {
		
		List<SearchResult> resultList = new ArrayList<>();
		workQueue.execute(new Task(words, resultList, "partial"));
		return resultList;
	}

	/**
	 * Override wouldn't work
	 * 
	 * @param files the files to use
	 * @return the search results as a map.
	 */
	@Override
	public TreeMap<String, List<SearchResult>> completePartialSearch(List<Path> files) {
		
		fullResults = super.completePartialSearch(files);
		workQueue.join();
		return fullResults;
	}

	/**
	 * The non-static task class (runnable interclass with run method)
	 */
	private class Task implements Runnable {

		/**
		 * the list to add to
		 */
		List<SearchResult> resultList;

		/**
		 * the entire string into lil queries
		 */
		private TreeSet<String> words;

		/**
		 * the type of search the task must perform
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
		public Task(TreeSet<String> words, List<SearchResult> resultList, String searchType) {
			this.words = words;
			this.resultList = resultList;
			this.searchType = searchType;

		}

		public void run() { // just add search or partial search
			List<SearchResult> results = resultList; // add to passed in list?
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

								SearchResult nextResult = new SearchResult();
								nextResult.where = file;
								int count1 = 0;

								for (int x = 0; x < parsedWords.size(); x++) {
									count1 += partialWordGetter(parsedWords.get(x), file);
								}

								nextResult.count = count1;
								nextResult.score = ((double) count1 / (double) (builder.wordCountGetter(file)));
								synchronized(resultList){
									resultList.add(nextResult);
								}
								
							}

						}
					}
				}
				
			} else {
				// run exact search
			
				for (String i : parsedWords) {
					if (getLocations(i) != null) {

						for (String file : getLocations(i)) {
							// if file is not already been used
							if (!usedFiles.contains(file)) {
								usedFiles.add(file);

								SearchResult nextResult = new SearchResult();
								nextResult.where = file;
								int count1 = 0;
								for (int x = 0; x < parsedWords.size(); x++) {
									count1 += wordGetter(parsedWords.get(x), file);
								}
								nextResult.count = count1;
								nextResult.score = ((double) count1 / (double) (builder.wordCountGetter(file)));
								synchronized(resultList){
									resultList.add(nextResult);
								}
							}

						}
					}
				}
			}
			synchronized(results) {
				Collections.sort(results);
			}
		}
	}

}
