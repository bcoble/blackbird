import java.util.Comparator;


public class JOComparator implements Comparator<JSONObject>{
	
	public int compare(JSONObject a, JSONObject b){
		return a.getScore() < b.getScore() ? -1 : a.getScore() == b.getScore() ? 0 : 1;
	}
}
