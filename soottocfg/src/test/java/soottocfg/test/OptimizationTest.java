package soottocfg.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.optimization.CfgUpdater;
import soottocfg.cfg.optimization.ConstantProp;
import soottocfg.cfg.optimization.DeadCodeElimination;
import soottocfg.cfg.optimization.ExpressionInliner;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;

public class OptimizationTest {
	public OptimizationTest() {}

	@Test
	public void test_dce(){
		System.out.println("Testing Dead Code Elim");

		OptimizationExample testGen = new OptimizationExample();
		Method m = testGen.getMethod1();

		CfgUpdater ie = new ExpressionInliner();
CfgUpdater dce = new DeadCodeElimination();
		ie.runFixpt(m);
dce.runFixpt(m);

		for(CfgBlock b : m.getCfg()){
			System.out.println(b);
//			if(b.getLabel().equals("Block0")){
//				String expected = "Block0:\n(ln 0)\tb := ((12 * 0) > ((--1) + 0))\n\tgoto:\n\t  if ((12 * 0) > ((--1) + 0)): Block1\n\t  if (!((12 * 0) > ((--1) + 0))): Block2\n";
//				Assert.assertTrue(expected.equals(b.toString()));
//			} else if(b.getLabel().equals("Block1")){
//				String expected = "Block1:\n(ln 0)	rval := (!((12 * 0) > ((--1) + 0)))\n	return\n";
//				Assert.assertTrue(expected.equals(b.toString()));
//			} else if (b.getLabel().equals("Block2")) {
//				String expected = "Block2:\n(ln 0)	rval := (((12 * 0) > ((--1) + 0)))\n	return\n";
//				Assert.assertTrue(expected.equals(b.toString()));
//			}	else {
//				Assert.fail("Shouldn't be an extra block here");
//			}
		}		
	}
	
	
	@Test
	public void test_ie(){
		System.out.println("Testing Expression Inliner");

		OptimizationExample testGen = new OptimizationExample();
		Method m = testGen.getMethod1();

		CfgUpdater opt = new ExpressionInliner();

		opt.runFixpt(m);

		for(CfgBlock b : m.getCfg()){
			if(b.getLabel().equals("Block0")){
				String expected = "Block0:\n(ln 0)\tb := ((12 * 0) > ((--1) + 0))\n\tgoto:\n\t  if ((12 * 0) > ((--1) + 0)): Block1\n\t  if (!((12 * 0) > ((--1) + 0))): Block2\n";
				Assert.assertEquals(expected, b.toString());
			} else if(b.getLabel().equals("Block1")){
				String expected = "Block1:\n(ln 0)	rval := (!((12 * 0) > ((--1) + 0)))\n	return\n";
				Assert.assertEquals(expected, b.toString());
			} else if (b.getLabel().equals("Block2")) {
				String expected = "Block2:\n(ln 0)	rval := ((12 * 0) > ((--1) + 0))\n	return\n";
				Assert.assertEquals(expected, b.toString());
			}	else {
				Assert.fail("Shouldn't be an extra block here");
			}
		}		
	}


	@Test 
	public void test_cp(){

		System.out.println("Testing ConstantProp");

		OptimizationExample testGen = new OptimizationExample();
		Method m = testGen.getMethod1();

		CfgUpdater cp = new ConstantProp();

		cp.runFixpt(m);

		for(CfgBlock b : m.getCfg()){
			if(b.getLabel().equals("Block0")){
				List<Statement> sl = b.getStatements();
				Assert.assertEquals(1, sl.size());
				Statement s = sl.get(0);
				Assert.assertTrue(s instanceof AssignStatement);
				Expression rhs = ((AssignStatement) s).getRight();
				Assert.assertTrue(rhs instanceof BooleanLiteral);
				Assert.assertEquals(false,((BooleanLiteral) rhs).getValue());
			}
		}		
	}
}
