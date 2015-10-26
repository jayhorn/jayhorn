package soottocfg.cfg.optimization;

import java.util.LinkedList;
import java.util.List;

import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.*;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;

//DSN should this be an abstract class
public class CfgScanner extends CfgVisitor {
	public CfgScanner () {}
	
	@Override
	protected void processCfgBlock(CfgBlock block) {
		//Update the node
		setCurrentCfgBlock(block);
		processStatementList(block.getStatements());
		setCurrentCfgBlock(null);
	}

	
	@Override
	protected List<Statement> processStatementList(List<Statement> sl){
		for(Statement s : sl){
			processStatement(s);
		}
		return sl;
	}

	
	@Override
	protected Statement processStatement(AssertStatement s)
	{
		processExpression(s.getExpression());
		return s;
	}
	
	@Override
	protected Statement processStatement(AssignStatement s)
	{
		Expression lhs = processExpression(s.getLeft());
		Expression rhs = processExpression(s.getRight());
		return new AssignStatement(s.getSourceLocation(),lhs,rhs);
	}
	
	
	@Override
	protected Statement processStatement(AssumeStatement s)
	{
		Expression e = processExpression(s.getExpression());
		return new AssumeStatement(s.getSourceLocation(),e);
	}

	protected Statement processStatement(CallStatement s)
	{
		List<Expression> args = processExpressionList(s.getArguments());
		List<Expression> receivers = processExpressionList(s.getReceiver());
		return new CallStatement(s.getSourceLocation(),s.getCallTarget(),args,receivers);
	}
	///Expressions
	@Override
	protected List<Expression> processExpressionList(List<Expression> el) {
		List<Expression> rval = new LinkedList<Expression>();
		for(Expression e : el){
			rval.add(processExpression(e));
		}
		return rval;
	}

	@Override
	protected Expression processExpression(ArrayAccessExpression e) {
		Expression newBase = processExpression(e.getBase());
		List<Expression> newIndicies = processExpressionList(e.getIndicies());
		Expression[] indexArray = newIndicies.toArray(new Expression[newIndicies.size()]);
		Expression rval = new ArrayAccessExpression(newBase,indexArray);
		return rval;
	}

	@Override
	protected Expression processExpression(BinaryExpression e) {
		Expression left = processExpression(e.getLeft());
		Expression right = processExpression(e.getRight());
		return new BinaryExpression(e.getOp(),left,right);
	}

	@Override
	protected Expression processExpression(BooleanLiteral e) {
		return e;
	}

	@Override
	protected Expression processExpression(IdentifierExpression e) {
		return e;
	}

	@Override
	protected Expression processExpression(InstanceOfExpression e) {
		Expression exp = processExpression(e.getExpression());
		Variable t = e.getTypeVariable();
		return new InstanceOfExpression(exp,t);
	}

	@Override
	protected Expression processExpression(IntegerLiteral e) {
		return e;
	}

	@Override
	protected Expression processExpression(IteExpression ite) {
		Expression i = processExpression(ite.getCondition());
		Expression t = processExpression(ite.getThenExpr());
		Expression e = processExpression(ite.getElseExpr());
		return new IteExpression(i,t,e);
	}

	@Override
	protected Expression processExpression(UnaryExpression e) {
		Expression exp = processExpression(e.getExpression());
		return new UnaryExpression(e.getOp(),exp);
	}
}
