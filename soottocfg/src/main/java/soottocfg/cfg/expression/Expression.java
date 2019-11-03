/**
 * 
 */
package soottocfg.cfg.expression;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.Node;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public abstract class Expression implements Node, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6933938841592139875L;

	public abstract Set<IdentifierExpression> getUseIdentifierExpressions();

	public Set<Variable> getUseVariables() {
		Set<Variable> res = new HashSet<Variable>();
		for (IdentifierExpression id : this.getUseIdentifierExpressions()) {
			res.add(id.getVariable());
		}
		return res;
	}

	private final SourceLocation sourceLocation;

	public Expression(SourceLocation loc) {
		this.sourceLocation = loc;
	}
	
	public SourceLocation getSourceLocation() {
		return this.sourceLocation;
	}

	public abstract Type getType();

	/**
	 * TODO: this one should be replaced by something that is easier to remove
	 * when restoring boolean types.
	 * 
	 * @return
	 */
	public Expression castToBoolIfNecessary() {
		if (this.getType() == IntType.instance()) {
			return new BinaryExpression(this.sourceLocation, BinaryOperator.Ne, this, IntegerLiteral.zero());
		} else if (this.getType() == BoolType.instance()) {
			return this;
		} else {
			throw new RuntimeException("Cannot cast " + this + " of type" + this.getType() + " to Boolean.");
		}
	}

	/**
	 * Returns true, if this can be assigned to other. That is, if either
	 * the types match or this is a sub-type of other.
	 * @param other
	 * @return true if this is sub- or equal-type to other.
	 */
	public boolean canBeAssignedTo(Expression other) {
		if (!other.getType().getClass().isAssignableFrom(this.getType().getClass())
				&& !SootTranslationHelpers.v().getMemoryModel().isNullReference(this)) {
			return false;
		}
		return true;
	}

	public boolean canBeAssignedToType(Type t) {
		return t.getClass().isAssignableFrom(this.getType().getClass());
	}
	
	/**
	 * Substitute variables in subs.
	 * @param subs
	 * @return
	 */
	public abstract Expression substitute(Map<Variable, Variable> subs);
	
	/**
	 * Substitute variables with the expressions in subs.
	 * @param subs
	 * @return
	 */
	public abstract Expression substituteVarWithExpression(Map<Variable, Expression> subs);

}
