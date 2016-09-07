/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.TupleType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class TupleExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8006376980144161854L;

	private final List<Expression> elements = new LinkedList<Expression>(); 
	private final TupleType tupleType;
	
	/**
	 * @param loc
	 */
	public TupleExpression(SourceLocation loc, List<Expression> elem) {
		super(loc);
		this.elements.addAll(elem);
		List<Type> elTypes = new LinkedList<Type>();
		for (Expression e : this.elements) {
			elTypes.add(e.getType());
		}
		this.tupleType = new TupleType(elTypes);
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
		Set<IdentifierExpression> xps = new HashSet<IdentifierExpression>();
		for (Expression ex : this.elements) {
			xps.addAll(ex.getUseIdentifierExpressions());
		}
		return xps;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#getType()
	 */
	@Override
	public Type getType() {		
		return this.tupleType;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#deepCopy()
	 */
	@Override
	public Expression deepCopy() {
		List<Expression> elementsCopy = new LinkedList<Expression>();
		for (Expression e : this.elements) {
			elementsCopy.add(e.deepCopy());
		}
		return new TupleExpression(this.getSourceLocation(), elementsCopy);
	}

}
