package soottocfg.cfg.optimization;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;

import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.Type;

//DSN should this be an abstract class
//DSN There might be value in tracking whether an expression actually changed.  Possible optimization
public class CfgUpdater extends CfgVisitor {

	protected Boolean changed = false;

	public boolean updateMethod(Method m) {
		changed = false;
		processMethod(m);
		return changed;
	}

	public boolean runFixpt(Method m) {
		boolean everChanged = false;
		while (updateMethod(m)) {
			everChanged = true;
		}
		return everChanged;
	}

	public CfgUpdater() {
	}

	@Override
	protected void processCfgBlock(CfgBlock block) {
		setCurrentCfgBlock(block);
		// We need to update the statements
		block.setStatements(processStatementList(block.getStatements()));
		// and also the graph conditions, in case those have been changed by the
		// analysis
		for (CfgEdge edge : block.getMethod().outgoingEdgesOf(block)) {
			if (edge.getLabel().isPresent()) {
				edge.setLabel(processExpression(edge.getLabel().get()));
			}
		}
		setCurrentCfgBlock(null);
	}

	@Override
	protected List<Statement> processStatementList(List<Statement> sl) {
		List<Statement> rval = new LinkedList<Statement>();
		for (Statement s : sl) {
			rval.add(processStatement(s));
		}
		return rval;
	}

	@Override
	protected Statement processStatement(AssertStatement s) {
		Expression e = processExpression(s.getExpression());
		return new AssertStatement(s.getSourceLocation(), e);
	}

	@Override
	protected Statement processStatement(AssignStatement s) {
		Expression lhs = processExpression(s.getLeft());
		Expression rhs = processExpression(s.getRight());
		return new AssignStatement(s.getSourceLocation(), lhs, rhs);
	}

	@Override
	protected Statement processStatement(ArrayReadStatement s) {
		IdentifierExpression base = (IdentifierExpression)processExpression(s.getBase());
		Expression[] ids = s.getIndices();
		List<Expression> indices = new LinkedList<Expression>();
		for (int i = 0; i < ids.length; i++) {
			indices.add(processExpression(ids[i]));
		}
		IdentifierExpression lhs = (IdentifierExpression) processExpression(s.getLeftValue());
		return new ArrayReadStatement(s.getSourceLocation(), base, indices.toArray(new Expression[indices.size()]),
				lhs);
	}

	@Override
	protected Statement processStatement(ArrayStoreStatement s) {
		IdentifierExpression base = (IdentifierExpression)processExpression(s.getBase());
		Expression[] ids = s.getIndices();
		List<Expression> indices = new LinkedList<Expression>();
		for (int i = 0; i < ids.length; i++) {
			indices.add(processExpression(ids[i]));
		}
		Expression value = processExpression(s.getValue());
		return new ArrayStoreStatement(s.getSourceLocation(), base, indices.toArray(new Expression[indices.size()]),
				value);
	}

	@Override
	protected Statement processStatement(AssumeStatement s) {
		Expression e = processExpression(s.getExpression());
		return new AssumeStatement(s.getSourceLocation(), e);
	}

	protected Statement processStatement(CallStatement s) {
		List<Expression> args = processExpressionList(s.getArguments());
		Optional<Expression> receiver = Optional.absent();
		if (s.getReceiver().isPresent()) {
			receiver = Optional.of(processExpression(s.getReceiver().get()));
		}		
		return new CallStatement(s.getSourceLocation(), s.getCallTarget(), args, receiver);
	}

	/// Expressions
	@Override
	protected List<Expression> processExpressionList(List<Expression> el) {
		List<Expression> rval = new LinkedList<Expression>();
		for (Expression e : el) {
			rval.add(processExpression(e));
		}
		return rval;
	}

	@Override
	protected Expression processExpression(BinaryExpression e) {
		Expression left = processExpression(e.getLeft());
		Expression right = processExpression(e.getRight());
		return new BinaryExpression(e.getSourceLocation(), e.getOp(), left, right);
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
		Type t = e.getCheckedType();
		return new InstanceOfExpression(e.getSourceLocation(), exp, t);
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
		return new IteExpression(ite.getSourceLocation(), i, t, e);
	}

	@Override
	protected Expression processExpression(UnaryExpression e) {
		Expression exp = processExpression(e.getExpression());
		return new UnaryExpression(e.getSourceLocation(), e.getOp(), exp);
	}
}
