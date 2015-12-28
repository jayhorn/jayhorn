/**
 * 
 */
package soottocfg.cfg.expression;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Node;
import soottocfg.cfg.SourceLocation;
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
			return new IteExpression(this.sourceLocation, new BinaryExpression(this.sourceLocation, BinaryOperator.Eq, this, IntegerLiteral.zero()),
					BooleanLiteral.falseLiteral(), BooleanLiteral.trueLiteral());
		} else if (this.getType() == BoolType.instance()) {
			return this;
		} else {
			throw new RuntimeException("Cannot cast " + this + " of type" + this.getType() + " to Boolean.");
		}
	}

	public abstract Expression deepCopy();
}
