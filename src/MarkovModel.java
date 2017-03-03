import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;


public class MarkovModel {
	private static MarkovModel model = null;
	public static MarkovModel getInstance(){
		if(model!=null){
			return model;
		}
		return new MarkovModel();
	}
	private MarkovModel(){
		boyNames = new HashSet<String>();
		girlNames = new HashSet<String>();
		boyLetters = new Hashtable<String,Letters>();
		girlLetters = new Hashtable<String,Letters>();
		readData();
		setUp();

	}
	private HashSet<String> boyNames;
	private HashSet<String> girlNames;
	private Hashtable<String,Letters> boyLetters;
	private Hashtable<String,Letters> girlLetters;
	private String startEndIndicator = "__";

	private void readData(){
		try {

			BufferedReader boyReader = new BufferedReader(new FileReader("src/namesBoys.txt"));
			BufferedReader girlReader = new BufferedReader(new FileReader("src/namesGirls.txt"));
			String boyName = boyReader.readLine();
			String girlName = girlReader.readLine();
			while(boyName != null && girlName != null){
				StringBuilder sb = new StringBuilder();
				sb.append(startEndIndicator);
				boyName = boyReader.readLine();
				sb.append(boyName);
				sb.append(startEndIndicator);
				boyNames.add(sb.toString().toLowerCase());
				sb.setLength(0);
				sb.append(startEndIndicator);
				girlName = girlReader.readLine();
				sb.append(girlName);
				sb.append(startEndIndicator);
				girlNames.add(sb.toString().toLowerCase());

			}
			boyNames.remove("__null__");
			girlNames.remove("__null__");
			boyReader.close();
			girlReader.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private void setUp(){
		for(String boyName : boyNames){

			processName(boyName, "boy");
		}
		for(String girlName : girlNames){

			processName(girlName,"girl");
		}

	}
	private void processName(String name, String sex){
		if(sex.equals("girl")){
			for(int i = 0; i< name.length()-3;i++){
				int x = i;
				String current = name.substring(i, i+2);

				girlLetters.compute(current, (k,v) -> (v == null) 
						? new Letters(current) : v.setFollowingLetterChance(name.substring(x+2, x+3)));		
			}
		}
		else{
			for(int i = 0; i< name.length()-3;i++){
				int x = i;
				String current = name.substring(i, i+2);

				boyLetters.compute(current, (k,v) -> (v == null) 
						? new Letters(current) : v.setFollowingLetterChance(name.substring(x+2, x+3)));		
			}

		}


	}
	public boolean learnNewName(String name, String sex){
		StringBuilder sb = new StringBuilder();
		sb.append(startEndIndicator);
		sb.append(name);
		sb.append(startEndIndicator);
		if(sex.equals("boy")){
			if(boyNames.add(name)){
				processName(name,sex);
				return true;
			}
			else{
				return false;
			}
		}
		else if(sex.equals("girl")){
			if(girlNames.add(name)){
				processName(name,sex);
				return true;
			}
			else{
				return false;
			}

		}
		else{
			return false;
		}

	}


	private Hashtable<String,Double> getGirlLetterDistribution(String letters){
		if(this.girlLetters.containsKey(letters)){
			return this.girlLetters.get(letters).getThisLetterFullModel();
		}
		return null;
	}
	private Hashtable<String,Double> getBoyLetterDistribution(String letters){
		if(this.boyLetters.containsKey(letters)){
			return this.boyLetters.get(letters).getThisLetterFullModel();
		}
		return null;
	}
	private String getNextLetter(String letters,String sex){
		double random = Math.random();
		double chance = 0.0;
		Hashtable<String,Double> data;
		if(sex.equals("girl")){
			data = getGirlLetterDistribution(letters);
		}
		else{
			data = getBoyLetterDistribution(letters);	
		}
		if(data!=null){
			for(String each : data.keySet()){
				chance = chance + data.get(each);
				if(random<chance){
					return each;
				}
			}
		}
		return null;
	}
	public ArrayList<String> generateName(String sex, int minLength, int maxLength, int numberOfNames){
		StringBuilder sb = new StringBuilder();
		ArrayList<String> names = new ArrayList<String>();
		while(names.size()<numberOfNames){
			String current = "__";
			sb.append(current);
			String letter = getNextLetter(current,sex);
			while(!letter.equals("_")){
				sb.append(letter);
				current = sb.toString().substring(sb.toString().length()-2);
				letter = getNextLetter(current,sex);
				if(letter==null){
					break;
				}

			}
			String name = sb.toString().substring(2);
			if(letter!=null&&name.length()>= minLength&&name.length()<=maxLength){

				names.add(name);
			}
			sb.setLength(0);
		}

		return names;
	}
	public static void main(String args[]){
		MarkovModel model = MarkovModel.getInstance();
		ArrayList<String> names = model.generateName("boy", 4, 7, 5);
		for(String name : names){
			System.out.println(name);
		}

	}



}
