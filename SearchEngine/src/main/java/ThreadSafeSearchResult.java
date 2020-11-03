import java.nio.file.Path;
import java.util.List;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
/**
 * thread safe version of search result class
 * 
 * @author sarah
 * 
 */
public class ThreadSafeSearchResult extends SearchResult{

	/** Logger to use for this class. */
	//private static final Logger log = LogManager.getLogger();
	/** The lock used to protect concurrent access to the underlying set. */
	private final ReadWriteLock lock;
	/**
	 * ThreadSafeInvertedIndex object to use its methods for creating a search result
	 * object
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
	 * basic/blank constructor; creates search result object without having to
	 * provide values
	 */
	public ThreadSafeSearchResult() {
		super();
		lock = new ReadWriteLock();
	};

	//do SearchResult(InvertedIndex n, int count, double score, String location) { ??
	/**
	 * @param safeIndex        the nested inverted index to use for results
	 * @param count    total matches within the text file
	 * @param score    the percent of words in the file that match the query
	 *                 (frequency)
	 * @param location path of text file
	 */
	public ThreadSafeSearchResult(ThreadSafeInvertedIndex safeIndex, int count, double score, String location) {
		index = safeIndex;
		this.count = count;
		this.score = score;
		this.where = location;
		this.lock = new ReadWriteLock();
	}
	
	//do public void buildSearchResult(String word, String fileName) {   ??
	/**
	 * @param word the word which to base and create a single search result off of
	 * @param fileName    the text file for the result
	 */
	
	@Override
	public void buildSearchResult(String word, String fileName) {
		lock.writeLock().lock();
		try {
			super.buildSearchResult(word, fileName);
		} finally {
			lock.writeLock().unlock();
		}
	
	}
	
	
	@Override
	public List<Path> getAllFiles(Path p) {
		lock.readLock().lock();
		try {
			return super.getAllFiles(p);
		} finally {
			lock.readLock().unlock();
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
	public int compareTo(SearchResult o) {  //should be ThreadSafeSearchResult ?? 
		lock.readLock().lock();
		try {
			return super.compareTo(o);
		} finally {
			lock.readLock().unlock();
		}
	}
}
