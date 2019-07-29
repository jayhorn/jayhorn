/**
 * 
 */
package soottocfg.cfg.expression.literal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class NullLiteral extends Expression implements Literal{

	private static final long serialVersionUID = -5790805200149816187L;

	public NullLiteral(SourceLocation loc) {
		super(loc);
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("$null$");
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		return new HashSet<IdentifierExpression>();
	}

	@Override
	public Set<Variable> getDefVariables() {
		// because this can't happen on the left.
		Set<Variable> used = new HashSet<Variable>();
		return used;
	}

	@Override
	public Type getType() {
		return ReferenceType.instance();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof NullLiteral) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 17;
	}

	@Override
	public Expression substitute(Map<Variable, Variable> subs) {
		return this;
	}

	@Override
	public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
		return this;
	}
	
}
