import java.util.Comparator;


public class TCComparatorA implements Comparator<TextCorpus>{
	
	public int compare(TextCorpus a, TextCorpus b){
		return a.getScore() < b.getScore() ? -1 : a.getScore() == b.getScore() ? 0 : 1;
	}
}
