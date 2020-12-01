package soottocfg.cfg.util;

import java.util.List;

import soottocfg.cfg.expression.*;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.expression.literal.StringLiteral;
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

public abstract class CfgVisitor {
	protected Method currentMethod = null;

	protected void setCurrentMethod(Method m) {
		currentMethod = m;
	}

	protected Method getCurrentMethod() {
		return currentMethod;
	}

	// IF you override this, you must include the setCurrentMethod calls in the
	// overridden fn
	protected void processMethod(Method m) {
		setCurrentMethod(m);
		for (CfgBlock b : m.vertexSet()) {
			processCfgBlock(b);
		}
		setCurrentMethod(null);
	}

	private CfgBlock currentCfgBlock = null;

	protected void setCurrentCfgBlock(CfgBlock c) {
		currentCfgBlock = c;
	}

	protected CfgBlock getCurrentCfgBlock() {
		return currentCfgBlock;
	}

	protected abstract void processCfgBlock(CfgBlock block);

	public CfgVisitor() {
	}

	protected Statement processStatement(Statement s) {
		if (s instanceof AssertStatement) {
			return processStatement((AssertStatement) s);
		} else if (s instanceof AssignStatement) {
			return processStatement((AssignStatement) s);
		} else if (s instanceof AssumeStatement) {
			return processStatement((AssumeStatement) s);
		} else if (s instanceof CallStatement) {
			return processStatement((CallStatement) s);
		} else if (s instanceof HavocStatement) {
			return processStatement((HavocStatement) s);
		} else if (s instanceof PullStatement) {
			return processStatement((PullStatement) s);
		} else if (s instanceof PushStatement) {
			return processStatement((PushStatement) s);
		} else if (s instanceof NewStatement) {
			return processStatement((NewStatement) s);
		} else {
			throw new RuntimeException("unexpected statement type: " + s);
		}
	}

	protected abstract List<Statement> processStatementList(List<Statement> sl);

	protected abstract Statement processStatement(AssignStatement s);

	protected abstract Statement processStatement(AssertStatement s);

	protected abstract Statement processStatement(AssumeStatement s);

	protected abstract Statement processStatement(CallStatement s);

	protected abstract Statement processStatement(HavocStatement s);

	protected abstract Statement processStatement(PullStatement s);

	protected abstract Statement processStatement(PushStatement s);
  
	protected abstract Statement processStatement(NewStatement s);
  
	protected Expression processExpression(Expression e) {
		if (e instanceof BinaryExpression) {
			return processExpression((BinaryExpression) e);
		} else if (e instanceof NaryExpression) {
			return processExpression((NaryExpression) e);
		} else if (e instanceof BooleanLiteral) {
			return processExpression((BooleanLiteral) e);
		} else if (e instanceof IdentifierExpression) {
			return processExpression((IdentifierExpression) e);
		} else if (e instanceof IntegerLiteral) {
			return processExpression((IntegerLiteral) e);
//		} else if (e instanceof StringLiteral) {
//			return processExpression((StringLiteral) e);
		} else if (e instanceof IteExpression) {
			return processExpression((IteExpression) e);
		} else if (e instanceof UnaryExpression) {
			return processExpression((UnaryExpression) e);
		} else if (e instanceof NullLiteral) {
			return processExpression((NullLiteral) e);
		} else if (e instanceof TupleAccessExpression) {
			return processExpression((TupleAccessExpression) e);
		} else {
			throw new RuntimeException("unexpected expression type: " + e);
		}
	}

	protected abstract List<Expression> processExpressionList(List<Expression> el);

	protected abstract Expression processExpression(BinaryExpression e);

	protected abstract Expression processExpression(NaryExpression e);

	protected abstract Expression processExpression(BooleanLiteral e);

	protected abstract Expression processExpression(IdentifierExpression e);

	protected abstract Expression processExpression(IntegerLiteral e);

//	protected abstract Expression processExpression(StringLiteral e);

	protected abstract Expression processExpression(IteExpression ite);

	protected abstract Expression processExpression(UnaryExpression e);
	
	protected abstract Expression processExpression(NullLiteral e);
	
	protected abstract Expression processExpression(TupleAccessExpression e);
}

