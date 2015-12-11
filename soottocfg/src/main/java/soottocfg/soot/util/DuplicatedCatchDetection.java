/**
 * 
 */
package soottocfg.soot.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import com.google.common.base.Preconditions;

import soot.Body;
import soot.Hierarchy;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.SwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * @author schaef TODO: this class is only a test and should be moved to an
 *         appropriate location once it works.
 */
public class DuplicatedCatchDetection {

	/**
	 * Java finally blocks get inlined in bytecode. That is, one Java statement
	 * can correspond to multiple bytecode instructions that are almost
	 * identical but the Locals may be different. These duplications can confuse
	 * inconsistent code detection if one instance is inconsistent but another
	 * one is not. This procedure tries to identify for each statement in a
	 * finally block the set of statements it corresponds to.
	 * 
	 * @param body
	 * @return
	 */
	public Map<Unit, Set<Unit>> identifiedDuplicatedUnitsFromFinallyBlocks(Body body) {
		Hierarchy hierarchy = Scene.v().getActiveHierarchy();
		SootClass throwableClass = Scene.v().getSootClass("java.lang.Throwable");

		Map<Trap, List<Unit>> catchBlocks = new HashMap<Trap, List<Unit>>();
		// first collect all monitor traps.
		Set<Unit> usedHandlers = new HashSet<Unit>();
		for (Trap t : body.getTraps()) {
			if (t.getException() == throwableClass) {
				if (!usedHandlers.contains(t.getHandlerUnit())) {
					// collect the catch block of this trap
					List<Unit> catchUnits = collectCatchBlock(body, t.getHandlerUnit());
					// if its a proper catch block
					if (tryRemoveCatchBlockBoilderplate(catchUnits)) {
						// do not add trivial catch blocks.
						if (!catchUnits.isEmpty()) {
							catchBlocks.put(t, catchUnits);
							usedHandlers.add(t.getHandlerUnit());
						}
					}
				}
			} else {
				Preconditions.checkArgument(hierarchy.isClassSubclassOfIncluding(t.getException(), throwableClass),
						"Unexpected type " + t.getException().getJavaStyleName());
			}
		}

		Map<Unit, Set<Unit>> duplicatedUnits = new HashMap<Unit, Set<Unit>>();

		if (!catchBlocks.isEmpty()) {
			UnitGraph graph = new CompleteUnitGraph(body);
			Iterator<Unit> iterator = graph.iterator();
			while (iterator.hasNext()) {
				Unit u = iterator.next();
				for (Entry<Trap, List<Unit>> entry : catchBlocks.entrySet()) {
					List<Unit> finallyCopy = new LinkedList<Unit>(entry.getValue());
					Map<Unit, Unit> dupl = findDuplicates(graph, u, finallyCopy);
					if (dupl.size() > 0) {
						// StringBuilder sb = new StringBuilder();
						for (Entry<Unit, Unit> en : dupl.entrySet()) {
							if (!duplicatedUnits.containsKey(en.getKey())) {
								duplicatedUnits.put(en.getKey(), new HashSet<Unit>());
							}
							duplicatedUnits.get(en.getKey()).add(en.getValue());
							// sb.append(en.getKey() +
							// "\t"+en.getValue());
							// sb.append("\n");
						}
						// sb.append("***\n");
						// System.err.println(sb.toString());
					}
				}
			}
		}
		return duplicatedUnits;
	}

