/**
 * 
 */
package jayhorn.cfg;

import java.util.HashMap;
import java.util.Map;

import jayhorn.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class Program {

	private final Map<String, Variable> globalVariables = new HashMap<String, Variable>();
	
	public Variable loopupGlobalVariable(String varName, Type t) {
		if (!this.globalVariables.containsKey(varName)) {
			this.globalVariables.put(varName, new Variable(varName, t));
		}
		return this.globalVariables.get(varName);
	}

}
