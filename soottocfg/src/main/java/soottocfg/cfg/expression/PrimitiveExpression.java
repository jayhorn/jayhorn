package soottocfg.cfg.expression;

import com.google.common.base.Preconditions;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PrimitiveExpression extends Expression {

	/**
	 *
	 */
	private static final long serialVersionUID = 4897451861767209389L;
	private final Variable variable;

	public PrimitiveExpression(SourceLocation loc, Variable v) {
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
		return new HashSet<IdentifierExpression>();
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

	@Override
	public PrimitiveExpression deepCopy() {
		return new PrimitiveExpression(getSourceLocation(), variable);
	}
	
	public PrimitiveExpression substitute(Map<Variable, Variable> subs) {
		if (subs.containsKey(variable)) {
			return new PrimitiveExpression(getSourceLocation(), subs.get(variable));
		}
		return new PrimitiveExpression(getSourceLocation(), variable);
	}
	
	@Override
	public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
		if (subs.containsKey(variable)) {
			return subs.get(variable).deepCopy();
		}
		return this.deepCopy();
	}


}
