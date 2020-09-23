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
	TreeMap<String, TreeMap<Path, List<Integer>>> invertedMap = new TreeMap<String, TreeMap<Path, List<Integer>>>();
	
	
	//public void add(String word, HashMap<Path, List<Integer>> map)
  @Override
	public void add(String word, Path file, Integer i){
	  
	  if(contains(word)) { //if stem in index
		  if(!contains(word,file)) { //if textfile is not already stored for stem 
			  List<Integer> indices = new ArrayList<>(Arrays.asList(i));  //create new list for indices
			  invertedMap.get(word).put(file, indices); //add to treemap
		  }else { //if textfile is already in there
			  invertedMap.get(word).get(file).add(i);	//add integer to the index list
		  }  
	 }else { //if stem not in invertedMap
		 List<Integer> indices2 = new ArrayList<>(Arrays.asList(i));  //create new list for indices
		 TreeMap<Path, List<Integer>> fileNindex = new TreeMap<>();		//create new hashmap for stem value
		 fileNindex.put(file, indices2); //create new hashmap and populate with first value
		 invertedMap.put(word, fileNindex);
	 }
		return;
	}
  
/**
 * @return the inverted index
 */
//	@Override
//	public void add(String word, HashMap<Path, List<Integer>> map) {
//		// TODO Auto-generated method stub
//		
//	}

  public TreeMap<String, TreeMap<Path, List<Integer>>> returnIndex() {
	  return invertedMap;
  }
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

	

	@Override
	public boolean contains(String word, Path location) {
		// TODO Auto-generated method stub
		if(invertedMap.containsKey(word) && invertedMap.get(word).containsKey(location)) {
			return true;
		}
		return false;
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
	public Collection<HashMap<Path, List<Integer>>> get(String stem) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(Path p, Integer i) {
		// TODO Auto-generated method stub
		
	}

}
