/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class NewStatement extends Statement {

	private static final long serialVersionUID = 6725099779878843508L;
	private final IdentifierExpression left;
	private final ClassVariable classVariable;
	Variable counterVar = null;
	
	public Expression getLeft() {
		return left;
	}

	public ClassVariable getClassVariable() {
		return this.classVariable;
	}
	/**
	 * @param createdFrom
	 */
	public NewStatement(SourceLocation loc, IdentifierExpression lhs, ClassVariable classVar) {
		super(loc);
		this.left = lhs;
		this.classVariable = classVar;
	}

	public NewStatement(SourceLocation loc, IdentifierExpression lhs, ClassVariable classVar, Variable cvar) {
		super(loc);
		this.left = lhs;
		this.classVariable = classVar;
		this.counterVar = cvar;
	}

	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.left);
		sb.append(" := new ");
		sb.append(this.classVariable);
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
//		used.addAll(right.getUseIdentifierExpressions());
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		res.add(left);	
		return res;
	}

	/*
	 * TODO: the counter var is just a hacky way to keep track
	 * of the current heap counter. Later, we could use different
	 * heap counters to keep track of how many objects of a particular
	 * type can be allocated and how many objects are allocated in a 
	 * certain scope. 
	 */
	public void setCounterVar(Variable v) {
		this.counterVar = v;
	}
	
	public Variable getCounterVar() {
		return this.counterVar;
	}
	
	@Override
	public NewStatement deepCopy() {
		return new NewStatement(getSourceLocation(), left, this.classVariable, this.counterVar);
	}

	@Override
	public NewStatement substitute(Map<Variable, Variable> subs) {
            ClassVariable cv = this.classVariable;
            if (subs.containsKey(cv))
                cv = (ClassVariable)subs.get(cv);
            IdentifierExpression newLeft = left.substitute(subs);
            if (cv == classVariable && newLeft == left)
                return this;
            else
                return new NewStatement(getSourceLocation(), newLeft, cv, this.counterVar);
	}
	
	@Override
	public NewStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
		return this;
	}
	
}
