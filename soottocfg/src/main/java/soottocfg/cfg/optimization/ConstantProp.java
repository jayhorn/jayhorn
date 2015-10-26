package soottocfg.cfg.optimization;

import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;


public class ConstantProp extends CfgUpdater {
	//Records if anything changed in this run of the visitor.

	//This probably should take a method?
	public ConstantProp (){}

	//Upgrade this at some point to also account for constant variables
	protected Boolean isConstant(Expression e)
	{
		return (e instanceof IntegerLiteral || e instanceof BooleanLiteral);
	}

	@Override
	protected Expression processExpression(InstanceOfExpression e)
	{
		Expression exp = processExpression(e.getExpression());
		Variable t = e.getTypeVariable();
		if(exp.getType().equals(t.getType())) {
			return BooleanLiteral.trueLiteral();
		} else {
			return new InstanceOfExpression(exp,t);
		}
	}
	
	@Override
	protected Expression processExpression(IteExpression ite)
	{
		Expression i = processExpression(ite.getCondition());
		Expression t = processExpression(ite.getThenExpr());
		Expression e = processExpression(ite.getElseExpr());
		if(i instanceof BooleanLiteral){
			return ((BooleanLiteral) i).getValue() ? t : e;
		}
		return new IteExpression(i,t,e);
	}
	
	@Override
	protected Expression processExpression(UnaryExpression e)
	{
		Expression exp = processExpression(e.getExpression());
		if (exp instanceof IntegerLiteral){
			changed = true;
			long val = ((IntegerLiteral) exp).getValue();
			switch(e.getOp()){
			case Neg: return new IntegerLiteral(-1 * val);
			default: throw new RuntimeException("unexpected unary operator " + e);
			}
		}
		if (exp instanceof BooleanLiteral){
			changed = true;
			Boolean val = ((BooleanLiteral) exp).getValue();
			switch(e.getOp()){
			case LNot: return new BooleanLiteral(!val);
			default: throw new RuntimeException("unexpected unary operator " + e);
			}
		}
		return new UnaryExpression(e.getOp(),exp);
	}
	
	@Override
	protected Expression processExpression(BinaryExpression e)
	{
		Expression left = processExpression(e.getLeft());
		Expression right = processExpression(e.getRight());

		//TODO worry about potential problems with size/overflow
		if(left instanceof IntegerLiteral && right instanceof IntegerLiteral){
			long leftVal = ((IntegerLiteral) left).getValue();
			long rightVal = ((IntegerLiteral) right).getValue();

			switch(e.getOp()){
			case Plus:  { changed = true; return new IntegerLiteral(leftVal + rightVal); }
			case Mul: 	{ changed = true; return new IntegerLiteral(leftVal * rightVal); }
			case Minus: { changed = true; return new IntegerLiteral(leftVal - rightVal); }
			case Div: 	{ changed = true; return new IntegerLiteral(leftVal / rightVal); }
			case Mod:	{ changed = true; return new IntegerLiteral(leftVal % rightVal); }
			case Xor: 	{ changed = true; return new IntegerLiteral(leftVal ^ rightVal); }
			case BOr:	{ changed = true; return new IntegerLiteral(leftVal | rightVal); }
			case BAnd:	{ changed = true; return new IntegerLiteral(leftVal & rightVal); }
			case Shl:	{ changed = true; return new IntegerLiteral(leftVal << rightVal); }
			case Shr:	{ changed = true; return new IntegerLiteral(leftVal >> rightVal); }

			case Eq: 	{ changed = true; return new BooleanLiteral(leftVal == rightVal); }
			case Ne: 	{ changed = true; return new BooleanLiteral(leftVal != rightVal); }
			case Gt: 	{ changed = true; return new BooleanLiteral(leftVal > rightVal); }
			case Ge: 	{ changed = true; return new BooleanLiteral(leftVal >= rightVal); }
			case Lt: 	{ changed = true; return new BooleanLiteral(leftVal < rightVal); }
			case Le: 	{ changed = true; return new BooleanLiteral(leftVal <= rightVal); }

			case And:
			case Or:
			case Implies: 
			{
				throw new RuntimeException("Type error on " + e);
			}
			case Ushr: 
			{
				throw new RuntimeException("not handled " + e);
			}
			}
		}

		if(left instanceof BooleanLiteral && right instanceof BooleanLiteral){
			boolean leftVal = ((BooleanLiteral) left).getValue();
			boolean rightVal = ((BooleanLiteral) right).getValue();

			switch(e.getOp()) {
			case Plus:  
			case Mul: 
			case Minus: 
			case Div: 	
			case Mod:	
			case Xor: 
			case BOr:	
			case BAnd:	
			case Shl:
			case Ushr:
			case Shr: 
			case Gt:
			case Lt:
			case Ge:
			case Le:
			{
				throw new RuntimeException("Can't apply int operation to boolean: " + e);
			}

			case And: 		{ changed = true; return new BooleanLiteral(leftVal && rightVal); }
			case Or: 		{ changed = true; return new BooleanLiteral(leftVal || rightVal); }	 
			case Implies: 	{ changed = true; return new BooleanLiteral(!leftVal || rightVal); }
			case Eq: 		{ changed = true; return new BooleanLiteral(leftVal == rightVal); }	
			case Ne:		{ changed = true;  return new BooleanLiteral(!leftVal != rightVal); }	
			}


		}

		//Left is, but right isn't
		if(left instanceof BooleanLiteral){
			Boolean leftVal = ((BooleanLiteral) left).getValue();
			switch(e.getOp()){
			case And: 		{ changed = true; return leftVal ? right : BooleanLiteral.falseLiteral(); }
			case Or:		{ changed = true; return leftVal ? BooleanLiteral.trueLiteral() : right; }
			case Implies:	{ changed = true; return leftVal ? right : BooleanLiteral.trueLiteral(); }
			default: 		{ /* Do nothing */ }
			}
		}

		//Right is, but left isn't
		if(right instanceof BooleanLiteral){
			Boolean rightVal = ((BooleanLiteral) right).getValue();
			switch(e.getOp()){
			case And: 		{ changed = true; return rightVal ? right : BooleanLiteral.falseLiteral(); }
			case Or:		{ changed = true; return rightVal ? BooleanLiteral.trueLiteral() : right; }
			case Implies:	{ changed = true; return rightVal ? BooleanLiteral.trueLiteral(): new UnaryExpression(UnaryOperator.LNot, left); }
			default: 		{ /* Do nothing */ }
			}
		}

		if(left instanceof IntegerLiteral){
			long leftVal = ((IntegerLiteral) left).getValue();
			switch(e.getOp()){
			case Plus: 
			{
				if (leftVal == 0) { changed = true; return right;}
			}
			case Mul:
			{
				if (leftVal == 0) { changed = true; return IntegerLiteral.zero();}
				if (leftVal == 1) { changed = true; return right;}
			}
			default: {/*do nothing*/}

			}
		}

		if(right instanceof IntegerLiteral){
			long rightVal = ((IntegerLiteral) right).getValue();
			switch(e.getOp()){
			case Plus: 
			{
				if (rightVal == 0) { changed = true; return left;}
			}
			case Mul:
			{
				if (rightVal == 0) { changed = true; return IntegerLiteral.zero();}
				if (rightVal == 1) { changed = true; return left;}
			}
			default: {/*do nothing*/}
			}
		}

		return new BinaryExpression(e.getOp(),left,right);
	}



}
