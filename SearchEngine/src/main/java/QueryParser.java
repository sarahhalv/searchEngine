import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * class that deals with query file and produces search results
 * 
 * @author sarah
 *
 */
public class QueryParser {

	/**
	 * index to use
	 */
	private final InvertedIndex index;
	/**
	 * map containing search results from query
	 */
	private final TreeMap<String, List<InvertedIndex.SearchResult>> searchResults;

	/**
	 * query parser constructor
	 * 
	 * @param index the index to use for this class
	 */
	public QueryParser(InvertedIndex index) {
		this.index = index;
		this.searchResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
	}

	/**
	 * parses the query file line by line and calls parseQueryLine for the results
	 * 
	 * @param path  the path to the query file
	 * @param exact whether to perform exact or partial search
	 * @throws IOException if IO error occurs
	 */
	public void parseQueryFile(Path path, boolean exact) throws IOException {
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
	public void parseQueryLine(String line, boolean exact) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		String query = String.join(" ", stems);

		if (stems.size() != 0) {
			if(!searchResults.containsKey(query)) {
				searchResults.put(query, index.search(stems, exact));
			}
		}
	}

	/**
	 * outputs the searchResults map to file
	 * 
	 * @param path the file to output/write results to
	 * @throws IOException if IO error encountered
	 */
	public void writeJson(Path path) throws IOException {
		SimpleJsonWriter.asFullResults(searchResults, path);
	}
}
