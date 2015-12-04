/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.Body;
import soot.BooleanType;
import soot.Hierarchy;
import soot.Immediate;
import soot.IntType;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.IdentityRef;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.SwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.tagkit.Host;
import soottocfg.soot.util.DuplicatedCatchDetection;
import soottocfg.util.Pair;

/**
 * @author schaef
 *
 */
public class ExceptionTransformer extends AbstractTransformer {

	private NullnessAnalysis nullnessAnalysis;
	protected final SootClass exceptionClass, runtimeExceptionClass, nullPointerExceptionClass,
			arrayIndexOutOfBoundsExceptionClass, classCastExceptionClass, errorExceptionClass, throwableClass;

	private final boolean treatUncaughtExceptionsAsAssertions;
	
	private Body body;
	protected Local exceptionVariable;

	private Hierarchy hierarchy;

	private static final String exceptionLocalName = "$exception";

	public static final String getExceptionLocalName() {
		return ExceptionTransformer.exceptionLocalName;
	}

	private final Map<Unit, List<Pair<Value, SootClass>>> runtimeExceptions = new HashMap<Unit, List<Pair<Value, SootClass>>>();

	private final Set<Pair<Unit, InvokeExpr>> methodInvokes = new HashSet<Pair<Unit, InvokeExpr>>();
	private final Set<Pair<Unit, Value>> throwStatements = new HashSet<Pair<Unit, Value>>();

	private final Map<Unit, Local> caughtExceptionLocal = new HashMap<Unit, Local>();
	private Map<SootClass, Unit> generatedThrowStatements = new HashMap<SootClass, Unit>();

	/**
	 * 
	 */
	public ExceptionTransformer(NullnessAnalysis nna) {
		this(nna, true);
	}
	
	public ExceptionTransformer(NullnessAnalysis nna, boolean uncaughtAsAssertion) {
		treatUncaughtExceptionsAsAssertions = uncaughtAsAssertion;
		nullnessAnalysis = nna;
		exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		throwableClass = Scene.v().getSootClass("java.lang.Throwable");
		runtimeExceptionClass = Scene.v().getSootClass("java.lang.RuntimeException");
		nullPointerExceptionClass = Scene.v().getSootClass("java.lang.NullPointerException");
		arrayIndexOutOfBoundsExceptionClass = Scene.v().getSootClass("java.lang.ArrayIndexOutOfBoundsException");
		classCastExceptionClass = Scene.v().getSootClass("java.lang.ClassCastException");
		errorExceptionClass = Scene.v().getSootClass("java.lang.Error");
	}

