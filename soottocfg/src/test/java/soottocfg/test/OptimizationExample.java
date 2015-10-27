package soottocfg.test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import java.util.List;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.soot.util.SootTranslationHelpers;

public class OptimizationExample {
	
	public OptimizationExample() {}
	
	public Method getMethod1(){	
		SootTranslationHelpers.v().reset();
		final SourceLocation fakeSl = new SourceLocation("fake",0);
		
		final Collection<Variable> localVars = new HashSet<Variable>();
		final Variable bVar = new Variable("b",BoolType.instance());
		localVars.add(bVar);
		final Expression b = new IdentifierExpression(bVar);
		final Expression notB = new UnaryExpression(UnaryOperator.LNot,b);
		
		final Variable rval = new Variable("rval",BoolType.instance());
		final Expression rvalIdent = new IdentifierExpression(rval);
		final Variable exceptional = new Variable("exception",BoolType.instance());
		
		final Expression one = new UnaryExpression(UnaryOperator.Neg,IntegerLiteral.minusOne());
		final Expression rhs = new BinaryExpression(BinaryOperator.Plus,one,IntegerLiteral.zero());
		final Expression lhs = new BinaryExpression(BinaryOperator.Mul,new IntegerLiteral(12),IntegerLiteral.zero());
		final Expression e = new BinaryExpression(BinaryOperator.Gt,lhs,rhs);
		Statement s1 = new AssignStatement(fakeSl,b,e);
		
		final CfgBlock b1 = new CfgBlock();
		List<Statement> b1Stmts = new LinkedList<Statement>();
		b1Stmts.add(s1);
		b1.setStatements(b1Stmts);

		
		final CfgBlock b2 = new CfgBlock();
		List<Statement> b2Stmts = new LinkedList<Statement>();
		b2Stmts.add(new AssignStatement(fakeSl,rvalIdent,notB));
		b2.setStatements(b2Stmts);
		
		final CfgBlock b3 = new CfgBlock();
		List<Statement> b3Stmts = new LinkedList<Statement>();
		b3Stmts.add(new AssignStatement(fakeSl,rvalIdent,b));
		b3.setStatements(b3Stmts);
		
		b1.addConditionalSuccessor(b, b2);
		b1.addConditionalSuccessor(notB, b3);
		final Method m = new Method("testM");
		m.initialize(null, rval, exceptional, new LinkedList<Variable>(), localVars, b1, true);
		return m;
	}
	
}
