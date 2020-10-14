import java.nio.file.Path;
import java.util.HashMap;
// import java.util.Map; // TODO Clean up commented out code

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
	 * public counter for unique flags
	 */
	int uniqueFlags = 0; // TODO Remove

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
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
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
					if (map.containsKey(args[i]) == false) { // if key isn't in the hashmap yet
						uniqueFlags++;
					}
					map.put(args[i], null); // set flag as a key
				}
				if (i >= 1 && isValue(args[i]) && isFlag(args[i - 1]) && map.get(args[i - 1]) == null) { // if value
																											// after
																											// key, add
																											// it
					map.put(args[i - 1], args[i]);
				}
			}
			
			// TODO This makes ArgumentMap less general, remove
			// check if -index flag is there without a value
			if (map.containsKey("-index") && map.get("-index") == null) {
				map.put("-index", "index.json"); // add default value
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

		if (arg == null) {
			return false;
		}
		if (arg.length() >= 2) {
			if (arg.charAt(0) == '-') { // if starts with dash
				if (!Character.isDigit(arg.charAt(1))) { // if followed by number
					return true; // is a flag
				}
			}
		}
		return false;
		
		// TODO return (arg != null) && (arg.length() >= 2) && (arg.charAt(0) == '-') && (!Character.isDigit(arg.charAt(1)));
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
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return !isFlag(arg);
	}

	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		// TODO return map.size();
		return uniqueFlags;
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag find
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {

		if (map.containsKey(flag)) { // TODO Simplify to a single return
			return true;
		}
		return false;
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to find
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) { // TODO Simplify to a single return

		if (map.get(flag) != null) { // if that flag/key has a value thats not null...
			return true;
		}
		return false;
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

		if (hasValue(flag)) {
			return (map.get(flag)); // TODO Only line need
		}
		return null;
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
		// DO NOT MODIFY; THIS METHOD IS PROVIDED
		return this.map.toString();
	}
}
