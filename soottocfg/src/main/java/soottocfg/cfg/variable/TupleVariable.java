/**
 * 
 */
package soottocfg.cfg.variable;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.TupleType;

/**
 * @author schaef
 *
 */
public class TupleVariable extends Variable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8567782310486211026L;
	
	/**
	 * @param name
	 * @param t
	 */
	public TupleVariable(String name, TupleType t) {
		super(name, t);		
		// TODO Auto-generated constructor stub
	}

	public TupleVariable(String name, TupleType t, SourceLocation allocSite) {
		this(name, t);		
		
	}
	
	/**
	 * @param name
	 * @param t
	 * @param constant
	 * @param unique
	 */
	public TupleVariable(String name, TupleType t, boolean constant, boolean unique) {
		super(name, t, constant, unique);
		// TODO Auto-generated constructor stub
	}

	public TupleVariable(String name, TupleType t, SourceLocation allocSite, boolean constant, boolean unique) {
		this(name, t, constant, unique);
		
	}

	@Override
	public TupleType getType() {
		return (TupleType)this.type;
	}

	
}
