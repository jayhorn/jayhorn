package jayhorn.hornify;

import java.util.LinkedHashMap;
import java.util.Map;

import soottocfg.cfg.ClassVariable;

public class ClassType {
	
	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();
	
	public void addClassVar(ClassVariable var, Integer value){
		typeIds.put(var, value);
	}

	public Integer getValue(ClassVariable var){
		return typeIds.get(var);
	}
}
