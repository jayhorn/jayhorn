package soottocfg.cfg.util;

import java.util.List;

import soottocfg.cfg.expression.*;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.HavocStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;

//DSN should this be an abstract class
//The scanner visits all nodes, but does not modify them.
//Whereas the updater visits and rebuilds all nodes
public class CfgScanner extends CfgVisitor {
	public CfgScanner() {
	}

	public boolean scanMethod(Method m) {
		processMethod(m);
		return true;
	}

	@Override
	protected void processCfgBlock(CfgBlock block) {
		// Update the node
		setCurrentCfgBlock(block);
		processStatementList(block.getStatements());
		setCurrentCfgBlock(null);
	}

	@Override
	protected List<Statement> processStatementList(List<Statement> sl) {
		for (Statement s : sl) {
			processStatement(s);
		}
		return sl;
	}

	@Override
	protected Statement processStatement(AssertStatement s) {
		processExpression(s.getExpression());
		return s;
	}

	@Override
	protected Statement processStatement(AssignStatement s) {
		processExpression(s.getLeft());
		processExpression(s.getRight());
		return s;
	}

	@Override
	protected Statement processStatement(AssumeStatement s) {
		processExpression(s.getExpression());
		return s;
	}

	protected Statement processStatement(CallStatement s) {
		processExpressionList(s.getArguments());		
		for (Expression e : s.getReceiver()) {
			processExpression(e);
		}
		return s;
	}

        @Override
	protected Statement processStatement(HavocStatement s) {
		Expression x = processExpression(s.getHavocedExpression());
                // prevent Findbugs warnings
                if (x == null)
                    return s;
		return s;
	}

	@Override
	protected Statement processStatement(NewStatement s) {
		processExpression(s.getLeft());
		return s;
	}
	
	/// Expressions
	@Override
	protected List<Expression> processExpressionList(List<Expression> el) {
		for (Expression e : el) {
			processExpression(e);
		}
		return el;
	}

	@Override
	protected Statement processStatement(PullStatement s) { 
//		for (IdentifierExpression l : s.getLeft()) {
//			processExpression(l);
//		}
		processExpression(s.getObject());
		return s;
	}

	@Override
	protected Statement processStatement(PushStatement s) {
		for (Expression r : s.getRight()) {
			processExpression(r);
		}		
		processExpression(s.getObject());
		return s;
	}
	
	@Override
	protected Expression processExpression(BinaryExpression e) {
		processExpression(e.getLeft());
		processExpression(e.getRight());
		return e;
	}

	@Override
	protected Expression processExpression(NaryExpression e) {
		for (int i = 0; i < e.getArity(); i++) {
			processExpression(e.getExpression(i));
		}
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
	protected Expression processExpression(IntegerLiteral e) {
		return e;
	}

//	@Override
//	protected Expression processExpression(StringLiteral e) {
//		return e;
//	}

	@Override
	protected Expression processExpression(NullLiteral e) {
		return e;
	}
  
	@Override
	protected Expression processExpression(TupleAccessExpression e) {
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
