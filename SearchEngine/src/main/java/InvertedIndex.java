import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
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
	 * inverted index class object constructor
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * adds a set of specific data to the index
	 * 
	 * @param word     stem word to be an index key
	 * @param file     location to add to index
	 * @param position positions where word is found in that loation
	 */
	public void add(String word, String file, Integer position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(file, new TreeSet<>());
		index.get(word).get(file).add(position);
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
	 * finds the number of files this word is found in
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
	 * finds the number of times the passed in word is in the index
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
	 * grabs and returns all of the locations for the specified word
	 * 
	 * @param word the specified stem word
	 * @return Set of locations
	 */
	public Set<String> getLocations(String word) {
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
	public String toString() {
		return index.toString();
	}

}
