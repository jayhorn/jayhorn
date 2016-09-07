/**
 * 
 */
package soottocfg.cfg.variable;

import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class TupleVariable extends Variable {

	/**
	 * @param name
	 * @param t
	 */
	public TupleVariable(String name, Type t) {
		super(name, t);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param name
	 * @param t
	 * @param constant
	 * @param unique
	 */
	public TupleVariable(String name, Type t, boolean constant, boolean unique) {
		super(name, t, constant, unique);
		// TODO Auto-generated constructor stub
	}

}
