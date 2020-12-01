
/**
 * Class that deals with the webcrawling aspects of building the inverted index
 * from a seed URL.
 * 
 * @author sarah
 */
public class WebCrawler {
	
	
	//html fetcher to download html
	//parts of html cleaner to remove block elements from that html
	//link parser to parse out all of the remaining links
	//html cleaner again to strip out the remaining HTML
	//then textfile stemmer might have method you could use for stemming the remaining text into words and adding to your inverted index.

	
	/**for each normalized unique URL that must be crawled: 
	//Each worker thread should be responsible for parsing a single link.
	
	//Remove any HTML block elements that should not be considered for parsing links, 
	//including the head, style, script, noscript, and svg elements. 
	
	//Parse all of the URLs remaining on the page and add to the queue of URLs to process as appropriate. 
	//(You must do this before you remove the other HTML tags.)
	
	//Remove all of the remaining HTML tags and entities.
	
	//Clean, parse, and stem the resulting text to populate the inverted index 
	//in the same way plain text files were handled in previous projects.
	*/
}
