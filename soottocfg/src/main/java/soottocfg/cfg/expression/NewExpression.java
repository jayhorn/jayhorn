/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class NewExpression extends Expression {

	private static final long serialVersionUID = 5557227115003613106L;

	private final ReferenceType referenceType;
	private final int statementId;
	
	/**
	 * @param loc
	 */
	public NewExpression(SourceLocation loc, ClassVariable cvar, int id) {
		super(loc);
		this.statementId=id;
		this.referenceType = new ReferenceType(cvar);
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
		return new HashSet<IdentifierExpression>();
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#getType()
	 */
	@Override
	public Type getType() {		
		return this.referenceType;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#deepCopy()
	 */
	@Override
	public Expression deepCopy() {
		return new NewExpression(getSourceLocation(), this.referenceType.getClassVariable(), this.statementId);
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#substitute(java.util.Map)
	 */
	@Override
	public Expression substitute(Map<Variable, Variable> subs) {
		return deepCopy();
	}

}
