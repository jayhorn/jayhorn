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
	protected final boolean inlineable, unique;

	public Variable(String name, Type t) {
		this(name, t, false, false);
	}

	public Variable(String name, Type t, boolean inlineable, boolean unique) {
		Preconditions.checkNotNull(t);
		this.variableName = name;
		this.type = t;
		this.inlineable = inlineable;
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

	public boolean isInlineable() {
		return inlineable;
	}

	public boolean isUnique() {
		return unique;
	}

	public String toString() {
//		return this.variableName + "/" + this.type;
		return this.type + " " + this.variableName;
	}

/*	
        Because a program can contain multiple distinct variables with the same name and type,
        it is not a good idea to implement an equals method in this class

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Variable) {
			Variable other = (Variable) obj;
			return this.variableName.equals(other.variableName); // &&	this.type.equals(other.type);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = 42;
		result = 37 * result + this.variableName.hashCode();
//		result = 37 * result + this.type.hashCode();
		return result;
	}
*/
}
