/**
 * 
 */
package soottocfg.cfg.variable;

import java.io.Serializable;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class Variable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2599846473306218710L;

	protected final String variableName;
	protected Type type; //don't make the type final because java.lang.Class is recursive
	protected final boolean constant, unique;

	public Variable(String name, Type t) {
		this(name, t, false, false);
	}

	public Variable(String name, Type t, boolean constant, boolean unique) {
		Preconditions.checkNotNull(t);
		this.variableName = name;
		this.type = t;
		this.constant = constant;
		this.unique = unique;
	}

	public IdentifierExpression mkExp(SourceLocation loc) {
		return new IdentifierExpression(loc, this);
	}
	
	public String getName() {
		return this.variableName;
	}

	public Type getType() {
		return this.type;
	}

	public boolean isConstant() {
		return constant;
	}

	public boolean isUnique() {
		return unique;
	}

	public String toString() {
//		return this.variableName + "/" + this.type;
		return this.type + " " + this.variableName;
	}
}
