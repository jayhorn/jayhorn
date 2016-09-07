package jayhorn.hornify;

import java.util.LinkedHashMap;
import java.util.Map;

import soottocfg.cfg.variable.ClassVariable;

public class ClassType {
	
	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();
	
	public void addClassVar(ClassVariable var){
		typeIds.put(var, typeIds.size());
	}

	public Integer getTypeID(ClassVariable var){
		return typeIds.get(var);
	}
	
	public Map<ClassVariable, Integer> getTypeIds(){
		return typeIds;
	}
}
