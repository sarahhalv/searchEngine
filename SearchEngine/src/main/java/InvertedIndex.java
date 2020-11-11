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

/**
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class InvertedIndex {

	/**
	 * data structure for inverted index object
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * map that records how many words in a text file
	 */
	TreeMap<String, Integer> countMap; // TODO keywords

	/**
	 * inverted index class object constructor
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.countMap = new TreeMap<String, Integer>();
	}

	/**
	 * adds a set of specific data to the index
	 * 
	 * @param word     stem word to be an index key
	 * @param file     location to add to index
	 * @param position positions where word is found in that location
	 */
	public void add(String word, String file, Integer position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(file, new TreeSet<>());
		index.get(word).get(file).add(position);
		
		/*
		 * TODO Assumes positions are added in order and not...
		 * 
		 * add(hello, hello.txt, 99);
		 * add(world, hello.txt, 2);
		 * 
		 * Check that the new position is greater than the old one
		 * Math.max to compare the two positions
		 */
		countMap.put(file, position); // If add something new, update the countMap for this file
	}

	/**
	 * finds the number of stemmed words in the index
	 * 
	 * @return number of words in index
	 */
	public int size() {
		return index.size();
	}

	/**
	 * finds the number of files this word is found in index
	 * 
	 * @param word the specific stem/word
	 * @return # of paths stored for that word
	 */
	public int size(String word) {

		if (contains(word)) {
			return index.get(word).keySet().size();
		}
		return 0;
	}

	/**
	 * finds the number of times the passed in word is in specific text file
	 * 
	 * @param word     the stem word
	 * @param location the specific text file
	 * @return # of positions stored in that location
	 */
	public int size(String word, String location) {
		if (contains(word, location)) {
			return index.get(word).get(location).size();
		}
		return 0;
	}

	/**
	 * finds if stem word is present in the inverted index
	 * 
	 * @param stem the specific word being looked for
	 * @return true if stem word is in index
	 */
	public boolean contains(String stem) {
		return index.containsKey(stem);
	}

	/**
	 * finds if stem word is present in specified file
	 * 
	 * @param word     the specific stem word
	 * @param location the specific text file
	 * @return true if word can be found in that file (if file is in words key set)
	 */
	public boolean contains(String word, String location) {
		return index.containsKey(word) && index.get(word).containsKey(location);
	}

	/**
	 * finds if passed in word exists in the specified file at the specified
	 * position
	 * 
	 * @param word     stem
	 * @param location file location
	 * @param position location in file where word may be
	 * @return if word exists in file in that location
	 */
	public boolean contains(String word, String location, int position) {
		return index.containsKey(word) && index.get(word).containsKey(location)
				&& index.get(word).get(location).contains(position);
	}

	/**
	 * outputs the index to an output file in JSON format
	 * 
	 * @param path output file path to write to
	 * @throws IOException if encounter IO error
	 */
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asDoubleNestedStructure(index, path);
	}

	/**
	 * grabs and returns all of the words in the index
	 * 
	 * @return an unmodifiable set of the words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/**
	 * grabs and returns all of the locations/files for the specified word
	 * 
	 * @param word the specified stem word
	 * @return Set of locations
	 */
	public Set<String> getLocations(String word) { // FILEGETTER REPLACEMENT
		if (contains(word)) { // if word exists
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * grabs and returns a set of locations for a specified word in a specified file
	 * 
	 * @param word     the specified stem
	 * @param location the filename
	 * @return set of locations where the stem is found
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (contains(word, location)) { // if word is present in file
			return Collections.unmodifiableSet(index.get(word).get(location));
		}
		return Collections.emptySet();
	}

	/*
	 * returns string value of index
	 */
	@Override
	public String toString() {
		return index.toString();
	}

	/**
	 * @param word the word from which to grab files for partial search
	 * @return set of files where word is located
	 */
	public Set<String> partialFileGetter(String word) { // TODO Remove
		Set<String> files = new TreeSet<String>();

		for (String stem : index.keySet()) {
			if (stem.startsWith(word)) { // if word in index begins with query word
				if (index.get(stem) != null) {
					if (index.get(stem).keySet() != null) {
						for (String p : index.get(stem).keySet()) {
							files.add(p);
						}
					}
				}
			}
		}
		return files;
	}

	/**
	 * @param word the word to get count for in exact search
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	public int wordGetter(String word, String file) {
		if (index.get(word) == null) {
			return 0;
		}
		if (index.get(word).get(file) == null) {
			return 0;
		}
		return index.get(word).get(file).size(); // return how many times in file for this particular word
	}

	/**
	 * @param filename the file which to count the words
	 * @return the number of words in the passed in file
	 */
	public int wordCountGetter(String filename) {
		return countMap.get(filename); // TODO getOrDefault or verify filename is a key in countMap
	}

	/**
	 * @return the countMap created alongside the inverted index
	 */
	public TreeMap<String, Integer> returnCountMap() {
		return countMap; // TODO Make unmodifiable
	}

	/**
	 * @param word the word to get count for in partial search
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	public int partialWordGetter(String word, String file) { // TODO Remove
		int matches = 0;

		for (String stem : index.keySet()) {
			if (index.get(stem) == null) {
				continue;
			}
			if (index.get(stem).get(file) == null) {
				continue;
			}
			if (stem.startsWith(word)) {
				matches += index.get(stem).get(file).size();
			}
		}
		return matches;
	}

	/**
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of EXACT search results
	 */
	// TODO public List<SearchResult> exactSearch(Set<String> words) {
	public List<SearchResult> exactSearch(TreeSet<String> words) {
		List<SearchResult> results = new ArrayList<>();
		ArrayList<String> parsedWords = new ArrayList<String>(words);
		ArrayList<String> usedFiles = new ArrayList<String>();

		for (String word : parsedWords) {
			if (getLocations(word) != null) {

				commonSearch(results, parsedWords, usedFiles, word, true);

			}
		}
		
		/*
		 * TODO
		 * 
		List<SearchResult> results = new ArrayList<>();
		Map<String (location), SearchResult> lookup = ....
		
		for (String query : words ) {
			if (index.containsKey(query)) {
			   for (String location : index.get(query).keySet()) {
			        if (lookup.containsKey(location)) {
			        	lookup.get(location).update(...);
			        }
			        else {
			        	SearchResult current = ...
			        	results.add(current);
			        	lookup.put(location, current);
			        }
			   }
			}
		}
		 */
		
		
		
		Collections.sort(results);
		return results;
	}

	// TODO Move logic form completeExact/PartialSearch into QueryParser
	/**
	 * methods performs all processes necessary for exact search
	 * 
	 * @param p the text file of queries to be used for search
	 * @return full EXACT search results
	 * @throws IOException if IO error encountered
	 */
	public TreeMap<String, List<SearchResult>> completeExactSearch(Path p) throws IOException {
		TreeMap<String, List<SearchResult>> fullExactResults = new TreeMap<String, List<SearchResult>>();
		// parse query file by line

		BufferedReader buff = Files.newBufferedReader(p, StandardCharsets.UTF_8);
		String line;
		while ((line = buff.readLine()) != null) { // while still lines in query file, parse

			if (TextFileStemmer.uniqueStems(line) != null && TextFileStemmer.uniqueStems(line).size() != 0) {
				fullExactResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
						exactSearch(TextFileStemmer.uniqueStems(line)));
			}
		}
		return fullExactResults;
	}

	/**
	 * does the inner workings of the complete partial search
	 * 
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of PARTIAL search results
	 */
	public List<SearchResult> partialSearch(TreeSet<String> words) {
		// TODO Don't call commonSearch just yet, need to optimize search before removing duplicate logic
		List<SearchResult> results = new ArrayList<>();
		ArrayList<String> parsedWords = new ArrayList<String>(words);
		ArrayList<String> usedFiles = new ArrayList<String>();

		// TODO for String word : words
		for (String word : parsedWords) {
			// TODO Directly loop through the inverted index keys here
			// TODO for String key : index.keySet()
			// TODO if the key starts with the query...
			// TODO loop through index.get(key).keySet() <--- locations
			if (partialFileGetter(word) != null) {

				commonSearch(results, parsedWords, usedFiles, word, false);

			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * methods performs all processes necessary for partial search
	 * 
	 * @param file the text file of queries to be used for search
	 * @return full PARTIAL search results
	 * @throws IOException if IO error encountered
	 */
	public TreeMap<String, List<SearchResult>> completePartialSearch(Path file) throws IOException {
		TreeMap<String, List<SearchResult>> fullPartialResults = new TreeMap<String, List<SearchResult>>();

		BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
		String line;
		while ((line = reader.readLine()) != null) { // while still lines in query file, parse

			if (TextFileStemmer.uniqueStems(line) != null && TextFileStemmer.uniqueStems(line).size() != 0) {

				fullPartialResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
						partialSearch(TextFileStemmer.uniqueStems(line)));
			}
		}
		return fullPartialResults;
	}

	/**
	 * common-ish parts of the searches, besides few tweaks of method calling within
	 * 
	 * @param results     results list to add to
	 * @param parsedWords list of parse words of query
	 * @param usedFiles   files used within query
	 * @param word        the word to focus on
	 * @param exact       if performing exact search or not
	 */
	private void commonSearch(List<SearchResult> results, ArrayList<String> parsedWords, ArrayList<String> usedFiles,
			String word, boolean exact) {
		if (exact) { // exact search
			for (String file : getLocations(word)) {
				// if file is not already been used
				if (!usedFiles.contains(file)) { // TODO This is a linear time operation on a list
					usedFiles.add(file);

					SearchResult nextResult = new SearchResult();
					nextResult.where = file;
					int count1 = 0;
					for (int x = 0; x < parsedWords.size(); x++) {
						count1 += wordGetter(parsedWords.get(x), file);
					}
					nextResult.count = count1;
					nextResult.score = ((double) count1 / (double) (wordCountGetter(file)));
					results.add(nextResult);
				}
			}
		} else { // do partial search
			for (String file : partialFileGetter(word)) { //
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
					nextResult.score = ((double) count1 / (double) (wordCountGetter(file)));
					results.add(nextResult);
				}
			}
		}
	}

	/**
	 * 
	 * class for the search result object
	 * 
	 * @author sarah
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * NestedInvertedIndex object to use its methods for creating a search result
		 * object
		 */
		InvertedIndex index; // TODO Remove
		/**
		 * location
		 */
		String where; // TODO private final
		/**
		 * total word count of the location
		 */
		int totalWords; // TODO remove, access countMap directly
		/**
		 * total matches within the text file
		 */
		int count; // TODO private
		/**
		 * the percent of words in the file that match the query (like the score)
		 */
		double score; // TODO private

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
		
		/*
		 * TODO Have 1 constructor
		 * public SearchResult(String location) {
		 *    init the count and score to 0
		 * }
		 * 
		 * private void update(String word) {
		 *    this.count += index.get(word).get(location).size();
		 *    this.score = this.count / countMap.get(location);
		 * }
		 */

		/**
		 * @param word     the word which to base and create a single search result off
		 *                 of
		 * @param fileName the text file for the result
		 */
		public void buildSearchResult(String word, String fileName) { // TODO Remove
			where = fileName;
			count = index.size(word, fileName);
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

}
