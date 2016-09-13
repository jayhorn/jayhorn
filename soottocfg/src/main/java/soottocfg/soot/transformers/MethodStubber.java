/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *         For the verification to work, we need to add bodies for constructors
 *         of library classes
 *         that set the dynamic type field; otherwise, instanceof operations for
 *         these classes will
 *         turn into assume false.
 *         The tricky part is that, in order to that, we need to lift the
 *         resolving level of the class
 *         from < SootClass.Hierarchy to SootClass.Body which means we have to
 *         stub ALL methods in that
 *         class.
 * 
 */
public class MethodStubber extends AbstractSceneTransformer {

	public void applyTransformation() {
		Set<SootMethod> invokedLibraryMethods = getInvokedLibraryMethods();
		Set<SootClass> modifiedClasses = new HashSet<SootClass>();
		for (SootMethod m : invokedLibraryMethods) {
			JimpleBody body = Jimple.v().newBody(m);
			SootClass cls = m.getDeclaringClass();
			body.insertIdentityStmts();
			if (m.isConstructor()) {

				if (!cls.declaresFieldByName(SootTranslationHelpers.typeFieldName)) {
					cls.addField(new SootField(SootTranslationHelpers.typeFieldName,
							RefType.v(Scene.v().getSootClass("java.lang.Class")), Modifier.PUBLIC | Modifier.FINAL));
				}
				SootField dyntTypeField = cls.getFieldByName(SootTranslationHelpers.typeFieldName);
				Unit init = Jimple.v().newAssignStmt(
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), dyntTypeField.makeRef()),
						SootTranslationHelpers.v().getClassConstant(RefType.v(cls)));
				body.getUnits().add(init);
				// add call to super class constructor if necessary
				if (cls.hasSuperclass()) {
					SootMethod superConstructor = null;
					for (SootMethod sm : cls.getSuperclass().getMethods()) {
						if (sm.isConstructor() && sm.getParameterCount() == 0) {
							superConstructor = sm;
							break;
						}
					}
					if (superConstructor != null) {
						SpecialInvokeExpr ivk = Jimple.v().newSpecialInvokeExpr(body.getThisLocal(),
								superConstructor.makeRef());
						body.getUnits().add(Jimple.v().newInvokeStmt(ivk));
//						System.err.println("Added: " + ivk + " to " + m.getSignature());
					}
				}
			}

			//havoc the exception global.
			Type exType = SootTranslationHelpers.v().getExceptionGlobal().getType();
			SootMethod havocCall = SootTranslationHelpers.v().getHavocMethod(exType);
			Local havocLocal = Jimple.v().newLocal("havocEx", exType);
			body.getLocals().add(havocLocal);
			body.getUnits().add(Jimple.v().newAssignStmt(havocLocal, Jimple.v().newStaticInvokeExpr(havocCall.makeRef())));
			body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(SootTranslationHelpers.v().getExceptionGlobal().makeRef()) , havocLocal));		
			//instead of default return, we have to return HAVOC!!!!!
			if (m.getReturnType() instanceof VoidType) {
				body.getUnits().add(SootTranslationHelpers.v().getDefaultReturnStatement(m.getReturnType(), m));
			} else {
				SootMethod havoc = SootTranslationHelpers.v().getHavocMethod(m.getReturnType());
				Local ret = Jimple.v().newLocal("havoc", m.getReturnType());
				body.getLocals().add(ret);
				body.getUnits().add(Jimple.v().newAssignStmt(ret, Jimple.v().newStaticInvokeExpr(havoc.makeRef())));
				body.getUnits().add(Jimple.v().newReturnStmt(ret));
			}
			
			m.setActiveBody(body);
			cls.setApplicationClass();
			cls.setResolvingLevel(SootClass.BODIES);
			modifiedClasses.add(cls);
		}

		for (SootClass sc : modifiedClasses) {
			for (SootMethod sm : new LinkedList<SootMethod>(sc.getMethods())) {
				if (!invokedLibraryMethods.contains(sm) && !sm.isStaticInitializer()) {
					sc.removeMethod(sm);
				}
			}
		}
	}

	public Set<SootMethod> getInvokedLibraryMethods() {
		Set<SootMethod> invokedLibraryMethods = new HashSet<SootMethod>();
		for (SootClass sc : new LinkedList<SootClass>(Scene.v().getClasses())) {
			if (sc.resolvingLevel() >= SootClass.SIGNATURES) {
				for (SootMethod sm : sc.getMethods()) {
					if (sc.resolvingLevel() >= SootClass.BODIES && sm.isConcrete()) {
						// record all methods for which we found a body.
						for (Unit u : sm.retrieveActiveBody().getUnits()) {
							Stmt s = (Stmt) u;
							if (s.containsInvokeExpr()) {
								SootMethod invoked = s.getInvokeExpr().getMethod();
								if (invoked.isConcrete() && invoked.isJavaLibraryMethod()) {
									invokedLibraryMethods.add(invoked);
									// TODO stubbing the static initializers is
									// probably a bit too imprecise.
									for (SootMethod otherMethod : invoked.getDeclaringClass().getMethods()) {
										if (otherMethod.isStaticInitializer()) {
											invokedLibraryMethods.add(otherMethod);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		// if library methods include constructors, we also have to add the
		// constructors of their super classes.
		for (SootMethod sm : new HashSet<SootMethod>(invokedLibraryMethods)) {
			if (sm.isConstructor()) {
				SootClass dc = sm.getDeclaringClass();
				if (dc.resolvingLevel() >= SootClass.HIERARCHY && dc.hasSuperclass()) {
					for (SootMethod superMethod : dc.getSuperclass().getMethods()) {
						if (superMethod.isConcrete() && superMethod.isConstructor()) {
							// TODO: hack - we only need default constructors.
							if (superMethod.getParameterCount() > 0)
								continue;
							invokedLibraryMethods.add(superMethod);
						}
					}
				}
			}
		}

		return invokedLibraryMethods;
	}

}
