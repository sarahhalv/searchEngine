import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	 * Returns a list of text files using traditional approach
	 *
	 * @param start the initial path to search
	 * @return list of text files
	 * @throws IOException if an IO error occurs
	 *
	 * @see Collectors#toList()
	 */
	public static List<Path> list(Path start) throws IOException {

		List<Path> textfiles = new ArrayList<>();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(start)) {

			for (Path p : ds) {
				// if its a text file
				if (isTextFile(p)) {
					textfiles.add(p);
				} else if (Files.isDirectory(p)) {
					textfiles.addAll(list(p));
				}
			}
		}

		return textfiles;
	}

	/**
	 * checks if path is of a text file
	 * 
	 * @param filepath path to see if text file
	 * @return true if text file
	 */
	public static boolean isTextFile(Path filepath) {
		if (Files.isRegularFile(filepath) && ((filepath.toString().toLowerCase()).endsWith(".txt")
				|| ((filepath.toString().toLowerCase()).endsWith(".text")))) {
			return true;
		}
		return false;
	}
}
