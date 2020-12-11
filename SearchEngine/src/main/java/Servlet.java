import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * servlet to display HTML form with a text box and a button on the search
 * engine web page.
 * 
 * @author sarah
 */
public class Servlet extends HttpServlet {

	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The title to use for this webpage. */
	private static final String TITLE = "SURCH";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();

	/** The thread-safe data structure to use for storing messages. */
	private final List<String> searchResults;

	/** Template for HTML. **/
	private final String htmlTemplate;

	/** storing last inputted queries for partial search toggle **/
	private Set<String> oldQueries;

	/** index to use **/
	private final ThreadSafeInvertedIndex index;

	/**
	 * Initializes this servlet to be a web page
	 * 
	 * @param index the index to use/search
	 * @throws IOException if unable to read template
	 */
	public Servlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		searchResults = new ArrayList<>();
		htmlTemplate = Files.readString(Path.of("html", "index.html"), StandardCharsets.UTF_8);
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

		// setup form
		values.put("method", "POST");
		values.put("action", request.getServletPath());

		// compile all of the messages together
		// keep in mind multiple threads may access this at once!
		synchronized (values) {
			values.put("searchResults", String.join("\n", searchResults));
		}

		// generate html from template
		StringSubstitutor replacer = new StringSubstitutor(values);
		String html = replacer.replace(htmlTemplate);

		// output generated html
		PrintWriter out = response.getWriter();
		out.println(html);
		out.flush();

		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		String query = request.getParameter("query");
		String partialTogglePartial = request.getParameter("partialTogglePartial");
		String partialToggleExact = request.getParameter("partialToggleExact");
		String reverse = request.getParameter("reverse");
		String lucky = request.getParameter("lucky");

		// if reverse order was pressed, reverse list and send back to doPost
		if (reverse != null) {
			Collections.reverse(searchResults);

		} else if (partialTogglePartial != null || partialToggleExact != null) {
			// toggle between partial and exact search depending on which option pressed
			if (partialTogglePartial != null) {
				handlePartialToggle(true); // partial results
			} else {
				handlePartialToggle(false); // exact results
			}

		} else {
			query = query == null ? "" : query;

			// avoid xss attacks using apache commons text
			query = StringEscapeUtils.escapeHtml4(query);

			// separate into queries and partial search the index
			Set<String> queries = TextFileStemmer.uniqueStems(query);
			// store queries/search data incase need
			oldQueries = queries;

			List<InvertedIndex.SearchResult> results = index.partialSearch(queries);

			// if i'm feeling lucky instead of result list
			if (lucky != null) {
				response.sendRedirect(results.get(0).getWhere());
			} else {
				// make sure get fresh results
				synchronized (searchResults) {
					searchResults.clear();
				}
				// outputting search results to html
				outputToHTML(results);
			}
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * adds the search results as clickable html links to the results list for the
	 * web page output
	 * 
	 * @param results the results to display
	 */
	protected void outputToHTML(List<InvertedIndex.SearchResult> results) {
		for (InvertedIndex.SearchResult result : results) {
			String formatted = String.format("<br><li><a href=%s>%s</a></li>", result.getWhere(), result.getWhere());
			synchronized (searchResults) {
				searchResults.add(formatted);
			}
		}
	}

	/**
	 * performs the actions of toggling between partial and exact results, depending
	 * on which type of results was last returned
	 * 
	 * @param partial if the toggle was on partial button or not (exact otherwise)
	 */
	protected void handlePartialToggle(Boolean partial) {
		List<InvertedIndex.SearchResult> results;
		synchronized (searchResults) {
			searchResults.clear();
		}
		if (partial == true) {
			// partial search
			results = index.partialSearch(oldQueries);
		} else {
			// exact search
			results = index.exactSearch(oldQueries);
		}
		// outputting search results to html
		outputToHTML(results);
	}

}
