import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {

		int size = elements.size();
		int counter = 0;

		writer.write("[\n");

		for (Integer i : elements) {

			indent(i, writer, level + 1);

			if (counter != size - 1) { // if not last element, place a comma
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
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

		int size = map.size();
		int counter = 0;

		writer.write("{\n");

		for (String i : map.keySet()) {

			indent(i, writer, level + 1);
			writer.write(": " + map.get(i));

			if (counter != size - 1) { // if not last element, place a comma
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * Writes the elements as a pretty JSON object with a nested array. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException if an IO error occurs
	 */
	public static void asNestedArray(Map<Path, List<Integer>> elements, Writer writer, int level) throws IOException {

		int size = elements.size();
		int counter = 0;

		writer.write("{\n");
		for (Path i : elements.keySet()) { // iterate through path keys of nested array
			indent(i, writer, level + 1); // print "key"/non-nested array element

			writer.write(": ");
			asArray(elements.get(i), writer, level + 2);// write out the integers of path
			if (counter != size - 1) {
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
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
	public static void asDoubleNestedArray(Map<String, TreeMap<Path, List<Integer>>> elements, Writer writer, int level)
			throws IOException {
		int size = elements.size();
		int counter = 0;

		writer.write("{\n");
		for (String i : elements.keySet()) { // iterate through the keys of nested array
			indent(i, writer, level + 1); // print "key"/non-nested array element

			writer.write(": ");
			asNestedArray(elements.get(i), writer, level + 1);// write out the content of the nested array
			if (counter != size - 1) {
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
		writer.write("}");
	}

	/**
	 * @param map    the map to write
	 * @param writer the writer to use
	 * @param level  the indent level
	 * @throws IOException if encounter IO error
	 */
	public static void asResultMap(HashMap<String, Object> map, Writer writer, int level) throws IOException {

		int size = map.size();
		int counter = 0;

		writer.write("{\n");

		for (String i : map.keySet()) {

			indent(i, writer, level + 1);

			if (i.equalsIgnoreCase("score")) { // if it's the score, format appropriately
				DecimalFormat FORMATTER = new DecimalFormat("0.00000000");
				writer.write(": " + FORMATTER.format(map.get(i)));
			} else {
				writer.write(": " + map.get(i));
			}

			if (counter != size - 1) { // if not last element, place a comma
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
		indent(writer, level);
		writer.write("}");
	}

	/**
	 * @param elements list of hash maps to format
	 * @param writer   writer to use
	 * @param level    indent level
	 * @throws IOException if error
	 */
	public static void asArrayOfResultMap(ArrayList<HashMap<String, Object>> elements, Writer writer, int level)
			throws IOException {

		int size = elements.size();
		int counter = 0;

		writer.write("[\n");

		for (HashMap<String, Object> i : elements) { // for every hashmap

			// indent(i, writer, level + 1); //string
			asResultMap(i, writer, level + 1);

			if (counter != size - 1) { // if not last element, place a comma
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
		indent(writer, level);
		writer.write("]");

	}

	/**
	 * @param elements elements to format
	 * @param writer   writer to use
	 * @param level    indentation level
	 * @throws IOException if IO error occurs
	 */
	public static void asFullResults(TreeMap<String, ArrayList<HashMap<String, Object>>> elements, Writer writer,
			int level) throws IOException {
		int size = elements.size();
		int counter = 0;

		writer.write("{\n");
		for (String i : elements.keySet()) { // iterate through the keys of nested array
			indent(i, writer, level + 1); // print "key"/non-nested array element

			writer.write(": ");
			asArrayOfResultMap(elements.get(i), writer, level + 1);// write out the content of the nested array
			if (counter != size - 1) {
				writer.write(",");
			}
			writer.write("\n");
			counter++;
		}
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

	/*
	 * These methods are provided for you. No changes are required.
	 */

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Integer> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
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
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(Map<Path, List<Integer>> elements) {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
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
	 * @see #asDoubleNestedArray(Map, Writer, int)
	 */
	public static void asDoubleNestedArray(Map<String, TreeMap<Path, List<Integer>>> elements, Path path)
			throws IOException {
		// THIS CODE IS PROVIDED FOR YOU; DO NOT MODIFY
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asDoubleNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a nested pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asDoubleNestedArray(Map<String, TreeMap<Path, List<Integer>>> elements) {

		try {
			StringWriter writer = new StringWriter();
			asDoubleNestedArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}

	}

	/**
	 * Writes the search result elements as a pretty JSON object to file.
	 * 
	 * @param elements the elements to format
	 * @param path the file to write to 
	 * @throws IOException if IO error occurs
	 */
	public static void asFullResults(TreeMap<String, ArrayList<HashMap<String, Object>>> elements, Path path)
			throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asFullResults(elements, writer, 0);
		}
	}

	public static String asFullResults(TreeMap<String, ArrayList<HashMap<String, Object>>> elements) {

		try {
			StringWriter writer = new StringWriter();
			asFullResults(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}

	}

}
