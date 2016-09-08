/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.TupleVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class TupleAccessExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1822090397563051877L;
	private final TupleVariable tupleVariable;
	private final Integer position;
	
	/**
	 * @param loc
	 */
	public TupleAccessExpression(SourceLocation loc, TupleVariable tvar, int position) {
		super(loc);
		Preconditions.checkNotNull(tvar);
		Preconditions.checkPositionIndex(position, tvar.getType().getElementTypes().size(), "Index "+position+" out of bounds for type "+tvar.getType());
		this.tupleVariable = tvar; 
		this.position = position;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.Node#getDefVariables()
	 */
	@Override
	public Set<Variable> getDefVariables() {
		return new HashSet<Variable>();
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#getUseIdentifierExpressions()
	 */
	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		throw new RuntimeException("Not implemented");
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#getType()
	 */
	@Override
	public Type getType() {		
		return this.tupleVariable.getType().getElementTypes().get(this.position);
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#deepCopy()
	 */
	@Override
	public Expression deepCopy() {
		return new TupleAccessExpression(this.getSourceLocation(), this.tupleVariable, this.position);
	}

}
