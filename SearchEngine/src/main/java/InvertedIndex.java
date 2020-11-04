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
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Fall 2020
 */
public class InvertedIndex {
	/**
	 * inverted index builder object to grab some of the methods
	 */
	InvertedIndexBuilder builder = new InvertedIndexBuilder();
	/**
	 * data structure for inverted index object
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	/**
	 * map that records how many words in a text file
	 */
	TreeMap<String, Integer> countMap = new TreeMap<String, Integer>();

	/**
	 * inverted index class object constructor
	 */
	public InvertedIndex() {
		this.index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
	}

	/**
	 * adds a set of specific data to the index
	 * 
	 * @param word     stem word to be an index key
	 * @param file     location to add to index
	 * @param position positions where word is found in that location
	 */
	public void add(String word, String file, Integer position) {
		index.putIfAbsent(word, new TreeMap<>());
		index.get(word).putIfAbsent(file, new TreeSet<>());
		index.get(word).get(file).add(position);
	}

	/**
	 * finds the number of stemmed words in the index
	 * 
	 * @return number of words in index
	 */
	public int size() {
		return index.size();
	}

	/**
	 * finds the number of files this word is found in index
	 * 
	 * @param word the specific stem/word
	 * @return # of paths stored for that word
	 */
	public int size(String word) {

		if (contains(word)) {
			return index.get(word).keySet().size();
		}
		return 0;
	}

	/**
	 * finds the number of times the passed in word is in specific text file
	 * 
	 * @param word     the stem word
	 * @param location the specific text file
	 * @return # of positions stored in that location
	 */
	public int size(String word, String location) {
		if (contains(word, location)) {
			return index.get(word).get(location).size();
		}
		return 0;
	}

	/**
	 * finds if stem word is present in the inverted index
	 * 
	 * @param stem the specific word being looked for
	 * @return true if stem word is in index
	 */
	public boolean contains(String stem) {
		return index.containsKey(stem);
	}

	/**
	 * finds if stem word is present in specified file
	 * 
	 * @param word     the specific stem word
	 * @param location the specific text file
	 * @return true if word can be found in that file (if file is in words key set)
	 */
	public boolean contains(String word, String location) {
		return index.containsKey(word) && index.get(word).containsKey(location);
	}

	/**
	 * finds if passed in word exists in the specified file at the specified
	 * position
	 * 
	 * @param word     stem
	 * @param location file location
	 * @param position location in file where word may be
	 * @return if word exists in file in that location
	 */
	public boolean contains(String word, String location, int position) {
		return index.containsKey(word) && index.get(word).containsKey(location)
				&& index.get(word).get(location).contains(position);
	}

	/**
	 * outputs the index to an output file in JSON format
	 * 
	 * @param path output file path to write to
	 * @throws IOException if encounter IO error
	 */
	public void toJson(Path path) throws IOException {
		SimpleJsonWriter.asDoubleNestedStructure(index, path);
	}

	/**
	 * grabs and returns all of the words in the index
	 * 
	 * @return an unmodifiable set of the words
	 */
	public Set<String> getWords() {
		return Collections.unmodifiableSet(index.keySet());
	}

	/**
	 * grabs and returns all of the locations/files for the specified word
	 * 
	 * @param word the specified stem word
	 * @return Set of locations
	 */
	public Set<String> getLocations(String word) { // FILEGETTER REPLACEMENT
		if (contains(word)) { // if word exists
			return Collections.unmodifiableSet(index.get(word).keySet());
		}
		return Collections.emptySet();
	}

	/**
	 * grabs and returns a set of locations for a specified word in a specified file
	 * 
	 * @param word     the specified stem
	 * @param location the filename
	 * @return set of locations where the stem is found
	 */
	public Set<Integer> getPositions(String word, String location) {
		if (contains(word, location)) { // if word is present in file
			return Collections.unmodifiableSet(index.get(word).get(location));
		}
		return Collections.emptySet();
	}

