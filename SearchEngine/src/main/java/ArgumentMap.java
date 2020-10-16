import java.nio.file.Path;
import java.util.HashMap;

/**
 * Parses and stores command-line arguments into simple key = value pairs.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class ArgumentMap {

	/**
	 * Stores command-line arguments in key = value pairs.
	 */
	private final HashMap<String, String> map;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() {
		map = new HashMap<>();
	}

	/**
	 * Initializes this argument map and then parsers the arguments into flag/value
	 * pairs where possible. Some flags may not have associated values. If a flag is
	 * repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public ArgumentMap(String[] args) {
		this();
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may not
	 * have associated values. If a flag is repeated, its value is overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {

		if (args.length > 0) { // if argument is not null
			for (int i = 0; i < args.length; i++) { // iterate through command line arguments
				if (isFlag(args[i])) { // if argument is a flag
					if (map.containsKey(args[i]) == false) { // if key isn't in the hash map yet

					}
					map.put(args[i], null); // set flag as a key
				}
				// if value is after key, add it
				if (i >= 1 && isValue(args[i]) && isFlag(args[i - 1]) && map.get(args[i - 1]) == null) {
					map.put(args[i - 1], args[i]);
				}
			}
		}
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-digit character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 * @see String#charAt(int)
	 * @see Character#isDigit(char)
	 */
	public static boolean isFlag(String arg) {

		return (arg != null) && (arg.length() >= 2) && (arg.charAt(0) == '-') && (!Character.isDigit(arg.charAt(1)));

	}

	/**
	 * Determines whether the argument is a value. Anything that is not a flag is
	 * considered a value.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#length()
	 */
	public static boolean isValue(String arg) {

		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {

		return map.size();

	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {

		return (map.containsKey(flag));

	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {

		return (map.get(flag) != null);

	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or null if there is no mapping.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping
	 */
	public String getString(String flag) {

		return (map.get(flag));

	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link String},
	 * or the default value if there is no mapping.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping
	 * @return the value to which the specified flag is mapped, or the default value
	 *         if there is no mapping
	 */
	public String getString(String flag, String defaultValue) {

		if (map.get(flag) == null) {
			return defaultValue;
		} else {
			return map.get(flag);
		}
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path}, or
	 * {@code null} if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping
	 *
	 * @see Path#of(String, String...)
	 */
	public Path getPath(String flag) {

		if (hasValue(flag)) {
			return Path.of(getString(flag));
		} else {
			return null;
		}
	}

	/**
	 * Returns the value the specified flag is mapped as a {@link Path}, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to a {@link Path} or if no value exists).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a {@link Path}, or the
	 *         default value if there is no valid mapping
	 */
	public Path getPath(String flag, Path defaultValue) {
		if (hasValue(flag)) {
			return Path.of(getString(flag));
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the value the specified flag is mapped as an integer value, or the
	 * default value if unable to retrieve this mapping (including being unable to
	 * convert the value to an integer or if no value exists).
	 *
	 * @param flag         the flag whose associated value will be returned
	 * @param defaultValue the default value to return if there is no valid mapping
	 * @return the value the specified flag is mapped as a integer, or the default
	 *         value if there is no valid mapping
	 */
	public int getInteger(String flag, int defaultValue) {

		if (map.get(flag) != null) {
			try {
				return Integer.parseInt(map.get(flag));
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
