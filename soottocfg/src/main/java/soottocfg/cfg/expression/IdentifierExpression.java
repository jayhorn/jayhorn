/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Set;

import soottocfg.cfg.Variable;
import soottocfg.cfg.type.Type;

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
	private Integer ssaIncarnation = -1;
	
	/**
	 * 
	 */
	public IdentifierExpression(Variable v) {
		this.variable = v;
	}

	public IdentifierExpression(Variable v, int incarnation) {
		this.variable = v;
		this.ssaIncarnation = incarnation;
	}

	
	public Integer getIncarnation() {
		return ssaIncarnation;
	}
	
	public void setIncarnation(Integer inc) {
		ssaIncarnation = inc;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if (this.variable==null) {
			sb.append("==NOT IMPLEMENTED==");
		} else {
			sb.append(this.variable.getName());
		}
		return sb.toString();		
	}	
	
	@Override
	public Set<IdentifierExpression> getIdentifierExpressions() {
		Set<IdentifierExpression> ret = new HashSet<IdentifierExpression>();
		ret.add(this);
		return ret;
	}

		
	@Override
	public Set<Variable> getLVariables() {
		return getUsedVariables();
	}

    public Variable getVariable() {
        return variable;
    }
	
	@Override
	public Type getType() {
		return variable.getType();
	}
}
