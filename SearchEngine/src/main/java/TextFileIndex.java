import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/*
 * TODO 
 * Consider combining SimpleIndex and TextFileIndex... optional.
 * Rename this to InvertedIndex
 */

/**
 * A special type of {@link SimpleIndex} that indexes the UNIQUE words that were
 * found in a text file.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class TextFileIndex implements SimpleIndex {
	// TODO Modify class declaration to implement the Index interface for String
	// elements
	// TODO Modify anything within this class as necessary

	/**
	 * initializes map for the index
	 */
	TreeMap<String, TreeMap<Path, List<Integer>>> invertedMap = new TreeMap<String, TreeMap<Path, List<Integer>>>();

	/*
	 * TODO 
	 * private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedMap;
	 * 
	 * Initialize instance members in a constructor
	 */
	// TODO public void add(String word, String file, Integer position) {
	@Override
	public void add(String word, Path file, Integer i) {

		if (contains(word)) { // if stem in index
			if (!contains(word, file)) { // if text file is not already stored for stem
				List<Integer> indices = new ArrayList<>(Arrays.asList(i)); // create new list for indices
				invertedMap.get(word).put(file, indices); // add to tree map
			} else { // if textfile is already in there
				invertedMap.get(word).get(file).add(i); // add integer to the index list
			}
		} else { // if stem not in invertedMap
			List<Integer> indices2 = new ArrayList<>(Arrays.asList(i)); // create new list for indices
			TreeMap<Path, List<Integer>> fileNindex = new TreeMap<>(); // create new hash map for stem value
			fileNindex.put(file, indices2); // create new hash map and populate with first value
			invertedMap.put(word, fileNindex);
		}
		return;
		
		/* TODO Refactor round 1
		if (!invertedMap.containsKey(word)) {
			invertedMap.put(word, new TreeMap<>());
		}
		
		if (!invertedMap.get(word).containsKey(file)) {
			invertedMap.get(word).put(file, new ArrayList<>());
		}
		
		invertedMap.get(word).get(file).add(i);
		*/
		
		/* TODO 
		invertedMap.putIfAbsent(word, new TreeMap<>());
		invertedMap.get(word).putIfAbsent(file, new ArrayList<>());
		invertedMap.get(word).get(file).add(i);
		*/
	}

	@Override
	public int size() { // TODO Returns number of words.
		// TODO Auto-generated method stub
		return invertedMap.size();
	}
	
	/*
	 * TODO 
	 * public int size(String word) ---> # of paths stored for that word
	 * public int size(String word, String location) --> # of positions stored
	 */

	@Override
	public boolean contains(String stem) {
		// TODO Auto-generated method stub
		if (invertedMap.containsKey(stem)) { // TODO Single return statement
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean contains(String word, Path location) {
		// TODO Auto-generated method stub
		if (invertedMap.containsKey(word) && invertedMap.get(word).containsKey(location)) {
			return true;
		}
		return false;
	}
	
	// TODO public boolean contains(String word, Path location, int position) {

	@Override
	public Collection<String> get() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableCollection(invertedMap.keySet());
	}

	// TODO Remove?
	@Override
	public boolean contains(HashMap<Path, List<Integer>> map) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<HashMap<Path, List<Integer>>> get(String stem) {
		// TODO Auto-generated method stub
		return null; // TODO ???
	}
	
	/*
	 * TODO

	Methods currently are breaking encapsulation

	public Set<String> get()
	
	public Set<String> get(String word) {
		if the word exists
			return Collections.unmodifiableSet(invertedMap.get(word).keySet());
		else
			return Collections.emtpySet();
	}
	
	public Set<Integer> get(String word, String location)

	 */

	@Override
	public void add(Path p, Integer i) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the inverted map in its form
	 */
	public TreeMap<String, TreeMap<Path, List<Integer>>> returnIndex() {
		// TODO Auto-generated method stub
		return invertedMap;
	}

	/* TODO 
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asDoubleNestedArray(invertedMap, path);
	}
	*/
}
