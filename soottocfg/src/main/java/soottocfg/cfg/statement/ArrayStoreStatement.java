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
public class ArrayStoreStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5637930701746354268L;
	private final Expression base, value;
	private final List<Expression> indices;

	/**
	 * 
	 * @param loc
	 * @param base
	 * @param indices
	 * @param value
	 */
	public ArrayStoreStatement(SourceLocation loc, Expression base, Expression[] indices, Expression value) {
		super(loc);
		this.base = base;
		this.indices = new LinkedList<Expression>();
		for (int i = 0; i < indices.length; i++) {
			this.indices.add(indices[i]);
		}
		this.value = value;
	}

	public Expression getBase() {
		return this.base;
	}

	public Expression[] getIndices() {
		return this.indices.toArray(new Expression[this.indices.size()]);
	}

	public Expression getValue() {
		return this.value;
	}

	@Override
	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(base.getIdentifierExpressions());
		for (Expression e : indices) {
			used.addAll(e.getIdentifierExpressions());
		}
		used.addAll(value.getIdentifierExpressions());
		return used;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soottocfg.cfg.statement.Statement#getLVariables()
	 */
	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		used.addAll(base.getLVariables());
		return used;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("store(");
		sb.append(this.base);
		sb.append(",");
		String comma = "";
		for (Expression e : this.indices) {
			sb.append(comma);
			sb.append(e);
			comma = ", ";
		}
		sb.append(",");
		sb.append(this.value);
		sb.append(")");
		return sb.toString();
	}

}
