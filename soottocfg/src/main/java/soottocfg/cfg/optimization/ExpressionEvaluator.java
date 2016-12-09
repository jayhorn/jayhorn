package soottocfg.cfg.optimization;

import com.google.common.base.Optional;

import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
//import soottocfg.cfg.type.ReferenceType;

/**
 * Evaluate expressions statically if possible.
 * Mostly still TODO. Right now only expressions over boolean literals are handled.
 * 
 * @author rodykers
 *
 */
public class ExpressionEvaluator {
	
	public static  Optional<Object> eval(Expression e) {
		if (e instanceof BinaryExpression) {
			return eval((BinaryExpression) e);
		} else if (e instanceof BooleanLiteral) {
			return eval((BooleanLiteral) e);
		} else if (e instanceof IdentifierExpression) {
			return eval((IdentifierExpression) e);
		} else if (e instanceof IntegerLiteral) {
			return eval((IntegerLiteral) e);
		} else if (e instanceof IteExpression) {
			return eval((IteExpression) e);
		} else if (e instanceof UnaryExpression) {
			return eval((UnaryExpression) e);
		} else if (e instanceof NullLiteral) {
			return eval((NullLiteral) e);
		} else {
			throw new RuntimeException("unexpected expression type: " + e);
		}
	}
	
	public static Optional<Object> eval(BinaryExpression e) {
		Optional<Object> left = eval(e.getLeft());
		Optional<Object> right = eval(e.getRight());
		switch (e.getOp()) {
		case And:
			if ( (left.isPresent() && !(Boolean) left.get()) 
					|| (right.isPresent() && !(Boolean) right.get()))
				return Optional.of((Object) false);
			else if (left.isPresent() && right.isPresent())
				return Optional.of((Object) ((Boolean) left.get() && (Boolean) right.get()));
			else return Optional.absent();
		case Or:
			if ( (left.isPresent() && (Boolean) left.get()) 
					|| (right.isPresent() && (Boolean) right.get()))
				return Optional.of((Object) true);
			else if (left.isPresent() && right.isPresent())
				return Optional.of((Object) ((Boolean) left.get() || (Boolean) right.get()));
			else return Optional.absent();
		case Xor:
			if (left.isPresent() && right.isPresent())
				return Optional.of((Object) ((Boolean) left.get() ^ (Boolean) right.get()));
			else return Optional.absent();
		case Implies:
			if (left.isPresent() && !(Boolean) left.get())
				return Optional.of((Object) true);
			else if (left.isPresent() && right.isPresent())
				return right;
			else return Optional.absent();
		case Eq:
			if (right.isPresent() && right.get() instanceof NullLiteral) {
				// This hack confirms that SatAliasing01 and 02 fail with inlining
				// because this class is not implemented.
				//				ReferenceType rt = (ReferenceType) e.getLeft().getType();
				//				if (rt.toString().equals("A"))
				//					return Optional.of(true);

				// TODO We need a bit of data flow analysis here...
			}
			break;
		// TODO all other cases
		default:
			break;
		}
		return Optional.absent();
	}

	public static Optional<Object> eval(BooleanLiteral e) {
		return Optional.of((Object) e.getValue());
	}

	public static Optional<Object> eval(UnaryExpression e) {
		if (e.getOp() == UnaryOperator.LNot) {
			Optional<Object> neg = eval(e.getExpression());
			if (neg.isPresent())
				return Optional.of((Object) !(Boolean) neg.get());
		}
		return Optional.absent();
	}
	
	public static Optional<Object> eval(IdentifierExpression e) {
		return Optional.absent();
	}

	public static Optional<Object> eval(IntegerLiteral e) {
		return Optional.absent();
	}

	public static Optional<Object> eval(IteExpression ite) {
		return Optional.absent();
	}
	
	public static Optional<Object> eval(NullLiteral e) {
		// not sure what to put in the optional here... using the expression for now
		return Optional.of((Object) e);
	}
}