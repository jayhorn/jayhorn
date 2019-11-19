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

import com.google.common.base.Verify;

import soot.Body;
import soot.BooleanType;
import soot.Hierarchy;
import soot.Immediate;
import soot.IntType;
import soot.Local;
import soot.PatchingChain;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.dava.toolkits.base.misc.ConditionFlipper;
import soot.jimple.AnyNewExpr;
import soot.jimple.ArrayRef;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ConditionExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.DivExpr;
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
import soot.jimple.JimpleBody;
import soot.jimple.LengthExpr;
import soot.jimple.NullConstant;
import soot.jimple.Ref;
import soot.jimple.RemExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.SwitchStmt;
import soot.jimple.ThrowStmt;
import soot.jimple.UnopExpr;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.jimple.internal.AbstractNewArrayExpr;
import soot.tagkit.Host;
import soot.toolkits.graph.CompleteUnitGraph;
import soottocfg.soot.util.SootTranslationHelpers;
import soottocfg.util.Pair;

/**
 * @author schaef
 *         TODO: we should do a fixed point iteration over all methods
 *         and add throws clauses to all those that can throw user created
 *         RuntimeExceptions.
 */
public class ExceptionTransformer extends AbstractSceneTransformer {

	protected final SootClass exceptionClass, runtimeExceptionClass, nullPointerExceptionClass,
			arrayIndexOutOfBoundsExceptionClass, classCastExceptionClass, errorExceptionClass, 
			throwableClass, arithmeticExceptionClass, negativeArraySizeExceptionClass;
	private final Hierarchy hierarchy;

	private boolean treatUncaughtExceptionsAsAssertions;

	private Body body;
	private NullnessAnalysis nullnessAnalysis;
	private Map<Unit, List<Pair<Value, SootClass>>> runtimeExceptions;
	private Set<Pair<Unit, InvokeExpr>> methodInvokes;
	private Set<Pair<Unit, Value>> throwStatements;
	private Map<Unit, Local> caughtExceptionLocal;
	private Map<SootClass, Unit> generatedThrowStatements;

	/**
	 * 
	 */
	public ExceptionTransformer() {
		this(true);
	}

	public ExceptionTransformer(boolean uncaughtAsAssertion) {
		treatUncaughtExceptionsAsAssertions = uncaughtAsAssertion;
		exceptionClass = Scene.v().getSootClass("java.lang.Exception");
		throwableClass = Scene.v().getSootClass("java.lang.Throwable");
		runtimeExceptionClass = Scene.v().getSootClass("java.lang.RuntimeException");
		nullPointerExceptionClass = Scene.v().getSootClass("java.lang.NullPointerException");
		arrayIndexOutOfBoundsExceptionClass = Scene.v().getSootClass("java.lang.ArrayIndexOutOfBoundsException");
		negativeArraySizeExceptionClass = Scene.v().getSootClass("java.lang.NegativeArraySizeException");
		classCastExceptionClass = Scene.v().getSootClass("java.lang.ClassCastException");
		arithmeticExceptionClass = Scene.v().getSootClass("java.lang.ArithmeticException");
		errorExceptionClass = Scene.v().getSootClass("java.lang.Error");
		hierarchy = Scene.v().getActiveHierarchy();
	}

	public void applyTransformation() {
		boolean tmp = treatUncaughtExceptionsAsAssertions;
		for (JimpleBody body : this.getSceneBodies()) {

			if (body.getMethod().isMain()) {
				treatUncaughtExceptionsAsAssertions = true;
			} else {
				treatUncaughtExceptionsAsAssertions = tmp;
			}
			transform(body);
		}
	}

