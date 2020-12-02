import java.io.IOException;
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
public class QueryParser implements QueryParserInterface {

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

	@Override
	public void parseQueryLine(String line, boolean exact) {
		TreeSet<String> stems = TextFileStemmer.uniqueStems(line);
		String query = String.join(" ", stems);

		if (stems.size() != 0) {
			if (!searchResults.containsKey(query)) {
				searchResults.put(query, index.search(stems, exact));
			}
		}
	}

	@Override
	public void writeJson(Path path) throws IOException {
		SimpleJsonWriter.asFullResults(searchResults, path);
	}
}
