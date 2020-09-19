import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * A special type of {@link SimpleIndex} that indexes the UNIQUE words that were
 * found in a text file.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class TextFileIndex implements SimpleIndex{
	// TODO Modify class declaration to implement the Index interface for String elements
	// TODO Modify anything within this class as necessary
	
	  /**
		 * initializes hashmap for the index
		 */
	HashMap<Path, List<String>> indexMap = new HashMap<>();
	
	@Override
	public void add(Path location, String word) {
		// TODO Auto-generated method stub
		if(contains(location)) {	//if path is already in there
			if(indexMap.get(location).contains(word)) { //if word is already in there
				return; //don't add it and end method
			}
			indexMap.get(location).add(word);
		}else {
			List<String> words = new ArrayList<>(Arrays.asList(word)); //if no location at all, create new list for values
			indexMap.put(location, words);
		}
		return;
	}

	@Override
	public int size(Path location) {
		// TODO Auto-generated method stub
		if(contains(location)) { //check if specific path exists
			return (indexMap.get(location)).size();
		}else {
			return 0;
		}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return indexMap.size();
	}

	@Override
	public boolean contains(Path location) {
		// TODO Auto-generated method stub
		if(indexMap.containsKey(location)) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean contains(Path location, String word) {
		// TODO Auto-generated method stub
		if(contains(location) && ((indexMap.get(location)).contains(word))) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public Collection<Path> get() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableCollection(indexMap.keySet());
	}

	@Override
	public Collection<String> get(Path location) {
		// TODO Auto-generated method stub
		if(contains(location)) {
			return Collections.unmodifiableCollection(indexMap.get(location));
		}
		return Collections.emptyList();
	}
	/**
	* overrides to string to handle the index
	*/
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(HashMap.Entry<Path, List<String>> entry: indexMap.entrySet()) {
			sb.append(entry.getKey().toString()+": "+ entry.getValue().toString() +"\n");
		}
		return sb.toString();
	}
	

}
