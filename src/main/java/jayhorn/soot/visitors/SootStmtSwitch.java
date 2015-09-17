/*
 * jimple2boogie - Translates Jimple (or Java) Programs to Boogie
 * Copyright (C) 2013 Martin Schaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaeaef and Stephan Arlt
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package jayhorn.soot.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jayhorn.cfg.expression.BinaryExpression;
import jayhorn.cfg.expression.BinaryExpression.BinaryOperator;
import jayhorn.cfg.expression.Expression;
import jayhorn.cfg.expression.IntegerLiteral;
import jayhorn.cfg.expression.UnaryExpression;
import jayhorn.cfg.expression.UnaryExpression.UnaryOperator;
import jayhorn.cfg.method.CfgBlock;
import jayhorn.cfg.statement.AssertStatement;
import jayhorn.cfg.statement.AssignStatement;
import jayhorn.cfg.statement.Statement;
import jayhorn.soot.SootToCfg;
import jayhorn.soot.util.MethodInfo;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.toolkits.graph.Block;

/**
 * @author schaef
 */
public class SootStmtSwitch implements StmtSwitch {
	
	private final SootMethod sootMethod;
	private final MethodInfo methodInfo;
	private final SootValueSwitch valueSwitch;

	private final Block sootBlock;

	private CfgBlock currentBlock, entryBlock, exitBlock;
	private boolean insideMonitor = false;

	public SootStmtSwitch(Block block, MethodInfo mi) {
		this.methodInfo = mi;
		this.sootBlock = block;
		this.sootMethod = block.getBody().getMethod();

		this.valueSwitch = new SootValueSwitch(this);

		Unit head = block.getHead();
		// check if the block is empty.
		if (head != null) {
			this.entryBlock = methodInfo.lookupCfgBlock(head);
			this.currentBlock = this.entryBlock;
			Iterator<Unit> iterator = block.iterator();			
			while (iterator.hasNext()) {
				Unit u = iterator.next();
				u.apply(this);
			}
		} else {
			this.entryBlock = new CfgBlock();
			this.currentBlock = this.entryBlock;
		}
		
		if (this.currentBlock!=null) {
			this.exitBlock = this.currentBlock;
		} else {			
			this.exitBlock=null;
		}
	}	
		
	public CfgBlock getEntryBlock() {
		return this.entryBlock;
	}

	public CfgBlock getExitBlock() {
		return this.exitBlock;
	}
	
	public MethodInfo getMethodInto() {
		return this.methodInfo;
	}

	public SootMethod getMethod() {
		return this.sootMethod;
	}

	/**
	 * Checks if the current statement is synchronized or inside a monitor
	 * 
	 * @return True if the current statement is inside a monitor or synchronized
	 *         and false, otherwise.
	 */
	public boolean isSynchronizedOrInsideMonitor() {
		return this.insideMonitor || this.sootMethod.isSynchronized();
	}

	public void push(Statement stmt) {
		this.currentBlock.addStatement(stmt);
	}

	private void precheck(Stmt st) {
		//first check if we already created a block
		//for this statement.
		if (methodInfo.findBlock(st) != null) {
			currentBlock = methodInfo.findBlock(st);
		}		
		//If not, and we currently don't have a block,
		//create a new one.
		if (currentBlock == null) {
			currentBlock = methodInfo.lookupCfgBlock(st);
		}
	}

	@Override
	public void caseAssignStmt(AssignStmt arg0) {
		precheck(arg0);
		if (arg0.containsInvokeExpr()) {
			assert(arg0.getRightOp() instanceof InvokeExpr);
			translateMethodInvokation(arg0, arg0.getLeftOp(), arg0.getInvokeExpr());
		} else {
			translateAssignment(arg0, arg0.getLeftOp(), arg0.getRightOp());
		}
	}

