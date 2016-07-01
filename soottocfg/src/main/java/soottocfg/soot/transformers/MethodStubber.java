/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.Arrays;
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
public class MethodStubber {

	public static final String HavocClassName = "Havoc_Class";

	/**
	 * Get a method that returns an unknown value of type t.
	 * @param t
	 * @return
	 */
	private SootMethod getHavocMethod(soot.Type t) {
		if (!Scene.v().containsClass(HavocClassName)) {
			SootClass sClass = new SootClass(HavocClassName, Modifier.PUBLIC | Modifier.PUBLIC);
			sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
			sClass.setResolvingLevel(SootClass.SIGNATURES);
			Scene.v().addClass(sClass);			
		}
		SootClass cls = Scene.v().getSootClass(HavocClassName);
		final String havocMethodName = "havoc_" + t.toString();
		if (!cls.declaresMethodByName(havocMethodName)) {
			cls.addMethod(new SootMethod(havocMethodName, Arrays.asList(new Type[] {}), t,
					Modifier.PUBLIC | Modifier.STATIC));
		}
		return cls.getMethodByName("havoc_" + t.toString());
	}

	public void stubUsedLibraryMethods() {
		Set<SootMethod> invokedLibraryMethods = getInvokedLibraryMethods();
		Set<SootClass> modifiedClasses = new HashSet<SootClass>();
		for (SootMethod m : invokedLibraryMethods) {
			System.err.println("Creating body for " + m.getSignature());
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
			}
			// TODO: instead of default return, we have to return HAVOC!!!!!
			if (m.getReturnType() instanceof VoidType) {
				body.getUnits().add(SootTranslationHelpers.v().getDefaultReturnStatement(m.getReturnType(), m));
			} else {
				SootMethod havoc = getHavocMethod(m.getReturnType());
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
		return invokedLibraryMethods;
	}
}
