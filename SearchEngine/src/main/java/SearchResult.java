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
	NestedInvertedIndex nestedInvertedIndex1;
	/**
	 * location
	 */
	Path where;
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
	public SearchResult(NestedInvertedIndex n, int count, double score, Path location) {
		nestedInvertedIndex1 = n;
		this.count = count;
		this.score = score;
		this.where = location;
	}

	/**
	 * @param word the word which to base & create a single search result off of
	 * @param p    the path of the text file for the result
	 */
	public void buildSearchResult(String word, Path p) {
		where = p;
		count = nestedInvertedIndex1.wordGetter(word, p);
		totalWords = nestedInvertedIndex1.wordCountGetter(p.toString());
		score = (nestedInvertedIndex1.wordGetter(word, p)) / (nestedInvertedIndex1.wordCountGetter(p.toString()));
	}

	// /**
	// * @param word the stem word to look for matches
	// * @param p text file in which to look
	// * @return the number of matches within file
	// */
	// public int getCount(String word, Path p) {
	// return nestedInvertedIndex1.wordGetter(word, p);
	// }

	/**
	 * @param p the path to a file or potential directory
	 * @return list of text files
	 */
	public List<Path> getAllFiles(Path p) {
		TextFileFinder textFileFinder1 = new TextFileFinder();
		List<Path> textfiles = new ArrayList<>();
		if (Files.isDirectory(p)) { // if path is directory
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its subdirectories.
			try {
				textfiles = textFileFinder1.list(p);
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
	public Path getWhere() {
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
		// TODO Auto-generated method stub
		/*
		 * can use Double.compare(…), Integer.compare(…), and
		 * String.compareToIgnoreCase(…) for these comparisons and the built-in sort
		 * methods in Java.
		 */
		//if equal in score
		if(Double.compare(getScore(), o.score) == 0) {
			if(Integer.compare(getCount(), o.count) ==0) {
				return (getWhere().toString()).compareToIgnoreCase((o.where.toString()));
				}
			return Integer.compare(o.count, getCount());
			}
		return Double.compare(o.score, getScore());
	}

}
