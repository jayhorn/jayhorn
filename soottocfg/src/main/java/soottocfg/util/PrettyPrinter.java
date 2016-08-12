package soottocfg.util;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import soottocfg.cfg.method.CfgBlock;

public class PrettyPrinter {
	public PrettyPrinter() {
	}

	public static String ppCfgBlockSet(Set<CfgBlock> set) {
		// Do it in sorted order for easier checking later
		TreeSet<String> sortedSet = new TreeSet<String>();
		for (CfgBlock b : set) {
			sortedSet.add(b.getLabel());
		}

		return sortedSet.toString();
	}

	public static String ppCfgBlockMapSet(Map<CfgBlock, Set<CfgBlock>> ms) {
		// sort the map for easy use
		TreeMap<String, String> sortedMap = new TreeMap<String, String>();
		for (Entry<CfgBlock, Set<CfgBlock>> entry : ms.entrySet()) {
			sortedMap.put(entry.getKey().getLabel(), ppCfgBlockSet(entry.getValue()));
		}
		return sortedMap.toString().replace("],", "],\n");
	}

}
