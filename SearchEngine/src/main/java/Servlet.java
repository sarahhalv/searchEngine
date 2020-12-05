import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
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
	private static final String TITLE = "SearchEngine";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();

	/** The thread-safe data structure to use for storing messages. */
	private final ConcurrentLinkedQueue<String> messages;

	/** Template for HTML. **/
	private final String htmlTemplate;

	/**
	 * index to use
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Initializes this index page
	 * 
	 * @param index the index to use/search
	 * @throws IOException if unable to read template
	 */
	public Servlet(ThreadSafeInvertedIndex index) throws IOException {
		super();
		messages = new ConcurrentLinkedQueue<>();
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
		values.put("messages", String.join("\n\n", messages));

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

		String username = request.getParameter("name");
		String query = request.getParameter("query");

		username = username == null ? "anonymous" : username;
		query = query == null ? "" : query;

		// avoid xss attacks using apache commons text
		username = StringEscapeUtils.escapeHtml4(username);
		query = StringEscapeUtils.escapeHtml4(query);

		// separate into queries and partial search the index
		Set<String> queries = TextFileStemmer.uniqueStems(query);
		List<InvertedIndex.SearchResult> results = index.partialSearch(queries);

		// outputting index to html
		for (InvertedIndex.SearchResult result : results) {
			String formatted = String.format("<br><li><a href=%s>%s</a></li>", result.getWhere(), result.getWhere());
			// keep in mind multiple threads may access at once
			// but we are using a thread-safe data structure here to avoid any issues
			messages.add(formatted);
		}

		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect(request.getServletPath());
	}

	/**
	 * Returns the date and time in a long format. For example: "12:00 am on
	 * Saturday, January 01 2000".
	 *
	 * @return current date and time
	 */
	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}
}
