/**
 * 
 */
package soottocfg.soot.transformers;

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
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThrowStmt;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class AssertionReconstruction extends AbstractSceneTransformer {

	/*
	 * Code to handle Java Assertions.
	 */
	private static final String javaAssertionType = "java.lang.AssertionError";
	private static final String javaAssertionFlag = "$assertionsDisabled";

	public void applyTransformation() {
		for (JimpleBody body : this.getSceneBodies()) {
			transform(body);
		}
	}
	
	
	private void transform(Body body) {		
		removeAssertionRelatedNonsense(body); //TODO: is it sufficient to do this
		                                      //for constructors?
		reconstructJavaAssertions(body);
	}
	
	/**
	 * removes useless statements that are generated when java assertions are
	 * translated into bytecode.
	 * 
	 * @param body
	 */
	public void removeAssertionRelatedNonsense(Body body) {
		/*
		 * Look for the following sequence and remove it. $r0 = class
		 * "translation_tests/TranslationTest01"; $z0 = virtualinvoke
		 * $r0.<java.lang.Class: boolean desiredAssertionStatus()>(); if $z0 !=
		 * 0 goto label1; $z1 = 1; (0) goto label2; label1: (1) $z1_1 = 0;
		 * label2: <translation_tests.TranslationTest01: boolean
		 * $assertionsDisabled> = $z1_2;
		 */
		Set<Unit> unitsToRemove = new HashSet<Unit>();
		Iterator<Unit> iterator = body.getUnits().iterator();
		while (iterator.hasNext()) {
			Unit u = iterator.next();
			if (u instanceof AssignStmt) {
				AssignStmt asgn = (AssignStmt) u;
				if (asgn.getLeftOp() instanceof Local && asgn.getRightOp() instanceof ClassConstant) {
					ClassConstant rhs = (ClassConstant) asgn.getRightOp();
					final String c = body.getMethod().getDeclaringClass().getName().replace('.', '/');
					// search for $r0 = class
					// "translation_tests/TranslationTest01";
					if (c.startsWith(rhs.getValue())) {
						unitsToRemove.add(u);
						final int lookAhead = 6;
						Stmt[] uslessBlock = new Stmt[lookAhead];
						for (int i = 0; i < lookAhead; i++) {
							if (!iterator.hasNext()) {
								unitsToRemove.clear();
								break;
							}
							uslessBlock[i] = (Stmt) iterator.next();
							unitsToRemove.add(uslessBlock[i]);
						}

						// $z0 = virtualinvoke $r0.<java.lang.Class: boolean
						// desiredAssertionStatus()>();
						if (!uslessBlock[0].containsInvokeExpr() || !uslessBlock[0].getInvokeExpr().getMethod()
								.getName().equals("desiredAssertionStatus")) {
							unitsToRemove.clear();
							continue;
						}

						// if $z0 != 0 goto label1;
						if (!(uslessBlock[1] instanceof IfStmt)
								|| ((IfStmt) uslessBlock[1]).getTarget() != uslessBlock[4]) {
							unitsToRemove.clear();
							continue;
						}
						// $z1 = 1;
						if (!(uslessBlock[2] instanceof AssignStmt)
								|| !((AssignStmt) uslessBlock[2]).getRightOp().toString().equals("1")) {
							unitsToRemove.clear();
							continue;
						}
						// goto label2;
						if (!(uslessBlock[3] instanceof GotoStmt)
								|| ((GotoStmt) uslessBlock[3]).getTarget() != uslessBlock[5]) {
							unitsToRemove.clear();
							continue;
						}
						// $z1_1 = 0;
						if (!(uslessBlock[4] instanceof AssignStmt)
								|| !((AssignStmt) uslessBlock[4]).getRightOp().toString().equals("0")) {
							unitsToRemove.clear();
							continue;
						}
						// <translation_tests.TranslationTest01: boolean
						// $assertionsDisabled> = $z1_2;
						if (!(uslessBlock[5] instanceof AssignStmt) || !((AssignStmt) uslessBlock[5]).getLeftOp()
								.toString().contains("$assertionsDisabled")) {
							unitsToRemove.clear();
							continue;
						}
						// we found the block.
						break;
					}
				}
			}
		}
		if (!unitsToRemove.isEmpty()) {
			// System.out.println("removed useless block in
			// "+body.getMethod().getBytecodeSignature());
			body.getUnits().removeAll(unitsToRemove);
			body.validate();
		}
	}

	/** @formatter:off
	 * Look for parts of the body that have been created from Java assert
	 * statements. These are always of the form:
	 * 
	 * $z0 = <Assert: boolean $assertionsDisabled>; 
	 * if $z0 != 0 goto label1;
	 * [[some statements]] 
	 * if [[some condition]] goto label1; 
	 * $r1 = new java.lang.AssertionError; 
	 * specialinvoke $r1.<java.lang.AssertionError:
	 * void <init>()>(); 
	 * throw $r1;
	 * 
	 * and replace those blocks by: 
	 * [[some statements]] 
	 * $assert_condition = [[some condition]]; 
	 * staticinvoke <JayHornAssertions: void
	 * super_crazy_assertion(boolean[])>($assert_condition);
	 * 
	 * @param body
	 */
	public void reconstructJavaAssertions(Body body) {		
		Set<Unit> unitsToRemove = new HashSet<Unit>();
		Map<Unit, Value> assertionsToInsert = new HashMap<Unit, Value>();
		Map<Unit, Unit> postAssertionGotos = new HashMap<Unit, Unit>();
		//This is a hack to ensure that the generated assertions
		//have a nice line number. The actual assertion in the byte code
		//is always off by one.
		Map<Unit, Unit> assertionToLineNumberUnit = new HashMap<Unit, Unit>();
		
		// body.getUnits().insertAfter(toInsert, point);
		Iterator<Unit> iterator = body.getUnits().iterator();
		while (iterator.hasNext()) {
			Unit u = iterator.next();
			if (isSootAssertionFlag(u)) {
				// u := $z0 = <Assert: boolean $assertionsDisabled>;
				//This has also the line number tag that we want for the assert
				//statement! Anything after that is off by one.
				unitsToRemove.add(u);
				Unit unitWithInterestingLineNumber = u;
				// u := if $z0 != 0 goto label1;
				u = iterator.next();
				if (!(u instanceof IfStmt)) {
					throw new RuntimeException("");
				}
				//remember where to go if the assertion holds.
//				Unit postAssertionStatement = ((IfStmt)u).getTarget();
				unitsToRemove.add(u);

				// now search for the new java.lang.AssertionError
//				Unit previousUnit = null;
//				Unit lastAssertion = null;
				while (iterator.hasNext()) {
					u = iterator.next();
					
//					if (u instanceof IfStmt) {
//						unitsToRemove.add(u);
//						IfStmt ite = (IfStmt) u;
//						if (ite.getTarget().equals(postAssertionStatement)) {							
//							assertionsToInsert.put(u, ite.getCondition());
//							assertionToLineNumberUnit.put(u, unitWithInterestingLineNumber);							
//						} else {
//							//this is a negated assertion.							
//							assertionsToInsert.put(u, ConditionFlipper.flip((ConditionExpr) ite.getCondition())) ;
//							assertionToLineNumberUnit.put(u, unitWithInterestingLineNumber);
//							
//						}
//						lastAssertion = u;
//					}
					
					if (isNewJavaAssertionError(u)) {
						//make an assert false
						// u := $r1 = new java.lang.AssertionError;
						unitsToRemove.add(u);
						assertionsToInsert.put(u, IntConstant.v(0));
						assertionToLineNumberUnit.put(u, unitWithInterestingLineNumber);
//						lastAssertion = u;

//						if (lastAssertion!=null) {
//							postAssertionGotos.put(lastAssertion, postAssertionStatement);
//						}
						break;
					}
//					previousUnit = u;
				}
				// u := specialinvoke $r1.<java.lang.AssertionError: void
				// <init>()>(); 
				//but there might be some initialization code in between that
				//we have to skip.
				if (!iterator.hasNext()) {
					/* Only observed that for cases of trivial asserts.
					 * See issue #84:
					 * assert(cond || true);
					 */
					break;
				}
				
				u = iterator.next();
				while (!(u instanceof InvokeStmt) || 
						!((InvokeStmt)u).getInvokeExpr().getMethod().getSignature().contains("java.lang.AssertionError: void <init>")) {
					if (!iterator.hasNext()) {
						throw new RuntimeException("Assertion reconstruction failed");
					}
					u = iterator.next();
				}
				unitsToRemove.add(u);

				// u := throw $r1;
				u = iterator.next();
				if (!(u instanceof ThrowStmt)) {
					throw new RuntimeException(u.toString());
				}
				unitsToRemove.add(u);

				continue;
			}
		}
		Local assertionLocal = null;
		if (!assertionsToInsert.isEmpty()) {
			assertionLocal = Jimple.v().newLocal("$assert_"+(body.getLocals().size()), BooleanType.v());
			body.getLocals().add(assertionLocal);
		}
		// remove all boilerplate statements
		// generated from the assertions.
		for (Entry<Unit, Value> entry : assertionsToInsert.entrySet()) {
			List<Unit> unitsToInsert = new LinkedList<Unit>();
			unitsToInsert.add(this.assignStmtFor(assertionLocal, entry.getValue(), assertionToLineNumberUnit.get(entry.getKey())));
			unitsToInsert.add(SootTranslationHelpers.v().makeAssertion(assertionLocal, assertionToLineNumberUnit.get(entry.getKey())));
			if (postAssertionGotos.containsKey(entry.getKey())) {
				//go to the proper successor if the assertion passes.
				unitsToInsert.add(this.gotoStmtFor(postAssertionGotos.get(entry.getKey()), entry.getKey()));
			}
			
			
			body.getUnits().insertBefore(unitsToInsert, entry.getKey());
			unitsToRemove.add(entry.getKey());
		}

		body.getUnits().removeAll(unitsToRemove);
		body.validate();
	}

	/**
	 * Checks if u has been created from a Java assert statement and is of the
	 * form: $r1 = new java.lang.AssertionError;
	 * 
	 * @param u
	 * @return
	 */
	private boolean isNewJavaAssertionError(Unit u) {
		if (u instanceof AssignStmt) {
			AssignStmt ids = (AssignStmt) u;
			if (ids.getRightOp() instanceof NewExpr) {
				NewExpr ne = (NewExpr) ids.getRightOp();
				return ne.getType().toString().equals(javaAssertionType);
			}
		}
		return false;
	}

	/**
	 * Checks if u has been created from a Java assert statement and is of the
	 * form: $z0 = <Assert: boolean $assertionsDisabled>;
	 * 
	 * @param u
	 * @return
	 */
	private boolean isSootAssertionFlag(Unit u) {
		if (u instanceof AssignStmt) {
			AssignStmt ids = (AssignStmt) u;
			if (ids.getRightOp() instanceof StaticFieldRef) {
				StaticFieldRef sfr = (StaticFieldRef) ids.getRightOp();
				return sfr.getField().getName().equals(javaAssertionFlag);
			}
		}
		return false;
	}

}
