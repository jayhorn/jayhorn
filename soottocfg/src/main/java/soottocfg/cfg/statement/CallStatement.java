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
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.Type;
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

	public CallStatement(SourceLocation loc, Method method, List<Expression> arguments,
			List<Expression> returnReceiver) {
		super(loc);
		Preconditions.checkArgument(method.getInParams().size() == arguments.size());

		this.method = method;
		this.arguments = new LinkedList<Expression>();
		this.returnReceiver = returnReceiver;

		for (int i = 0; i < arguments.size(); i++) {
			Expression arg = arguments.get(i);
			Type parType = method.getInParam(i).getType();

			if (!(arg instanceof NullLiteral) && !arg.canBeAssignedToType(parType)) {

				if (arg instanceof IntegerLiteral && parType == BoolType.instance()) {
					if (((IntegerLiteral) arg).getValue() == 0L) {
						this.arguments.add(BooleanLiteral.falseLiteral());
					} else {
						this.arguments.add(BooleanLiteral.trueLiteral());
					}
				} else {
					StringBuilder sb = new StringBuilder();
					sb.append("Type mismatch:\n");
					sb.append(this.toString());
					sb.append("\nAt position " + i);
					sb.append(":\nArg " + arg);
					sb.append(" of type " + arg.getType());
					sb.append(" cannot be assigned to type " + parType);
					throw new RuntimeException(sb.toString());
				}
			} else {
				this.arguments.add(arg);
			}
		}

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
				res.add((IdentifierExpression) e);
			}
		}
		return res;
	}

	@Override
	public CallStatement deepCopy() {
		List<Expression> argCopy = new LinkedList<Expression>();
		for (Expression e : arguments) {
			argCopy.add(e);
		}
		List<Expression> rec = new LinkedList<Expression>();
		for (Expression e : returnReceiver) {
			rec.add(e);
		}
		return new CallStatement(getSourceLocation(), method, argCopy, rec);
	}

	@Override
	public CallStatement substitute(Map<Variable, Variable> subs) {
            boolean changed = false;
            List<Expression> argCopy = new LinkedList<Expression>();
            for (Expression e : arguments) {
                Expression newE = e.substitute(subs);
                changed = changed || (newE != e);
                argCopy.add(newE);
            }
            List<Expression> rec = new LinkedList<Expression>();
            for (Expression e : returnReceiver) {
                Expression newE = e.substitute(subs);
                changed = changed || (newE != e);
                rec.add(newE);
            }
            if (changed)
                return new CallStatement(getSourceLocation(), method, argCopy, rec);
            else
                return this;
	}

	@Override
	public CallStatement substituteVarWithExpression(Map<Variable, Expression> subs) {
            boolean changed = false;
            List<Expression> argCopy = new LinkedList<Expression>();
            for (Expression e : arguments) {
                Expression newE = e.substituteVarWithExpression(subs);
                changed = changed || (newE != e);
                argCopy.add(newE);
            }
            List<Expression> rec = new LinkedList<Expression>();
            for (Expression e : returnReceiver) {
                Expression newE = e.substituteVarWithExpression(subs);
                changed = changed || (newE != e);
                rec.add(newE);
            }
            if (changed)
                return new CallStatement(getSourceLocation(), method, argCopy, rec);
            else
                return this;
	}
	
}