	/*
	 * returns string value of index
	 */
	public String toString() {
		return index.toString();
	}

	/**
	 * @param word the word from which to grab files for partial search
	 * @return set of files where word is located
	 */
	public Set<String> partialFileGetter(String word) {
		Set<String> files = new TreeSet<String>();

		for (String stem : index.keySet()) {
			if (stem.startsWith(word)) { // if word in index begins with query word
				if (index.get(stem) != null) {
					if (index.get(stem).keySet() != null) {
						for (String p : index.get(stem).keySet()) {
							files.add(p);
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
	public int wordGetter(String word, String file) {
		if (index.get(word) == null) {
			return 0;
		}
		if (index.get(word).get(file) == null) {
			return 0;
		}
		return index.get(word).get(file).size(); // return how many times in file for this particular word
	}

	/**
	 * @param word the word to get count for in partial search
	 * @param file the file in which to search for appearances of the word
	 * @return count of appearances of word in file
	 */
	public int partialWordGetter(String word, String file) {
		int matches = 0;

		for (String stem : index.keySet()) {
			if (index.get(stem) == null) {
				continue;
			}
			if (index.get(stem).get(file) == null) {
				continue;
			}
			if (stem.startsWith(word)) {
				matches += index.get(stem).get(file).size();
			}
		}
		return matches;
	}

	/**
	 * @param words the already parsed words from a single line of the query file
	 * @return a sorted list of EXACT search results
	 */
	public List<SearchResult> exactSearch(TreeSet<String> words) {
		List<SearchResult> results = new ArrayList<>();
		ArrayList<String> parsedWords = new ArrayList<String>(words);
		ArrayList<String> usedFiles = new ArrayList<String>();

		for (String i : parsedWords) {
			if (getLocations(i) != null) {

				for (String file : getLocations(i)) {
					// if file is not already been used
					if (!usedFiles.contains(file)) {
						usedFiles.add(file);

						SearchResult nextResult = new SearchResult();
						nextResult.where = file;
						int count1 = 0;
						for (int x = 0; x < parsedWords.size(); x++) {
							count1 += wordGetter(parsedWords.get(x), file);
						}
						nextResult.count = count1;
						nextResult.score = ((double) count1 / (double) (builder.wordCountGetter(file)));
						results.add(nextResult);
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
		//make this into a class member (@ top tht keeps all search results)
		ArrayList<String> parsedWords = new ArrayList<String>(words);
		ArrayList<String> usedFiles = new ArrayList<String>();

		for (String word : parsedWords) {
			if (partialFileGetter(word) != null) {

				for (String file : partialFileGetter(word)) {
					// if file is not already been used
					if (!usedFiles.contains(file)) {
						usedFiles.add(file);

						SearchResult nextResult = new SearchResult();
						nextResult.where = file;
						int count1 = 0;

						for (int x = 0; x < parsedWords.size(); x++) {
							count1 += partialWordGetter(parsedWords.get(x), file);
						}

						nextResult.count = count1;
						nextResult.score = ((double) count1 / (double) (builder.wordCountGetter(file)));
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
	 * @param files the text files of queries to be used for search
	 * @return full PARTIAL search results
	 */
	public TreeMap<String, List<SearchResult>> completePartialSearch(List<Path> files) {
		TreeMap<String, List<SearchResult>> fullPartialResults = new TreeMap<String, List<SearchResult>>();
		// parse query file by line

		for (Path file : files) { // loop through all files
			try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
				String line;
				while ((line = reader.readLine()) != null) { // while still lines in query file, parse

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

//	/**
//	 * @param files the files to use
//	 * @param type the search type
//	 * @return map 
//	 */
//	public TreeMap<String, List<SearchResult>> search(List<Path> files, String type){
//	TreeMap<String, List<SearchResult>> results = new TreeMap<String, List<SearchResult>>();
//		if(type.equals("partial")) {
//			results = partialSearch(files);
//			
//		}else {
//			results = exactSearch(files);
//		}
//		return results;
//	}
//	//
}
