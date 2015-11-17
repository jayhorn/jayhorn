package soottocfg.cfg.optimization;

import java.util.List;

import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;

//DSN should this be an abstract class
//The scanner visits all nodes, but does not modify them.
//Whereas the updater visits and rebuilds all nodes
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
		processExpression(s.getLeft());
		processExpression(s.getRight());
		return s;
	}


	@Override
	protected Statement processStatement(AssumeStatement s)
	{
		processExpression(s.getExpression());
		return s;
	}

	protected Statement processStatement(CallStatement s)
	{
		processExpressionList(s.getArguments());
		processExpressionList(s.getReceiver());
		return s;
	}


	@Override
	protected Statement processStatement(ArrayReadStatement s)
	{
		processExpression(s.getBase());
		Expression[] ids = s.getIndices();
		for (int i = 0; i < ids.length; i++) {
			processExpression(ids[i]);
		}		
		processExpression(s.getLeftValue());
		return s;
	}

	@Override
	protected Statement processStatement(ArrayStoreStatement s)
	{
		processExpression(s.getBase());
		Expression[] ids = s.getIndices();
		for (int i = 0; i < ids.length; i++) {
			processExpression(ids[i]);
		}		
		processExpression(s.getValue());
		return s;
	}	

	///Expressions
	@Override
	protected List<Expression> processExpressionList(List<Expression> el) {
		for(Expression e : el){
			processExpression(e);
		}
		return el;
	}


	@Override
	protected Expression processExpression(BinaryExpression e) {
		processExpression(e.getLeft());
		processExpression(e.getRight());
		return e;
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
		processExpression(ite.getCondition());
		processExpression(ite.getThenExpr());
		processExpression(ite.getElseExpr());
		return ite;
	}

	@Override
	protected Expression processExpression(UnaryExpression e) {
		processExpression(e.getExpression());
		return e;
	}
}
