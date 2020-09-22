import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
		 * initializes map for the index
		 */
	//HashMap<Path, List<String>> indexMap = new HashMap<>();
	TreeMap<String, HashMap<Path, List<Integer>>> invertedMap = new TreeMap<String, HashMap<Path, List<Integer>>>();
	
	
	//public void add(String word, HashMap<Path, List<Integer>> map)
  @Override
	public void add(String word, HashMap<Path, List<Integer>> map){
//		// TODO Auto-generated method stub
//		if(contains(word)) {	//if stem is already in there
//			if(invertedMap.get(word).contains(map)) { //if hashmap is already present under stem
//				return; //don't add it and end method
//			}
//			invertedMap.get(word).add(map); //add hashmap
//		}else {
//			List<HashMap<Path,List<Integer>>> files = new ArrayList<>(Arrays.asList(map)); //if no location at all, create new list for values
//			invertedMap.put(word, map);
//		}
		return;
	}
  
//	@Override
//	public void add(String word, HashMap<Path, List<Integer>> map) {
//		// TODO Auto-generated method stub
//		
//	}

//	@Override
//	public int size(String stem) {
//		// TODO Auto-generated method stub
//		if(contains(stem)) { //check if specific path exists
//			return (invertedMap.get(stem)).size();
//		}else {
//			return 0;
//		}
//	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return invertedMap.size();
	}

//	@Override
//	public boolean contains(Path location) {
//		// TODO Auto-generated method stub
//		if(invertedMap.containsKey(location)) {
//			return true;
//		}else {
//			return false;
//		}
//	}
	
	@Override
	public boolean contains(String stem) {
		// TODO Auto-generated method stub
		if(invertedMap.containsKey(stem)){
			return true;
		}else {
			return false;
		}
	}

//	@Override
//	public boolean contains(Path location, String word) {
//		// TODO Auto-generated method stub
//		if(contains(location) && ((invertedMap.get(location)).contains(word))) {
//			return true;
//		}else {
//			return false;
//		}
//	}

	@Override
	public Collection<String> get() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableCollection(invertedMap.keySet());
	}

//	@Override
//	public Collection<HashMap<Path, List<Integer>>> get(String stem) {
//		// TODO Auto-generated method stub
//		if(contains(stem)) {
//			return Collections.unmodifiableSet(invertedMap.get(stem));
//		}
//		return Collections.emptyList();
//	}
	/**
	* overrides to string to handle the index
	*/
	@Override
	public String toString() {   //FIX THIS
		StringBuilder sb = new StringBuilder();
		for(Map.Entry entry: invertedMap.entrySet()) {
			sb.append(entry.getKey().toString()+": "+ entry.getValue().toString() +"\n");
		}
		return sb.toString();
	}

	@Override
	public boolean contains(HashMap<Path, List<Integer>> map) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean contains(Path location, String word) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<HashMap<Path, List<Integer>>> get(String stem) {
		// TODO Auto-generated method stub
		return null;
	}

}
