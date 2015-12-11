/**
 * 
 */
package soottocfg.cfg.expression;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Node;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public abstract class Expression implements Node, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6933938841592139875L;

	public abstract Set<IdentifierExpression> getIdentifierExpressions();

	public Set<Variable> getUsedVariables() {
		Set<Variable> res = new HashSet<Variable>();
		for (IdentifierExpression id : this.getIdentifierExpressions()) {
			res.add(id.getVariable());
		}
		return res;
	}

	// TODO: remove?
	public abstract Set<Variable> getLVariables();

	public abstract Type getType();

	/**
	 * TODO: this one should be replaced by something that is easier to remove
	 * when restoring boolean types.
	 * 
	 * @return
	 */
	public Expression castToBoolIfNecessary() {
		if (this.getType() == IntType.instance()) {
			return new IteExpression(new BinaryExpression(BinaryOperator.Eq, this, IntegerLiteral.zero()),
					BooleanLiteral.falseLiteral(), BooleanLiteral.trueLiteral());
		} else if (this.getType() == BoolType.instance()) {
			return this;
		} else {
			throw new RuntimeException("Cannot cast " + this + " of type" + this.getType() + " to Boolean.");
		}
	}

}
