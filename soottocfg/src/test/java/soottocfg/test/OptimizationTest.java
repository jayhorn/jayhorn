package soottocfg.test;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import soottocfg.util.PrettyPrinter;

public class OptimizationTest {
	public OptimizationTest() {}

	@Test 
	public void test_dom1() {
		System.out.println("Testing dominators 1");

		OptimizationExample testGen = new OptimizationExample();
		Method m = testGen.getMethod1();
		Map<CfgBlock,Set<CfgBlock>> dom = m.computeDominators();
		String actual = PrettyPrinter.ppCfgBlockMapSet(dom);
		String expected = 
				"{Block0=[Block0],\n" + 
				" Block1=[Block0, Block1],\n" + 
				" Block2=[Block0, Block2]}";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test_dom2() {
		System.out.println("Testing dominators 2");

		OptimizationExample testGen = new OptimizationExample();
		Method m = testGen.getMethod2();
		Map<CfgBlock,Set<CfgBlock>> dom = m.computeDominators();
		String actual = PrettyPrinter.ppCfgBlockMapSet(dom);
		String expected = 
				"{Block1=[Block1],\n" +
				" Block10=[Block1, Block10, Block3, Block4, Block7, Block8],\n" + 
				" Block2=[Block1, Block2],\n" + 
				" Block3=[Block1, Block3],\n" + 
				" Block4=[Block1, Block3, Block4],\n" + 
				" Block5=[Block1, Block3, Block4, Block5],\n" + 
				" Block6=[Block1, Block3, Block4, Block6],\n" + 
				" Block7=[Block1, Block3, Block4, Block7],\n" + 
				" Block8=[Block1, Block3, Block4, Block7, Block8],\n" + 
				" Block9=[Block1, Block3, Block4, Block7, Block8, Block9]}";
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void test_ie_dce_cp(){

		System.out.println("Testing ie, then de, then cp then dce again");

		OptimizationExample testGen = new OptimizationExample();
		Method m = testGen.getMethod1();
		CfgUpdater ie = new ExpressionInliner();
		CfgUpdater dce = new DeadCodeElimination();
		CfgUpdater cp = new ConstantProp();

		ie.runFixpt(m);
		dce.runFixpt(m);
		cp.runFixpt(m);
		dce.runFixpt(m);

		for(CfgBlock b : m.getCfg()){
			if(b.getLabel().equals("Block0")){
				String expected = "Block0:\n	goto:\n	  if true: Block2\n";
				Assert.assertEquals(expected, b.toString());
			} else if (b.getLabel().equals("Block2")) {
				String expected = "Block2:\n(ln 0)	rval := false\n	return\n";
				Assert.assertEquals(expected, b.toString());
			}	else {
				Assert.fail("Shouldn't be an extra block here");
			}
		}		
		System.out.println("success at ie_dce_cp");
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
