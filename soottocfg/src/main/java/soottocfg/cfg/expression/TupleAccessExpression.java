/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class TupleAccessExpression extends Expression {

	private static final long serialVersionUID = -7526110216710195228L;
	
	private final Variable tupleVariable;
	private final Type projectionType;
	private final String tupleKey;
	private final Integer keyPos;
	
	public TupleAccessExpression(SourceLocation loc, Variable v, String posKey) {
		super(loc);
		this.tupleVariable = v;
		this.tupleKey = posKey;
		Verify.verify(this.tupleVariable.getType() instanceof ReferenceType);
		ReferenceType rt = (ReferenceType)this.tupleVariable.getType();
		projectionType = rt.getElementTypes().get(posKey);
		keyPos = ((ReferenceType)this.tupleVariable.getType()).getElementTypeList().indexOf(tupleKey);
		Verify.verifyNotNull(projectionType);
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.Node#getDefVariables()
	 */
	@Override
	public Set<Variable> getDefVariables() {
		// TODO Auto-generated method stub
		return new HashSet<Variable>();
	}

	public Variable getVariable() {
		return this.tupleVariable;
	}
	
	public int getAccessPosition() {
		return keyPos;
	}
	
	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#getUseIdentifierExpressions()
	 */
	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		res.add(new IdentifierExpression(getSourceLocation(), tupleVariable));
		return res;
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#getType()
	 */
	@Override
	public Type getType() {
		return projectionType;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.tupleVariable);
		sb.append("#");				
		sb.append(keyPos);
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#deepCopy()
	 */
	@Override
	public Expression deepCopy() {
		return new TupleAccessExpression(this.getSourceLocation(), tupleVariable, tupleKey);
	}

	/* (non-Javadoc)
	 * @see soottocfg.cfg.expression.Expression#substitute(java.util.Map)
	 */
	@Override
	public Expression substitute(Map<Variable, Variable> subs) {
		if (subs.containsKey(tupleVariable)) {
			return new TupleAccessExpression(this.getSourceLocation(), subs.get(tupleVariable), tupleKey);
		}
		return new TupleAccessExpression(this.getSourceLocation(), tupleVariable, tupleKey);
	}

}
