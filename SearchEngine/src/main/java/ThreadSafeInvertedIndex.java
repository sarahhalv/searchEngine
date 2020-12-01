import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Inverted Index class that is thread safe
 * 
 * @author sarah
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/** Logger to use for this class. */
	// private static final Logger log = LogManager.getLogger();
	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;

	/**
	 * Initializes a thread-safe index
	 *
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

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
		lock.readLock().lock();
		try {
			super.toJson(path);
		} finally {
			lock.readLock().unlock();
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
	public int wordGetter(String word, String file) {
		lock.readLock().lock();
		try {
			return super.wordGetter(word, file);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> exactSearch(Set<String> words) {
		// log.debug("inside exact search safe index");

		lock.readLock().lock();
		try {
			return super.exactSearch(words);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<SearchResult> partialSearch(Set<String> words) {
		// log.debug("inside partial search safe index");
		lock.readLock().lock();
		try {
			return super.partialSearch(words);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public int wordCountGetter(String filename) {
		lock.readLock().lock();
		try {
			return super.wordCountGetter(filename);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> returnCountMap() {
		lock.readLock().lock();
		try {
			return super.returnCountMap();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex local) {
		// loop thru local and add all values to threadsafe?
		lock.writeLock().lock();
		try {
			super.addAll(local);
		} finally {
			lock.writeLock().unlock();
		}
	}

}