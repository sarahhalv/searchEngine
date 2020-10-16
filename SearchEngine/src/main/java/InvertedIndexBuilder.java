import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sarah Class that works with and creates the nested Inverted Index
 *
 */
public class InvertedIndexBuilder {

	/**
	 * builds the inverted index that is passed in
	 * 
	 * @param path  the path to use
	 * @param index the index to populate
	 */
	public static void build(Path path, InvertedIndex index) {

		List<Path> textfiles = new ArrayList<>();
		if (Files.isDirectory(path)) { // if path is directory
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its sub directories.

			try {
				textfiles = TextFileFinder.list(path);
			} catch (IOException e) {
				System.out.println("unable to create array of gathered textfiles");
			}

			// storing a word, file path, and location into an inverted index data structure
			// (similar but lil diff to textfileindex)

			for (Path yee : textfiles) { // iterate through the files

				addFile(yee, index);
			}

		} else { // if single file, add it
			if (path != null) {

				addFile(path, index);
			}
		}

		System.out.println(textfiles.toString());
		return;
	}

	/**
	 * adds file and data to the inverted index
	 * 
	 * @param file  the file to use
	 * @param index the inverted index to add he file data to
	 */
	public static void addFile(Path file, InvertedIndex index) {
		ArrayList<String> stems1 = new ArrayList<>();
		try {
			stems1 = TextFileStemmer.listStems(file);
		} catch (IOException e) {
			System.out.println("unable to gather all of the stems of textfile: " + file.toString());
		}

		int index1 = 1;
		for (String stemmies : stems1) {
			// add data into inverted index
			index.add(stemmies, file.toString(), index1);
			index1++;
		}
	}

}
