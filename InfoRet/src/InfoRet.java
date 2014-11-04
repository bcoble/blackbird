import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



/*
 * Possible run configurations-
 * <score algorithm> <path to corpus directory> <query> 
 * 1 C:\Users\coblebj.000\Documents\Courses\AI\blackbird\Presidents abraham 
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
		ArrayList<TextCorpus> corpus = new ArrayList<TextCorpus>();

		if (args.length >= 3) {
			String content = null;
			File dir = new File(args[1]);
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					TextCorpus corpi = new TextCorpus();
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

		ArrayList<TextCorpus> results = null;
		String query = args[2];
		if(args.length>=4){
			for(int i=3;i<args.length;i++){
				query = query + " " + args[i];
			}
		}
		switch (Integer.valueOf(args[0])) {
		case (1):
			results = BM25(query, corpus);
			Collections.sort(results, new TCComparatorA());	// sorts low->high
		case (2):
			results = skip_Bi_Gram(query, corpus);
			Collections.sort(results, new TCComparatorB());	// sorts high->low
		default:
			printResults(results);
		}
	}

	/**
	 * 
	 * @param results
	 */
	private static void printResults(ArrayList<TextCorpus> results) {
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
	private static ArrayList<TextCorpus> BM25(String query,
			ArrayList<TextCorpus> corpus) {
		
		//TODO - refactor
		
		double avgdl=0;
//		System.out.println(corpus.size() + "..." + query.split(" ").length);
		for(int i=0;i<corpus.size();i++){
			avgdl += corpus.get(i).getBody().split(" ").length;
		}
		avgdl = avgdl/corpus.size();
		for (int index = 0; index < corpus.size(); index++) {
			TextCorpus corpi = corpus.get(index);
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

	private static ArrayList<TextCorpus> skip_Bi_Gram(String query, ArrayList<TextCorpus> corpus){
		
		// Set of SBGs in query
		Set<String> Q = makeBiGrams(query);
				
		// Loop over corpus
		for(int i=0;i<corpus.size();i++){
			TextCorpus doc = corpus.get(i);
			String body = doc.getBody().replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", " ");
			Set<String> P = makeBiGrams(body);

			float pScore = 0;
			float qScore = 0;
			int intersection = 0;
			
			Iterator<String> iter = Q.iterator();
			String gram = iter.next();

			if (Q.size() == 1){
				if (P.contains(gram)){
					intersection++;
				}
			} else {
				while(iter.hasNext()){
					if (P.contains(gram)){
						intersection++;
					}
					gram = iter.next();
				}
				
				// Catch the last gram
				if (P.contains(gram)){
					intersection++;
				}
			}
			pScore = intersection / P.size();
			pScore = intersection > 0 ? pScore+1 : pScore; 
			qScore = intersection / Q.size();
			qScore = intersection > 0 ? qScore+1 : qScore;
						
			float denom = pScore + qScore;
			denom = denom == 0 ? 1 : denom;
						
			float score = (2 * pScore * qScore) / denom;
//			System.out.println(intersection+"-"+pScore+"-"+qScore+"-"+denom+"-"+score);
//			score = score == 0 ? 9999 : score;
			corpus.get(i).setScore(score);
		}
		
		return corpus;
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	private static Set<String> makeBiGrams(String input){
		Set<String> results = new HashSet<String>();
		String[] split = input.split(" ");
		int size = split.length;
		
		if (size > 1){
			// Bi-gram
			for(int i=0;i<size;i++){
				if((i+1) < size){
					String bgram = split[i] + " " + split[i+1];
					bgram = bgram.toLowerCase();
					results.add(bgram);
				}
			}
			
			// skips
			for(int i=0;i<size;i++){
				if((i+2) < size){
					String sbgram = split[i] + " " + split[i+2];
					sbgram = sbgram.toLowerCase();
					results.add(sbgram);
				}
			}
		} else {
			results.add(input);
		}
		return results;
	}
	private static ArrayList<TextCorpus> PassageTerm(String query,
			ArrayList<TextCorpus> corpus) {
		String[] queryTerms = query.split(" ");
		int size = queryTerms.length;
		for(int i=0;i<corpus.size();i++){
			int score = 0;
			int matchTot=0;
			int tot=0;
			for(int j=0;j<size;j++){
				for(int k=0;k<corpus.size();k++){
					if (corpus.get(k).getBody().toLowerCase().contains(queryTerms[j].toLowerCase())) {
						numDocMatch++;
					}
				}
				IDF = Math.log10((corpus.size()) / (numDocMatch + 1));
				if(corpus.get(i).getBody().toLowerCase().contains(queryTerms[j].toLowerCase())){
					matchTot+=IDF;
				}
				tot+=IDF;
			}
			score = matchTot/tot;
			corpus.get(i).setScore(score);
		}
		return corpus;
	}

}

	