	public Local getExceptionVariable() {
		return this.exceptionVariable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see soot.BodyTransformer#internalTransform(soot.Body, java.lang.String,
	 * java.util.Map)
	 */
	@Override
	protected void internalTransform(Body b, String arg1, Map<String, String> arg2) {
		hierarchy = Scene.v().getActiveHierarchy();
		body = b;
		exceptionVariable = Jimple.v().newLocal(ExceptionTransformer.exceptionLocalName, RefType.v(exceptionClass));
		body.getLocals().add(exceptionVariable);

		// TODO just a test. remove at some point.
		DuplicatedCatchDetection xyz = new DuplicatedCatchDetection();
		xyz.identifiedDuplicatedUnitsFromFinallyBlocks(body);

		// first remove all the monitor related exceptions
		removeMonitorTraps(body);

		PatchingChain<Unit> units = body.getUnits();
		for (Unit u : units) {
			collectPossibleExceptions(u);
		}

		Set<Trap> usedTraps = new HashSet<Trap>();

		usedTraps.addAll(handleRuntimeException());
		usedTraps.addAll(handleMethodCalls());
		usedTraps.addAll(handleThrowStatements());

		// now remove the @caughtexceptionrefs
		Map<Unit, Unit> replacementMap = new HashMap<Unit, Unit>();
		for (Trap t : usedTraps) {
			// Replace the caughtExceptionRef in the handler unit by
			// the exception local so that we can remove the traps.
			// For that we also need to add assignments that assign the
			// exception
			// variable to the corresponding new exception.
			if (t.getHandlerUnit() instanceof DefinitionStmt) {
				DefinitionStmt ds = (DefinitionStmt) t.getHandlerUnit();
				if (ds.getRightOp() instanceof CaughtExceptionRef) {
					if (!replacementMap.containsKey(ds)) {
						Unit newAssign = assignStmtFor(ds.getLeftOp(), caughtExceptionLocal.get(t.getHandlerUnit()),
								ds);
						replacementMap.put(ds, newAssign);
					}
				} else {
					throw new RuntimeException(
							"Unexpected " + t.getHandlerUnit() + "\t" + caughtExceptionLocal.get(t.getHandlerUnit()));
				}
			} else {
				throw new RuntimeException("Unexpected " + t.getHandlerUnit() + "\n" + body);
			}
		}
		// now replace all @caughtexceptionrefs in on go.
		for (Entry<Unit, Unit> entry : replacementMap.entrySet()) {
			List<Unit> toInsert = new LinkedList<Unit>();
			toInsert.add(entry.getValue());
			// after the exception is caught, set the
			// $exception variable back to Null.
			toInsert.add(assignStmtFor(exceptionVariable, NullConstant.v(), entry.getKey()));
			body.getUnits().insertAfter(toInsert, entry.getKey());
			body.getUnits().remove(entry.getKey());
		}
		// finally, remove those traps:
		body.getTraps().removeAll(usedTraps);

		if (body.getTraps().isEmpty()) {
			// System.err.println("OK");
		} else {
			// StringBuilder sb = new StringBuilder();
			// sb.append(body.getMethod().getSignature());
			// sb.append("\n\t");
			// for (Trap t : body.getTraps()) {
			// sb.append(t.getException().getName());
			// sb.append(", ");
			// }
			// System.err.println(sb.toString());
			// in theory, all the remaining traps should be unreachable
			// so we can just throw them away.
			// if the body.validate() fires an exception, its most likely
			// a bug in our code.
			// System.err.println(body);
			// body.getTraps().removeAll(body.getTraps());

			// System.err.println("\t" + body.getMethod().getSignature());
		}
		body.validate();
	}

	private Set<Trap> handleRuntimeException() {
		Set<Trap> usedTraps = new HashSet<Trap>();
		// handle the runtime exceptions first.
		for (Entry<Unit, List<Pair<Value, SootClass>>> entry : runtimeExceptions.entrySet()) {
			Unit u = entry.getKey();
			List<Trap> surroundingTraps = getTrapsGuardingUnit(u, body);
			for (Pair<Value, SootClass> pair : entry.getValue()) {
				Trap trap = null;
				for (Trap t : surroundingTraps) {
					if (hierarchy.isClassSubclassOfIncluding(pair.getSecond(), t.getException())) {
						trap = t;
						break;
					}
				}
				if (trap != null) {
					handleCaughtRuntimeException(u, pair.getFirst(), pair.getSecond(), trap);
					usedTraps.add(trap);
				} else {
					// re-throw the exception
					handleUncaughtRuntimeException(u, pair.getFirst(), pair.getSecond());
				}
			}
		}
		return usedTraps;
	}

