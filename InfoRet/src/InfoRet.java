import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


/*
 * Possible run configurations-
 * <query> <score algorithm> <path to corpus directory>
 * abraham 1 C:\Users\coblebj.000\Documents\Courses\AI\blackbird\Presidents
 * 
 */



/**
 * 
 * @author coblebj
 *
 */
public class InfoRet {

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<JSONObject> corpus = new ArrayList<JSONObject>();

		if (args.length == 3) {
			String content = null;
			File dir = new File(args[2]);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					JSONObject corpi = new JSONObject();
					corpi.setName(child.getName());
					FileReader reader;
					try {
						reader = new FileReader(child);
						char[] chars = new char[(int) child.length()];
						reader.read(chars);
						content = new String(chars);
						reader.close();
						corpi.setBody(content);
						corpus.add(corpi);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else {
				// Handle the case where dir is not really a directory.
				// Checking dir.isDirectory() above would not be sufficient
				// to avoid race conditions with another process that deletes
				// directories.
			}

		}
		//System.out.println(corpus.get(0).getBody());

		// Switch statements for algorithm type

		ArrayList<JSONObject> results;
		String query = args[0];

		switch (Integer.valueOf(args[1])) {
		case (1):
			results = BM25(query, corpus);
		default:
			results = BM25(query, corpus);
		}

		Collections.sort(results, new JOComparator());		
		
		printResults(results);
	}

	/**
	 * 
	 * @param results
	 */
	private static void printResults(ArrayList<JSONObject> results) {
		for (int i = 0; i < 10; i++) {
			System.out.println(results.get(i).getName() + " - "
					+ results.get(i).getScore());
		}
	}

	/**
	 * 
	 * @param query
	 * @param corpus
	 * @return corpus with scores
	 */
	private static ArrayList<JSONObject> BM25(String query,
			ArrayList<JSONObject> corpus) {
		
		//TODO - refactor
		
		double avgdl=0;
//		System.out.println(corpus.size() + "..." + query.split(" ").length);
		for(int i=0;i<corpus.size();i++){
			avgdl += corpus.get(i).getBody().split(" ").length;
		}
		avgdl = avgdl/corpus.size();
		for (int index = 0; index < corpus.size(); index++) {
			JSONObject corpi = corpus.get(index);
			float score = 0;
			int totDoc = corpus.size();
			int numDocMatch = 0;
			double IDF;
			for (int i = 0; i < query.split(" ").length; i++) {
				for (int j = 0; j < corpus.size(); j++) {
					if (corpus.get(j).getBody().toLowerCase().contains(query.split(" ")[i].toLowerCase())) {
						numDocMatch++;
					}
				}
				int frequency = corpi.getBody().toLowerCase().split(query.split(" ")[i].toLowerCase()).length-1;
//				System.out.println(frequency + " ");
				IDF = Math.log10((totDoc - numDocMatch + 0.5) / (numDocMatch + 0.5));
				double k1 = 1.2;
				double b = .75;
				score += IDF * (frequency*k1+1)/(frequency+k1*(1-b+b*corpus.size()/avgdl));
			}
			corpus.get(index).setScore(score);
		}
		return corpus;

	}
}
