package soottocfg.cfg.optimization;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Verify;

import soottocfg.cfg.expression.*;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.NullLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
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
import soottocfg.cfg.util.CfgVisitor;

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
	protected Statement processStatement(PullStatement s) {
		List<IdentifierExpression> lhs = new LinkedList<IdentifierExpression>(); 
		for (IdentifierExpression l : s.getLeft()) {
			lhs.add((IdentifierExpression)processExpression(l));
		}
		IdentifierExpression obj = (IdentifierExpression) processExpression(s.getObject());
		return new PullStatement(s.getSourceLocation(), s.getClassSignature(), obj, lhs);
	}

	@Override
	protected Statement processStatement(PushStatement s) {
		List<Expression> rhs = new LinkedList<Expression>(); 
		for (Expression r : s.getRight()) {
			rhs.add(processExpression(r));
		}		
		IdentifierExpression obj = (IdentifierExpression) processExpression(s.getObject());
		return new PushStatement(s.getSourceLocation(), s.getClassSignature(), obj, rhs);
	}
	
	@Override
	protected Statement processStatement(AssignStatement s) {
		Expression lhs = processExpression(s.getLeft());
		Expression rhs = processExpression(s.getRight());
		return new AssignStatement(s.getSourceLocation(), lhs, rhs);
	}

	@Override
	protected Statement processStatement(AssumeStatement s) {
		Expression e = processExpression(s.getExpression());
		return new AssumeStatement(s.getSourceLocation(), e);
	}

	@Override
	protected Statement processStatement(CallStatement s) {
		List<Expression> args = processExpressionList(s.getArguments());
		List<Expression> rec = new LinkedList<Expression>();
		for (Expression e : s.getReceiver()) {
			rec.add(processExpression(e));
		}
		return new CallStatement(s.getSourceLocation(), s.getCallTarget(), args, rec);
	}
	
	@Override
	protected Statement processStatement(HavocStatement s) {
            Expression newVar = processExpression(s.getHavocedExpression());
            Verify.verify(newVar instanceof IdentifierExpression);
            return new HavocStatement(s.getSourceLocation(),
                                      (IdentifierExpression)newVar);
	}
	
	@Override
	protected Statement processStatement(NewStatement s) {
		Expression e = processExpression(s.getLeft());
		Verify.verify(e instanceof IdentifierExpression);
		return new NewStatement(s.getSourceLocation(), (IdentifierExpression) e, s.getClassVariable());
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
	protected Expression processExpression(NaryExpression e) {
		Expression[] processedExpressions = new Expression[e.getArity()];
		for (int i = 0; i < e.getArity(); i++) {
			processedExpressions[i] = processExpression(e.getExpression(i));
		}
		return new NaryExpression(e.getSourceLocation(), e.getOp(), processedExpressions);
	}

	@Override
	protected Expression processExpression(BooleanLiteral e) {
		return e;
	}

	@Override
	protected Expression processExpression(NullLiteral e) {
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
//		return e.deepCopy();
//	}

	@Override
	protected Expression processExpression(TupleAccessExpression e) {
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
