/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;

/**
 * @author schaef
 *
 */
public class AssignStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6725099779878843508L;
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
		if (left.getType().getClass()!=right.getType().getClass()) {
			System.err.println("");
		}
		Preconditions.checkArgument(left.getType().getClass()==right.getType().getClass(), "Types don't match: "+ left.getType() + " and " + right.getType() + " for " + left + " and " + right);
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
	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(left.getIdentifierExpressions());
		used.addAll(right.getIdentifierExpressions());
		return used;
	}

	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		used.addAll(left.getLVariables());				
		return used;
	}

}
