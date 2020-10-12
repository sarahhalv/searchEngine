import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author sarah Class that works with and creates the nested Inverted Index
 *
 */
public class NestedInvertedIndex {

	/**
	 * the inverted index
	 */
	TreeMap<String, TreeMap<Path, List<Integer>>> invertedIndex = new TreeMap<String, TreeMap<Path, List<Integer>>>();
	/**
	 * map that records how many words in a textfile
	 */
	TreeMap<String, Integer> countMap = new TreeMap<String, Integer>();

	/**
	 * creates the nested inverted index
	 *
	 * @param p   the path with which to create the index
	 * @param map the argument map containing path and index flags and values
	 * @return the created inverted index
	 * 
	 */

	public TreeMap<String, TreeMap<Path, List<Integer>>> createNestedInvertedIndex(Path p, ArgumentMap map) {
		TextFileFinder textFileFinder1 = new TextFileFinder();
		TextFileIndex textFileIndex1 = new TextFileIndex();

		// traverse a directory and return a list of all the text files found within
		// that directory (textfilefinder)
		boolean isDir = false;
		List<Path> textfiles = new ArrayList<>();
		if (Files.isDirectory(map.getPath("-path"))) { // if path is directory
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its subdirectories.
			isDir = true;
			try {
				textfiles = textFileFinder1.list(map.getPath("-path"));
			} catch (IOException e) {
				System.out.println("unable to create array of gathered textfiles");
			}
		} else { // if single file, add it
			if (map.getPath("-path") != null) {
				textfiles.add(map.getPath("-path"));
			}
		}

		// storing a word, file path, and location into an inverted index data structure
		// (similar but lil diff to textfileindex)

		// TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String,
		// TreeMap<Path, List<Integer>>>();

		// TreeMap<Path, List<Integer>> stemData = new TreeMap<>(); //text files and
		// locations within them
		for (Path yee : textfiles) { // iterate through the files
			int i = 0; // number of words in file
			ArrayList<String> stems1 = new ArrayList<>();
			try {
				stems1 = TextFileStemmer.listStems(yee);
			} catch (IOException e) {
				System.out.println("unable to gather all of the stems of textfile: " + yee.toString());
			}

			int index1 = 1;
			for (String stemmies : stems1) {
				// add this information into inverted index but now have to implement text file
				// index
				textFileIndex1.add(stemmies, yee, index1);
				index1++;
				i++; // increment how many words in file
			}
			// if theres a count at input file and its number of words into word count map
			if (i != 0) {
				countMap.putIfAbsent(yee.toString(), i);
			}
			// System.out.println(yee.toString() + " -- word count of: " + i);
		}
		invertedIndex = textFileIndex1.returnIndex();
		return invertedIndex;
	}

	/**
	 * @param word the word from which to grab files
	 * @return list of files where word is located
	 */
	public Set<Path> fileGetter(String word) {
		//System.out.println("filegetter content: " +invertedIndex.get(word));
		if(invertedIndex.get(word)!= null) {
			if(invertedIndex.get(word).keySet() != null) {
				return invertedIndex.get(word).keySet();
			}
		}
			return null;
	}

	/**
	 * @param word the word to get count for
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	public int wordGetter(String word, Path file) {
		return invertedIndex.get(word).get(file).size(); // return how many times in file for this particular word
	}

	/**
	 * @param filename the file which to count the words
	 * @return the number of words in the passed in file
	 */
	public int wordCountGetter(String filename) {
		return countMap.get(filename);
	}

	/**
	 * @return the countMap created alongside the inverted index
	 */
	public TreeMap<String, Integer> returnCountMap() {
		return countMap;
	}

}
