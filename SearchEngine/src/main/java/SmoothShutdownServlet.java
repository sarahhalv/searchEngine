import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import javax.servlet.http.HttpServlet;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

/**
 * servlet class to handle graceful shutdown feature
 * 
 * @author sarah
 */
public class SmoothShutdownServlet extends HttpServlet {

	/** Class version for serialization, in [YEAR][TERM] format (unused). */
	private static final long serialVersionUID = 202040;

	/** The title to use for this webpage. */
	private static final String TITLE = "SURCH";

	/** The logger to use for this servlet. */
	private static Logger log = Log.getRootLogger();

	/** Template for HTML. **/
	private final String htmlTemplate;

	/** storing last inputted queries for partial search toggle **/
	private Set<String> oldQueries;

	/**
	 * Initializes this servlet to be a web page displaying the locations of the
	 * index
	 * 
	 * @param index the index to use/search
	 * @throws IOException if unable to read template
	 */
	public SmoothShutdownServlet() throws IOException {
		super();
		htmlTemplate = Files.readString(Path.of("html", "entireIndex.html"), StandardCharsets.UTF_8);
	}

}
