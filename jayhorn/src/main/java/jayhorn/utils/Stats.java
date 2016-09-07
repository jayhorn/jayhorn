package jayhorn.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import jayhorn.hornify.HornHelper;
import jayhorn.hornify.MethodContract;

public class Stats {
	
	final String tag = "BRUNCH_STAT ";
	
	private static Stats stats;

	public static void resetInstance() {
		stats = null;	
	}
	
	private Map<String, String> statsMap = new LinkedHashMap<String, String>();
	
	public static Stats stats(String Key, String Val) {
		if (null == stats) {
			stats = new Stats(Key, Val);
		}
		return stats;
	}

	public static Stats stats() {
		if (null == stats) {
			stats = new Stats();
		}
		return stats;
	}
	
	private Stats(String Key, String Val) {
		statsMap.put(Key, Val);
	}
	
	private Stats(){}
	
	public void printStats(){
		for (Entry<String, String> entry: statsMap.entrySet()){
			System.out.println(tag + entry.getKey() + " " + entry.getValue());
		}
	}

}
