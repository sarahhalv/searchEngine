import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class SimpleJsonWriter {

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asCollection(Collection<Integer> elements, Writer writer, int level) throws IOException {

		java.util.Iterator<Integer> iterator = elements.iterator();
		writer.write("[");

		if (iterator.hasNext()) {
			writer.write("\n\t");
			indent((iterator.next()), writer, level + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n\t");
			indent((iterator.next()), writer, level + 1);
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("]");
	}

	/**
	 * @param map    the map to write
	 * @param writer the writer to use
	 * @param level  the initial indent to use
	 * @throws IOException if an IO error occurs
	 */
	public static void asMap(TreeMap<String, Integer> map, Writer writer, int level) throws IOException {

		Iterator<String> iterator = map.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writer.write("\n\t");
			var i = iterator.next();
			indent(i, writer, level + 1);
			writer.write(": " + map.get(i));
		}

		while (iterator.hasNext()) {
			writer.write(",\n\t");
			var i = iterator.next();
			indent(i, writer, level + 1);
			writer.write(": " + map.get(i));
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("}");

	}

	/**
	 * Writes the elements as a pretty JSON object with a set. The generic notation
	 * used allows this method to be used for any type of map with any type of
	 * nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedSet(Map<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {

		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writer.write("\n\t");
			var i = iterator.next();
			indent(i, writer, level + 1);
			writer.write(": ");
			asCollection(elements.get(i), writer, level + 2);// write out the integers of path
		}

		while (iterator.hasNext()) {
			writer.write(",\n\t");
			var i = iterator.next();
			indent(i, writer, level + 1);
			writer.write(": ");
			asCollection(elements.get(i), writer, level + 2);// write out the integers of path
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * * Writes the elements as a pretty JSON object with a double nested map then
	 * array. The generic notation used allows this method to be used for any type
	 * of map with any type of nested collection of integer objects.
	 * 
	 * @param elements the elements to print
	 * @param writer   the writer to use
	 * @param level    initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asDoubleNestedStructure(Map<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {

		java.util.Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writer.write("\n\t");
			var i = iterator.next();
			indent(i, writer, level + 1);
			writer.write(": ");
			asNestedSet(elements.get(i), writer, level + 2);// write out the integers of path
		}

		while (iterator.hasNext()) {
			writer.write(",\n\t");
			var i = iterator.next();
			indent(i, writer, level + 1);
			writer.write(": ");
			asNestedSet(elements.get(i), writer, level + 2);// write out the integers of path
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * @param elements elements to format
	 * @param writer   writer to use
	 * @param level    indentation level
	 * @throws IOException if IO error occurs
	 */
	public static void asFullResults(TreeMap<String, List<InvertedIndex.SearchResult>> elements, Writer writer,
			int level) throws IOException {
		
		Iterator<String> iterator = elements.keySet().iterator();
		writer.write("{");

		if (iterator.hasNext()) {
			writer.write("\n\t");
			var i = iterator.next();
			indent(i.replaceAll("[\\[\\]\\,]", ""), writer, level + 1); // print "key"/non-nested array element
			writer.write(": ");
			asObjectList(elements.get(i), writer, level + 1);// write out the content of the nested array
		}

		while (iterator.hasNext()) {
			writer.write(",\n\t");
			var i = iterator.next();
			indent(i.replaceAll("[\\[\\]\\,]", ""), writer, level + 1); // print "key"/non-nested array element
			writer.write(": ");
			asObjectList(elements.get(i), writer, level + 1);// write out the content of the nested array
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * @param elements the search result objects to format
	 * @param writer   the writer to use
	 * @param level    the indentation level
	 * @throws IOException if encounter IO error
	 */
	public static void asObjectList(List<InvertedIndex.SearchResult> elements, Writer writer, int level)
			throws IOException {
		// for each result in list
		Iterator<InvertedIndex.SearchResult> iterator = elements.iterator();
		writer.write("[");

		if (iterator.hasNext()) {
			writer.write("\n\t");
			asObject(iterator.next(), writer, level + 1);
		}

		while (iterator.hasNext()) {
			writer.write(",\n\t");
			asObject(iterator.next(), writer, level + 1);
		}

		writer.write("\n");
		indent(writer, level);
		writer.write("]");

	}

	/**
	 * @param i      the search result object to format
	 * @param writer the writer to use
	 * @param level  the indentation level
	 * @throws IOException if encounter IO error
	 */
	private static void asObject(InvertedIndex.SearchResult i, Writer writer, int level) throws IOException {

		indent(writer, level);
		writer.write("{\n");

		indent(writer, level + 1);
		writer.write("\"where\": " + "\"" + i.where.toString() + "\",\n");
		indent(writer, level + 1);
		writer.write("\"count\": " + i.count + ",\n");
		indent(writer, level + 1);
		DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
		writer.write("\"score\": " + FORMATTER.format(i.score) + "\n");

		indent(writer, level);
		writer.write("}");

	}

	/**
	 * Indents using a tab character by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException if an IO error occurs
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the integer element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element.toString());
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(Path element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		writer.write((element.normalize()).toString());
		writer.write('"');
	}

	/**
	 * Indents and then writes the text element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException if an IO error occurs
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Writes a map entry in pretty JSON format.
	 *
	 * @param entry  the nested entry to write
	 * @param writer the writer to use
	 * @param level  the initial indentation level
	 * @throws IOException if an IO error occurs
	 */
	public static void writeEntry(Entry<String, Integer> entry, Writer writer, int level) throws IOException {
		writer.write('\n');
		indent(entry.getKey(), writer, level);
		writer.write(": ");
		writer.write(entry.getValue().toString());
	}

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asCollection(Collection, Writer, int)
	 */
	public static void asCollection(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asCollection(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asCollection(Collection, Writer, int)
	 */
	public static String asCollection(Collection<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asCollection(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON map. to an output file
	 *
	 * @param map  the map to return in JSON
	 * @param path the file path to use
	 * @throws IOException if IO error occurs
	 *
	 */
	public static void asMap(TreeMap<String, Integer> map, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asMap(map, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedSet(Map, Writer, int)
	 */
	public static String asNestedSet(Map<String, TreeSet<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedSet(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the elements as a double nested pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asDoubleNestedStructure(Map, Writer, int)
	 */
	public static void asDoubleNestedStructure(Map<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNestedStructure(elements, writer, 0);
		}
	}

	/**
	 * Writes the search result elements as a pretty JSON object to file.
	 * 
	 * @param elements the elements to format
	 * @param path     the file to write to
	 * @throws IOException if IO error occurs
	 */
	public static void asFullResults(TreeMap<String, List<InvertedIndex.SearchResult>> elements, Path path)
			throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asFullResults(elements, writer, 0);
		}
	}

	/**
	 * @param elements results to format
	 * @return the results as a JSON string
	 */
	public static String asFullResults(TreeMap<String, List<InvertedIndex.SearchResult>> elements) {

		try {
			StringWriter writer = new StringWriter();
			asFullResults(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}

	}

}