	@Override
	public void caseBreakpointStmt(BreakpointStmt arg0) {
		precheck(arg0);
	}

	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt arg0) {
		precheck(arg0);
		arg0.getOp().apply(this.valueSwitch);
		this.valueSwitch.popExpression();
		this.insideMonitor = true;
		// TODO Havoc stuff
	}

	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt arg0) {
		precheck(arg0);
		arg0.getOp().apply(this.valueSwitch);
		this.valueSwitch.popExpression();
		this.insideMonitor = false;
		// TODO:
	}

	@Override
	public void caseGotoStmt(GotoStmt arg0) {
		precheck(arg0);
		CfgBlock target = this.methodInfo.lookupCfgBlock(arg0.getTarget());
		this.currentBlock.addSuccessor(target);
		this.currentBlock = null;
	}

	@Override
	public void caseIdentityStmt(IdentityStmt arg0) {
		precheck(arg0);
		if (arg0.containsInvokeExpr()) {
			assert(arg0.getRightOp() instanceof InvokeExpr);
			translateMethodInvokation(arg0, arg0.getLeftOp(), arg0.getInvokeExpr());
		} else {
			translateAssignment(arg0, arg0.getLeftOp(), arg0.getRightOp());
		}
	}

	@Override
	public void caseIfStmt(IfStmt arg0) {
		precheck(arg0);
		arg0.getCondition().apply(valueSwitch);
		Expression cond = valueSwitch.popExpression();
		CfgBlock thenBlock = methodInfo.lookupCfgBlock(arg0.getTarget());
		this.currentBlock.addConditionalSuccessor(cond, thenBlock);

		/*
		 * In jimple, conditionals are of the form if (x) goto y; So we end the
		 * current block and create two new blocks for then and else branch. The
		 * new currenBlock becomes the else branch.
		 */
		Unit next = this.sootBlock.getSuccOf(arg0);
		if (next != null) {
			CfgBlock elseBlock = methodInfo.lookupCfgBlock(this.sootBlock
					.getSuccOf(arg0));
			this.currentBlock.addConditionalSuccessor(new UnaryExpression(
					UnaryOperator.LNot, cond), elseBlock);
			this.currentBlock = elseBlock;
		} else {
			this.currentBlock.addConditionalSuccessor(new UnaryExpression(
					UnaryOperator.LNot, cond), methodInfo.getSink());
			this.currentBlock = null;
		}
	}

	@Override
	public void caseInvokeStmt(InvokeStmt arg0) {
		precheck(arg0);
		translateMethodInvokation(arg0, null, arg0.getInvokeExpr());
	}

	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt arg0) {
		precheck(arg0);

		List<Expression> cases = new LinkedList<Expression>();
		List<Unit> targets = new LinkedList<Unit>();		
		
		arg0.getKey().apply(this.valueSwitch);
		Expression key = this.valueSwitch.popExpression();
		for (int i = 0; i < arg0.getTargetCount(); i++) {
			BinaryExpression cond = new BinaryExpression(BinaryOperator.Eq,
					key, new IntegerLiteral(arg0.getLookupValue(i)));
			cases.add(cond);
			targets.add(arg0.getTarget(i));			
		}
		translateSwitch(cases, targets, arg0.getDefaultTarget());
	}

	
	@Override
	public void caseNopStmt(NopStmt arg0) {
		precheck(arg0);
	}

	@Override
	public void caseRetStmt(RetStmt arg0) {
		precheck(arg0);
		throw new RuntimeException("Not implemented " + arg0);
	}

	@Override
	public void caseReturnStmt(ReturnStmt arg0) {
		precheck(arg0);
		arg0.getOp().apply(valueSwitch);
		Expression returnValue = valueSwitch.popExpression();
		currentBlock.addStatement(new AssignStatement(arg0, methodInfo.getReturnVariable(), returnValue));
		currentBlock.addSuccessor(methodInfo.getSink());
		currentBlock = null;
	}

	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
		precheck(arg0);
		currentBlock.addSuccessor(methodInfo.getSink());
		currentBlock = null;
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt arg0) {
		precheck(arg0);
		List<Expression> cases = new LinkedList<Expression>();
		List<Unit> targets = new LinkedList<Unit>();

		arg0.getKey().apply(valueSwitch);
		Expression key = valueSwitch.popExpression();
		int counter = 0;
		for (int i = arg0.getLowIndex(); i <= arg0.getHighIndex(); i++) {
			Expression cond = new BinaryExpression(BinaryOperator.Eq, key,
					new IntegerLiteral(i));
			cases.add(cond);
			targets.add(arg0.getTarget(counter));
			counter++;
		}
		translateSwitch(cases, targets, arg0.getDefaultTarget());
	}

	@Override
	public void caseThrowStmt(ThrowStmt arg0) {
		precheck(arg0);
		arg0.getOp().apply(valueSwitch);
		Expression exception = valueSwitch.popExpression();
		currentBlock.addStatement(new AssignStatement(arg0, methodInfo.getExceptionVariable(), exception));
		// TODO connect to the next block
	}

	@Override
	public void defaultCase(Object arg0) {
		throw new RuntimeException("Case not implemented");
	}

	/**
	 * Translates a set of switche cases into a nested IfThenElse of
	 * the form:
	 * if (case1) goto target1 else { if (case2) goto target2 else { ...
	 * Asserts that size of cases and targets is equal.
	 * @param cases 
	 * @param targets
	 * @param defaultTarget
	 */
	private void translateSwitch(List<Expression> cases, List<Unit> targets, Unit defaultTarget) {
		assert (cases.size()==targets.size());
		for (int i=0; i<cases.size(); i++) {
			CfgBlock elseCase;
			if (i==cases.size()-1 && defaultTarget!=null) {
				elseCase = methodInfo.lookupCfgBlock(defaultTarget);
			} else {
				elseCase = new CfgBlock();
			}
			currentBlock.addConditionalSuccessor(cases.get(i), methodInfo.lookupCfgBlock(targets.get(i)));
			currentBlock.addConditionalSuccessor(new UnaryExpression(UnaryOperator.LNot, cases.get(i)), elseCase);
			currentBlock = elseCase;
		}
	}

	private void translateMethodInvokation(Unit u, Value optionalLhs, InvokeExpr call) {
		if (call.getMethod()==SootToCfg.getAssertMethod()) {
			assert (optionalLhs==null);
			assert(call.getArgCount()==1);
			call.getArg(0).apply(valueSwitch);			
			currentBlock.addStatement(new AssertStatement(u,valueSwitch.popExpression())); 
			return;
		}
		//TODO
//		for (RefType t : TrapManager.getExceptionTypesOf(u, sootBlock.getBody())) {
//			System.err.println("\t type "+t);
//		}
//
//		for (Trap t : TrapManager.getTrapsAt(u, sootBlock.getBody())) {
//			System.err.println("\t type "+t.getException());
//		}
	}

	private void translateAssignment(Unit u, Value lhs, Value rhs) {		
		lhs.apply(valueSwitch);
		Expression left = valueSwitch.popExpression();
		rhs.apply(valueSwitch);		
		Expression right = valueSwitch.popExpression();
		currentBlock.addStatement(new AssignStatement(u, left, right));
	}
	
}
