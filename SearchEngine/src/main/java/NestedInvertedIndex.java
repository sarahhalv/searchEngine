import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * @author sarah Class that works with and creates the nested Inverted Index
 *
 */
public class NestedInvertedIndex {

	/**
	 * the inverted index
	 */
	TreeMap<String, TreeMap<Path, List<Integer>>> invertedIndex = new TreeMap<String, TreeMap<Path, List<Integer>>>();
	/**
	 * map that records how many words in a textfile
	 */
	TreeMap<String, Integer> countMap = new TreeMap<String, Integer>();

	/**
	 * creates the nested inverted index
	 *
	 * @param p   the path with which to create the index
	 * @param map the argument map containing path and index flags and values
	 * @return the created inverted index
	 * 
	 */

	public TreeMap<String, TreeMap<Path, List<Integer>>> createNestedInvertedIndex(Path p, ArgumentMap map) {
		TextFileFinder textFileFinder1 = new TextFileFinder();
		TextFileIndex textFileIndex1 = new TextFileIndex();

		// traverse a directory and return a list of all the text files found within
		// that directory (textfilefinder)
		// boolean isDir = false;
		List<Path> textfiles = new ArrayList<>();
		if (Files.isDirectory(map.getPath("-path"))) { // if path is directory
			// find and process all of the text files (with .txt and .text extensions) in
			// that directory and its subdirectories.
			// isDir = true;
			try {
				textfiles = textFileFinder1.list(map.getPath("-path"));
			} catch (IOException e) {
				// System.out.println("unable to create array of gathered textfiles");
			}
		} else { // if single file, add it
			if (map.getPath("-path") != null) {
				textfiles.add(map.getPath("-path"));
			}
		}

		for (Path yee : textfiles) { // iterate through the files
			int i = 0; // number of words in file
			ArrayList<String> stems1 = new ArrayList<>();
			try {
				stems1 = TextFileStemmer.listStems(yee);
			} catch (IOException e) {
				// System.out.println("unable to gather all of the stems of textfile: " +
				// yee.toString());
			}

			int index1 = 1;
			for (String stemmies : stems1) {
				// add this information into inverted index but now have to implement text file
				// index
				textFileIndex1.add(stemmies, yee, index1);
				index1++;
				i++; // increment how many words in file
			}
			// if there's a count at input file and its number of words into word count map
			if (i != 0) {
				countMap.putIfAbsent(yee.toString(), i);
			}
			// System.out.println(yee.toString() + " -- word count of: " + i);
		}
		invertedIndex = textFileIndex1.returnIndex();
		return invertedIndex;
	}

	/**
	 * @param word the word from which to grab files
	 * @return list of files where word is located
	 */
	public Set<Path> fileGetter(String word) {

		if (invertedIndex.get(word) != null) {
			if (invertedIndex.get(word).keySet() != null) {
				return invertedIndex.get(word).keySet();
			}
		}
		return null;
	}

	/**
	 * @param word the word from which to grab files for partial search
	 * @return set of files where word is located
	 */
	public Set<Path> partialFileGetter(String word) {
		Set<Path> files = new TreeSet<Path>();
		for (String i : invertedIndex.keySet()) {
			if (i.startsWith(word)) { // if word in index begins with query word
				if (invertedIndex.get(i) != null) {
					if (invertedIndex.get(i).keySet() != null) {
						for (Path p : invertedIndex.get(i).keySet()) {
							files.add(p); // add every file to set
						}
					}
				}
			}
		}
		return files;
	}

	/**
	 * @param word the word to get count for in exact search
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	public int wordGetter(String word, Path file) {
		if (invertedIndex.get(word) == null) {
			return 0;
		}
		if (invertedIndex.get(word).get(file) == null) {
			return 0;
		}
		return invertedIndex.get(word).get(file).size(); // return how many times in file for this particular word
	}

	/**
	 * @param word the word to get count for in partial search
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	public int partialWordGetter(String word, Path file) {
		int matches = 0;
		try {
			for (String stem : TextFileStemmer.listStems(file)) { // for every stem in text file
				if (stem.startsWith(word)) {
					matches++;
				}
			}
		} catch (IOException e) {
			System.out.println("unable to list stems with textfilestemmer");
		}
		return matches;
	}

	/**
	 * @param filename the file which to count the words
	 * @return the number of words in the passed in file
	 */
	public int wordCountGetter(String filename) {
		return countMap.get(filename);
	}

	/**
	 * @return the countMap created alongside the inverted index
	 */
	public TreeMap<String, Integer> returnCountMap() {
		return countMap;
	}

