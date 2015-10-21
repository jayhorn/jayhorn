/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;

/**
 * @author schaef
 *
 */
public class AssignStatement extends Statement {

	public Expression getLeft() {
		return left;
	}

	public Expression getRight() {
		return right;
	}

	private final Expression left, right; 
	/**
	 * @param createdFrom
	 */
	public AssignStatement(SourceLocation loc, Expression lhs, Expression rhs) {
		super(loc);
		this.left = lhs;
		this.right = rhs;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
			sb.append(this.left);
			sb.append(" := ");
			sb.append(this.right);
		return sb.toString();
	}

	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> used = new HashSet<Variable>();
		used.addAll(left.getUsedVariables());
		used.addAll(right.getUsedVariables());
		return used;
	}

	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		used.addAll(left.getLVariables());				
		return used;
	}

}
