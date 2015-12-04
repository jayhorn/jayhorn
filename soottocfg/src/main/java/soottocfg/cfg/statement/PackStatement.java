/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.type.ClassConstant;

/**
 * @author schaef
 *
 */
public class PackStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5776310555422969945L;
	private final ClassConstant classConstant;
	private final IdentifierExpression object;
	private final List<Expression> right;
	
	
	/**
	 * @param loc
	 */
	public PackStatement(SourceLocation loc, ClassConstant c, IdentifierExpression obj, List<Expression> rhs) {
		super(loc);		
		classConstant = c;
		object = obj;
		right = new LinkedList<Expression>(rhs);
		assert(c.getAssociatedFields().length==right.size());
	}

	
	@Override
	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.add(object);
		return used;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.statement.Statement#getLVariables()
	 */
	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		ret.add(object.getVariable());
		//TODO
		return ret;		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("pack(");
		sb.append(classConstant.getName());
		sb.append(", ");
		sb.append(object);
		sb.append(", [");
		String comma = "";
		for (Expression v : right) {
			sb.append(comma);
			sb.append(v);
			comma = ", ";			
		}
		sb.append("]");
		sb.append(")");
		return sb.toString();
	}
	
}
