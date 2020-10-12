import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author sarah class that deals with the search results
 */
public class SearchResult implements Comparable<SearchResult> {

	/**
	 * NestedInvertedIndex object to use its methods for creating a search result
	 * object
	 */
	NestedInvertedIndex nestedInvertedIndex1 = new NestedInvertedIndex();
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
	double frequency;

	/**
	 * @param word the word which to base & create a single search result off of
	 * @param p    the path of the text file for the result
	 */
	 public void buildSearchResult(String word, Path p) {
	 where = p;
	 count = nestedInvertedIndex1.wordGetter(word, p);
	 totalWords = nestedInvertedIndex1.wordCountGetter(p.toString());
	 frequency = (nestedInvertedIndex1.wordGetter(word, p)) /
	 (nestedInvertedIndex1.wordCountGetter(p.toString()));
	 }

	/**
	 * @param word the stem word to look for matches
	 * @param p    text file in which to look
	 * @return the number of matches within file
	 */
	public int getCount(String word, Path p) {
		return nestedInvertedIndex1.wordGetter(word, p);
	}

	/**
	 * @param word the query word from which to base the results
	 * @return list of all files and results for query word
	 */
	// public ArrayList<SearchResult> buildResults(String word) {
	// ArrayList<SearchResult> results = new ArrayList<SearchResult>();
	// for (Path p : nestedInvertedIndex1.invertedIndex.get(word).keySet()) { // for
	// every file make object
	// SearchResult singleResult = new SearchResult(word, p);
	// results.add(singleResult);
	// }
	// return results;
	// }

	/**
	 * @param treeSet the already parsed words from a single line of the query file
	 * @return a sorted list of search results
	 */
	public ArrayList<HashMap<String, Object>> exactSearch(TreeSet<String> treeSet) {
		ArrayList<HashMap<String, Object>> results = new ArrayList<HashMap<String, Object>>();
		ArrayList<String> parsedWords = new ArrayList<String>(treeSet);
		// for each word go thru key set, arraylist doesn't contain file key set, make
		// results and add together to put in hashmap
		for (int i = 1; i < parsedWords.size(); i++) {
			System.out.println("parsed words content: " + parsedWords.toString());
			// System.out.println(nestedInvertedIndex1.invertedIndex.get(parsedWords.get(i)).keySet());
			// incorrect

			// NULL POINTER EXCEPTION HERE
			// go through every file that these words appear in
			for (Path p : nestedInvertedIndex1.invertedIndex.get(parsedWords.get(i)).keySet()) {

				// check if file is not mapped in a hash map in the Array list already
				for (int j = 0; j < results.size(); j++) {
					if (!results.get(j).containsValue(p)) {
						HashMap<String, Object> result = new HashMap<String, Object>();
						result.put("where", p);

						// calculate count
						int count1 = 0;
						for (int s = 0; i < parsedWords.size(); i++) {
							count1 += getCount(parsedWords.get(s), p);
						}
						result.put("count", count1);
						// input score of query line in file
						result.put("score", (count1 / nestedInvertedIndex1.wordCountGetter(p.toString())));
						// add hashmap to arraylist
						results.add(result);
					}
				}
			}

		}
		// return arraylist of results
		return results;
	}

	/**
	 * methods performs all processes necessary for exact search
	 * 
	 * @param p the text file of queries to be used for search
	 * @return full search results
	 * @throws IOException           if IO error encountered
	 * @throws FileNotFoundException if file not found error
	 */
	public TreeMap<String, ArrayList<HashMap<String, Object>>> completeExactSearch(Path p)
			throws FileNotFoundException, IOException {
		TreeMap<String, ArrayList<HashMap<String, Object>>> fullExactResults = new TreeMap<String, ArrayList<HashMap<String, Object>>>();
		// parse query file by line
		try (BufferedReader buff = new BufferedReader(new FileReader(p.toString()))) {
			String line;
			while ((line = buff.readLine()) != null) { // while still lines in query file, parse
				fullExactResults.put(line, exactSearch(TextFileStemmer.uniqueStems(line)));
			}
		}

		return fullExactResults;
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
		return 0;
	}

}
