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

/**
 * @author schaef
 *
 */
public class ArrayReadStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = 602143184115962549L;
	private final Expression base;
	private final List<Expression> indices;
	private final IdentifierExpression left;

	/**
	 * @param loc
	 */
	public ArrayReadStatement(SourceLocation loc, Expression base, Expression[] indices, IdentifierExpression lhs) {
		super(loc);
		this.base = base;
		this.indices = new LinkedList<Expression>();
		for (int i = 0; i < indices.length; i++) {
			this.indices.add(indices[i]);
		}
		this.left = lhs;
	}

	public Expression getBase() {
		return this.base;
	}

	public Expression[] getIndices() {
		return this.indices.toArray(new Expression[this.indices.size()]);
	}

	public Expression getLeftValue() {
		return this.left;
	}

	@Override
	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(base.getIdentifierExpressions());
		for (Expression e : indices) {
			used.addAll(e.getIdentifierExpressions());
		}
		used.add(left);
		return used;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soottocfg.cfg.statement.Statement#getLVariables()
	 */
	@Override
	public Set<Variable> getLVariables() {
		// because this can't happen on the left.
		Set<Variable> used = new HashSet<Variable>();
		used.add(left.getVariable());
		return used;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(left.toString());
		sb.append(" := ");
		sb.append("load(");
		sb.append(this.base);
		sb.append(",");
		String comma = "";
		for (Expression e : this.indices) {
			sb.append(comma);
			sb.append(e);
			comma = ", ";
		}
		sb.append(")");
		return sb.toString();
	}

}
