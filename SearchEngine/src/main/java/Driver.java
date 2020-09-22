import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

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
	 * @throws IOException 
	 */
	public static void main(String[] args){
		/*
		 * TODO Modify this method as necessary. 
		 */
		ArgumentMap argm = new ArgumentMap();
		TextFileFinder tff = new TextFileFinder();
		TextFileStemmer tfs = new TextFileStemmer();
		SimpleJsonWriter sjw = new SimpleJsonWriter();
		
		if(args.length == 0) { //no arguments provided
			System.out.println("no arguments!"); 
			return;
		}
		if(!Arrays.asList(args).contains("-path")) { //if no path flag/bad arguments
			System.out.println("bad arguments !");
			//write empty inverted index to default file
			TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String, TreeMap<Path, List<Integer>>>();
			Path p = Paths.get("index.json");
			try {
				SimpleJsonWriter.asDoubleNestedArray(invertm, p);
			} catch (IOException e) {
				//add some type of error thing
			}
			return;
		}
		// store initial start time
		Instant start = Instant.now();

		// output arguments
		//System.out.println(Arrays.toString(args));
		//System.out.println("\n");
		
		//parsing command-line arguments into flag/value pairs, and supports default values if a flag is missing a value (argument map)
		var Argmap = new ArgumentMap(args);
//		System.out.println("argument map: "+ Argmap);
//		System.out.println("path value "+ Argmap.getPath("-path"));
		
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
				// TODO Auto-generated catch block
				//e.printStackTrace();   //do something else beside stack trace
			}
		}else { //if single file, add it
			if(Argmap.getPath("-path") != null) {
				textfiles.add(Argmap.getPath("-path"));
			}
		}
		//System.out.println("text files: " + textfiles);
		
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
						// TODO Auto-generated catch block
						//e.printStackTrace(); //make something beside stack trace
					}
			}
		}else{ //if single file, record it's stems
				//System.out.println("value going into unique stems: " + Argmap.getPath("-path"));
				try {
					stems = TextFileStemmer.uniqueStems(Argmap.getPath("-path"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace(); //make something beside stack trace
				}
		}
		
		//System.out.println("word stems: " + stems);
		
		
		//storing a word, file path, and location into an inverted index data structure (similar but lil diff to textfileindex)
		TreeMap<String, TreeMap<Path, List<Integer>>> invertm = new TreeMap<String, TreeMap<Path, List<Integer>>>();
		for(String s: stems) { //iterate through stems
			TreeMap<Path, List<Integer>> stemData = new TreeMap<>(); //text files and locations within them
			for(Path yee: textfiles) { //iterate through the files
				
				ArrayList<String> stemmed = new ArrayList<>(); //word stems 
				
				try {
					stemmed = TextFileStemmer.listStems(yee);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
				int position = 1;
				ArrayList<Integer> positions = new ArrayList<>(); //arraylist of where the stem was found
				for(String stemmies: stemmed){
					if(s.equals(stemmies)){
						positions.add(position);
					}
					position++;
				}
				if(positions.size()>0) { //if word was found in file as least once
					stemData.put(yee, positions); //hashmap of the files (and their locations) that the stem can b found
				}
			}
			invertm.put(s, stemData);  //put map of files and their positions as value for stem key
		}
		
		
		//writing a nested data structure (matching your inverted index data structure) to a file in JSON format (SimpleJSONWriter)
		if(Arrays.asList(args).contains("-index")) {  //write JSON to a file bc index flag present
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
		//System.out.print(SimpleJsonWriter.asDoubleNestedArray(invertm));
		}
		
		// calculate time elapsed and output
		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

	/*
	 * Generally, "driver" classes are responsible for setting up and calling
	 * other classes, usually from a main() method that parses command-line
	 * parameters. If the driver were only responsible for a single class, we use
	 * that class name. For example, "TaxiDriver" is what we would name a driver
	 * class that just sets up and calls the "Taxi" class.
	 */
}
