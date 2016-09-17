/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class CallStatement extends Statement {

	private static final long serialVersionUID = 7267962774002374725L;
	private final Method method;
	private final List<Expression> arguments;
	private final List<Expression> returnReceiver;


	public CallStatement(SourceLocation loc, Method method, List<Expression> arguments, List<Expression> returnReceiver) {
		super(loc);
		Preconditions.checkArgument(method.getInParams().size()==arguments.size());
		this.method = method;
		this.arguments = arguments;
		this.returnReceiver = returnReceiver;
	}

	public Method getCallTarget() {
		return method;
	}

	public List<Expression> getArguments() {
		return arguments;
	}

	public List<Expression> getReceiver() {
		return returnReceiver;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (!returnReceiver.isEmpty()) {
			String delim = "";
			for (Expression e : returnReceiver) {
				sb.append(delim);
				sb.append(e);
				delim = ", ";
			}			
			sb.append(" := ");
		}
		sb.append("call ");
		sb.append(this.method.getMethodName());
		sb.append("(");
		String comma = "";
		for (Expression e : this.arguments) {
			sb.append(comma);
			sb.append(e);
			comma = ", ";
		}
		sb.append(")");
		return sb.toString();
	}

	@Override
	public Set<IdentifierExpression> getUseIdentifierExpressions() {
		Set<IdentifierExpression> used = new HashSet<IdentifierExpression>();
		for (Expression e : arguments) {
			used.addAll(e.getUseIdentifierExpressions());
		}
		return used;
	}

	@Override
	public Set<IdentifierExpression> getDefIdentifierExpressions() {
		Set<IdentifierExpression> res = new HashSet<IdentifierExpression>();
		for (Expression e : returnReceiver) {
			if (e instanceof IdentifierExpression) {
				res.add((IdentifierExpression)e);
			}
		}
		return res;
	}

	@Override
	public Statement deepCopy() {
		List<Expression> argCopy = new LinkedList<Expression>();
		for (Expression e : arguments) {
			argCopy.add(e.deepCopy());
		}
		List<Expression> rec = new LinkedList<Expression>();
		for (Expression e : returnReceiver) {
			rec.add(e.deepCopy());
		}
		return new CallStatement(getSourceLocation(), method, argCopy, rec);
	}
	
	@Override
	public void substitute(Map<Variable, Expression> subs) {
		for (int i=0;i<this.arguments.size(); i++) {
			this.arguments.set(i, this.arguments.get(i).substitute(subs));
		}
		for (int i=0;i<this.returnReceiver.size(); i++) {
			this.returnReceiver.set(i, this.returnReceiver.get(i).substitute(subs));
		}
	}

}
