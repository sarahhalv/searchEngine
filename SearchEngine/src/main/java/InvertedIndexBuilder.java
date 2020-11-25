import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO There isn't justification to have these methods suddenly non-static. 
 * They were static before:
 * 
 * https://github.com/usf-cs212-fall2020/project-sarahhalv/blob/v2.4.1/SearchEngine/src/main/java/InvertedIndexBuilder.java
 * 
 * If you want instance methods, there is a way to design this class to use
 * instance methods and instance data, but this isn't it. You can ask on Piazza
 * if you want to change the design. Otherwise, make these static again and also
 * take a static approach in your multithreaded builder class.
 */

/**
 * Class that works with and creates the nested Inverted Index
 * @author sarah 
 *
 */
public class InvertedIndexBuilder {
	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;
	/** Logger to use for this class. */
	private static final Logger log = LogManager.getLogger();
	/**
	 * builds the inverted index that is passed in
	 * 
	 * @param path  the path to use
	 * @param index the index to populate
	 * @throws IOException if IO exception encountered
	 */
	public void build(Path path, InvertedIndex index) throws IOException {
		log.debug("inside normal builder build instead?");
		if (Files.isDirectory(path)) {
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its sub directories.
			List<Path> files = TextFileFinder.list(path);
			// storing a word, file path, and location into an inverted index data structure
			for (Path file : files) { // iterate through the files
				addFile(file, index);
			}
		} else { // if single file, add it
			addFile(path, index);
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
	public void addFile(Path file, InvertedIndex index) throws IOException {
		Stemmer stemmer = new SnowballStemmer(DEFAULT);

		try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line = null;
			int location = 1;

			String fileLocation = file.toString();

			while ((line = reader.readLine()) != null) {
				String[] words = TextParser.parse(line);

				for (String word : words) {
					index.add((stemmer.stem(word)).toString(), fileLocation, location);
					location++;
				}
			}
		}
	}


}
