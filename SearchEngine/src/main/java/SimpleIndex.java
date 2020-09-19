import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

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
	 * @param location the location the word was found
	 * @param word the word found
	 */
	public void add(Path location, String word);
	
	/**
	 * Adds the location and the provided words.
	 *
	 * @param location the location the words were found
	 * @param words the words found at that location
	 */
	public default void add(Path location, String[] words) {
		
		for(int i=0; i<words.length; i++) {
			add(location, words[i]);
		}
		
	}
	
	/**
	 * Returns the number of words stored for the given path.
	 *
	 * @param location the location to lookup
	 * @return 0 if the location is not in the index or has no words, otherwise
	 *         the number of words stored for that element
	 */
	public int size(Path location);

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
	 * @param location the location to lookup
	 * @return {@true} if the location is stored in the index
	 */
	public boolean contains(Path location);

	/**
	 * Determines whether the location is stored in the index and the word is
	 * stored for that location.
	 *
	 * @param location the location to lookup
	 * @param word the word in that location to lookup
	 * @return {@true} if the location and word is stored in the index
	 */
	public boolean contains(Path location, String word);

	/**
	 * Returns an unmodifiable view of the locations stored in the index.
	 *
	 * @return an unmodifiable view of the locations stored in the index
	 * @see Collections#unmodifiableCollection(Collection)
	 */
	public Collection<Path> get();

	/**
	 * Returns an unmodifiable view of the words stored in the index for the
	 * provided location, or an empty collection if the location is not in the
	 * index.
	 *
	 * @param location the location to lookup
	 * @return an unmodifiable view of the words stored for the location
	 */
	public Collection<String> get(Path location);

}