	private void transform(Body b) {
		runtimeExceptions = new HashMap<Unit, List<Pair<Value, SootClass>>>();
		methodInvokes = new HashSet<Pair<Unit, InvokeExpr>>();
		throwStatements = new HashSet<Pair<Unit, Value>>();
		caughtExceptionLocal = new HashMap<Unit, Local>();
		generatedThrowStatements = new HashMap<SootClass, Unit>();

		body = b;
		this.nullnessAnalysis = new NullnessAnalysis(new CompleteUnitGraph(body));
		// first remove all the monitor related exceptions
		removeUnreachableTraps(body);
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
						Value left = ds.getLeftOp();
						Value right = caughtExceptionLocal.get(t.getHandlerUnit());
						if (!hierarchy.isClassSubclassOfIncluding(((RefType) right.getType()).getSootClass(),
								((RefType) left.getType()).getSootClass())) {
							// if the left has a narrower type then we need to
							// add a cast.
							right = Jimple.v().newCastExpr(right, left.getType());
						}

						Unit newAssign = assignStmtFor(left, right, ds);
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
			toInsert.add(assignStmtFor(SootTranslationHelpers.v().getExceptionGlobalRef(), NullConstant.v(),
					entry.getKey()));
			body.getUnits().insertAfter(toInsert, entry.getKey());
			body.getUnits().remove(entry.getKey());
		}
		// finally, remove those traps:
		body.getTraps().removeAll(usedTraps);

