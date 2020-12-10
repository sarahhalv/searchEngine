import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * Servlet to allow users to browse all of the locations and their word counts
 * stored by your inverted index as an HTML page with clickable links to all of
 * the indexed URLs.
 * 
 * @author sarah
 *
 */
public class LocationBrowserServlet extends HttpServlet {
	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The title to use for this webpage. */
	private static final String TITLE = "SURCH";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();

	/** Template for HTML. **/
	private final String htmlTemplate;

	/** index to use **/
	private final ThreadSafeInvertedIndex index;

	/** The thread-safe data structure to use for the locations and their counts */
	private final List<String> locations;

	/**
	 * Initializes this servlet to be a web page displaying the locations of the
	 * index
	 * 
	 * @param index the index to use/search
	 * @throws IOException if unable to read template
	 */
	public LocationBrowserServlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		htmlTemplate = Files.readString(Path.of("html", "locations.html"), StandardCharsets.UTF_8);
		this.index = index;
		locations = new ArrayList<>();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		log.info("MessageServlet ID " + this.hashCode() + " handling GET request.");

		// used to substitute values in our templates
		Map<String, String> values = new HashMap<>();
		values.put("title", TITLE);
		values.put("thread", Thread.currentThread().getName());

		// grab locations and the number of words they contain
		grabLocations();
		// keep in mind multiple threads may access this at once!
		values.put("locations", String.join("\n", locations));

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * puts all of the locations and their wordcount into an html list to output
	 */
	protected void grabLocations() {
		Map<String, Integer> countMap = index.returnCountMap();
		synchronized (locations) {
			for (String location : countMap.keySet()) {
				String formatted = String.format("<br><li><a href=%s>%s</a>%s</li>", location, location,
						": " + countMap.get(location).toString() + " words");
				locations.add(formatted);
			}
		}
	}

}
