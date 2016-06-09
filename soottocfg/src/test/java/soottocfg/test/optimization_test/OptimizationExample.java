package soottocfg.test.optimization_test;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.soot.util.SootTranslationHelpers;

public class OptimizationExample {

	public OptimizationExample() {
	}

	public Method getMethod1() {
		SootTranslationHelpers.v().reset();

		final Program p = new Program();
		final List<soottocfg.cfg.type.Type> rettype = new LinkedList<soottocfg.cfg.type.Type>();
		final Method m = Method.createMethodInProgram(p, "testM", new LinkedList<Variable>(), rettype, null); 
		final SourceLocation fakeSl = new SourceLocation("fake", 0);

		final Collection<Variable> localVars = new HashSet<Variable>();
		final Variable bVar = new Variable("b", BoolType.instance());
		localVars.add(bVar);
		final Expression b = new IdentifierExpression(null, bVar);
		final Expression notB = new UnaryExpression(null, UnaryOperator.LNot, b);

		final Variable rval = new Variable("rval", BoolType.instance());
		final Expression rvalIdent = new IdentifierExpression(null, rval);

		final Expression one = new UnaryExpression(null, UnaryOperator.Neg, IntegerLiteral.minusOne());
		final Expression rhs = new BinaryExpression(null, BinaryOperator.Plus, one, IntegerLiteral.zero());
		final Expression lhs = new BinaryExpression(null, BinaryOperator.Mul, new IntegerLiteral(null, 12), IntegerLiteral.zero());
		final Expression e = new BinaryExpression(null, BinaryOperator.Gt, lhs, rhs);
		Statement s1 = new AssignStatement(fakeSl, b, e);

		final CfgBlock b1 = new CfgBlock(m);
		List<Statement> b1Stmts = new LinkedList<Statement>();
		b1Stmts.add(s1);
		b1.setStatements(b1Stmts);

		final CfgBlock b2 = new CfgBlock(m);
		List<Statement> b2Stmts = new LinkedList<Statement>();
		b2Stmts.add(new AssignStatement(fakeSl, rvalIdent, notB));
		b2.setStatements(b2Stmts);

		final CfgBlock b3 = new CfgBlock(m);
		List<Statement> b3Stmts = new LinkedList<Statement>();
		b3Stmts.add(new AssignStatement(fakeSl, rvalIdent, b));
		b3.setStatements(b3Stmts);

		m.addEdge(b1, b2).setLabel(b);
		m.addEdge(b1, b3).setLabel(notB);

		m.initialize(null, rval, localVars, b1, true);
		return m;
	}

	// This method has the complicated Cfg from Aho 2nd ed p 657 for testing the
	// dominators
	public Method getMethod2() {
		SootTranslationHelpers.v().reset();
		final Program p = new Program();				
		final List<soottocfg.cfg.type.Type> rettype = new LinkedList<soottocfg.cfg.type.Type>();
		final Method m = Method.createMethodInProgram(p, "testM", new LinkedList<Variable>(), rettype, null); 

		/*
		 * Create a dummy block to increase the counter for the block label.
		 * Just to get the blocks numbering to work cause aho starts at b1
		 * 
		 * @Daniel, do we actually need this? Why don't you just change the
		 * oracle?
		 */
		CfgBlock b0 = new CfgBlock(m);
		// and remove that block again immediately.
		m.removeVertex(b0);

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

		m.addEdge(b1, b2);
		m.addEdge(b1, b3);

		m.addEdge(b2, b3);
		m.addEdge(b3, b4);

		m.addEdge(b4, b3);
		m.addEdge(b4, b5);
		m.addEdge(b4, b6);

		m.addEdge(b5, b7);

		m.addEdge(b6, b7);

		m.addEdge(b7, b4);
		m.addEdge(b7, b8);

		m.addEdge(b8, b9);
		m.addEdge(b8, b10);

		m.addEdge(b9, b1);
		m.addEdge(b10, b7);

		m.initialize(null, null, new HashSet<Variable>(), b1, true);
		return m;
	}

}
