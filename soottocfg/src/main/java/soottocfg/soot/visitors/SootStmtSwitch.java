/*
 * jimple2boogie - Translates Jimple (or Java) Programs to Boogie
 * Copyright (C) 2013 Martin Schaef and Stephan Arlt
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

package soottocfg.soot.visitors;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import soot.ArrayType;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.IntType;
import soot.ByteType;
import soot.CharType;
import soot.ShortType;
import soot.LongType;
import soot.BooleanType;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.Type;
import soot.jimple.Jimple;
import soot.jimple.AnyNewExpr;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.FieldRef;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LengthExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NopStmt;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThrowStmt;
import soot.toolkits.graph.CompleteUnitGraph;
import soottocfg.Options;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.*;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.BooleanLiteral;
import soottocfg.cfg.expression.literal.StringLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.HavocStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 */
public class SootStmtSwitch implements StmtSwitch {

	private final SootMethod sootMethod;
	private final Body sootBody;

	private final MethodInfo methodInfo;
	private final SootValueSwitch valueSwitch;

	private final PatchingChain<Unit> units;
	private final CompleteUnitGraph unitGraph;

	private CfgBlock currentBlock, entryBlock, exitBlock;
	private boolean insideMonitor = false;

	private Stmt currentStmt;

	protected SourceLocation loc;

	public SootStmtSwitch(Body body, MethodInfo mi) {
		this.methodInfo = mi;
		this.sootBody = body;
		this.sootMethod = sootBody.getMethod();

		this.valueSwitch = new SootValueSwitch(this);

		units = body.getUnits();
		Unit head = units.getFirst();
		unitGraph = new CompleteUnitGraph(sootBody);
		// check if the block is empty.
		if (head != null) {
			this.entryBlock = methodInfo.lookupCfgBlock(head);
			this.currentBlock = this.entryBlock;
			Iterator<Unit> iterator = units.iterator();
			while (iterator.hasNext()) {
				Unit u = iterator.next();
				u.apply(this);
			}
		} else {
			if (methodInfo.getMethod().getSource()==null) {
				methodInfo.getMethod().setSource(new CfgBlock(methodInfo.getMethod()));
			}
//			this.entryBlock = new CfgBlock(methodInfo.getMethod());
			this.entryBlock = methodInfo.getMethod().getSource();
			this.currentBlock = this.entryBlock;
		}

		if (this.currentBlock != null) {
			this.exitBlock = this.currentBlock;
		} else {
			this.exitBlock = null;
		}
		// TODO: connect stuff to exit.
	}

	public CfgBlock getEntryBlock() {
		return this.entryBlock;
	}

	public CfgBlock getExitBlock() {
		return this.exitBlock;
	}

	public MethodInfo getMethodInfo() {
		return this.methodInfo;
	}

	public SootMethod getMethod() {
		return this.sootMethod;
	}

	public Stmt getCurrentStmt() {
		return this.currentStmt;
	}

