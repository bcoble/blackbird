import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class InfoRet {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
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
		System.out.println(query);
		switch (Integer.valueOf(args[1])) {
		case (1):
			results = BM25(query, corpus);
		default:
			results = BM25(query, corpus);
		}

		results.sort(new JOComparator());

		printResults(results);
	}

	private static void printResults(ArrayList<JSONObject> results) {
		for (int i = 0; i < 10; i++) {
			System.out.println(results.get(i).getName() + " - "
					+ results.get(i).getScore());
		}
	}

	private static ArrayList<JSONObject> BM25(String query,
			ArrayList<JSONObject> corpus) {
		double avgdl=0;
		System.out.println(corpus.size() + "..." + query.split(" ").length);
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
				System.out.println(frequency + " ");
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
