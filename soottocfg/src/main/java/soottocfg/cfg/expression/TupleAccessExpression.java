/**
 * 
 */
package soottocfg.cfg.expression;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;

import soottocfg.soot.util.SootTranslationHelpers;

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
		ReferenceType rt = (ReferenceType) this.tupleVariable.getType();
		projectionType = rt.getElementTypes().get(this.tupleKey);
		/*
		 * we have to count the position manually instead of doing an indexOf
		 * on the Values, because the values are not unique.
		 */
		int i = 0;
		for (Entry<String, Type> entry : rt.getElementTypes().entrySet()) {
			if (entry.getKey().equals(posKey)) {
				break;
			}
			i++;
		}
		this.keyPos = i;
		Verify.verifyNotNull(projectionType);
		Verify.verify(keyPos < rt.getElementTypes().size());
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see soottocfg.cfg.expression.Expression#getUseIdentifierExpressions()
	 */
	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		res.add(new IdentifierExpression(getSourceLocation(), tupleVariable));
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see soottocfg.cfg.expression.Expression#substitute(java.util.Map)
	 */
	@Override
	public Expression substitute(Map<Variable, Variable> subs) {
            if (subs.containsKey(tupleVariable)) {
                Variable newVar = subs.get(tupleVariable);
                if (newVar != tupleVariable)
                    return new TupleAccessExpression(this.getSourceLocation(), newVar, tupleKey);
            }
            return this;
	}

	@Override
	public Expression substituteVarWithExpression(Map<Variable, Expression> subs) {
            if (subs.containsKey(tupleVariable)) {
                Expression newVarE = subs.get(tupleVariable);
                if (newVarE instanceof NullLiteral) {
                    // then we can directly resolve the access
                    // TODO: do this in a more generic way?
                    if (tupleKey.equals(ReferenceType.RefFieldName))
                        return IntegerLiteral.zero();
                    else if (tupleKey.equals(ReferenceType.TypeFieldName))
                        return new IdentifierExpression(
                                      this.getSourceLocation(),
                                      SootTranslationHelpers.v().getNullTypeVariable());
                    else
                        Verify.verify(false,
                                      "do not know how to access field " + tupleKey +
                                      " of $null");
                } else {
                    Verify.verify(newVarE instanceof IdentifierExpression);
                    Variable newVar = ((IdentifierExpression)newVarE).getVariable();
                    if (newVar != tupleVariable)
                        return new TupleAccessExpression(this.getSourceLocation(),
                                                         newVar, tupleKey);
                }
            }
            return this;
	}
	
}