	private Set<Trap> handleMethodCalls() {
		Set<Trap> usedTraps = new HashSet<Trap>();
		// now handle method calls.
		for (Pair<Unit, InvokeExpr> pair : methodInvokes) {
			Unit u = pair.getFirst();
			InvokeExpr ivk = pair.getSecond();
			List<SootClass> possibleExceptions = new LinkedList<SootClass>();
			// first, add everything in the throws clause.
			possibleExceptions.addAll(ivk.getMethod().getExceptions());
			// now get all caught exceptions of type RuntimeException or Error
			List<Trap> surroundingTraps = getTrapsGuardingUnit(u, body);
			for (Trap t : surroundingTraps) {
				if (hierarchy.isClassSubclassOfIncluding(t.getException(), runtimeExceptionClass)
						|| hierarchy.isClassSubclassOfIncluding(t.getException(), errorExceptionClass)) {
					if (!possibleExceptions.contains(t.getException())) {
						possibleExceptions.add(t.getException());
					}
				}
				// if there is a catch block for exception or throwable add that
				// as well.
				if (hierarchy.isClassSubclassOfIncluding(exceptionClass, t.getException())) {
					if (!possibleExceptions.contains(t.getException())) {
						possibleExceptions.add(t.getException());
					}
				}
				// also add the exceptions of all catch blocks that are
				// sub-classes
				// of what is declared in the throws clause.
				for (SootClass sc : ivk.getMethod().getExceptions()) {
					if (hierarchy.isClassSubclassOfIncluding(t.getException(), sc)) {
						if (!possibleExceptions.contains(t.getException())) {
							possibleExceptions.add(t.getException());
						}
					}
				}
			}
			// now sort the classes.
			Collections.sort(possibleExceptions, new Comparator<SootClass>() {
				@Override
				public int compare(final SootClass a, final SootClass b) {
					if (a == b)
						return 0;
					hierarchy = Scene.v().getActiveHierarchy();
					if (hierarchy.isClassSubclassOf(a, b))
						return -1;
					if (hierarchy.isClassSuperclassOf(a, b))
						return 1;
					return 0;
				}
			});
			// create the exception handling statements
			List<Unit> toInsert = new LinkedList<Unit>();
			for (SootClass exception : possibleExceptions) {
				Trap trap = null;
				for (Trap t : surroundingTraps) {
					// check if the trap is either super- or sub-class
					// because the procedure might throw a sub type of
					// what it declares.
					if (hierarchy.isClassSubclassOfIncluding(exception, t.getException())) {
						trap = t;
						break;
					}
				}
				Local l = getFreshLocal(body, BooleanType.v());
				if (trap == null) {
					Unit throwStmt = generateThrowStatement(u, exception);					
					toInsert.add(
							assignStmtFor(l, Jimple.v().newInstanceOfExpr(exceptionVariable, exception.getType()), u));
					toInsert.add(ifStmtFor(jimpleNeZero(l), throwStmt, u));
				} else {
					usedTraps.add(trap);
					Unit newTarget = createNewExceptionAndGoToTrap(u, exception, trap);					
					toInsert.add(
							assignStmtFor(l, Jimple.v().newInstanceOfExpr(exceptionVariable, exception.getType()), u));
					toInsert.add(ifStmtFor(jimpleNeZero(l), newTarget, u));
				}
			}
			// now insert everything after the call
			body.getUnits().insertAfter(toInsert, u);
		}
		return usedTraps;
	}

	private Set<Trap> handleThrowStatements() {
		Set<Trap> usedTraps = new HashSet<Trap>();
		// last but not least eliminate all throw statements that are caught.
		Set<Unit> removeThrowStatements = new HashSet<Unit>();
		for (Pair<Unit, Value> pair : throwStatements) {
			Unit u = pair.getFirst();
			// must be a RefType
			RefType rt = (RefType) pair.getSecond().getType();
			SootClass thrownException = rt.getSootClass();
			List<Trap> surroundingTraps = getTrapsGuardingUnit(u, body);

			List<SootClass> possibleExceptions = new LinkedList<SootClass>();
			possibleExceptions.add(thrownException);
			// TODO: maybe we should treat the case where thrownException
			// is Throwable as a special case because then we have a
			// finally block.
			for (Trap t : surroundingTraps) {
				// find any trap that is sub- or super-class
				if (hierarchy.isClassSubclassOfIncluding(t.getException(), thrownException)
						|| hierarchy.isClassSubclassOfIncluding(thrownException, t.getException())) {
					if (!possibleExceptions.contains(t.getException())) {
						possibleExceptions.add(t.getException());
					}
				}
			}
			// now sort the classes.
			Collections.sort(possibleExceptions, new Comparator<SootClass>() {
				@Override
				public int compare(final SootClass a, final SootClass b) {
					if (a == b)
						return 0;
					hierarchy = Scene.v().getActiveHierarchy();
					if (hierarchy.isClassSubclassOf(a, b))
						return -1;
					if (hierarchy.isClassSuperclassOf(a, b))
						return 1;
					return 0;
				}
			});
			// insert a jump for each possible exception.
			List<Unit> toInsert = new LinkedList<Unit>();
			boolean caughtThrowable = false;

			for (SootClass exception : possibleExceptions) {
				Trap trap = null;
				for (Trap t : surroundingTraps) {
					// check if the trap is either super- or sub-class
					// because the procedure might throw a sub type of
					// what it declares.
					if (hierarchy.isClassSubclassOfIncluding(exception, t.getException())) {
						trap = t;
						break;
					}
				}
				if (trap != null) {
					if (exception == thrownException) {
						caughtThrowable = true;
					}
					usedTraps.add(trap);
					Unit newTarget = createNewExceptionAndGoToTrap(u, exception, trap);
					Local l = getFreshLocal(body, BooleanType.v());
					toInsert.add(
							assignStmtFor(l, Jimple.v().newInstanceOfExpr(exceptionVariable, exception.getType()), u));
					toInsert.add(ifStmtFor(jimpleNeZero(l), newTarget, u));
				}
				// if we caught Throwable, we can remove the
				// throw statement.
				if (caughtThrowable) {
					removeThrowStatements.add(u);
				}
			}
			if (!toInsert.isEmpty()) {
				body.getUnits().insertBefore(toInsert, u);
			}

		}
		for (Unit u : removeThrowStatements) {
			body.getUnits().remove(u);
		}
		return usedTraps;
	}

