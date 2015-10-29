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
import soottocfg.soot.util.SootTranslationHelpers;

public class OptimizationExample {
	
	public OptimizationExample() {}
	
	public Method getMethod1(){	
		SootTranslationHelpers.v().reset();

		final Method m = new Method("testM");		
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
		
		final CfgBlock b1 = new CfgBlock(m);
		List<Statement> b1Stmts = new LinkedList<Statement>();
		b1Stmts.add(s1);
		b1.setStatements(b1Stmts);
		
		final CfgBlock b2 = new CfgBlock(m);
		List<Statement> b2Stmts = new LinkedList<Statement>();
		b2Stmts.add(new AssignStatement(fakeSl,rvalIdent,notB));
		b2.setStatements(b2Stmts);
		
		final CfgBlock b3 = new CfgBlock(m);
		List<Statement> b3Stmts = new LinkedList<Statement>();
		b3Stmts.add(new AssignStatement(fakeSl,rvalIdent,b));
		b3.setStatements(b3Stmts);
		
		b1.addConditionalSuccessor(b, b2);
		b1.addConditionalSuccessor(notB, b3);
		m.initialize(null, rval, exceptional, new LinkedList<Variable>(), localVars, b1, true);
		return m;
	}
	
	//This method has the complicated Cfg from Aho 2nd ed p 657 for testing the dominators
	public Method getMethod2(){	
		SootTranslationHelpers.v().reset();
		final Method m = new Method("testM2");
		
		CfgBlock b0 = new CfgBlock(m);//Don't bother using this :)  Just to get the blocks numbering to work cause aho starts at b1
		b0.getPredecessors();//useless call to quiet findbugs
		
		CfgBlock b1 = new CfgBlock(m);
		CfgBlock b2 = new CfgBlock(m);
		CfgBlock b3 = new CfgBlock(m);
		CfgBlock b4 = new CfgBlock(m);
		CfgBlock b5 = new CfgBlock(m);
		CfgBlock b6 = new CfgBlock(m);
		CfgBlock b7 = new CfgBlock(m);
		CfgBlock b8 = new CfgBlock(m);
		CfgBlock b9 = new CfgBlock(m);
		CfgBlock b10 = new CfgBlock(m);
		
		b1.addSuccessor(b2);
		b1.addSuccessor(b3);
		
		b2.addSuccessor(b3);
		
		b3.addSuccessor(b4);

		b4.addSuccessor(b3);
		b4.addSuccessor(b5);
		b4.addSuccessor(b6);
		
		b5.addSuccessor(b7);
		
		b6.addSuccessor(b7);

		b7.addSuccessor(b4);
		b7.addSuccessor(b8);
		
		b8.addSuccessor(b9);
		b8.addSuccessor(b10);
		
		b9.addSuccessor(b1);
		b10.addSuccessor(b7);
		
		m.initialize(null, null, null, new LinkedList<Variable>(), new HashSet<Variable>(), b1, true);		
		return m;
	}

	
	
}
