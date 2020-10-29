import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class for finding all text files in a directory using lambda
 * functions and streams.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class TextFileFinder {

	/**
	 * A lambda function that returns true if the path is a file that ends in a .txt
	 * or .text extension (case-insensitive).
	 */
	public static final Predicate<Path> IS_TEXT = i -> Files.isRegularFile(i)
			&& (i.getFileName().toString().toLowerCase().endsWith(".txt")
					|| i.getFileName().toString().toLowerCase().endsWith(".text"));

	/**
	 * Returns a stream of matching files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @param keep  function that determines whether to keep a file
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 */
	public static Stream<Path> find(Path start, Predicate<Path> keep) throws IOException {

		Stream<Path> matchingFiles = Files.walk(start, FileVisitOption.FOLLOW_LINKS).filter(keep); // if fits predicate
		return matchingFiles; // return files
	}

	/**
	 * Returns a stream of text files, following any symbolic links encountered.
	 *
	 * @param start the initial path to start with
	 * @return a stream of text files
	 * @throws IOException if an IO error occurs
	 */
	public static Stream<Path> find(Path start) throws IOException {

		Stream<Path> textFiles = find(start, IS_TEXT); // keep if text file
		return textFiles; // return text files
	}

	/**
	 * Returns a list of text files using streams.
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an IO error occurs
	 */
	public static List<Path> list(Path start) throws IOException {

		Stream<Path> textFiles = find(start); // gather stream via previous find()
		List<Path> textFileList = textFiles.collect(Collectors.toList()); // convert Stream to list
		return textFileList; // return list of text files

	}
	
}