	/**
	 * Handle an exception that has a catch block
	 * 
	 * @param b
	 *            Body of the procedure
	 * @param u
	 *            The unit that throws the exception
	 * @param ce
	 *            The ConditionalException
	 * @param t
	 *            The trap that catches this exception
	 */
	protected void handleCaughtRuntimeException(Unit u, Value v, SootClass exception, Trap t) {
		List<Pair<Value, List<Unit>>> guards = constructGuardExpression(v, exception, true, u);
		Unit newTarget = createNewExceptionAndGoToTrap(u, exception, t);
		for (Pair<Value, List<Unit>> pair : guards) {
			List<Unit> toInsert = new LinkedList<Unit>();
			toInsert.addAll(pair.getSecond());
			toInsert.add(ifStmtFor(pair.getFirst(), newTarget, u));
			body.getUnits().insertBefore(toInsert, u);
		}
	}

	private Unit createNewExceptionAndGoToTrap(Unit u, SootClass exception, Trap t) {
		// add a block that creates an exception object
		// and assigns it to $exception.
		if (!caughtExceptionLocal.containsKey(t.getHandlerUnit())) {
			// only create one local per trap so that we can
			// replace the CaughtExceptionRef later.
			caughtExceptionLocal.put(t.getHandlerUnit(), getFreshLocal(body, exceptionVariable.getType()));
		}
		Local execptionLocal = caughtExceptionLocal.get(t.getHandlerUnit());
		List<Unit> excCreation = createNewException(body, execptionLocal, exception, u);
		excCreation.add(gotoStmtFor(t.getHandlerUnit(), u));
		body.getUnits().addAll(excCreation);
		return excCreation.get(0);
	}

	/**
	 * Handle an exception that has no catch block but is declared in the
	 * procedures throws clause.
	 * 
	 * @param b
	 *            Body of the procedure
	 * @param u
	 *            The unit that throws the exception
	 * @param ce
	 *            The ConditionalException
	 * @param tc
	 *            The class in the throws clause
	 */
	protected void handleUncaughtRuntimeException(Unit u, Value v, SootClass exception) {
		// runtime exceptions that also occur in the throws clause get re-thrown		
		if (!treatUncaughtExceptionsAsAssertions) {			
			List<Pair<Value, List<Unit>>> guards = constructGuardExpression(v, exception, true, u);
			Unit throwStmt = generateThrowStatement(u, exception);
			for (Pair<Value, List<Unit>> pair : guards) {
				List<Unit> toInsert = new LinkedList<Unit>();
				toInsert.addAll(pair.getSecond());
				toInsert.add(ifStmtFor(pair.getFirst(), throwStmt, u));
				body.getUnits().insertBefore(toInsert, u);
			}
		} else {
			List<Pair<Value, List<Unit>>> guards = constructGuardExpression(v, exception, false, u);
			Local assertionLocal = null;
			if (!guards.isEmpty()) {
				assertionLocal = Jimple.v().newLocal("$assert_condition", BooleanType.v());
				body.getLocals().add(assertionLocal);
			}
			
			for (Pair<Value, List<Unit>> pair : guards) {
				List<Unit> toInsert = new LinkedList<Unit>();
				toInsert.addAll(pair.getSecond());
				toInsert.add(Jimple.v().newAssignStmt(assertionLocal, pair.getFirst()));				
				toInsert.add(AssertionReconstruction.v().makeAssertion(assertionLocal));
				body.getUnits().insertBefore(toInsert, u);
			}			
		}

	}