		if (!body.getTraps().isEmpty()) {
                        String msg = String.format("%d traps could not be removed for %s", body.getTraps().size(),
					body.getMethod().getSignature());

                        if (soottocfg.Options.v().strictlySound()) {
                            throw new RuntimeException(msg);
                        } else {
                            System.err.println(msg);
                            System.err.println("trying to continue ...");
                        }

			// TODO!!!!!
			body.getTraps().clear();
			Set<Unit> caughtExceptionUnits = new HashSet<Unit>();

			for (Unit u : body.getUnits()) {
				if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof CaughtExceptionRef) {
					caughtExceptionUnits.add(u);
					// deadLocals.add((Local) ((DefinitionStmt) u).getLeftOp());
				}
			}
			body.getUnits().removeAll(caughtExceptionUnits);
			// body.getLocals().removeAll(deadLocals);
		}
		// System.err.println(body);
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
			
			possibleExceptions.add(nullPointerExceptionClass);
			possibleExceptions.add(arrayIndexOutOfBoundsExceptionClass);
			possibleExceptions.add(negativeArraySizeExceptionClass);
			possibleExceptions.add(classCastExceptionClass);
			possibleExceptions.add(arithmeticExceptionClass);
			possibleExceptions.add(errorExceptionClass);
			
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
			
			if (mayThrowRuntimeException(ivk)) {
				//every method could throw a runtime exception.			
				addRuntimExceptionIfNecessary(possibleExceptions);
			}
			// now sort the classes.
			sortExceptionsTightestFirst(possibleExceptions);
			
			// create the exception handling statements
			List<Unit> toInsert = new LinkedList<Unit>();
			Local exceptionVarLocal = getFreshLocal(body, throwableClass.getType());
			toInsert.add(assignStmtFor(exceptionVarLocal, SootTranslationHelpers.v().getExceptionGlobalRef(), u));

			boolean throwsUncaughtException = false;

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

				if (trap == null) {
					throwsUncaughtException = true;
					continue;
				}

				Value instOf = Jimple.v().newInstanceOfExpr(exceptionVarLocal, RefType.v(exception));
				Local l = getFreshLocal(body, BooleanType.v());
				toInsert.add(assignStmtFor(l, instOf, u));
				Unit target;

				usedTraps.add(trap);
				if (!caughtExceptionLocal.containsKey(trap.getHandlerUnit())) {
					// only create one local per trap so that we can
					// replace the CaughtExceptionRef later.
					caughtExceptionLocal.put(trap.getHandlerUnit(), getFreshLocal(body, throwableClass.getType()));
				}
				toInsert.add(assignStmtFor(caughtExceptionLocal.get(trap.getHandlerUnit()), exceptionVarLocal, u));
				target = trap.getHandlerUnit();

				toInsert.add(ifStmtFor(jimpleNeZero(l), target, u));
			}
			
			if (throwsUncaughtException) {
				//check if the exception global is non-null and return.								
				if (this.body.getMethod().isMain()) {					
					//if this is main, we can't re-throw, so we
					//have to assert that the assertion is not null.
					Local assertionLocal = Jimple.v().newLocal("$assert_" + (body.getLocals().size()), BooleanType.v());
					body.getLocals().add(assertionLocal);
					toInsert.add(assignStmtFor(assertionLocal, Jimple.v().newEqExpr(exceptionVarLocal, NullConstant.v()), u));
					toInsert.add(SootTranslationHelpers.v().makeAssertion(assertionLocal, u));
				} else {
					//normal case: if the exception isn't null, re-throw by
					//returning a default value.
					Unit defaultReturn = SootTranslationHelpers.v().getDefaultReturnStatement(body.getMethod().getReturnType(), u);
					body.getUnits().add(defaultReturn);				
					toInsert.add(ifStmtFor(Jimple.v().newNeExpr(exceptionVarLocal, NullConstant.v()), defaultReturn, u));
				}				
			}
			
			// now insert everything after the call
			if (toInsert.size() > 1) {
				body.getUnits().insertAfter(toInsert, u);
			}
		}
		return usedTraps;
	}

	/**
	 * Returns true if we assume that the invoked method may throw an
	 * exception and false otherwise.
	 * @param ivk
	 * @return
	 */
	private boolean mayThrowRuntimeException(InvokeExpr ivk) {
		return mayThrowRuntimeException(ivk, new HashSet<InvokeExpr>());
	}
	
	private boolean mayThrowRuntimeException(InvokeExpr ivk, Set<InvokeExpr> visited) { 
		final SootMethod m = ivk.getMethod();
		if (visited.contains(ivk)) {
			//for recursive calls, worst case assume that they may throw.
			mayThrowRuntimeException.put(m, true);
			return true;
		}
		visited.add(ivk);
		//TODO: this is only a stub.		
		if (m.isStaticInitializer()) {
			return false;
		} else if (m.isJavaLibraryMethod()) {
			return false;
		} 
		if (!mayThrowRuntimeException.containsKey(m)) {
			boolean mayThrow = true;
			if (m.hasActiveBody()) {
				JimpleBody jb = (JimpleBody)m.getActiveBody();
				NullnessAnalysis nna = new NullnessAnalysis(new CompleteUnitGraph(jb));
				mayThrow = false;
				for (Unit u : jb.getUnits()) {
					Stmt st = (Stmt)u;
					if (st.containsArrayRef()) {
						mayThrow = true; break;
					} else if (st.containsFieldRef() && st.getFieldRef() instanceof InstanceFieldRef) {
						InstanceFieldRef fr = (InstanceFieldRef)st.getFieldRef();						
						Immediate base = (Immediate)fr.getBase();
						if (!m.isStatic() && !base.equals(jb.getThisLocal())) {
//						if (!nna.isAlwaysNonNullBefore(u, base)) {
							mayThrow = true; break;
						}
					} else if (st.containsInvokeExpr() ) {
						if (st.getInvokeExpr() instanceof InstanceInvokeExpr) {
							if (!nna.isAlwaysNonNullBefore(u, (Immediate)((InstanceInvokeExpr)st.getInvokeExpr()).getBase())) {
								mayThrow = true; break;
							}							
						}
						if (mayThrowRuntimeException(st.getInvokeExpr(), visited)) {
							mayThrow = true; break;	
						}						
					} else if (st instanceof ThrowStmt) {
						mayThrow = true; break;
					}
				}
			}
			mayThrowRuntimeException.put(m, mayThrow);
		} 
		return mayThrowRuntimeException.get(ivk.getMethod());		
	}
	
	Map<SootMethod, Boolean> mayThrowRuntimeException = new HashMap<SootMethod, Boolean>();
	
	/**
	 * Takes a list of SootClasses and adds the RuntimeException class
	 * if the list does not contain RuntimeException or any or its
	 * supertypes.
	 * 
	 * @param classes
	 */
	private void addRuntimExceptionIfNecessary(List<SootClass> classes) {
		boolean containRteOrAbove = false;
		for (SootClass c : classes) {
			if (hierarchy.isClassSubclassOfIncluding(c, runtimeExceptionClass)) {
				containRteOrAbove = true;
				break;
			}
		}
		if (!containRteOrAbove) {
			classes.add(runtimeExceptionClass);
		}
	}

	private void sortExceptionsTightestFirst(List<SootClass> exceptions) {
		Collections.sort(exceptions, new Comparator<SootClass>() {
			@Override
			public int compare(final SootClass a, final SootClass b) {
				if (a == b)
					return 0;
				if (hierarchy.isClassSubclassOf(a, b))
					return 1;
				if (hierarchy.isClassSuperclassOf(a, b))
					return -1;
				return 0;
			}
		});
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

			// TODO: maybe we should treat the case where thrownException
			// is Throwable as a special case because then we have a
			// finally block.
			boolean catchesThrownException = false;
			for (Trap t : surroundingTraps) {
				// find any trap that is sub- or super-class
				if (hierarchy.isClassSubclassOfIncluding(t.getException(), thrownException)
						|| hierarchy.isClassSubclassOfIncluding(thrownException, t.getException())) {
					if (!possibleExceptions.contains(t.getException())) {
						possibleExceptions.add(t.getException());
						if (hierarchy.isClassSubclassOf(thrownException, t.getException())) {
							catchesThrownException = true;
						}
					}
				}
			}

			if (!catchesThrownException) {
				possibleExceptions.add(thrownException);
			}

			// now sort the classes.
			sortExceptionsTightestFirst(possibleExceptions);

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
					Unit newTarget = updateExceptionVariableAndGoToTrap(u, ((ThrowStmt) u).getOp(), trap);
					Local l = getFreshLocal(body, BooleanType.v());
					Value instOf = Jimple.v().newInstanceOfExpr(((ThrowStmt) u).getOp(), RefType.v(exception));
					toInsert.add(assignStmtFor(l, instOf, u));
					toInsert.add(ifStmtFor(jimpleNeZero(l), newTarget, u));
				}
				// if we caught Throwable, we can remove the
				// throw statement.
				if (caughtThrowable) {
					removeThrowStatements.add(u);
				}
			}
			if (!removeThrowStatements.contains(u)) {
				// If the throw was not caught, replace it by a return.
				removeThrowStatements.add(u);
				toInsert.addAll(updateExceptionVariableAndReturn(body, ((ThrowStmt) u).getOp(), u));
			}
			// check if toInsert only contains the assignment to the
			// exception local. If so, we do not need to add it.
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

	private Unit updateExceptionVariableAndGoToTrap(Unit u, Value v, Trap t) {
		if (!caughtExceptionLocal.containsKey(t.getHandlerUnit())) {
			// only create one local per trap so that we can
			// replace the CaughtExceptionRef later.
			caughtExceptionLocal.put(t.getHandlerUnit(),
					getFreshLocal(body, SootTranslationHelpers.v().getExceptionGlobal().getType()));
		}
		Local execptionLocal = caughtExceptionLocal.get(t.getHandlerUnit());
		List<Unit> units = new LinkedList<Unit>();
		units.add(assignStmtFor(execptionLocal, v, u));
		units.add(gotoStmtFor(t.getHandlerUnit(), u));
		body.getUnits().addAll(units);
		return units.get(0);
	}

	private Unit createNewExceptionAndGoToTrap(Unit u, SootClass exception, Trap t) {
		// add a block that creates an exception object
		// and assigns it to $exception.
		if (!caughtExceptionLocal.containsKey(t.getHandlerUnit())) {
			// only create one local per trap so that we can
			// replace the CaughtExceptionRef later.
			caughtExceptionLocal.put(t.getHandlerUnit(),
					getFreshLocal(body, SootTranslationHelpers.v().getExceptionGlobal().getType()));
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
			Unit throwStmt = generateExceptionalReturn(u, exception);
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
				assertionLocal = Jimple.v().newLocal("$assert_" + (body.getLocals().size()), BooleanType.v());
				body.getLocals().add(assertionLocal);
			}

			for (Pair<Value, List<Unit>> pair : guards) {
				List<Unit> toInsert = new LinkedList<Unit>();
				toInsert.addAll(pair.getSecond());
				toInsert.add(assignStmtFor(assertionLocal, pair.getFirst(), u));
				toInsert.add(SootTranslationHelpers.v().makeAssertion(assertionLocal, u));
				body.getUnits().insertBefore(toInsert, u);
			}
		}

	}

	private Unit generateExceptionalReturn(Unit u, SootClass exception) {
		if (!generatedThrowStatements.containsKey(exception)) {
			List<Unit> units = new LinkedList<Unit>();
			Local exceptionLocal = getFreshLocal(body, exception.getType());
			units.addAll(createNewException(body, exceptionLocal, exception, u));
			units.addAll(updateExceptionVariableAndReturn(body, exceptionLocal, u));
			Unit target = units.get(0);
			body.getUnits().addAll(units);
			generatedThrowStatements.put(exception, target);
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
		boolean foundConstructor = false;
		for (SootMethod sm : exc.getMethods()) {
			if (sm.isConstructor() && sm.getParameterCount() == 0) {
				// This is the constructor we are looking for.
				foundConstructor = true;
				result.add(invokeStmtFor(Jimple.v().newSpecialInvokeExpr(l, sm.makeRef()), createdFrom));
				break;
			}
		}
		Verify.verify(foundConstructor, "Don't try to call a constructor on an abstract class or interface!");
		return result;
	}

	protected List<Unit> updateExceptionVariableAndReturn(Body b, Value v, Host createdFrom) {
		List<Unit> result = new LinkedList<Unit>();
		if (b.getMethod().isMain()) {
			//if it's a main we add an assert false.
			Local assertionLocal = Jimple.v().newLocal("$assert_" + (body.getLocals().size()), BooleanType.v());
			body.getLocals().add(assertionLocal);
			result.add(assignStmtFor(assertionLocal, IntConstant.v(0), createdFrom));
			result.add(SootTranslationHelpers.v().makeAssertion(assertionLocal, createdFrom));			
		} else {
			//otherwise we update the exception variable and return a default value.
			if (!SootTranslationHelpers.v().getExceptionGlobalRef().equals(v)) {
				result.add(assignStmtFor(SootTranslationHelpers.v().getExceptionGlobalRef(), v, createdFrom));
			}			
		}
		result.add(SootTranslationHelpers.v().getDefaultReturnStatement(b.getMethod().getReturnType(), createdFrom));
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

			List<Unit> helperStatements = new LinkedList<Unit>();
			Value arrayLen = Jimple.v().newLengthExpr(e.getBase());
			Local len = getFreshLocal(body, arrayLen.getType());

			Unit helperStmt = assignStmtFor(len, arrayLen, createdFrom);
			helperStatements.add(helperStmt);

			// (index < array.length)
			Local left = getFreshLocal(body, IntType.v());
			helperStmt = assignStmtFor(left, e.getIndex(), createdFrom);
			helperStatements.add(helperStmt);

			ConditionExpr guard = Jimple.v().newLtExpr(left, len);
			if (negated) {
				guard = ConditionFlipper.flip(guard);
			}
			result.add(new Pair<Value, List<Unit>>(guard, helperStatements));

			// index >= 0
			helperStatements = new LinkedList<Unit>();
			guard = Jimple.v().newLeExpr(IntConstant.v(0), left);
			if (negated) {
				guard = ConditionFlipper.flip(guard);
			}
			result.add(new Pair<Value, List<Unit>>(guard, helperStatements));

			return result;

		} else if (exception == negativeArraySizeExceptionClass) {
			List<Unit> helperStatements = new LinkedList<Unit>();
			
			// size >= 0
//			Local left = getFreshLocal(body, IntType.v());
//			Unit helperStmt = assignStmtFor(left, val, createdFrom);
//			helperStatements.add(helperStmt);
			ConditionExpr guard = Jimple.v().newLeExpr(IntConstant.v(0), val);
			if (negated) {
				guard = ConditionFlipper.flip(guard);
			}
			result.add(new Pair<Value, List<Unit>>(guard, helperStatements));

			return result;

		} else if (exception == classCastExceptionClass) {
			CastExpr e = (CastExpr) val;
			// e instanceof t
			/*
			 * Since instanceof cannot be part of a UnOp, we have to create a
			 * helper local l and a statement l = e instanceof t first.
			 */
			if (!(e.getCastType() instanceof PrimType)) {
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
//			} else {
//				System.err.println("Not guarding cast from " + e.getOp().getType() + " to " + e.getCastType()
//						+ ". This should be done by the compiler.");
			}
			return result;
		} else if (exception == arithmeticExceptionClass) {
			if (negated) {
				result.add(new Pair<Value, List<Unit>>(jimpleEqZero(val),
						new LinkedList<Unit>()));
			} else {
				result.add(new Pair<Value, List<Unit>>(jimpleNeZero(val),
					new LinkedList<Unit>()));
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

	private boolean isAssertionMethod(SootMethod sm) {
		if (sm.getSignature().equals(SootTranslationHelpers.v().getAssertMethod().getSignature())) {
			// throw new RuntimeException("WORKS");
			return true;
		}
		// TODO: do that for others as well like guava Verify, or Junit Assert
		return false;
	}

	private void collectPossibleExceptions(Unit u, Value v) {
		if (v instanceof BinopExpr) {
			BinopExpr e = (BinopExpr) v;
			if (e instanceof DivExpr || e instanceof RemExpr) {
				// check for division by 0
				Value divisor = e.getOp2();
				if (!isNonZeroConstant(divisor)) {
					registerRuntimeException(u, divisor, arithmeticExceptionClass);
				}
			}			
			// precedence says left before right.
			collectPossibleExceptions(u, e.getOp1());
			collectPossibleExceptions(u, e.getOp2());
		} else if (v instanceof UnopExpr) {
			UnopExpr e = (UnopExpr) v;
			collectPossibleExceptions(u, e.getOp());
		} else if (v instanceof InvokeExpr) {
			InvokeExpr ivk = (InvokeExpr) v;
			if (isAssertionMethod(ivk.getMethod())) {
				// do not tinker with assertion methods because
				// they will be turned into assertions anyway.
				return;
			}
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
		} else if (v instanceof LengthExpr) {
			LengthExpr le = (LengthExpr)v;
			registerRuntimeException(u, le.getOp(), nullPointerExceptionClass);
                } else if (v instanceof AbstractNewArrayExpr) {
                        AbstractNewArrayExpr nae = (AbstractNewArrayExpr)v;
			collectPossibleExceptions(u, nae.getSize());
			registerRuntimeException(u, nae.getSize(), negativeArraySizeExceptionClass);
		} else if (v instanceof AnyNewExpr || v instanceof Immediate) {
			// ignore
		} else {
			throw new RuntimeException("Not handling " + v + " of type " + v.getClass());
		}
	}
	
	private boolean isNonZeroConstant(Value divisor) {
		if (divisor instanceof IntConstant) {
			IntConstant divint = (IntConstant) divisor;
			if (divint.value != 0)
				return true;
		}
		return false;
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
			registerRuntimeException(u, e.getBase(), nullPointerExceptionClass);
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

	protected void removeUnreachableTraps(Body b) {
		List<Trap> duplicates = new LinkedList<Trap>();
		for (Trap t : b.getTraps()) {
			Trap current = t;
			while (b.getTraps().getPredOf(current) != null) {
				current = b.getTraps().getPredOf(current);
				if (equalTraps(current, t)) {
					// System.err.println("DUPLICATE "+t);
					duplicates.add(t);
					break;
				}
			}
		}
		b.getTraps().removeAll(duplicates);
	}

	protected boolean equalTraps(Trap a, Trap b) {
		return a.getBeginUnit() == b.getBeginUnit() && a.getEndUnit() == b.getEndUnit()
				&& a.getException() == b.getException();
	}
	
}