	/**
	 * DFS through the graph to find the largest copy of the finally block.
	 * 
	 * @param g
	 * @param u
	 * @param finallyBlock
	 * @return
	 */
	private Map<Unit, Unit> findDuplicates(UnitGraph g, Unit u, List<Unit> finallyBlock) {
		Map<Unit, Unit> duplicates = new LinkedHashMap<Unit, Unit>();

		if (!finallyBlock.isEmpty() && isDuplicateButNotSame(u, finallyBlock.get(0))) {
			duplicates.put(finallyBlock.get(0), u);
			finallyBlock.remove(0);
			for (Unit s : g.getSuccsOf(u)) {
				if (finallyBlock.isEmpty()) {
					// we're done
					break;
				}
				if (isDuplicateButNotSame(s, finallyBlock.get(0))) {

					/*
					 * TODO: For try-with-resources (i.e., try (bla = new blub)
					 * { ... }) there are duplicated CaughtExceptionRefs with
					 * non-identical line numbers. I believe this is ok, but
					 * further testing may be needed.
					 */
					// if
					// (s.getJavaSourceStartLineNumber()!=finallyBlock.get(0).getJavaSourceStartLineNumber())
					// {
					// int la = s.getJavaSourceStartLineNumber();
					// int lb =
					// finallyBlock.get(0).getJavaSourceStartLineNumber();
					// System.err.println("line a "+ la + ", line b " +lb);
					// System.err.println("a "+ s + ", b "
					// +finallyBlock.get(0));
					// }

					duplicates.putAll(findDuplicates(g, s, finallyBlock));
				}
			}
		}
		return duplicates;
	}

	/**
	 * Returns true if a does the same as b but returns false if a==b.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean isDuplicateButNotSame(Unit a, Unit b) {
		if (a == b) {
			// make sure that we do not mark a unit as its own duplicate.
			return false;
		}
		// NOTE: DO NOT USE getJavaSourceStartLineNumber because it is
		// non-deterministic
		// if
		// (a.getJavaSourceStartLineNumber()!=b.getJavaSourceStartLineNumber())
		// {
		// return false;
		// }

		if (a instanceof DefinitionStmt && b instanceof DefinitionStmt) {
			return shallowCompareDefinitionStatements((DefinitionStmt) a, (DefinitionStmt) b);
		} else if (a instanceof InvokeStmt && b instanceof InvokeStmt) {
			return shallowCompareValue(((InvokeStmt) a).getInvokeExpr(), ((InvokeStmt) b).getInvokeExpr());
		} else if (a instanceof IfStmt && b instanceof IfStmt) {
			IfStmt ia = (IfStmt) a;
			IfStmt ib = (IfStmt) b;
			// TODO: also consider the case that one condition might include
			// a double negation.
			return shallowCompareValue(ia.getCondition(), ib.getCondition());
		} else if (a instanceof IfStmt && b instanceof IfStmt) {

		} else if (a instanceof SwitchStmt && b instanceof SwitchStmt) {
			SwitchStmt sa = (SwitchStmt) a;
			SwitchStmt sb = (SwitchStmt) b;
			return shallowCompareValue(sa.getKey(), sb.getKey());
		}
		// we do not care if goto, return, monitor, throw , or noop statements
		// are clones of other statemens.
		return false;
	}

	private boolean shallowCompareDefinitionStatements(DefinitionStmt a, DefinitionStmt b) {
		return shallowCompareValue(a.getLeftOp(), b.getLeftOp()) && shallowCompareValue(a.getRightOp(), b.getRightOp());
	}

	private boolean shallowCompareValue(Value a, Value b) {
		if (a instanceof Local && b instanceof Local) {
			return a.getType().equals(b.getType());
		} else if (a instanceof InstanceFieldRef && b instanceof InstanceFieldRef) {
			InstanceFieldRef ia = (InstanceFieldRef) a;
			InstanceFieldRef ib = (InstanceFieldRef) b;
			if (ia.getField() == ib.getField()) {
				return shallowCompareValue(ia.getBase(), ib.getBase());
			}
			return false;
		} else if (a instanceof InvokeExpr && b instanceof InvokeExpr) {
			InvokeExpr ia = (InvokeExpr) a;
			InvokeExpr ib = (InvokeExpr) b;
			if (ia.getMethod() == ib.getMethod()) {
				for (int i = 0; i < ia.getArgCount(); i++) {
					if (!shallowCompareValue(ia.getArg(i), ib.getArg(i))) {
						return false;
					}
				}
			} else {
				return false;
			}
			if (a instanceof InstanceInvokeExpr && b instanceof InstanceInvokeExpr) {
				InstanceInvokeExpr iia = (InstanceInvokeExpr) a;
				InstanceInvokeExpr iib = (InstanceInvokeExpr) b;
				return shallowCompareValue(iia.getBase(), iib.getBase());
			} else {
				return true;
			}
		} else if (a instanceof UnopExpr && b instanceof UnopExpr) {
			return shallowCompareUnopExpr((UnopExpr) a, (UnopExpr) b);
		} else if (a instanceof BinopExpr && b instanceof BinopExpr) {
			return shallowCompareBinopExpr((BinopExpr) a, (BinopExpr) b);
		} else {
			return a.toString().equals(b.toString());
		}
	}

	private boolean shallowCompareUnopExpr(UnopExpr a, UnopExpr b) {
		// TODO add stuff to handle double negation.
		return a.getClass() == b.getClass() && shallowCompareValue(a.getOp(), b.getOp());
	}

	private boolean shallowCompareBinopExpr(BinopExpr a, BinopExpr b) {
		if (!a.getSymbol().equals(b.getSymbol())) {
			return false;
		}
		return shallowCompareValue(a.getOp1(), b.getOp1()) && shallowCompareValue(a.getOp2(), b.getOp2());
	}

	/**
	 * Gets the handler unit of a trap and collects all units that are only
	 * reachable via (or dominated by) this handler unit.
	 * 
	 * @param body
	 *            Body of the procedure.
	 * @param catchEntry
	 *            the first statement in a catch-block (i.e., the handler unit
	 *            in a Trap).
	 * @return List of Units that constitute a catch-block.
	 */
	private List<Unit> collectCatchBlock(Body body, Unit catchEntry) {
		// TODO: check if there is a function to compute this on the UnitGraph
		// in soot.
		UnitGraph graph = new CompleteUnitGraph(body);
		// collect all blocks reachable from catchEntry
		Queue<Unit> todo = new LinkedList<Unit>();
		List<Unit> done = new LinkedList<Unit>();
		todo.add(catchEntry);

		while (!todo.isEmpty()) {
			Unit u = todo.poll();
			done.add(u);
			for (Unit next : graph.getSuccsOf(u)) {
				if (done.containsAll(graph.getPredsOf(next))) {
					// if we have seen all predecessors, we're done.
					if (!todo.contains(next) && !done.contains(next)) {
						todo.add(next);
					}
				} else {
					// skip next, because it will be added later,
					// or skipped if it has a predecessor that is not reachable
					// from catchEntry.
				}
			}
		}
		return done;
	}

