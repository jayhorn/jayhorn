/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class IdentifierExpression extends Expression {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4897450861767209309L;
	private final Variable variable;

	public IdentifierExpression(SourceLocation loc, Variable v) {
		super(loc);
		Preconditions.checkNotNull(v);
		this.variable = v;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.variable == null) {
			sb.append("==NOT IMPLEMENTED==");
		} else {
			sb.append(this.variable.getName());
		}
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> ret = new HashSet<IdentifierExpression>();
		ret.add(this);
		return ret;
	}

	@Override
	public Set<Variable> getDefVariables() {
		return getUseVariables();
	}

	public Variable getVariable() {
		return variable;
	}

	@Override
	public Type getType() {
		return variable.getType();
	}

	public IdentifierExpression substitute(Map<Variable, Variable> subs) {
		if (subs.containsKey(variable)) {
                    Variable newVar = subs.get(variable);
                    if (newVar != variable)
                        return new IdentifierExpression(getSourceLocation(), newVar);
		}
		return this;
	}
	
	@Override
	public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
		if (subs.containsKey(variable)) {
			return subs.get(variable);
		}
		return this;
	}


	public boolean sameVariable(IdentifierExpression other) {
		return this.variable.equals(other.variable);
	}

}
