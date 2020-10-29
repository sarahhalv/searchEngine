import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sarah class that deals with the search results
 */
public class SearchResult implements Comparable<SearchResult> {

	/**
	 * NestedInvertedIndex object to use its methods for creating a search result
	 * object
	 */
	InvertedIndex index;
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
	public SearchResult() {
	};

	/**
	 * @param n        the nested inverted index to use for results
	 * @param count    total matches within the text file
	 * @param score    the percent of words in the file that match the query
	 *                 (frequency)
	 * @param location path of text file
	 */
	public SearchResult(InvertedIndex n, int count, double score, String location) {
		index = n;
		this.count = count;
		this.score = score;
		this.where = location;
	}

	/**
	 * @param word the word which to base and create a single search result off of
	 * @param fileName    the text file for the result
	 */
	public void buildSearchResult(String word, String fileName) {
		where = fileName;
		count = index.size(word, fileName);
		//totalWords = index.wordCountGetter(fileName);
		//score = (index.wordGetter(word, fileName)) / (index.wordCountGetter(fileName));
	}


	/**
	 * @param p the path to a file or potential directory
	 * @return list of text files
	 */
	public List<Path> getAllFiles(Path p) {
		//TextFileFinder textFileFinder1 = new TextFileFinder();
		List<Path> textfiles = new ArrayList<>();
		if (Files.isDirectory(p)) { // if path is directory
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its subdirectories.
			try {
				textfiles = TextFileFinder.list(p);
			} catch (IOException e) {
				System.out.println("unable to create array of gathered textfiles");
			}
		} else { // if single file, add it
			if (p != null) {
				textfiles.add(p);
			}
		}
		return textfiles;
	}

	/**
	 * @return frequency/score of relative matches to number of words in file
	 */
	public double getScore() {
		return score;
	}

	/**
	 * @return location of result
	 */
	public String getWhere() {
		return where;
	}

	/**
	 * @return number of matches in result
	 */
	public int getCount() {
		return count;
	}
	/*
	 * how the results will be sorted
	 */

	@Override
	public int compareTo(SearchResult o) {
		// if equal in score
		if (Double.compare(getScore(), o.score) == 0) {
			if (Integer.compare(getCount(), o.count) == 0) {
				return (getWhere()).compareToIgnoreCase((o.where));
			}
			return Integer.compare(o.count, getCount());
		}
		return Double.compare(o.score, getScore());
	}
}