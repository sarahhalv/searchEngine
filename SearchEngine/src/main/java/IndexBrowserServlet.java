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
 * Servlet to allow users to browse your entire inverted index as an HTML page
 * with clickable links to all of the indexed URLs.
 * 
 * @author sarah
 *
 */
public class IndexBrowserServlet extends HttpServlet {

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

	/** The thread-safe data structure to use for storing messages. */
	private final List<String> indexForHTML;

	/**
	 * Initializes this servlet to be a web page displaying the locations of the
	 * index
	 * 
	 * @param index the index to use/search
	 * @throws IOException if unable to read template
	 */
	public IndexBrowserServlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		indexForHTML = new ArrayList<>();
		htmlTemplate = Files.readString(Path.of("html", "entireIndex.html"), StandardCharsets.UTF_8);
		this.index = index;
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
		outputAsHTML();
		// multiple threads may access this at once!
		values.put("index", String.join("\n", indexForHTML));

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
	 * adds the inverted index as html with clickable location links to the html string list
	 * value to output
	 */
	protected void outputAsHTML() {
		synchronized (indexForHTML) {
			// for stem words
			for (String stem : index.getWords()) {
				// get inner mapping
				List<String> innerList = new ArrayList<>();
				for (String location : index.getLocations(stem)) {
					String innerFormatted = String.format("<br><li><a href=%s>%s</a>%s</li>", location, location,
							": " + index.getPositions(stem, location).toString());
					innerList.add(innerFormatted);
				}
				String formatted = String.format("<br><li><b>%s</b><ul>%s</ul></li>", stem, String.join("", innerList));
				indexForHTML.add(formatted);
			}
		}
	}

}
