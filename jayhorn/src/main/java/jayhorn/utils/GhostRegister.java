/**
 * 
 */
package jayhorn.utils;

import java.util.LinkedHashMap;
import java.util.Map;

import soottocfg.cfg.type.Type;

/**
 * @author schaef
 * 
 */
public class GhostRegister {

	public final Map<String, Type> ghostVariableMap = new LinkedHashMap<String, Type>();

	public static GhostRegister v() {
		if (instance==null) {
			instance = new GhostRegister();
		}
		return instance;
	}
	
	public static void reset() {
		instance = null;
	}
	
	private static GhostRegister instance = null;
	
	private GhostRegister() {
	}

}
