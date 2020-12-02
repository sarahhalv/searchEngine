import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that deals with the webcrawling aspects of building the inverted index
 * from a seed URL.
 * 
 * @author sarah
 */
public class WebCrawler {

	/**
	 * urls that have already been parsed
	 */
	private Set<URL> parsedURLs = new HashSet<>();
	/**
	 * work queue to use for dealing with urls/links and building
	 */
	private final WorkQueue workQueue;

	/**
	 * total number of URLs to crawl
	 */
	private int total;

	/**
	 * the index to populate/build on
	 */
	ThreadSafeInvertedIndex safeIndex;

	/**
	 * counter for how many URLS have been crawled
	 */
	private int urlsCrawled = 0;

	/**
	 * web crawler class constructor
	 * 
	 * @param workQueue the work queue to use
	 * @param total     the total number of URLs to crawl
	 * @param safeIndex the index to populate/build on
	 */
	public WebCrawler(WorkQueue workQueue, int total, ThreadSafeInvertedIndex safeIndex) {
		this.workQueue = workQueue;
		this.total = total;
		this.safeIndex = safeIndex;
	}

	/**
	 * begins the web crawl starting with the seed URL
	 * 
	 * @param seed the initial URL to begin from
	 */
	public void crawl(URL seed) {
		// should really only add a task to your work queue (and maybe track the URL
		// being parsed)
		parsedURLs.add(seed); // marked as parsed
		workQueue.execute(new Task(seed));
	}

	/**
	 * The non-static task class (runnable interclass with run method)
	 */
	private class Task implements Runnable {
		// Only the task should download the HTML, parse the links, and build the index.

		/**
		 * the url link to parse
		 */
		private URL url;

		/**
		 * Initializes this task.
		 * 
		 * @param url the url to crawl
		 */
		public Task(URL url) {
			this.url = url;
			// log.debug("Task for {} created.", link);
		}

		/*
		 * run function for the task (performs the search on each query line)
		 */
		@Override
		public void run() {
			System.out.println("inside run rn: url is: " + url);
			// log.debug("starting to run webcrawler task of: " + url);
			// download the html
			String html = HtmlFetcher.fetch(url, 3);
			System.out.println("after fetch: " + html);

			// Remove any HTML block elements that should not be considered for parsing
			// links
			html = HtmlCleaner.stripBlockElements(html);
			System.out.println("after html cleaner :" + html);

			// parse the links (unique links that havent been crawled and if below max)
			ArrayList<URL> links = LinkParser.getValidLinks(url, html);
			System.out.println("valid links grabbed :" + links.toString());
			
			synchronized (parsedURLs) {
				for (URL link : links) {

					if (parsedURLs.size() >= total) {
						// total met. done parsing
						System.out.println("total met so returning");
						break;
					} else if (!parsedURLs.contains(link)) {
						// add new task to queue & url to used
						parsedURLs.add(link);
						workQueue.execute(new Task(link));
						System.out.println("new task just created");
					}
				}
			}
			
			// Remove all of the remaining HTML tags and entities.
			html = HtmlCleaner.stripTags(html);
			html = HtmlCleaner.stripEntities(html);
			System.out.println("stripped html of tags and entities: " + html);

			// Clean, parse, and stem the resulting text to
			// populate the inverted index in the same way plain text files were handled in
			// previous projects.
			InvertedIndex local = new InvertedIndex();
			ArrayList<String> stems = TextFileStemmer.listStems(html);
			int location = 1;
			for (String stem : stems) {
				local.add(stem, url.toString(), location);
				location++;
			}
			//System.out.println("local index : " + local.toString());
			// merge the shared data with the local data
			safeIndex.addAll(local);
			//System.out.println("safe index: " + safeIndex.toString());
			// log.debug("finished running webcrawler task of:" + url);
		}
	}
}