import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	double frequency;

	
	
	/**
	 * @param n the nested inverted index to use for results
	 */
	public SearchResult(NestedInvertedIndex n) {
		nestedInvertedIndex1 = n;
	}
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
	 * @param p the path to a file or potential directory
	 * @return list of text files
	 */
	public List<Path> getAllFiles(Path p){
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
		for (String i: parsedWords) {
			System.out.println("parsed words content: " + parsedWords.toString());
			// System.out.println(nestedInvertedIndex1.invertedIndex.get(parsedWords.get(i)).keySet());
			// incorrect

			// NULL POINTER EXCEPTION HERE
			// go through every file that these words appear in
			for (Path s : nestedInvertedIndex1.fileGetter(i)) {

				// check if file is not mapped in a hash map in the Array list already
				for (int j = 0; j < results.size(); j++) {
					if (!results.get(j).containsValue(s)) {
						HashMap<String, Object> result = new HashMap<String, Object>();
						result.put("where", s);

						// calculate count
						int count1 = 0;
						for (int x = 0; x < parsedWords.size(); x++) {
							count1 += getCount(parsedWords.get(x), s);
						}
						result.put("count", count1);
						// input score of query line in file
						result.put("score", (count1 / nestedInvertedIndex1.wordCountGetter(s.toString())));
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
	public TreeMap<String, ArrayList<HashMap<String, Object>>> completeExactSearch(List<Path> p)
			throws FileNotFoundException, IOException {
		TreeMap<String, ArrayList<HashMap<String, Object>>> fullExactResults = new TreeMap<String, ArrayList<HashMap<String, Object>>>();
		// parse query file by line
		for(Path file: p) { //loop through all files
			try (BufferedReader buff = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
				String line;
				while ((line = buff.readLine()) != null) { // while still lines in query file, parse
					System.out.println(line);
					System.out.println("calling exact search");
					fullExactResults.put(line, exactSearch(TextFileStemmer.uniqueStems(line)));
				}
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
