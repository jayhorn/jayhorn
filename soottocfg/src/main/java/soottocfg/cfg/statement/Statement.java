/**
 * 
 */
package soottocfg.cfg.statement;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.Node;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public abstract class Statement implements Node, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4810592044342837988L;

	public SourceLocation getSourceLocation() {
		return sourceLocation;
	}

	private final SourceLocation sourceLocation;

	public Statement(SourceLocation loc) {
		this.sourceLocation = loc;

	}

	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<>();
		res.addAll(getUseIdentifierExpressions());
		res.addAll(getDefIdentifierExpressions());
		return res;
	}
	
	public abstract Set<IdentifierExpression> getUseIdentifierExpressions();
	
	public abstract Set<IdentifierExpression> getDefIdentifierExpressions();

	public Set<Variable> getUseVariables() {
		Set<Variable> res = new HashSet<Variable>();
		for (IdentifierExpression ie : getUseIdentifierExpressions()) {
			res.add(ie.getVariable());
		}
		return res;
	}

	public Set<Variable> getDefVariables() {
		Set<Variable> res = new HashSet<Variable>();
		for (IdentifierExpression ie : getDefIdentifierExpressions()) {
			res.add(ie.getVariable());
		}
		return res;
	}

	public Set<Variable> getAllVariables() {
		Set<Variable> res = new HashSet<Variable>();
		for (IdentifierExpression ie : getIdentifierExpressions()) {
			res.add(ie.getVariable());
		}
		return res;
	}
	
	public int getJavaSourceLine() {
		if (sourceLocation == null) {
			return -1;
		}
		return this.sourceLocation.getLineNumber();
	}
	
	public abstract Statement deepCopy();
	
	/**
	 * Returns a variant of the statement where all
	 * variables in the map have been substituted.
	 * @param subs
	 */
	public abstract Statement substitute(Map<Variable, Variable> subs);

	/**
	 * Returns a variant of the statement where all
	 * variables in the map have been substituted by the expression
	 * they map to.
	 * @param subs
	 */
	public abstract Statement substituteVarWithExpression(Map<Variable, Expression> subs);
}
