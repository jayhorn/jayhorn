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
public class ArrayStoreStatement extends Statement {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5637930701746354268L;
	private final IdentifierExpression base;
	private final Expression value;
	private final List<Expression> indices;

	/**
	 * 
	 * @param loc
	 * @param base
	 * @param indices
	 * @param value
	 */
	public ArrayStoreStatement(SourceLocation loc, IdentifierExpression base, Expression[] indices, Expression value) {
		super(loc);
		//TODO change type of base is an IdentifierExpression. 
		this.base = base;
		this.indices = new LinkedList<Expression>();
		for (int i = 0; i < indices.length; i++) {
			this.indices.add(indices[i]);
		}
		this.value = value;
	}

	public IdentifierExpression getBase() {
		return this.base;
	}

	public Expression[] getIndices() {
		return this.indices.toArray(new Expression[this.indices.size()]);
	}

	public Expression getValue() {
		return this.value;
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();		
		for (Expression e : indices) {
			used.addAll(e.getUseIdentifierExpressions());
		}
		used.addAll(value.getUseIdentifierExpressions());
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> def = new HashSet<IdentifierExpression>();
		def.add(base);
		return def;
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

	@Override
	public Statement deepCopy() {
		List<Expression> idxCopy = new LinkedList<Expression>();
		for (Expression e : indices) {
			idxCopy.add(e.deepCopy());
		}
		return new ArrayStoreStatement(getSourceLocation(), base.deepCopy(), idxCopy.toArray(new Expression[idxCopy.size()]), value.deepCopy());
	}

}
