
public class TextCorpus {
	private String body;
	private String name;
	private float score;
	
	public TextCorpus(){
		this.score = 9999;
	}
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}
	public String toString(){
		return this.getBody();
	}
}