	/**
	 * Tries to remove boilerplate stuff from catch blocks like $r6
	 * := @caughtexception r2 = $r6 ... throw r2; and returns true if successful
	 * and false otherwise.
	 * 
	 * @param catchStmts
	 * @return
	 */
	private boolean tryRemoveCatchBlockBoilderplate(List<Unit> catchStmts) {
		if (catchStmts.size() >= 2) {
			Unit first = catchStmts.get(0);
			Unit second = catchStmts.get(1);
			Unit last = catchStmts.get(catchStmts.size() - 1);
			/*
			 * Remove the default catch block statements: $r6
			 * := @caughtexception r2 = $r6 ... throw r2
			 */
			DefinitionStmt handler = (IdentityStmt) first;
			catchStmts.remove(handler);
			Local l = (Local) handler.getLeftOp();
			if (second instanceof DefinitionStmt) {
				DefinitionStmt exRename = (DefinitionStmt) second;
				if (exRename.getRightOp() == l) {
					// if there is a throw statement in the end, remove it.
					catchStmts.remove(exRename);
					if (last instanceof ThrowStmt) {
						ThrowStmt thr = (ThrowStmt) last;
						if (thr.getOp() == exRename.getLeftOp()) {
							catchStmts.remove(thr);
							return true;
						}
					}
				}
			} else {
				catchStmts.remove(handler);
				return true;
			}

			if (last instanceof ReturnStmt || last instanceof GotoStmt) {
				catchStmts.remove(last);
				return true;
			}

		}
		return false;
	}

}
