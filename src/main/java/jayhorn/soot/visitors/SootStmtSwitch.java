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

import java.util.LinkedList;
import java.util.List;

import jayhorn.cfg.statement.Statement;
import jayhorn.soot.memory_model.MemoryModel;
import soot.SootMethod;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;

/**
 * @author schaef
 */
public class SootStmtSwitch implements StmtSwitch {

	private final SootMethod currentMethod;
	private final MemoryModel memoryModel;
	private final SootValueSwitch valueSwitch;
	private final List<Statement> statementStack;

	private boolean insideMonitor = false;

	public SootStmtSwitch(SootMethod sm, MemoryModel mm) {
		this.currentMethod = sm;
		this.memoryModel = mm;
		this.statementStack = new LinkedList<Statement>();
		this.valueSwitch = new SootValueSwitch(this);

		this.memoryModel.setStmtSwitch(this);
		this.memoryModel.setValueSwitch(valueSwitch);
	}

	public SootMethod getMethod() {
		return this.currentMethod;
	}

	public MemoryModel getMemoryModel() {
		return this.memoryModel;
	}

	/**
	 * Checks if the current statement is synchronized or inside a monitor
	 * 
	 * @return True if the current statement is inside a monitor or synchronized
	 *         and false, otherwise.
	 */
	public boolean isSynchronizedOrInsideMonitor() {
		return this.insideMonitor || this.currentMethod.isSynchronized();
	}

	public void push(Statement stmt) {
		this.statementStack.add(stmt);
	}

	/**
	 * Pop all statements from the working stack.
	 * 
	 * @return List of statements on the stack.
	 */
	public List<Statement> popAll() {
		List<Statement> ret = new LinkedList<Statement>();
		ret.addAll(this.statementStack);
		this.statementStack.clear();
		return ret;
	}

	// private void injectLabelStatements(LookupSwitchStmt arg0) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void caseAssignStmt(AssignStmt arg0) {
		// TODO
		arg0.getLeftOp().apply(valueSwitch);
		arg0.getRightOp().apply(valueSwitch);
	}

	@Override
	public void caseBreakpointStmt(BreakpointStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt arg0) {
		arg0.getOp().apply(this.valueSwitch);
		this.valueSwitch.popExpression();
		this.insideMonitor = true;
		// TODO Havoc stuff
	}

	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt arg0) {
		arg0.getOp().apply(this.valueSwitch);
		this.valueSwitch.popExpression();
		this.insideMonitor = false;
		// TODO:
	}

	@Override
	public void caseGotoStmt(GotoStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseIdentityStmt(IdentityStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseIfStmt(IfStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseInvokeStmt(InvokeStmt arg0) {
		// TODO Auto-generated method stub
		arg0.getInvokeExpr().apply(this.valueSwitch);
	}

	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt arg0) {
		// injectLabelStatements(arg0);
		// LinkedList<ProverExpr> cases = new LinkedList<ProverExpr>();
		// LinkedList<Statement[]> targets = new LinkedList<Statement[]>();
		//
		// arg0.getKey().apply(this.valueSwitch);
		// ProverExpr key = this.valueSwitch.getExpression();
		// for (int i = 0; i < arg0.getTargetCount(); i++) {
		// this.prover.mkEq(key, this.prover.mkLiteral(arg0.getLookupValue(i)));
		// Expression cond = this.pf.mkBinaryExpression(
		// this.pf.getBoolType(), BinaryOperator.COMPEQ, key, this.pf
		// .mkIntLiteral(Integer.toString(arg0.getLookupValue(i))));
		// cases.add(cond);
		// Statement[] gototarget = { this.pf.mkGotoStatement(GlobalsCache.v()
		// .getUnitLabel((Stmt) arg0.getTarget(i))) };
		// targets.add(gototarget);
		// }

		//
		// Statement[] gototarget = { this.pf.mkGotoStatement(
		//
		// GlobalsCache.v().getUnitLabel((Stmt) arg0.getDefaultTarget())) };
		// targets.add(gototarget);
		//
		// translateSwitch(cases, targets);

	}

	@Override
	public void caseNopStmt(NopStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseRetStmt(RetStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseReturnStmt(ReturnStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void caseThrowStmt(ThrowStmt arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void defaultCase(Object arg0) {
		// TODO Auto-generated method stub

	}

}
