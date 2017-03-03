import java.util.Hashtable;

public class Letters {
	private String letters;
	private int totalCount;
	private Hashtable<String, Double> followingLetters;

	public Letters(String letters){
		this.letters = letters;
		this.totalCount = 0;
		this.followingLetters = new Hashtable<String,Double>();
	}

	public double getFollowingLetterChance(String followingLetter){
		if(this.followingLetters.containsKey(followingLetter)){
			return this.followingLetters.get(followingLetter)/totalCount;
		}
		else{
			return 0.0;
		}
	}
	public Hashtable<String,Double> getThisLetterFullModel(){
		Hashtable<String,Double> returnValue = new Hashtable<String,Double>();
		returnValue = (Hashtable<String, Double>) followingLetters.clone();
		for(String key: returnValue.keySet()){
			returnValue.put(key, returnValue.get(key)/totalCount);
		}
		return returnValue;
	}
	public int getTotalCount(){
		return this.totalCount;
	}
	public Letters setFollowingLetterChance(String followingLetter){
		this.totalCount++;
		this.followingLetters.compute(followingLetter,  (k, v) -> (v == null) ? 1 : (v += 1));
		return this;
	}
}
