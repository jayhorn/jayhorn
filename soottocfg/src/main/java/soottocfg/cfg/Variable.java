/**
 * 
 */
package soottocfg.cfg;

import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class Variable {

	private final String variableName;
	private final Type type;
	/**
	 * 
	 */
	public Variable(String name, Type t) {
		// TODO Auto-generated constructor stub
		assert (t!=null);
		this.variableName = name;
		this.type = t;
	}
	
	public String getName() {
		return this.variableName;
	}

	public Type getType() {
		return this.type;
	}

    public String toString() {
        return this.variableName + "/" + this.type;
    }
}
