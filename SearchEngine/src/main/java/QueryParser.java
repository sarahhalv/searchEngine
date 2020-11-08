import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

/**
 * class that dealsw with query file and produces search results
 * 
 * @author sarah
 *
 */
public class QueryParser {

	/**
	 * index to use
	 */
	InvertedIndex index;
	/**
	 * map containing search results from query
	 */
	TreeMap<String, List<InvertedIndex.SearchResult>> searchResults;

	/**
	 * query parser constructor
	 * 
	 * @param index         the index to use for this class
	 * @param searchResults the map to store results in
	 */
	public QueryParser(InvertedIndex index, TreeMap<String, List<InvertedIndex.SearchResult>> searchResults) {
		this.index = index;
		// initialize search results in here
		this.searchResults = searchResults;

	}

	/**
	 * @param path  the path to the query file
	 * @param exact whether to perform exact or partial search
	 * @throws IOException if IO error occurs
	 */
	public void parseQueryFile(Path path, boolean exact) throws IOException {

		if (exact) {
			searchResults = index.completeExactSearch(path);
		} else {
			searchResults = index.completePartialSearch(path);
		}
		return;
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
