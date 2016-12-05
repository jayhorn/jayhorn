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
	
	
	public Expression getLeft() {
		return left;
	}

	/**
	 * @param createdFrom
	 */
	public NewStatement(SourceLocation loc, IdentifierExpression lhs, ClassVariable classVar) {
		super(loc);
		this.left = lhs;
		this.classVariable = classVar;
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
		if (left instanceof IdentifierExpression) {
			res.add((IdentifierExpression)left);	
		} else {
			throw new RuntimeException("Did not consider the case where lhs is not an IdentifierExpression.");
		}
		return res;
	}

	/*
	 * TODO: the counter var is just a hacky way to keep track
	 * of the current heap counter. Later, we could use different
	 * heap counters to keep track of how many objects of a particular
	 * type can be allocated and how many objects are allocated in a 
	 * certain scope. 
	 */
	Variable counterVar = null;
	public void setCounterVar(Variable v) {
		this.counterVar = v;
	}
	
	public Variable getCounterVar() {
		return this.counterVar;
	}
	
	@Override
	public Statement deepCopy() {
		return new NewStatement(getSourceLocation(), left.deepCopy(), this.classVariable);
	}

	@Override
	public NewStatement substitute(Map<Variable, Variable> subs) {
		ClassVariable cv = this.classVariable;
		if (subs.containsKey(cv)) {
			cv = (ClassVariable)subs.get(cv);
		}
		return new NewStatement(getSourceLocation(), left.substitute(subs), cv);
	}

}
