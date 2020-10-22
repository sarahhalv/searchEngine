import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author sarah Class that works with and creates the nested Inverted Index
 *
 */
public class InvertedIndexBuilder {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * builds the inverted index that is passed in
	 * 
	 * @param path  the path to use
	 * @param index the index to populate
	 * @throws IOException if IO exception encountered
	 */
	public static void build(Path path, InvertedIndex index) throws IOException {

		List<Path> files = new ArrayList<>();
		if (Files.isDirectory(path)) {
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its sub directories.

			files = TextFileFinder.list(path);

			// storing a word, file path, and location into an inverted index data structure
			for (Path file : files) { // iterate through the files

				addFile(file, index);
			}

		} else { // if single file, add it
			if (path != null) {

				addFile(path, index);
			}
		}

		return;
	}

	/**
	 * adds file and data to the inverted index
	 * 
	 * @param file  the file to use
	 * @param index the inverted index to add he file data to
	 * @throws IOException if IO exception occurs
	 */
	public static void addFile(Path file, InvertedIndex index) throws IOException {

		Stemmer stemmer = new SnowballStemmer(DEFAULT);
		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = null;
			int location = 1;
			while ((line = reader.readLine()) != null) {
				String[] words = TextParser.parse(line);
				for (String word : words) {
					index.add((stemmer.stem(word)).toString(), file.toString(), location);
					location++;
				}

			}
		}
	}

}
