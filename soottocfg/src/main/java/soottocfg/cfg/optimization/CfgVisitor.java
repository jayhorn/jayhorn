package soottocfg.cfg.optimization;

import java.util.List;

import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
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
		} else if (s instanceof ArrayReadStatement) {
			return processStatement((ArrayReadStatement) s);
		} else if (s instanceof ArrayStoreStatement) {
			return processStatement((ArrayStoreStatement) s);
		} else {
			throw new RuntimeException("unexpected statement type: " + s);
		}
	}

	protected abstract List<Statement> processStatementList(List<Statement> sl);

	protected abstract Statement processStatement(AssignStatement s);

	protected abstract Statement processStatement(AssertStatement s);

	protected abstract Statement processStatement(AssumeStatement s);

	protected abstract Statement processStatement(CallStatement s);

	protected abstract Statement processStatement(ArrayReadStatement s);

	protected abstract Statement processStatement(ArrayStoreStatement s);

	protected Expression processExpression(Expression e) {
		if (e instanceof BinaryExpression) {
			return processExpression((BinaryExpression) e);
		} else if (e instanceof BooleanLiteral) {
			return processExpression((BooleanLiteral) e);
		} else if (e instanceof IdentifierExpression) {
			return processExpression((IdentifierExpression) e);
		} else if (e instanceof InstanceOfExpression) {
			return processExpression((InstanceOfExpression) e);
		} else if (e instanceof IntegerLiteral) {
			return processExpression((IntegerLiteral) e);
		} else if (e instanceof IteExpression) {
			return processExpression((IteExpression) e);
		} else if (e instanceof UnaryExpression) {
			return processExpression((UnaryExpression) e);
		} else {
			throw new RuntimeException("unexpected expression type: " + e);
		}
	}

	protected abstract List<Expression> processExpressionList(List<Expression> el);

	protected abstract Expression processExpression(BinaryExpression e);

	protected abstract Expression processExpression(BooleanLiteral e);

	protected abstract Expression processExpression(IdentifierExpression e);

	protected abstract Expression processExpression(InstanceOfExpression e);

	protected abstract Expression processExpression(IntegerLiteral e);

	protected abstract Expression processExpression(IteExpression ite);

	protected abstract Expression processExpression(UnaryExpression e);
}