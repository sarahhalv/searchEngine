import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;


/**
 * class that dealsw with query file and produces search results
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
	TreeMap<String, List<InvertedIndex.SearchResult>> searchResults = new TreeMap<String, List<InvertedIndex.SearchResult>>();
	
	/**
	 * search result object to use
	 */
	InvertedIndex.SearchResult searchResult1 = index.new SearchResult();
	
	/**
	 * query parser constructor
	 * 
	 * @param index the index to use for this class
	 */
	public QueryParser(InvertedIndex index) {
		this.index = index;
	}
	
	
	/**
	 * @param path the path to the query file
	 * @param exact whether to perform exact or partial search
	 * @return map of search results 
	 */
	public void parseQueryFile(Path path, boolean exact){
//		if(exact) {
//			index.completeExactSearch(searchResult1.getAllFiles(path));
//		}else {
//			index.completePartialSearch(searchResult1.getAllFiles(path));
//		}
		
		
//		open up the query file
//		stem the lines
//		ask for search results
	}
	
	/**
	 * outputs the searchResults map to file
	 * 
	 * @param path the file to output/write results to
	 * @throws IOException  if IO error encountered
	 */
	public void writeJson(Path path) throws IOException {
			SimpleJsonWriter.asFullResults(searchResults, path);
		
	}
}
