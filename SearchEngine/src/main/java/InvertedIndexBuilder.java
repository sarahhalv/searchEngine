import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

// TODO Clean up

/**
 * @author sarah Class that works with and creates the nested Inverted Index
 *
 */
public class InvertedIndexBuilder {
	
	/**
	 * TextFileFinder object to use
	 */
	TextFileFinder textFileFinder1 = new TextFileFinder();
	/**
	 * TextFileIndex class object to use
	 */
	TextFileIndex textFileIndex1 = new TextFileIndex();

//	public static void build(Path path, ArgumentMap map) {
////		if this is a directory
////			get the listing
////			on each path in the listing
////				addFile(file, index);
////		else
////				addFile(path, index);
//		TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String, TreeMap<Path, List<Integer>>>();
//		List<Path> textfiles = new ArrayList<>();
//		if (Files.isDirectory(map.getPath("-path"))) { // if path is directory
//			// find and process all of the text files (with .txt and .text extensions) in
//			// that directory and its sub directories.
//		
//			try {
//				textfiles = textFileFinder1.list(map.getPath("-path"));
//			} catch (IOException e) {
//				System.out.println("unable to create array of gathered textfiles");
//			}
//
//		// storing a word, file path, and location into an inverted index data structure
//		// (similar but lil diff to textfileindex)
//		
//		for (Path yee : textfiles) { // iterate through the files
//			
//			addFile(yee, textFileIndex1);
//		}
//		
//		} else { // if single file, add it
//			if (map.getPath("-path") != null) {
//				textfiles.add(map.getPath("-path"));
//			}
//		}
//		invertm = textFileIndex1.returnIndex();
//		return invertm;
//	}
//	
	
	/**
	 * adds file and data to the inverted index
	 * @param file the file to use
	 * @param index the inverted index to add he file data to 
	 */
	public static void addFile(Path file, TextFileIndex index) {
		// TODO This moves into an addFile method instead
		ArrayList<String> stems1 = new ArrayList<>();
			try {
				stems1 = TextFileStemmer.listStems(file);
			} catch (IOException e) {
				System.out.println("unable to gather all of the stems of textfile: " + file.toString());
			}

			int index1 = 1;
			for (String stemmies : stems1) {
				// add this information into inverted index but now have to implement text file
				// index
				index.add(stemmies, file, index1);
				index1++;
			}
	}

	/**
	 * creates the nested inverted index
	 *
	 * @param p   the path with which to create the index
	 * @param map the argument map containing path and index flags and values
	 * @return the created inverted index
	 * 
	 */
	public TreeMap<String, TreeMap<Path, List<Integer>>> createNestedInvertedIndex(Path p, ArgumentMap map) {

		// traverse a directory and return a list of all the text files found within
		// that directory (textfilefinder)

		List<Path> textfiles = new ArrayList<>();
		if (Files.isDirectory(map.getPath("-path"))) { // if path is directory
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its sub directories.
		
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
		for (Path yee : textfiles) { // iterate through the files
			
			addFile(yee, textFileIndex1);
		}
		invertm = textFileIndex1.returnIndex();
		return invertm;
	}

}
