/**
 * 
 */
package soottocfg.cfg.statement;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.Method;

/**
 * @author schaef
 *
 */
public class CallStatement extends Statement {

	private static final long serialVersionUID = 7267962774002374725L;
	private final Method method;
	private final List<Expression> arguments;
	private final Optional<Expression> returnReceiver;

	/**
	 * @param createdFrom
	 */
	public CallStatement(SourceLocation loc, Method method, List<Expression> arguments, Optional<Expression> returnReceiver) {
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

	public Optional<Expression> getReceiver() {
		return returnReceiver;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		if (returnReceiver.isPresent()) {		
			sb.append(returnReceiver.get());
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
		if (returnReceiver.isPresent() && returnReceiver.get() instanceof IdentifierExpression) {
			res.add((IdentifierExpression)returnReceiver.get());
		}
		return res;
	}

	@Override
	public Statement deepCopy() {
		List<Expression> argCopy = new LinkedList<Expression>();
		for (Expression e : arguments) {
			argCopy.add(e.deepCopy());
		}
		Optional<Expression> left = Optional.absent();
		if (returnReceiver.isPresent()) {
			left = Optional.of(returnReceiver.get().deepCopy());
		}
		return new CallStatement(getSourceLocation(), method, argCopy, left);
	}
}
