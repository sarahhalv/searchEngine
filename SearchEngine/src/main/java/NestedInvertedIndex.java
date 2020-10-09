import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * @author sarah Class that works with and creates the nested Inverted Index
 *
 */
public class NestedInvertedIndex {

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
			System.out.println("dir is " + isDir);
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
		TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String, TreeMap<Path, List<Integer>>>();
		// TreeMap<Path, List<Integer>> stemData = new TreeMap<>(); //text files and
		// locations within them
		for (Path yee : textfiles) { // iterate through the files
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
			}
		}
		invertm = textFileIndex1.returnIndex();
		return invertm;
	}

}
