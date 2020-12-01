import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	private final TreeMap<String, Integer> countMap;

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
		countMap.put(file, Math.max(wordCountGetter(file), position)); // update the countMap for this file
	}

	/**
	 * merges the shared data (of index) with the local index data/a snippet of new
	 * index data
	 * 
	 * @param local the local index data to add to threadsafe index
	 */
	public void addAll(InvertedIndex local) {
		for (String stemWord : local.getWords()) {
			//System.out.println("stemword : " + stemWord);
			if (!this.index.containsKey(stemWord)) {
				this.index.put(stemWord, local.index.get(stemWord));
			} else {
				//System.out.println("else contains");
				for (String location : local.getLocations(stemWord)) {
					if (!this.index.get(stemWord).containsKey(location)) {
						this.index.get(stemWord).put(location, local.index.get(stemWord).get(location));
					}else {
						this.index.get(stemWord).get(location).addAll(local.index.get(stemWord).get(location));
					}
				}
			}
		}
		//merge wordcounts
		for(String word: local.countMap.keySet()) {
			if(!this.countMap.containsKey(word)) {
				this.countMap.put(word, local.countMap.get(word));
			}else {
				if(this.wordCountGetter(word) < local.wordCountGetter(word)) {
					this.countMap.put(word, local.wordCountGetter(word));
				}
			}
		}
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
	 * grabs the number of appearances a word has in a particular file
	 * 
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
	 * grabs how many words in a given file
	 * 
	 * @param filename the file which to count the words
	 * @return the number of words in the passed in file
	 */
	public int wordCountGetter(String filename) {
		return countMap.getOrDefault(filename, 0);
	}

	/**
	 * returns the countmap as an unmodifiable structure
	 * 
	 * @return the countMap created alongside the inverted index
	 */
	public Map<String, Integer> returnCountMap() {
		return Collections.unmodifiableMap(countMap);
	}

	/**
	 * a search convenience method that returns a list of search results for the
	 * passed in queries
	 * 
	 * @param words the queries
	 * @param exact whether its exact or partial search
	 * @return a list of search results
	 */
	public List<SearchResult> search(Set<String> words, boolean exact) {
		if (exact) {
			return exactSearch(words);
		} else {
			return partialSearch(words);
		}
	}

	/**
	 * performs an exact search from a given set of words
	 * 
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of EXACT search results
	 */
	public List<SearchResult> exactSearch(Set<String> words) {
		List<SearchResult> results = new ArrayList<>();
		// string is location
		Map<String, SearchResult> lookup = new HashMap<String, SearchResult>();

		for (String query : words) {
			if (index.containsKey(query)) {
				commonSearch(query, results, lookup);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * performs a partial search for a given set of words (if word is substring of a
	 * key)
	 *
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of PARTIAL search results
	 */
	public List<SearchResult> partialSearch(Set<String> words) {
		List<SearchResult> results = new ArrayList<>();
		// string is location
		Map<String, SearchResult> lookup = new HashMap<String, SearchResult>();

		for (String query : words) {
			for (String key : index.tailMap(query).keySet()) {
				if (key.startsWith(query)) {
					commonSearch(key, results, lookup);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * the common functionality present in both exact and partial search, adds
	 * results to results and a lookup map
	 * 
	 * @param input   the specific input for the different searches (query for
	 *                exact, key for partial)
	 * @param results the search results list to add to
	 * @param lookup  the lookup map to add results to
	 */
	private void commonSearch(String input, List<SearchResult> results, Map<String, SearchResult> lookup) {

		for (String location : index.get(input).keySet()) {
			if (!lookup.containsKey(location)) {
				SearchResult current = new SearchResult(location);
				results.add(current);
				lookup.put(location, current);
			}
			lookup.get(location).update(input);
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
		 * location
		 */
		private final String where;
		/**
		 * total matches within the text file
		 */
		private int count;
		/**
		 * the percent of words in the file that match the query (like the score)
		 */
		private double score;

		/**
		 * search result constructor
		 * 
		 * @param location the file location of the result
		 */
		public SearchResult(String location) {
			this.count = 0;
			this.score = 0;
			this.where = location;
		}

		/**
		 * updates the count and score of search result object
		 * 
		 * @param word the word to add
		 */
		private void update(String word) {
			this.count += index.get(word).get(where).size();
			this.score = this.count / (double) countMap.get(where);
		}

		/**
		 * grabs the score of the search result
		 * 
		 * @return frequency/score of relative matches to number of words in file
		 */
		public double getScore() {
			return score;
		}

		/**
		 * grabs the location/filename for the search result
		 * 
		 * @return location of result
		 */
		public String getWhere() {
			return where;
		}

		/**
		 * grabs the count (of matches) for a search result
		 * 
		 * @return number of matches in result
		 */
		public int getCount() {
			return count;
		}

		/*
		 * method for how the results will be sorted
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
