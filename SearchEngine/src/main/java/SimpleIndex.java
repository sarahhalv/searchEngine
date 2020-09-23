import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * An index to store locations and the words found at those locations. Makes no
 * assumption about order or duplicates.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public interface SimpleIndex {

	/**
	 * Adds the location and word.
	 *
	 * @param p the file where the stem was found
	 * @param i the index in the file where the word was found 
	 */
	public void add(Path p, Integer i);
	
	/**
	 * Adds the location and word.
	 *
	 * @param map the file and locations within it the stem is found
	 * @param word the stem
	 * @param file the path to the textfile 
	 */
	public default void add(String word, Path file, Integer i) {
	
	}
	
	
	/**
	 * @param file  the location textfile
	 * @param indexes the array of integers to add
	 */
	public default void add(Path file, Integer[] indexes) {
		for(int i=0; i<indexes.length; i++) {
			add(file, indexes[i]);
		}
	}
//	
	/**
	 * Adds the location and the provided words.
	 *
	 * @param maps the paths/files and the locations that stem is found in
	 * @param word the stem word
	 */
//	public default void add(String word, HashMap<Path, List<Integer>>[] maps) {
//		
//		for(int i=0; i<maps.length; i++) {
//			add(word, maps[i]);
//		}
//	
//	}
	
//	/**
//	 * Returns the number of words stored for the given path.
//	 *
//	 * @param location the location to lookup
//	 * @return 0 if the location is not in the index or has no words, otherwise
//	 *         the number of words stored for that element
//	 */
//	public int size(Path location);

	/**
	 * Returns the number of locations stored in the index.
	 *
	 * @return 0 if the index is empty, otherwise the number of locations in the
	 *         index
	 */
	public int size();

	/**
	 * Determines whether the location is stored in the index.
	 *
	 * @param stem the stemmed word to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(String stem);

	/**
	 * Determines whether the location is stored in the index for the specific word (stem)
	 *
	 * @param location the location to lookup
	 * @param word the word in that location to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(String word, Path location);

	/**
	 * Determines whether the map is stored in index and t
	 *
	 * @param map the text file and locations
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(HashMap<Path, List<Integer>> map);
	
	/**
	 * Returns an unmodifiable view of the stems stored in the index.
	 *
	 * @return an unmodifiable view of the locations stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<String> get();

	/**
	 * Returns an unmodifiable view of the words stored in the index for the
	 * provided location, or an empty collection if the location is not in the
	 * index.
	 *
	 * @param stem the stem to lookup
	 * @return an unmodifiable view of the words stored for the location
	 */
	public Collection<HashMap<Path, List<Integer>>> get(String stem);
	


}