	private Unit generateThrowStatement(Unit u, SootClass exception) {
		if (!generatedThrowStatements.containsKey(exception)) {
			List<Unit> exc = throwNewException(body, exception, u);
			Unit newException = exc.get(0);
			body.getUnits().addAll(exc);
			generatedThrowStatements.put(exception, newException);
		}
		return generatedThrowStatements.get(exception);
	}

	protected List<Unit> createNewException(Body b, Local exLocal, SootClass exc, Host createdFrom) {
		List<Unit> result = new LinkedList<Unit>();
		/*
		 * generate l := new Exception constructor call throw l
		 */
		Local l = exLocal;
		// l = new Exception
		Unit newException = assignStmtFor(l, Jimple.v().newNewExpr(RefType.v(exc)), createdFrom);
		result.add(newException);
		// constructor call
		for (SootMethod sm : exc.getMethods()) {
			if (sm.isConstructor() && sm.getParameterCount() == 0) {
				// This is the constructor we are looking for.
				result.add(invokeStmtFor(Jimple.v().newSpecialInvokeExpr(l, sm.makeRef()), createdFrom));
				break;
			}
		}
		return result;
	}

	protected List<Unit> throwNewException(Body b, SootClass exc, Host createdFrom) {
		Local l = getFreshLocal(b, exc.getType());
		List<Unit> result = createNewException(b, l, exc, createdFrom);
		result.add(throwStmtFor(l, createdFrom));
		return result;
	}

	/**
	 * Generates for a given value and exception a list of pairs of the Value
	 * under which the exception occurs (or the negated version if negated is
	 * true), and the list of supporting statements, such as temp variables.
	 * 
	 * In most cases, the list contains only one element. Only for
	 * IndexOutOfBoundsExceptions it returns two elements. One checking the
	 * lower bound and one checking the upper bound. This is because Jimple does
	 * not have disjunctions.
	 * 
	 * @param val
	 * @param exception
	 * @param negated
	 * @param createdFrom
	 * @return
	 */
	protected List<Pair<Value, List<Unit>>> constructGuardExpression(Value val, SootClass exception, boolean negated,
			Host createdFrom) {
		List<Pair<Value, List<Unit>>> result = new LinkedList<Pair<Value, List<Unit>>>();

		if (exception == nullPointerExceptionClass) {
			// no helper statements needed.
			if (negated) {
				result.add(new Pair<Value, List<Unit>>(Jimple.v().newEqExpr(val, NullConstant.v()),
						new LinkedList<Unit>()));
			} else {
				result.add(new Pair<Value, List<Unit>>(Jimple.v().newNeExpr(val, NullConstant.v()),
						new LinkedList<Unit>()));
			}
			return result;
		} else if (exception == arrayIndexOutOfBoundsExceptionClass) {
			ArrayRef e = (ArrayRef) val;
			// index < array.length
			/*
			 * Since array.length cannot be part of a BinOp, we have to create a
			 * helper local l and a statement l = array.length first.
			 */
			List<Unit> helperStatements = new LinkedList<Unit>();
			Local len = getFreshLocal(body, IntType.v());
			Unit helperStmt = assignStmtFor(len, Jimple.v().newLengthExpr(e.getBase()), createdFrom);
			helperStatements.add(helperStmt);

			Local left = getFreshLocal(body, BooleanType.v());
			helperStmt = assignStmtFor(left, Jimple.v().newLtExpr(e.getIndex(), len), createdFrom);
			helperStatements.add(helperStmt);
			// !(index < array.length)
			if (negated) {
				result.add(new Pair<Value, List<Unit>>(jimpleEqZero(left), helperStatements));
			} else {
				result.add(new Pair<Value, List<Unit>>(jimpleNeZero(left), helperStatements));
			}

			// index >= 0
			helperStatements = new LinkedList<Unit>();
			Local right = getFreshLocal(body, BooleanType.v());
			helperStmt = assignStmtFor(right, Jimple.v().newGeExpr(e.getIndex(), IntConstant.v(0)), createdFrom);
			helperStatements.add(helperStmt);
			// !(index>=0)
			if (negated) {
				result.add(new Pair<Value, List<Unit>>(jimpleEqZero(right), helperStatements));
			} else {
				result.add(new Pair<Value, List<Unit>>(jimpleNeZero(right), helperStatements));
			}

			return result;

		} else if (exception == classCastExceptionClass) {
			CastExpr e = (CastExpr) val;
			// e instanceof t
			/*
			 * Since instanceof cannot be part of a UnOp, we have to create a
			 * helper local l and a statement l = e instanceof t first.
			 */
			List<Unit> helperStatements = new LinkedList<Unit>();
			Local helperLocal = getFreshLocal(body, BooleanType.v());
			Unit helperStmt = assignStmtFor(helperLocal, Jimple.v().newInstanceOfExpr(e.getOp(), e.getCastType()),
					createdFrom);
			helperStatements.add(helperStmt);
			if (negated) {
				result.add(new Pair<Value, List<Unit>>(jimpleEqZero(helperLocal), helperStatements));
			} else {
				result.add(new Pair<Value, List<Unit>>(jimpleNeZero(helperLocal), helperStatements));
			}
			return result;
		}
		throw new RuntimeException("not implemented");
	}

