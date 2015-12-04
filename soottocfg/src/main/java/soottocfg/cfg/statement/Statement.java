/**
 * 
 */
package soottocfg.cfg.statement;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Node;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.IdentifierExpression;

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
	
	public abstract Set<IdentifierExpression> getIdentifierExpressions();
	
	public Set<Variable> getUsedVariables() {
			Set<Variable> res = new HashSet<Variable>();
			for (IdentifierExpression id : this.getIdentifierExpressions()) {
				res.add(id.getVariable());
			}
			return res;
	}
	
	public abstract Set<Variable> getLVariables();
	
	public int getJavaSourceLine() {
		return this.sourceLocation.getLineNumber();
	}
}
