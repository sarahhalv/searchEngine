import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * TODO You do not need to add Javadoc to overridden methods most of the time,
 * unless something is significantly different.
 */

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
	 * Initializes a thread-safe index
	 * 
	 * @param threads the amount of threads to use
	 *
	 */
	public ThreadSafeInvertedIndex(int threads) { // TODO Do not pass threads to this class
		super();
		lock = new ReadWriteLock();
	}

	/**
	 * thread safe add method.
	 * adds a set of specific data to the index
	 * 
	 * @param word     stem word to be an index key
	 * @param file     location to add to index
	 * @param position positions where word is found in that location
	 */
	@Override
	public void add(String word, String file, Integer position) {
		lock.writeLock().lock();
		try {
			super.add(word, file, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * thread safe size method.
	 * finds the number of stemmed words in the index
	 * 
	 * @return number of words in index
	 */
	@Override
	public int size() {
		lock.readLock().lock();
		try {
			return super.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe more specific size method.
	 * finds the number of files this word is found in index
	 * 
	 * @param word the specific stem/word
	 * @return # of paths stored for that word
	 */
	@Override
	public int size(String word) {
		lock.readLock().lock();
		try {
			return super.size(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe more specific size method.
	 * finds the number of times the passed in word is in specific text file
	 * 
	 * @param word     the stem word
	 * @param location the specific text file
	 * @return # of positions stored in that location
	 */
	@Override
	public int size(String word, String location) {
		lock.readLock().lock();
		try {
			return super.size(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * thread safe contains method.
	 * finds if stem word is present in the inverted index
	 * 
	 * @param stem the specific word being looked for
	 * @return true if stem word is in index
	 */
	@Override
	public boolean contains(String stem) {
		lock.readLock().lock();
		try {
			return super.contains(stem);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe more specific contains method.
	 * finds if stem word is present in specified file
	 * 
	 * @param word     the specific stem word
	 * @param location the specific text file
	 * @return true if word can be found in that file (if file is in words key set)
	 */
	@Override
	public boolean contains(String word, String location) {
		lock.readLock().lock();
		try {
			return super.contains(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * most specific thread safe contains method.
	 * finds if passed in word exists in the specified file at the specified
	 * position
	 * 
	 * @param word     stem
	 * @param location file location
	 * @param position location in file where word may be
	 * @return if word exists in file in that location
	 */
	@Override
	public boolean contains(String word, String location, int position) {
		lock.readLock().lock();
		try {
			return super.contains(word, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe toJson method.
	 * outputs the index to an output file in JSON format
	 * 
	 * @param path output file path to write to
	 * @throws IOException if encounter IO error
	 */
	@Override
	public void toJson(Path path) throws IOException {
		/*
		 * TODO What is being read from versus written to? What is the shared
		 * data here? 
		 */
		lock.writeLock().lock();
		try {
			super.toJson(path);
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * thread safe get words method.
	 * grabs and returns all of the words in the index
	 * 
	 * @return an unmodifiable set of the words
	 */
	@Override
	public Set<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe get locations method.
	 * grabs and returns all of the locations/files for the specified word
	 * 
	 * @param word the specified stem word
	 * @return Set of locations
	 */
	@Override
	public Set<String> getLocations(String word) {
		lock.readLock().lock();
		try {
			return super.getLocations(word);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe get positions method.
	 * grabs and returns a set of locations for a specified word in a specified file
	 * 
	 * @param word     the specified stem
	 * @param location the filename
	 * @return set of locations where the stem is found
	 */
	@Override
	public Set<Integer> getPositions(String word, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(word, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	/*
	 * thread safe toString method.
	 * returns string value of index
	 */
	@Override
	public String toString() {
		lock.readLock().lock();
		try {
			return super.toString();
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * thread safe word getter method
	 * 
	 * @param word the word to get count for in exact search
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	@Override
	public int wordGetter(String word, String file) {
		lock.readLock().lock();
		try {
			return super.wordGetter(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	/**
	 * thread safe exact search method
	 * 
	 * @param words the query line
	 * @return the list of results
	 */
	@Override
	public List<SearchResult> exactSearch(Set<String> words) {
		// TODO This must be protected with the lock!
		//log.debug("inside exact search safe index");
		return super.exactSearch(words);
	}

	/**
	 * thread safe partial search method
	 * 
	 * @param words the query line
	 * @return list of search results
	 */
	@Override
	public List<SearchResult> partialSearch(Set<String> words) {
		// TODO This must be protected with the lock!
		//log.debug("inside partial search safe index");
		return super.partialSearch(words);
	}

	// TODO Remove, make private in InvertedIndex
	/**
	 * common search for threadsafe index class
	 * 
	 * @param input   the specific input for the different searches (query for
	 *                exact, key for partial)
	 * @param results the search results list to add to
	 * @param lookup  the lookup map to add results to
	 */
	@Override
	public void commonSearch(String input, List<SearchResult> results, Map<String, SearchResult> lookup) {
		//log.debug("inside common search safe index");
		super.commonSearch(input, results, lookup);
	}

	// TODO Remove, don't need to override if any of the methods that change data are made private
	/**
	 * thread safe version of search result class
	 * 
	 * @author sarah
	 * 
	 */
	public class ThreadSafeSearchResult extends SearchResult {

		/** The lock used to protect concurrent access to the underlying set. */
		private final ReadWriteLock lock;
		/**
		 * ThreadSafeInvertedIndex object to use its methods for creating a search
		 * result object
		 */
		ThreadSafeInvertedIndex safeIndex;
		/**
		 * location
		 */
		String where;
		/**
		 * total word count of the location
		 */
		int totalWords;
		/**
		 * total matches within the text file
		 */
		int count;
		/**
		 * the percent of words in the file that match the query (like the score)
		 */
		double score;

		/**
		 * thread safe search result constructor
		 * 
		 * @param where the location of the result
		 */
		public ThreadSafeSearchResult(String where) {
			super(where);
			lock = new ReadWriteLock();
		};

		/**
		 * updates the count and score of search result object
		 * 
		 * @param word the word to add
		 */
		@Override
		public void update(String word) {
			lock.writeLock().lock();
			try {
				super.update(word);
			} finally {
				lock.writeLock().unlock();
			}
		}

		@Override
		public double getScore() {
			lock.readLock().lock();
			try {
				return super.getScore();
			} finally {
				lock.readLock().unlock();
			}
		}

		@Override
		public String getWhere() {
			lock.readLock().lock();
			try {
				return super.getWhere();
			} finally {
				lock.readLock().unlock();
			}
		}

		@Override
		public int getCount() {
			lock.readLock().lock();
			try {
				return super.getCount();
			} finally {
				lock.readLock().unlock();
			}
		}

		@Override
		public int compareTo(SearchResult o) { // should be ThreadSafeSearchResult ??
			lock.readLock().lock();
			try {
				return super.compareTo(o);
			} finally {
				lock.readLock().unlock();
			}
		}
	}

	// TODO Missing some methods: wordCountGetter, returnCountMap
}