import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * TODO Code style and variable names
 *
 * Decide on a consistent code style that uses spaces consistently
 * Use the built-in formatter in Eclipse
 *
 * Java has a strict naming convention. Usually without abbreviation and
 * always using camelCase.
 */

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 */
public class Driver {

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args){

		TextFileFinder tff = new TextFileFinder();
		TextFileIndex tfi = new TextFileIndex(); // TODO Refactor these in Eclipse


		if(args.length == 0) { //no arguments provided
			System.out.println("no arguments!");
			return;
		}

		/*
		 * TODO This is very inefficient:
		 * Arrays.asList(args).contains("-path")
		 * argumentMap.hasFlag("-path")
		 */

		/* TODO
			TreeMap<String, TreeMap<Path, List<Integer>>> index = new TreeMap<String, TreeMap<Path, List<Integer>>>();
			ArgumentMap map = new ArgumentMap(args);

			if (map.hasFlag(-path)) {
				Path path = map.getPath("-path");
				try {

				}
				catch (    ) {
					Unable to build the inverted index from path: + path
				}
			}

			if (map.hasFlag(-index)) {

			}

		 */

		/*
		 * TODO Driver is the only class that doesn't get shared with other
		 * developers, so anything generally useful should be in another class.
		 *
		 * Moving the code that works with the nested inverted index data structure
		 * into a separate class so it can be reused.
		 */

		// TODO I think you can remove the somestems logic

		if(!Arrays.asList(args).contains("-path")) { //if no path flag/bad arguments
			System.out.println("bad arguments !");
			//write empty inverted index to default file
			TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String, TreeMap<Path, List<Integer>>>();
			Path p = Paths.get("index.json");
			try {
				SimpleJsonWriter.asDoubleNestedArray(invertm, p);
			} catch (IOException e) {
				// TODO add some type of error thing
			}
			return;
		}
		// store initial start time
		Instant start = Instant.now();


		//parsing command-line arguments into flag/value pairs, and supports default values if a flag is missing a value (argument map)
		var Argmap = new ArgumentMap(args);


		if(Argmap.getString("-path")==null) { //if no path provided
			System.out.println("path is missing");
			return;
		}
		if(!Files.isDirectory(Argmap.getPath("-path")) && !Files.exists(Argmap.getPath("-path"))) {
			System.out.println("invalid path");
			return;
		}

		//traverse a directory and return a list of all the text files found within that directory (textfilefinder)
		boolean isDir = false;
		List<Path> textfiles = new ArrayList<>();
		if(Files.isDirectory(Argmap.getPath("-path"))) { //if path is directory
			//find and process all of the text files (with .txt and .text extensions) in that directory and its subdirectories.
			isDir = true;
			System.out.println("dir is " + isDir);
			try {
				textfiles = tff.list(Argmap.getPath("-path"));
			} catch (IOException e) {

			}
		}else { //if single file, add it
			if(Argmap.getPath("-path") != null) {
				textfiles.add(Argmap.getPath("-path"));
			}
		}

		/*parse text into words, including converting that text to lowercase, replacing special characters and digits,
		*splitting that text into words by whitespaces, and finally stemming the words (textStemmer)
		*/
		TreeSet<String> stems = new TreeSet<>();
		if(isDir) { //if directory
			//loop through files and parse each file's text
			for(Path p: textfiles) {
					TreeSet<String> somestems;
					try {
						somestems = TextFileStemmer.uniqueStems(p);
						for(String s: somestems) {
							stems.add(s); //add new unique stems to main tree set
						}
					} catch (IOException e) {

					}
			}
		}else{ //if single file, record it's stems
				try {
					stems = TextFileStemmer.uniqueStems(Argmap.getPath("-path"));
				} catch (IOException e) {

				}
		}



		//storing a word, file path, and location into an inverted index data structure (similar but lil diff to textfileindex)
//
		TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String, TreeMap<Path, List<Integer>>>();
		//TreeMap<Path, List<Integer>> stemData = new TreeMap<>(); //text files and locations within them
		for(Path yee: textfiles) { //iterate through the files
			ArrayList<String> stems1 = new ArrayList<>();
			try {
				stems1 = TextFileStemmer.listStems(yee);
			} catch (IOException e) {

			}

			int index = 1;
			for(String stemmies: stems1){
				//add this information into inverted index but now have to implement text file index
				tfi.add(stemmies, yee, index); //is this even adding anything to invertm?
				index++;
			}
		}
		invertm = tfi.returnIndex();


		//writing a nested data structure (matching your inverted index data structure) to a file in JSON format (SimpleJSONWriter)
		if(Arrays.asList(args).contains("-index")) {  //write JSON to a file because index flag present
			if(Argmap.getPath("-index") != null) { //if has path value, use it
				try {
					SimpleJsonWriter.asDoubleNestedArray(invertm, Argmap.getPath("-index"));
				} catch (IOException e) {

				}
			}else { //use default value
				Path p = Paths.get("index.json");
				try {
					SimpleJsonWriter.asDoubleNestedArray(invertm, p);
				} catch (IOException e) {
					//add some type of error thing
				}
			}
		}

		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}