	/**
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of EXACT search results
	 */
	public List<SearchResult> exactSearch(TreeSet<String> words) {
		List<SearchResult> results = new ArrayList<>();
		ArrayList<String> parsedWords = new ArrayList<String>(words);
		ArrayList<Path> usedFiles = new ArrayList<Path>();

		for (String i : parsedWords) {
			if (fileGetter(i) != null) {
				// System.out.println("filegetter not null, going IN");
				for (Path s : fileGetter(i)) {
					// System.out.println("inside file -- " + s.toString() + " -- now");
					// if file is not already been used
					if (!usedFiles.contains(s)) {
						usedFiles.add(s);
						// System.out.println("USING A FILE THAT HASNT BEEN USED ---" + s.toString());
						SearchResult nextResult = new SearchResult();
						nextResult.where = s;
						int count1 = 0;
						for (int x = 0; x < parsedWords.size(); x++) {
							count1 += wordGetter(parsedWords.get(x), s);
						}
						nextResult.count = count1;
						nextResult.score = ((double) count1 / (double) (wordCountGetter(s.toString())));

						results.add(nextResult);
						// System.out.println("*************");
						// System.out.println("search result being added to list: " +
						// nextResult.toString() + "SCORE IS =" + nextResult.score);
						// System.out.println("*****************");
					}

				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * methods performs all processes necessary for exact search
	 * 
	 * @param p the text file of queries to be used for search
	 * @return full EXACT search results
	 */
	public TreeMap<String, List<SearchResult>> completeExactSearch(List<Path> p) {
		TreeMap<String, List<SearchResult>> fullExactResults = new TreeMap<String, List<SearchResult>>();
		// parse query file by line
		for (Path file : p) { // loop through all files
			try (BufferedReader buff = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
				String line;
				while ((line = buff.readLine()) != null) { // while still lines in query file, parse

					if (TextFileStemmer.uniqueStems(line) != null && TextFileStemmer.uniqueStems(line).size() != 0) {
						// System.out.println();
						// System.out.println("calling exact search in complete exact search using line:
						// "
						// + (TextFileStemmer.uniqueStems(line)).toString());
						// System.out.println();
						fullExactResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
								exactSearch(TextFileStemmer.uniqueStems(line)));
					}
				}
			} catch (IOException e) {
				System.out.println("no file found or buffered reader unable to work with file");
			}
		}

		return fullExactResults;
	}

	/**
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of PARTIAL search results
	 * 
	 */
	public List<SearchResult> partialSearch(TreeSet<String> words) {
		List<SearchResult> results = new ArrayList<>();
		ArrayList<String> parsedWords = new ArrayList<String>(words);
		ArrayList<Path> usedFiles = new ArrayList<Path>();

		for (String i : parsedWords) {
			if (partialFileGetter(i) != null) {
				// System.out.println("filegetter not null, going IN");
				for (Path s : partialFileGetter(i)) {
					
					// if file is not already been used
					if (!usedFiles.contains(s)) {
						usedFiles.add(s);
						// System.out.println("USING A FILE THAT HASNT BEEN USED ---" + s.toString());
						SearchResult nextResult = new SearchResult();
						nextResult.where = s;
						int count1 = 0;
						for (int x = 0; x < parsedWords.size(); x++) {
							count1 += partialWordGetter(parsedWords.get(x), s);
						}
						nextResult.count = count1;
						nextResult.score = ((double) count1 / (double) (wordCountGetter(s.toString())));

						results.add(nextResult);

					}

				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * methods performs all processes necessary for partial search
	 * 
	 * @param p the text file of queries to be used for search
	 * @return full PARTIAL search results
	 */
	public TreeMap<String, List<SearchResult>> completePartialSearch(List<Path> p) {
		TreeMap<String, List<SearchResult>> fullPartialResults = new TreeMap<String, List<SearchResult>>();
		// parse query file by line

		for (Path file : p) { // loop through all files
			try (BufferedReader buff = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
				String line;
				while ((line = buff.readLine()) != null) { // while still lines in query file, parse

					if (TextFileStemmer.uniqueStems(line) != null && TextFileStemmer.uniqueStems(line).size() != 0) {

						fullPartialResults.put(String.join(" ", (TextFileStemmer.uniqueStems(line))),
								partialSearch(TextFileStemmer.uniqueStems(line)));
					}
				}
			} catch (IOException e) {
				System.out.print("buffered reader was unable to work with file");
			}
		}

		return fullPartialResults;
	}

}