	/**
	 * This a pre-processing hack that removes all traps that are related to
	 * entermonitor and exitmonitor. These traps are always looping. E.g., catch
	 * java.lang.Throwable from label07 to label08 with label07; So it is easy
	 * to spot them. We remove these traps and their code. We also have to
	 * remove all other traps that share the handler unit with these traps.
	 * 
	 * @param body
	 */
	private void removeMonitorTraps(Body body) {
		List<Trap> monitorTraps = new LinkedList<Trap>();
		Map<Trap, List<Unit>> catchBlocks = new HashMap<Trap, List<Unit>>();

		// first collect all monitor traps.
		for (Trap t : body.getTraps()) {
			if (t.getBeginUnit() == t.getHandlerUnit() && t.getException() == throwableClass) {
				// collect the statements caught by this trap.
				List<Unit> catchblock = new LinkedList<Unit>();
				Iterator<Unit> it = body.getUnits().iterator(t.getBeginUnit(), t.getEndUnit());
				boolean containsExitMonitor = false;
				while (it.hasNext()) {
					Unit stmt = it.next();
					if (stmt instanceof ExitMonitorStmt) {
						containsExitMonitor = true;
					}
					catchblock.add(stmt);
				}
				if (!containsExitMonitor) {
					// then this is not a monitor.
					continue;
				}
				// if there is an exit monitor, the catch block also has to end
				// on a throw statement.
				if (!(catchblock.get(catchblock.size() - 1) instanceof ThrowStmt)) {
					throw new RuntimeException("didn't expect that.");
				}
				catchBlocks.put(t, catchblock);

				monitorTraps.add(t);

			}
		}

		// now remove all other traps that jump into those.
		List<Trap> toRemove = new LinkedList<Trap>();
		for (Trap t : body.getTraps()) {
			if (!catchBlocks.containsKey(t)) {
				boolean found = false;
				for (Trap mt : monitorTraps) {
					if (t.getHandlerUnit() == mt.getHandlerUnit()) {
						found = true;
						break;
					}
				}
				if (found) {
					toRemove.add(t);
				}
			}
		}
		body.getTraps().removeAll(toRemove);

		// //now remove all statements in the monitor traps.
		for (Entry<Trap, List<Unit>> entry : catchBlocks.entrySet()) {
			body.getUnits().removeAll(entry.getValue());
			body.getTraps().remove(entry.getKey());
		}
	}

	private void collectPossibleExceptions(Unit u) {
		if (u instanceof DefinitionStmt) {
			DefinitionStmt s = (DefinitionStmt) u;
			// precedence says left before right.
			collectPossibleExceptions(u, s.getLeftOp());
			collectPossibleExceptions(u, s.getRightOp());
		} else if (u instanceof SwitchStmt) {
			SwitchStmt s = (SwitchStmt) u;
			collectPossibleExceptions(u, s.getKey());
		} else if (u instanceof IfStmt) {
			IfStmt s = (IfStmt) u;
			collectPossibleExceptions(u, s.getCondition());
		} else if (u instanceof InvokeStmt) {
			InvokeStmt s = (InvokeStmt) u;
			collectPossibleExceptions(s, s.getInvokeExpr());
		} else if (u instanceof ReturnStmt) {
			ReturnStmt s = (ReturnStmt) u;
			collectPossibleExceptions(u, s.getOp());
		} else if (u instanceof ThrowStmt) {
			ThrowStmt s = (ThrowStmt) u;
			collectPossibleExceptions(u, s.getOp());
			throwStatements.add(new Pair<Unit, Value>(u, s.getOp()));
		}
	}