	public SourceLocation getCurrentLoc() {
		return this.loc;
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

	private void connectBlocks(CfgBlock from, CfgBlock to) {
		Preconditions.checkArgument(!methodInfo.getMethod().containsEdge(from, to));
		this.methodInfo.getMethod().addEdge(from, to);
	}

	private void connectBlocks(CfgBlock from, CfgBlock to, Expression label) {
		Preconditions.checkArgument(!methodInfo.getMethod().containsEdge(from, to));
		this.methodInfo.getMethod().addEdge(from, to).setLabel(label);
	}

	private void precheck(Stmt st) {
		this.currentStmt = st;
		loc = SootTranslationHelpers.v().getSourceLocation(currentStmt);

		if (currentBlock != null) {
			// first check if we already created a block
			// for this statement.
			CfgBlock block = methodInfo.findBlock(st);
			if (block != null) {
				if (block != currentBlock) {
					connectBlocks(currentBlock, block);
					currentBlock = block;
				} else {
					// do nothing.
				}
			} else {
				if (unitGraph.getPredsOf(st).size() > 1) {
					// then this statement might be reachable via a back edge
					// and we have to create a new block for it.
					CfgBlock newBlock = methodInfo.lookupCfgBlock(st);
					connectBlocks(currentBlock, newBlock);
					currentBlock = newBlock;
				} else {
					// do nothing.
				}
			}
		} else {
			// If not, and we currently don't have a block,
			// create a new one.
			currentBlock = methodInfo.lookupCfgBlock(st);
		}
	}

	/*
	 * Below follow the visitor methods from StmtSwitch
	 *
	 */

	@Override
	public void caseAssignStmt(AssignStmt arg0) {
		precheck(arg0);
		translateDefinitionStmt(arg0);
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
		connectBlocks(currentBlock, target);
		this.currentBlock = null;
	}

	@Override
	public void caseIdentityStmt(IdentityStmt arg0) {
		precheck(arg0);
		translateDefinitionStmt(arg0);
	}

	@Override
	public void caseIfStmt(IfStmt arg0) {
		precheck(arg0);
		arg0.getCondition().apply(valueSwitch);
		Expression cond = valueSwitch.popExpression();
		// apply the switch twice. Otherwise the conditional and its negation
		// are aliased.
		arg0.getCondition().apply(valueSwitch);
		Expression negCond = new UnaryExpression(loc, UnaryOperator.LNot, valueSwitch.popExpression());

		// create a new (empty) block for the fan out
		// CfgBlock block = methodInfo.lookupCfgBlock(arg0);
		// if (currentBlock!=null) {
		// connectBlocks(currentBlock, block);
		// }
		// currentBlock = block;

		/*
		 * In jimple, conditionals are of the form if (x) goto y; So we end the
		 * current block and create two new blocks for then and else branch. The
		 * new currenBlock becomes the else branch.
		 */

		Unit next = units.getSuccOf(arg0);
		/*
		 * In rare cases of empty If- and Else- blocks, next and
		 * arg0.getTraget() are the same. For these cases, we do not generate an
		 * If statement, but still translate the conditional in case it may
		 * throw an exception.
		 */
		if (next == arg0.getTarget()) {
			// ignore the IfStmt.
			return;
		}

		CfgBlock thenBlock = methodInfo.lookupCfgBlock(arg0.getTarget());
		connectBlocks(currentBlock, thenBlock, cond);
		if (next != null) {
			CfgBlock elseBlock = methodInfo.lookupCfgBlock(next);
			connectBlocks(currentBlock, elseBlock, negCond);
			this.currentBlock = elseBlock;
		} else {
			connectBlocks(currentBlock, methodInfo.getSink(), negCond);
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
		throw new RuntimeException("Should have been eliminated by SwitchStatementRemover");
	}

	@Override
	public void caseNopStmt(NopStmt arg0) {
		precheck(arg0);
	}

	@Override
	public void caseRetStmt(RetStmt arg0) {
		throw new RuntimeException("Not implemented " + arg0);
	}

	@Override
	public void caseReturnStmt(ReturnStmt arg0) {
		precheck(arg0);
		arg0.getOp().apply(valueSwitch);
		Expression returnValue = valueSwitch.popExpression();
		currentBlock.addStatement(new AssignStatement(SootTranslationHelpers.v().getSourceLocation(arg0),
				methodInfo.getReturnVariable().mkExp(loc), returnValue));
		connectBlocks(currentBlock, methodInfo.getSink());
		currentBlock = null;
	}

	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt arg0) {
		precheck(arg0);
		// if (sootMethod.isConstructor()) {
		// SourceLocation loc = getCurrentLoc();
		// SootClass currentClass =
		// SootTranslationHelpers.v().getCurrentMethod().getDeclaringClass();
		// List<SootField> fields =
		// SootTranslationHelpers.findFieldsRecursively(currentClass);
		// JimpleBody jb = (JimpleBody)this.sootMethod.getActiveBody();
		//
		// for (int i=1; i<methodInfo.getOutVariables().size();i++) {
		// Variable outVar = methodInfo.getOutVariables().get(i);
		// Variable tmp = methodInfo.createFreshLocal("afdafd",
		// outVar.getType(), false, false);
		//
		// AssignStatement as = new AssignStatement(loc,
		// new IdentifierExpression(loc, outVar),
		// new IdentifierExpression(loc, tmp));
		// currentBlock.addStatement(as);
		// }
		// }
		connectBlocks(currentBlock, methodInfo.getSink());
		currentBlock = null;
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt arg0) {
		throw new RuntimeException("Should have been eliminated by SwitchStatementRemover");
	}

	@Override
	public void caseThrowStmt(ThrowStmt arg0) {
		precheck(arg0);
		throw new RuntimeException("Apply the ExceptionRemover first.");
		// arg0.getOp().apply(valueSwitch);
		// Expression exception = valueSwitch.popExpression();
		// currentBlock.addStatement(new
		// AssignStatement(SootTranslationHelpers.v().getSourceLocation(arg0),
		// methodInfo.getExceptionVariable(), exception));
		// connectBlocks(currentBlock, methodInfo.getSink());
		// currentBlock = null;
	}

	@Override
	public void defaultCase(Object arg0) {
		throw new RuntimeException("Case not implemented");
	}

	/**
	 * Translate method invokation. This assumes that exceptions and virtual
	 * calls have already been removed.
	 *
	 * @param u
	 * @param optionalLhs
	 * @param call
	 */
	// FIXME: Invokation -> Invocation
	private void translateMethodInvokation(Unit u, Value optionalLhs, InvokeExpr call) {
		if (isHandledAsSpecialCase(u, optionalLhs, call)) {
			return;
		}
		// translate the expressions in the arguments first.
		LinkedList<Expression> args = new LinkedList<Expression>();
		for (int i = 0; i < call.getArgs().size(); i++) {
			call.getArg(i).apply(valueSwitch);
			args.add(valueSwitch.popExpression());
		}
		Expression baseExpression = null;
		// List of possible virtual methods that can be called at this point.
		// Order matters here.
		if (call instanceof InstanceInvokeExpr) {
			InstanceInvokeExpr iivk = (InstanceInvokeExpr) call;
			iivk.getBase().apply(valueSwitch);
			baseExpression = valueSwitch.popExpression();
			// add the "this" variable to the list of args
			args.addFirst(baseExpression);
			// this include Interface-, Virtual, and SpecialInvokeExpr
		} else if (call instanceof StaticInvokeExpr) {
			// no need to handle the base.
		} else if (call instanceof DynamicInvokeExpr) {
			// DynamicInvokeExpr divk = (DynamicInvokeExpr) call;
			System.err.println("Dynamic invoke translation is only a stub. Will be unsound!");
		} else {
			throw new RuntimeException("Cannot compute instance for " + call.getClass().toString());
		}
		List<Expression> receiver = new LinkedList<Expression>();
		receiver.add(methodInfo.getExceptionVariable());
		if (optionalLhs != null) {
			optionalLhs.apply(valueSwitch);
			Expression lhs = valueSwitch.popExpression();
			receiver.add(lhs);
		}
		// System.err.println(call);
		if (call.getMethod().isConstructor() && call instanceof SpecialInvokeExpr) {
			/*
			 * For our new memory model, we need special treatment of
			 * constructor invoke
			 */
			SootTranslationHelpers.v().getMemoryModel().mkConstructorCall(u, call.getMethod(), args);
		} else {
			Method method = SootTranslationHelpers.v().lookupOrCreateMethod(call.getMethod());

			if (method.getReturnType().size()>1 && optionalLhs==null) {
				for (int i=1; i<method.getReturnType().size(); i++) {
					final Variable dummyVar = methodInfo.createFreshLocal("dummy_ret", method.getReturnType().get(i), false, false);
					receiver.add(new IdentifierExpression(loc, dummyVar));
				}
			}

			CallStatement stmt = new CallStatement(SootTranslationHelpers.v().getSourceLocation(u), method, args,
					receiver);
			this.currentBlock.addStatement(stmt);
		}
	}

	private Expression getExpressionOrSelf(Expression expr) {
		if (expr instanceof IdentifierExpression) {
			Variable v = ((IdentifierExpression) expr).getVariable();
			Expression e = SootTranslationHelpers.v().getMemoryModel().lookupExpression(v);
			if (e != null) {
				return e;
			}
		}
		return expr;
	}

	private Expression valueToExpr(Value value) {
		value.apply(valueSwitch);
		return valueSwitch.popExpression();
	}

	/**
	 *
	 * @param value
	 * @return If the resulting expression is an IdentifierExpression, the expression that it is pointing to
	 *         will be returned. Otherwise, the resulting expression itself will be returned.
	 */
	private Expression valueToInnerExpr(Value value) {
		return getExpressionOrSelf(valueToExpr(value));
	}

	/**
	 * Check if the call is a special case such as System.exit. If so, translate
	 * it and return true. Otherwise, ignore it and return false.
	 *
	 * @param u
	 * @param optionalLhs
	 * @param call
	 * @return true, if its a special method that is handled by the procedure,
	 *         and false, otherwise.
	 */
	private boolean isHandledAsSpecialCase(Unit u, Value optionalLhs, InvokeExpr call) {
		String methodSignature = call.getMethod().getSignature();
		SourceLocation srcLoc = SootTranslationHelpers.v().getSourceLocation(u);
		if (methodSignature.equals(SootTranslationHelpers.v().getAssertMethod().getSignature())) {
			Verify.verify(optionalLhs == null);
			Verify.verify(call.getArgCount() == 1);
			call.getArg(0).apply(valueSwitch);
			currentBlock.addStatement(
					new AssertStatement(SootTranslationHelpers.v().getSourceLocation(u), valueSwitch.popExpression()));
			return true;
		}
		if (methodSignature.contains("<java.lang.String: int length()>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression thisExpr = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
				Expression lhs = valueToExpr(optionalLhs);
				Expression rhs = new UnaryExpression(srcLoc, UnaryOperator.Len, thisExpr);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			} // else: ignore
			return true;
		}
		if (methodSignature.contains("<java.lang.String: char charAt(int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression thisExpr = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
				Expression indexExpr = valueToInnerExpr(call.getArg(0));
				Expression lhs = valueToExpr(optionalLhs);
				Expression rhs = new BinaryExpression(srcLoc, BinaryOperator.CharAt, thisExpr, indexExpr);
				currentBlock.addStatement(new AssertStatement(srcLoc,
						new BinaryExpression(srcLoc, BinaryOperator.IndexInString, thisExpr, indexExpr)));
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			} // else: ignore
			return true;
		}
		if (methodSignature.contains("<java.lang.String: java.lang.String valueOf(int)>") ||
				methodSignature.contains("<java.lang.String: java.lang.String valueOf(long)>")) {
			assert (call instanceof StaticInvokeExpr);
			if (optionalLhs != null) {
				Expression itemExpr = valueToExpr(call.getArg(0));
				Expression lhs = valueToExpr(optionalLhs);
				Expression rhs = new BinaryExpression(srcLoc, BinaryOperator.ToString, itemExpr, lhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			} // else: ignore
			return true;
		}
		if (methodSignature.contains("<java.lang.String: java.lang.String valueOf(char)>")) {
			assert (call instanceof StaticInvokeExpr);
			if (optionalLhs != null) {
				Expression itemExpr = valueToExpr(call.getArg(0));
				Expression lhs = valueToExpr(optionalLhs);
				Expression rhs = new BinaryExpression(srcLoc, BinaryOperator.CharToString, itemExpr, lhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			} // else: ignore
			return true;
		}
		if (methodSignature.contains("<java.lang.String: java.lang.String valueOf(boolean)>")) {
			assert (call instanceof StaticInvokeExpr);
			if (optionalLhs != null) {
				Expression boolExpr = valueToExpr(call.getArg(0));
				Expression lhs = valueToExpr(optionalLhs);
				Expression rhs = new BinaryExpression(srcLoc, BinaryOperator.BoolToString, boolExpr, lhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			} // else: ignore
			return true;
		}
		if (methodSignature.contains("<java.lang.String: boolean equals(java.lang.Object)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToExpr(call.getArg(0));
					// TODO: should make string constant value present at retVar.variableName ?
					Variable retVar = new Variable("$string_" + call.getMethod().getName() + "(" + a + "," + b + ")", BoolType.instance());
					rhs = retVar.mkExp(srcLoc);
					BinaryExpression equality = new BinaryExpression(srcLoc, BinaryOperator.StringEq, a, b);
					currentBlock.addStatement(new AssignStatement(srcLoc, rhs, equality));
				} else {
					rhs = new BooleanLiteral(srcLoc, false);
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: java.lang.String concat(java.lang.String)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					rhs = new BinaryExpression(srcLoc, BinaryOperator.StringConcat, a, b);
				} else {
					throw new RuntimeException("String.concat(NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: java.lang.String substring(int,int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
				Expression startIndex = valueToExpr(call.getArg(0));
				Expression endIndex = valueToExpr(call.getArg(1));
				Expression rhs = new NaryExpression(srcLoc, NaryExpression.NaryOperator.Substring, a, startIndex, endIndex);
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: java.lang.String substring(int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
				Expression index = valueToExpr(call.getArg(0));
				Expression rhs = new NaryExpression(srcLoc, NaryExpression.NaryOperator.SubstringWithOneIndex, a, index);
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: int indexOf(java.lang.String)>") ||
				methodSignature.contains("<java.lang.String: int indexOf(int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType || call.getArg(0).getType() instanceof IntType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					rhs = new BinaryExpression(srcLoc, (call.getArg(0).getType() instanceof IntType) ? BinaryOperator.StringIndexOfChar : BinaryOperator.StringIndexOf, a, b);
				} else {
					throw new RuntimeException("String.indexOf(NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: int lastIndexOf(java.lang.String)>") ||
				methodSignature.contains("<java.lang.String: int lastIndexOf(int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType || call.getArg(0).getType() instanceof IntType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					rhs = new BinaryExpression(srcLoc, (call.getArg(0).getType() instanceof IntType) ? BinaryOperator.StringLastIndexOfChar : BinaryOperator.StringLastIndexOf, a, b);
				} else {
					throw new RuntimeException("String.lastIndexOf(NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: int indexOf(java.lang.String,int)>") ||
				methodSignature.contains("<java.lang.String: int indexOf(int,int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if ((call.getArg(0).getType() instanceof RefType || call.getArg(0).getType() instanceof IntType) && call.getArg(1).getType() instanceof IntType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					Expression c = valueToExpr(call.getArg(1));
					rhs = new NaryExpression(srcLoc, (call.getArg(0).getType() instanceof IntType) ? NaryExpression.NaryOperator.IndexOfCharWithOffset : NaryExpression.NaryOperator.IndexOfWithOffset, a, b, c);
				} else {
					throw new RuntimeException("String.indexOf(NonObject, NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: int lastIndexOf(java.lang.String,int)>") ||
				methodSignature.contains("<java.lang.String: int lastIndexOf(int,int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if ((call.getArg(0).getType() instanceof RefType || call.getArg(0).getType() instanceof IntType) && call.getArg(1).getType() instanceof IntType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					Expression c = valueToExpr(call.getArg(1));
					rhs = new NaryExpression(srcLoc, (call.getArg(0).getType() instanceof IntType) ? NaryExpression.NaryOperator.LastIndexOfCharWithOffset : NaryExpression.NaryOperator.LastIndexOfWithOffset, a, b, c);
				} else {
					throw new RuntimeException("String.lastIndexOf(NonObject, NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: int compareTo(java.lang.String)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					rhs = new BinaryExpression(srcLoc, BinaryOperator.StringCompareTo, a, b);
				} else {
					throw new RuntimeException("String.compareTo(NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (!Options.v().useBuiltInSpecs()) {
			if (methodSignature.contains("<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>") ||
					methodSignature.contains("<java.lang.StringBuffer: java.lang.StringBuffer append(java.lang.String)>")) {
				assert (call instanceof InstanceInvokeExpr);
				if (optionalLhs != null) {
					Expression rhs;
					if (call.getArg(0).getType() instanceof RefType) {
						Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
						Expression b = valueToInnerExpr(call.getArg(0));
						rhs = new BinaryExpression(srcLoc, BinaryOperator.StringConcat, a, b);
					} else {
						throw new RuntimeException("String.concat(NonObject) not implemented");
					}
					Expression lhs = valueToExpr(optionalLhs);
					currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
				}
				return true;
			}
		}
		if (methodSignature.contains("<java.lang.String: boolean startsWith(java.lang.String)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					rhs = new BinaryExpression(srcLoc, BinaryOperator.StartsWith, a, b);
				} else {
					throw new RuntimeException("String.startsWith(NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: boolean endsWith(java.lang.String)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					rhs = new BinaryExpression(srcLoc, BinaryOperator.EndsWith, a, b);
				} else {
					throw new RuntimeException("String.endsWith(NonObject) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (methodSignature.contains("<java.lang.String: boolean startsWith(java.lang.String,int)>")) {
			assert (call instanceof InstanceInvokeExpr);
			if (optionalLhs != null) {
				Expression rhs;
				if (call.getArg(0).getType() instanceof RefType) {
					Expression a = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression b = valueToInnerExpr(call.getArg(0));
					Expression c = valueToInnerExpr(call.getArg(1));
					rhs = new NaryExpression(srcLoc, NaryExpression.NaryOperator.StartsWithOffset, a, b, c);
				} else {
					throw new RuntimeException("String.startsWith(NonObject, int) not implemented");
				}
				Expression lhs = valueToExpr(optionalLhs);
				currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
			}
			return true;
		}
		if (!Options.v().useBuiltInSpecs()) {
			if (methodSignature.contains("<java.lang.StringBuilder: java.lang.String toString()>") ||
					methodSignature.contains("<java.lang.StringBuffer: java.lang.String toString()>")) {
				assert (call instanceof InstanceInvokeExpr);
				if (optionalLhs != null) {
					Expression thisExpr = valueToInnerExpr(((InstanceInvokeExpr) call).getBase());
					Expression lhs = valueToExpr(optionalLhs);
					Expression rhs = new BinaryExpression(srcLoc, BinaryOperator.ToString, thisExpr, lhs);
					currentBlock.addStatement(new AssignStatement(srcLoc, lhs, rhs));
				} // else: ignore
				return true;
			}
//			if (methodSignature.contains("<java.lang.StringBuilder: void <init>()>") ||
//				methodSignature.contains("<java.lang.StringBuffer: void <init>()>")) {
//				assert (call instanceof InstanceInvokeExpr);
//				// ignore for now
//			}
		}
		if (methodSignature.contains("<java.lang.System: void exit(int)>") ||
				methodSignature.contains("<java.lang.Runtime: void halt(int)>")) {
			// TODO: this is not sufficient for interprocedural analysis.
			currentBlock = null;
			return true;
		}

		if (call.getMethod().getDeclaringClass().getName().contains("org.junit.Assert")) {
			// TODO: this should not be hard coded!
			if (call.getMethod().getName().equals("fail")) {
				Stmt ret = SootTranslationHelpers.v().getDefaultReturnStatement(sootMethod.getReturnType(),
						currentStmt);
				ret.apply(this);
				return true;
			}
		}

		if (call.getMethod().getDeclaringClass().getName().contains("com.google.common.base.")) {
			if (methodSignature.contains("void checkArgument(boolean)")) {
				Preconditions.checkArgument(optionalLhs == null);
				call.getArg(0).apply(valueSwitch);
				Expression guard = valueSwitch.popExpression();
				currentBlock.addStatement(new AssertStatement(SootTranslationHelpers.v().getSourceLocation(u), guard));
				return true;
			}
		}

		if (methodSignature.equals("<java.lang.Class: boolean isAssignableFrom(java.lang.Class)>")) {
			InstanceInvokeExpr iivk = (InstanceInvokeExpr) call;
			Verify.verify(call.getArgCount() == 1);
			if (optionalLhs != null) {
				optionalLhs.apply(valueSwitch);
				Expression lhs = valueSwitch.popExpression();
				iivk.getBase().apply(valueSwitch);
				Expression binOpRhs = valueSwitch.popExpression();
				Verify.verify(binOpRhs instanceof IdentifierExpression);
				Variable rhsVar = ((IdentifierExpression)binOpRhs).getVariable();
				Verify.verify(rhsVar instanceof ClassVariable);
				call.getArg(0).apply(valueSwitch);
				IdentifierExpression binOpLhs = (IdentifierExpression)valueSwitch.popExpression();
				Expression instOf = SootTranslationHelpers.createInstanceOfExpression(getCurrentLoc(), binOpLhs.getVariable(), (ClassVariable)rhsVar);
				currentBlock.addStatement(
						new AssignStatement(SootTranslationHelpers.v().getSourceLocation(u), lhs, instOf));
				return true;
			} // otherwise ignore.
		} else if (methodSignature.equals("<java.lang.Object: java.lang.Class getClass()>")) {
			InstanceInvokeExpr iivk = (InstanceInvokeExpr) call;
			Verify.verify(call.getArgCount() == 0);
			if (optionalLhs != null) {
				Value objectToGetClassFrom = iivk.getBase();
				soot.Type t = objectToGetClassFrom.getType();
//				SootField typeField = null;
				if (t instanceof RefType) {
					// first make a heap-read of the type filed.
//					typeField = SootTranslationHelpers.getTypeField(((RefType) t).getSootClass());
//					// now get the dynamic type
//					SootTranslationHelpers.v().getMemoryModel().mkHeapReadStatement(getCurrentStmt(),
//							Jimple.v().newInstanceFieldRef(objectToGetClassFrom, typeField.makeRef()), optionalLhs);

					objectToGetClassFrom.apply(valueSwitch);
					IdentifierExpression base = (IdentifierExpression)valueSwitch.popExpression();
					optionalLhs.apply(valueSwitch);
					Expression left = valueSwitch.popExpression();
					currentBlock.addStatement(new AssignStatement(loc, left, new TupleAccessExpression(loc, base.getVariable(), ReferenceType.TypeFieldName)));

				} else if (t instanceof ArrayType) {
//					typeField = SootTranslationHelpers.getTypeField(Scene.v().getSootClass("java.lang.Object"));
					throw new RuntimeException("Arrays should be removed first.");
				} else {
					throw new RuntimeException("Not implemented. " + t + ", " + t.getClass());
				}
				return true;
			}
		} else if (methodSignature
				.equals("<java.lang.Class: java.lang.Object cast(java.lang.Object)>")) {
			// TODO: we have to check if we have to throw an exception or add
			// E.g, String.<java.lang.Class: java.lang.Object
			// cast(java.lang.Object)>(x); means (String)x
			InstanceInvokeExpr iivk = (InstanceInvokeExpr) call;
			Verify.verify(call.getArgCount() == 1);
			if (optionalLhs != null) {
				// TODO
				optionalLhs.apply(valueSwitch);
				Expression lhs = valueSwitch.popExpression();
				iivk.getBase().apply(valueSwitch);
				Expression binOpRhs = valueSwitch.popExpression();
				call.getArg(0).apply(valueSwitch);
				Expression binOpLhs = valueSwitch.popExpression();

				Expression instOf = new BinaryExpression(this.getCurrentLoc(), BinaryOperator.PoLeq, binOpLhs,
						binOpRhs);
				currentBlock.addStatement(new AssertStatement(SootTranslationHelpers.v().getSourceLocation(u), instOf));

				call.getArg(0).apply(valueSwitch);
				Expression asgnRhs = valueSwitch.popExpression();

				currentBlock.addStatement(
						new AssignStatement(SootTranslationHelpers.v().getSourceLocation(u), lhs, asgnRhs));
				return true;

			}
		} else if (methodSignature.equals("<java.lang.Class: boolean isInstance(java.lang.Object)>")) {
			/*
			 * E.g,
			 * $r2 = class "java/lang/String";
			 * $z0 = virtualinvoke $r2.<java.lang.Class: boolean
			 * isInstance(java.lang.Object)>(r1);
			 * checks if r1 instancof String
			 */
			InstanceInvokeExpr iivk = (InstanceInvokeExpr) call;
			Verify.verify(call.getArgCount() == 1);
			if (optionalLhs != null) {
				// TODO
				optionalLhs.apply(valueSwitch);
				Expression lhs = valueSwitch.popExpression();
				iivk.getBase().apply(valueSwitch);
				Expression binOpRhs = valueSwitch.popExpression();
				call.getArg(0).apply(valueSwitch);
				Expression binOpLhs = valueSwitch.popExpression();

				Expression instOf = new BinaryExpression(this.getCurrentLoc(), BinaryOperator.PoLeq, binOpLhs,
						binOpRhs);
				currentBlock.addStatement(
						new AssignStatement(SootTranslationHelpers.v().getSourceLocation(u), lhs, instOf));
				return true;

			}

		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: boolean nondetBoolean()>") ||
				methodSignature.equals("<java.util.Random: boolean nextBoolean()>")) {
			translateRandomNondet(BooleanType.v(), optionalLhs, call, false, 0, 0);
			return true;
		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: byte nondetByte()>")) {
			translateRandomNondet(ByteType.v(), optionalLhs, call,true, Byte.MIN_VALUE, Byte.MAX_VALUE);
			return true;
		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: char nondetChar()>")) {
			translateRandomNondet(CharType.v(), optionalLhs, call,true, Character.MIN_VALUE, Character.MAX_VALUE);
			return true;
		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: short nondetShort()>")) {
			translateRandomNondet(ShortType.v(), optionalLhs, call,true, Short.MIN_VALUE, Short.MAX_VALUE);
			return true;
		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: int nondetInt()>") ||
				methodSignature.equals("<java.util.Random: int nextInt()>")) {
			translateRandomNondet(IntType.v(), optionalLhs, call,true, Integer.MIN_VALUE, Integer.MAX_VALUE);
			return true;
		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: long nondetLong()>") ||
				methodSignature.equals("<java.util.Random: long nextLong()>")) {
			translateRandomNondet(LongType.v(), optionalLhs, call,true, Long.MIN_VALUE, Long.MAX_VALUE);
			return true;
			// TODO: cover other nondeterministic Verifier functions
		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: java.lang.String nondetString()>") &&
				!Options.v().useBuiltInSpecs()) {
			translateNondetString(RefType.v(), optionalLhs, call);
			return true;

		} else if (methodSignature.equals("<org.sosy_lab.sv_benchmarks.Verifier: void assume(boolean)>")) {
			Verify.verify(optionalLhs == null);
			Verify.verify(call.getArgCount() == 1);
			call.getArg(0).apply(valueSwitch);
			Expression cond = valueSwitch.popExpression();
			currentBlock.addStatement(
					new AssumeStatement(SootTranslationHelpers.v().getSourceLocation(u), cond)
			);
			return true;
		}

//System.out.println(methodSignature);

		return false;
	}

// replaced by translateRandomNondet()

//        /**
//         * Method nondet*() of the Verifier class used to formula
//         * SV-COMP problems.  Replace those method with a simple
//         * havoc.
//         */
//        private void translateVerifierNondet(Type t, Value optionalLhs,
//                                             InvokeExpr call,
//                                             boolean addBounds,
//                                             long lower, long upper) {
//            Verify.verify(call.getArgCount() == 0);
//
//            if (optionalLhs != null) {
//                optionalLhs.apply(valueSwitch);
//                Expression lhs = valueSwitch.popExpression();
//
//                Verify.verify(lhs instanceof IdentifierExpression,
//                              "do not know how to havoc " + lhs);
//                IdentifierExpression idLhs = (IdentifierExpression)lhs;
//
//                final SourceLocation loc = lhs.getSourceLocation();
//
//                currentBlock.addStatement(new HavocStatement(loc, idLhs));
//
//                if (addBounds)
//                currentBlock.addStatement(
//                        new AssumeStatement(loc,
//                          new BinaryExpression(
//                            loc, BinaryOperator.And,
//                            new BinaryExpression(
//                              loc, BinaryOperator.Le,
//                              new IntegerLiteral(loc, lower), idLhs),
//                            new BinaryExpression(
//                              loc, BinaryOperator.Le,
//                              idLhs, new IntegerLiteral(loc, upper)))));
//            }
//        }

	/**
	 * Method nondet*() of the Verifier class used to formulate
	 * SV-COMP problems.  Replace those method with a simple
	 * havoc.
	 */
	private void translateRandomNondet(Type t, Value optionalLhs,
									   InvokeExpr call,
									   boolean addBounds,
									   long lower, long upper) {
		Verify.verify(call.getArgCount() == 0);

		if (optionalLhs != null) {
			optionalLhs.apply(valueSwitch);
			Expression lhs = valueSwitch.popExpression();

			Verify.verify(lhs instanceof IdentifierExpression,
					"do not know how to havoc " + lhs);
			IdentifierExpression idLhs = (IdentifierExpression)lhs;

			final SourceLocation loc = lhs.getSourceLocation();

			currentBlock.addStatement(new HavocStatement(loc, idLhs));

			if (addBounds)
				currentBlock.addStatement(
						new AssumeStatement(loc,
								new BinaryExpression(
										loc, BinaryOperator.And,
										new BinaryExpression(
												loc, BinaryOperator.Le,
												new IntegerLiteral(loc, lower), idLhs),
										new BinaryExpression(
												loc, BinaryOperator.Le,
												idLhs, new IntegerLiteral(loc, upper)))));
		}
	}

	/**
	 * Method of the Random class, which are replaced with a simple
	 * havoc.
	 */
	private void translateNondetString(Type t, Value optionalLhs, InvokeExpr call) {

		Verify.verify(call.getArgCount() == 0);

		if (optionalLhs != null) {
			optionalLhs.apply(valueSwitch);
			Expression lhs = valueSwitch.popExpression();

			Verify.verify(lhs instanceof IdentifierExpression,
					"do not know how to havoc " + lhs);
			IdentifierExpression idLhs = (IdentifierExpression)lhs;

			final SourceLocation loc = lhs.getSourceLocation();

			// There is no soottocfg.cfg.expression.IdentifierExpression equivalent for the internal string,
			// so new HavocStatement(loc, internalString) is not possible.
			StringLiteral nondetStrIdExp = new StringLiteral(loc, idLhs.getVariable(), null);
			// see jayhorn.hornify.encoder.StringEncoder.mkStringPE(String value)

			currentBlock.addStatement(new AssumeStatement(loc,
					new BinaryExpression(loc, BinaryOperator.Eq, idLhs, nondetStrIdExp)
			));
		}
	}

	private void translateDefinitionStmt(DefinitionStmt def) {

		if (def.containsInvokeExpr()) {
			translateMethodInvokation(def, def.getLeftOp(), def.getInvokeExpr());
			return;
		}

		Value lhs = def.getLeftOp();
		Value rhs = def.getRightOp();

		if (def.containsFieldRef()) {
			Verify.verify(lhs instanceof FieldRef || rhs instanceof FieldRef);
			if (def.getFieldRef().getField().equals(SootTranslationHelpers.v().getExceptionGlobal())) {
				// Special treatment of the exception global.
				if (lhs instanceof FieldRef) {
					IdentifierExpression left = methodInfo.getExceptionVariable();
					if (rhs instanceof AnyNewExpr) {
						SootClass sc = ((RefType) ((AnyNewExpr) rhs).getType()).getSootClass();
						currentBlock.addStatement(
								new NewStatement(loc, left, SootTranslationHelpers.v().getClassVariable(sc)));
					} else {
						rhs.apply(valueSwitch);
						Expression right = valueSwitch.popExpression();
						currentBlock.addStatement(
								new AssignStatement(SootTranslationHelpers.v().getSourceLocation(def), left, right));
					}
				} else /* if (rhs instanceof FieldRef) */ {
					lhs.apply(valueSwitch);
					Expression left = valueSwitch.popExpression();
					Expression right = methodInfo.getExceptionVariable();
					currentBlock.addStatement(
							new AssignStatement(SootTranslationHelpers.v().getSourceLocation(def), left, right));
				}
			} else {
				if (lhs instanceof FieldRef) {
					Verify.verify(!(rhs instanceof AnyNewExpr));
					SootTranslationHelpers.v().getMemoryModel().mkHeapWriteStatement(def, def.getFieldRef(), rhs);
				} else /* if (rhs instanceof FieldRef) */ {
					SootTranslationHelpers.v().getMemoryModel().mkHeapReadStatement(def, def.getFieldRef(), lhs);
				}
			}
		} else if (def.containsArrayRef()) {
			throw new RuntimeException("Remove Arrays first.");
		} else if (rhs instanceof LengthExpr) {
			throw new RuntimeException("Remove Arrays first.");
		} else {

			// first tell memory model to copy all fields
			if (lhs instanceof Local && rhs instanceof Local)
				SootTranslationHelpers.v().getMemoryModel().mkCopy((Local) lhs, (Local) rhs);

			// local to local assignment.
			lhs.apply(valueSwitch);
			Expression left = valueSwitch.popExpression();

			if (rhs instanceof AnyNewExpr) {
				SootClass sc = ((RefType) ((AnyNewExpr) rhs).getType()).getSootClass();
				currentBlock.addStatement(new NewStatement(loc, (IdentifierExpression) left,
						SootTranslationHelpers.v().getClassVariable(sc)));
			} else {
				rhs.apply(valueSwitch);
				Expression right = valueSwitch.popExpression();

				if (left instanceof IdentifierExpression && right instanceof StringLiteral) { // TODO: find a better way
					SootTranslationHelpers.v().getMemoryModel().putExpression(((IdentifierExpression) left).getVariable(), right);
				}
				currentBlock.addStatement(
						new AssignStatement(SootTranslationHelpers.v().getSourceLocation(def), left, right));
			}
		}
//TODO: assume non-null is not needed because we have a NewStatement now.
//		if (rhs instanceof AnyNewExpr) {
//			// add an assume that lhs is not null.
//			lhs.apply(valueSwitch);
//			Expression left = valueSwitch.popExpression();
//			currentBlock.addStatement(new AssumeStatement(getCurrentLoc(), new BinaryExpression(getCurrentLoc(),
//					BinaryOperator.Ne, left, SootTranslationHelpers.v().getMemoryModel().mkNullConstant())));
//		}
	}

}
