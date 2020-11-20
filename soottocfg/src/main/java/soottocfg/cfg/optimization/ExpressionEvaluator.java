package soottocfg.cfg.optimization;

import com.google.common.base.Optional;

import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.TupleAccessExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.variable.Variable;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.BoolType;

/**
 * Evaluate expressions statically if possible.
 * 
 * TODO: verify that right integer semantics is used (Probably not)
 *
 * @author rodykers
 *
 */
public class ExpressionEvaluator {
		
	public static Expression simplify(Expression e) {
		if (e instanceof BinaryExpression) {
			return simplify((BinaryExpression) e);
		} else if (e instanceof BooleanLiteral) {
			return e;
		} else if (e instanceof IdentifierExpression) {
			return e;
		} else if (e instanceof IntegerLiteral) {
			return e;
		} else if (e instanceof IteExpression) {
			return simplify((IteExpression) e);
		} else if (e instanceof UnaryExpression) {
			return simplify((UnaryExpression) e);
		} else if (e instanceof NullLiteral) {
			return e;
		} else if (e instanceof TupleAccessExpression) {
			return e;
		} else {
			throw new RuntimeException("unexpected expression type: " + e);
		}	
	}
	
	public static Expression simplify(BinaryExpression e) {
		Expression left = simplify(e.getLeft());
		Expression right = simplify(e.getRight());
		if (left instanceof IntegerLiteral && right instanceof IntegerLiteral) {
                    int leftVal = ((IntegerLiteral)left).getValue().intValue();
                    int rightVal = ((IntegerLiteral)right).getValue().intValue();
                    Type resType = e.getType();
                    if (resType instanceof IntType) {
			return new IntegerLiteral(e.getSourceLocation(),
                                                  evalIntOp(e.getOp(), leftVal, rightVal));
                    } else if (resType instanceof BoolType) {
			return new BooleanLiteral(e.getSourceLocation(),
                                                  evalBoolOp(e.getOp(), leftVal, rightVal));
                    } else {
                        throw new RuntimeException("Not handled: result type "+resType);
                    }
		} else if (left instanceof BooleanLiteral && right instanceof BooleanLiteral) {
			boolean a = ((BooleanLiteral)left).getValue();
			boolean b = ((BooleanLiteral)right).getValue();
			boolean res = evalConnective(e.getOp(), a, b);
			return new BooleanLiteral(e.getSourceLocation(), res);
                } else if (left instanceof NullLiteral && right instanceof NullLiteral) {
                    if (e.getOp()==BinaryOperator.Eq) {
                        return BooleanLiteral.trueLiteral();
                    } else if (e.getOp()==BinaryOperator.Ne) {
                        return BooleanLiteral.falseLiteral();
                    }
		} else if (left instanceof IdentifierExpression && right instanceof IdentifierExpression) {
			Variable leftVar = ((IdentifierExpression)left).getVariable();
			Variable rightVar = ((IdentifierExpression)right).getVariable();
			// for the case a==a and a!=a
			if (leftVar.equals(rightVar)) {
				if (e.getOp()==BinaryOperator.Eq) {
					return BooleanLiteral.trueLiteral();
				} else if (e.getOp()==BinaryOperator.Ne) {
					return BooleanLiteral.falseLiteral();
				}
			} else if (leftVar.isUnique() && rightVar.isUnique()) {
				//unique variable cannot be equal
				if (e.getOp()==BinaryOperator.Eq) {
					return BooleanLiteral.falseLiteral();
				} else if (e.getOp()==BinaryOperator.Ne) {
					return BooleanLiteral.trueLiteral();
				}				
			}
		}
		return new BinaryExpression(e.getSourceLocation(), e.getOp(),left, right);
	}
	
	private static boolean evalConnective(BinaryOperator op, boolean a, boolean b) {
		switch (op) {
		case And:
			return a && b;
		case Implies:
			return !a || b;
		case Or:
			return a || b;
		case Eq:
			return a == b;
		case Ne:
			return a != b;
		default:
			break;
		}
		throw new RuntimeException("Not implemented: "+op);
	}

	private static boolean evalBoolOp(BinaryOperator op, int a, int b) {
		switch (op) {
		case Eq:
			return a == b;
		case Ge:
			return a >= b;
		case Gt:
			return a > b;
		case Le:
			return a <= b;
		case Lt:
			return a < b;
		case Ne:
			return a != b;
		case PoLeq:
			break;
		default:
			break;
		}
		throw new RuntimeException("Not implemented: "+op);
	}

	private static int evalIntOp(BinaryOperator op, int a, int b) {
		switch (op) {
		case BAnd:
			return a & b;
		case BOr:
			return a | b;
		case Div:
			return a / b;
		case Minus:
			return a - b;
		case Mod:
			return a % b;
		case Mul:
			return a * b;
		case Plus:
			return a + b;
		case Shl:
			return a << b;
		case Shr:
			return a >> b;
		case Ushr:
			return a >>> b;
		case Xor:
			return a ^ b;
		default:
			break;
		}
		throw new RuntimeException("Not handled: " + op);
	}


	public static Expression simplify(IteExpression e) {
		Expression cond = simplify(e.getCondition());
		Expression thenExpr = simplify(e.getThenExpr());
		Expression elseExpr = simplify(e.getElseExpr());
		
		if (cond instanceof BooleanLiteral) {
			if (((BooleanLiteral)cond).getValue()) {
				return thenExpr;
			} else {
				return elseExpr;
			}
		}		
		return new IteExpression(e.getSourceLocation(), cond, thenExpr, elseExpr);
	}

	public static Expression simplify(UnaryExpression e) {
		Expression inner = simplify(e.getExpression());
		switch (e.getOp()) {
		case LNot:
			if (inner instanceof BooleanLiteral) {
				if (((BooleanLiteral)inner).getValue()) {
					return BooleanLiteral.falseLiteral();
				} else {
					return BooleanLiteral.trueLiteral();
				}
			}
			break;
		case Neg:
			if (inner instanceof IntegerLiteral) {
				return new IntegerLiteral(e.getSourceLocation(), -1*(((IntegerLiteral)inner).getValue()).intValue());
			}
			break;
		default:
			break;
		
		}
		return new UnaryExpression(e.getSourceLocation(), e.getOp(), inner);
	}

	
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
		} else if (e instanceof TupleAccessExpression) {
			return eval((TupleAccessExpression) e);
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
  
	public static Optional<Object> eval(TupleAccessExpression e) {
		return Optional.absent();
	}
}