	private void collectPossibleExceptions(Unit u, Value v) {
		if (v instanceof BinopExpr) {
			BinopExpr e = (BinopExpr) v;
			// precedence says left before right.
			collectPossibleExceptions(u, e.getOp1());
			collectPossibleExceptions(u, e.getOp2());
		} else if (v instanceof UnopExpr) {
			UnopExpr e = (UnopExpr) v;
			collectPossibleExceptions(u, e.getOp());
		} else if (v instanceof InvokeExpr) {
			InvokeExpr ivk = (InvokeExpr) v;
			if (ivk instanceof InstanceInvokeExpr) {
				// if its an instance invoke, check
				// if the base is null.
				InstanceInvokeExpr iivk = (InstanceInvokeExpr) ivk;
				if (iivk.getBase() instanceof Immediate
						&& nullnessAnalysis.isAlwaysNonNullBefore(u, (Immediate) iivk.getBase())) {
					// do nothing.
				} else {
					registerRuntimeException(u, iivk.getBase(), nullPointerExceptionClass);
				}
				collectPossibleExceptions(u, iivk.getBase());
			}
			// handle the args.
			for (Value val : ivk.getArgs()) {
				collectPossibleExceptions(u, val);
			}
			// add the method as a source of exceptions.
			methodInvokes.add(new Pair<Unit, InvokeExpr>(u, ivk));
		} else if (v instanceof CastExpr) {
			CastExpr e = (CastExpr) v;
			collectPossibleExceptions(u, e.getOp());
			registerRuntimeException(u, v, classCastExceptionClass);
		} else if (v instanceof InstanceOfExpr) {
			InstanceOfExpr e = (InstanceOfExpr) v;
			collectPossibleExceptions(u, e.getOp());
		} else if (v instanceof Ref) {
			refMayThrowException(u, (Ref) v);
		} else if (v instanceof AnyNewExpr || v instanceof Immediate) {
			// ignore
		} else {
			throw new RuntimeException("Not handling " + v + " of type " + v.getClass());
		}
	}

	private void refMayThrowException(Unit u, Ref r) {
		if (r instanceof InstanceFieldRef) {
			InstanceFieldRef e = (InstanceFieldRef) r;
			collectPossibleExceptions(u, e.getBase());
			if (e.getBase() instanceof Immediate
					&& nullnessAnalysis.isAlwaysNonNullBefore(u, (Immediate) e.getBase())) {
				// no need to add null pointer check.
			} else {
				registerRuntimeException(u, e.getBase(), nullPointerExceptionClass);
			}
		} else if (r instanceof ArrayRef) {
			ArrayRef e = (ArrayRef) r;
			collectPossibleExceptions(u, e.getBase());
			collectPossibleExceptions(u, e.getIndex());
			registerRuntimeException(u, e, arrayIndexOutOfBoundsExceptionClass);
		} else if (r instanceof IdentityRef || r instanceof StaticFieldRef) {
			// do nothing.
		}
	}

	private void registerRuntimeException(Unit u, Value v, SootClass ex) {
		if (!runtimeExceptions.containsKey(u)) {
			runtimeExceptions.put(u, new LinkedList<Pair<Value, SootClass>>());
		}
		runtimeExceptions.get(u).add(new Pair<Value, SootClass>(v, ex));
	}

	/**
	 * Get the list of all traps that may catch exceptions thrown by u.
	 * 
	 * @param u
	 * @param b
	 * @return
	 */
	protected List<Trap> getTrapsGuardingUnit(Unit u, Body b) {
		List<Trap> result = new LinkedList<Trap>();
		for (Trap t : b.getTraps()) {
			Iterator<Unit> it = b.getUnits().iterator(t.getBeginUnit(), t.getEndUnit());
			while (it.hasNext()) {
				if (u.equals(it.next())) {
					result.add(t);
				}
			}
		}
		return result;
	}

}
