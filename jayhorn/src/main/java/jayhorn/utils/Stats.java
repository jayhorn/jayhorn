package jayhorn.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


public class Stats {
	
	final static String tag = "BRUNCH_STAT ";
	
	private static Stats stats;

	public static void resetInstance() {
		stats = null;	
	}
	
	private Map<String, String> statsMap = new LinkedHashMap<String, String>();
	

	public static Stats stats() {
		if (null == stats) {
			stats = new Stats();
		}
		return stats;
	}
	
	public void add(String Key, String Val) {
		statsMap.put(Key, Val);
	}
	
	private Stats(){}
	
	public void printStats(){
		System.out.println(this.toString());
	}
	
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry: statsMap.entrySet()){
			sb.append(tag + entry.getKey() + " " + entry.getValue());
			sb.append("\n");
		}	
		return sb.toString();
	}
	

}
