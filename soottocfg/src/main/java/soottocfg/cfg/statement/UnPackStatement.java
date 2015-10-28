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
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.type.ClassConstant;

/**
 * @author schaef
 *
 */
public class UnPackStatement extends Statement {

	private final ClassConstant classConstant;
	private final IdentifierExpression object;
	private final List<IdentifierExpression> left;
	
	
	/**
	 * @param loc
	 */
	public UnPackStatement(SourceLocation loc, ClassConstant c, IdentifierExpression obj, List<IdentifierExpression> lhs) {
		super(loc);		
		classConstant = c;
		object = obj;
		left = new LinkedList<IdentifierExpression>(lhs);
		assert(c.getAssociatedFields().length==left.size());
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.statement.Statement#getUsedVariables()
	 */
	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		ret.add(object.getVariable());
		return ret;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.statement.Statement#getLVariables()
	 */
	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> ret = new HashSet<Variable>();
		for (IdentifierExpression v : left) {
			ret.add(v.getVariable());
		}
		return ret;		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String comma = "";
		for (IdentifierExpression v : left) {
			sb.append(comma);
			sb.append(v);
			comma = ", ";			
		}
		sb.append(" := unpack(");
		sb.append(classConstant.getName());
		sb.append(", ");
		sb.append(object);
		sb.append(")");
		return sb.toString();
	}
	
}
