import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Interface used by both single and multithreaded query parser
 * 
 * @author sarah
 *
 */
public interface QueryParserInterface {

	/**
	 * parses the query file line by line and calls parseQueryLine for the results
	 * 
	 * @param path  the path to the query file
	 * @param exact whether to perform exact or partial search
	 * @throws IOException if IO error occurs
	 */

	public default void parseQueryFile(Path path, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {

			String line;
			while ((line = reader.readLine()) != null) { // while still lines in query file, parse
				parseQueryLine(line, exact);
			}
		}
	}

	/**
	 * parses a single line of queries and adds the appropriate type of search
	 * result to the result map
	 * 
	 * @param line  the query line of text
	 * @param exact whether its an exact search or not
	 */
	public void parseQueryLine(String line, boolean exact);

	/**
	 * outputs the searchResults map to file
	 * 
	 * @param path the file to output/write results to
	 * @throws IOException if IO error encountered
	 */
	public void writeJson(Path path) throws IOException;
}
