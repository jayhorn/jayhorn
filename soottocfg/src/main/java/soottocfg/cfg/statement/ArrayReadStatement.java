/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;

/**
 * @author schaef
 *
 */
public class ArrayReadStatement extends Statement {

	private static final long serialVersionUID = 602143184115962549L;
	private final IdentifierExpression base;
	private final List<Expression> indices;
	private final IdentifierExpression left;

	/**
	 * @param loc
	 */
	public ArrayReadStatement(SourceLocation loc, IdentifierExpression base, Expression[] indices, IdentifierExpression lhs) {
		super(loc);
		this.base = base;
		this.indices = new LinkedList<Expression>();
		for (int i = 0; i < indices.length; i++) {
			this.indices.add(indices[i]);
		}
		this.left = lhs;
	}

	public IdentifierExpression getBase() {
		return this.base;
	}

	public Expression[] getIndices() {
		return this.indices.toArray(new Expression[this.indices.size()]);
	}

	public Expression getLeftValue() {
		return this.left;
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		used.addAll(base.getUseIdentifierExpressions());
		for (Expression e : indices) {
			used.addAll(e.getUseIdentifierExpressions());
		}
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> def = new HashSet<IdentifierExpression>();
		def.add(left);
		return def;
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

	@Override
	public Statement deepCopy() {
		List<Expression> idxCopy = new LinkedList<Expression>();
		for (Expression e : indices) {
			idxCopy.add(e.deepCopy());
		}
		return new ArrayReadStatement(getSourceLocation(), base.deepCopy(), idxCopy.toArray(new Expression[idxCopy.size()]), left.deepCopy());
	}

}
