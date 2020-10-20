import java.io.IOException;
import java.nio.file.Path;
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
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex1; // TODO Better name

	/**
	 * inverted index class object constructor
	 */
	public InvertedIndex() {
		this.invertedIndex1 = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * @param word     stem word to be an index key
	 * @param file     location to add to index
	 * @param position positions where word is found in that loation
	 */
	public void add(String word, String file, Integer position) {
		invertedIndex1.putIfAbsent(word, new TreeMap<>());
		invertedIndex1.get(word).putIfAbsent(file, new TreeSet<>());
		invertedIndex1.get(word).get(file).add(position);
	}

	/**
	 * @return number of words in index
	 */
	public int size() {
		return invertedIndex1.size();
	}

	/**
	 * @param word the specific stem/word
	 * @return # of paths stored for that word
	 */
	public int size(String word) {
		/*
		 * TODO Throws a null pointer exception if get(word) returns null. Use your
		 * contains methods to make sure the word is in your index, if not, return 0.
		 */
		return invertedIndex1.get(word).keySet().size();
	}

	/**
	 * @param word     the stem word
	 * @param location the specific text file
	 * @return # of positions stored in that location
	 */
	int size(String word, String location) {
		return invertedIndex1.get(word).get(location).size(); // TODO Same null pointer problem.
	}

	/**
	 * @param stem the specific word being looked for
	 * @return true if stem word is in index
	 */
	public boolean contains(String stem) {
		return invertedIndex1.containsKey(stem);
	}

	/**
	 * @param word     the specific stem word
	 * @param location the specific text file
	 * @return true if word can be found in that file (if file is in words key set)
	 */
	public boolean contains(String word, String location) {
		return invertedIndex1.containsKey(word) && invertedIndex1.get(word).containsKey(location);
	}

	/**
	 * TODO Description here and all the other Javadoc where it is missing
	 * 
	 * @param word     stem
	 * @param location file location
	 * @param position location in file where word may be
	 * @return if word exists in file in that location
	 */
	public boolean contains(String word, String location, int position) {
		return invertedIndex1.containsKey(word) && invertedIndex1.get(word).containsKey(location)
				&& invertedIndex1.get(word).get(location).contains(position);
	}

	// TODO Breaks encapsulation, remove
	/**
	 * @return the inverted map in its form
	 */
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> returnIndex() {
		return invertedIndex1;
	}

	/**
	 * @param path output file path to write to
	 * @throws IOException if encounter IO error
	 */
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asDoubleNestedStructure(invertedIndex1, path);
	}
	
	/*
	 * TODO Contains methods look great, size methods look great, but need get methods
	 * that are safe...
	 * 
	 * Set<String> get() or getWords() ---> safely return all of the words (i.e. invertedIndex1.keySet() as unmodifiable set)
	 * Set<String> get(String word) or getLocations(String word) --> safely returns locations for a word if that word exists, otherwise Collections.emptySet()
	 * Set<Integer> get(String word, String location) or getPositions(String word, String location) --> safely return positions if exist
	 * 
	 * ...also override toString!
	 */

}
