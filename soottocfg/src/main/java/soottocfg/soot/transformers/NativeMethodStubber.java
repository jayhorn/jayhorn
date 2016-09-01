package soottocfg.soot.transformers;

import java.util.LinkedList;

import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author rodykers, mschaef
 * 
 * Look for native methods and replace them with a stub.
 * 
 */
public class NativeMethodStubber extends AbstractSceneTransformer {

	public void applyTransformation() {
		for (SootClass sc : new LinkedList<SootClass>(Scene.v().getClasses())) {
			if (sc.resolvingLevel() >= SootClass.BODIES) {
				for (SootMethod sm : sc.getMethods()) {
					if (sm.isNative()) {
						sm.setModifiers(sm.getModifiers() ^ Modifier.NATIVE);
						JimpleBody body = Jimple.v().newBody(sm);
						body.insertIdentityStmts();
						//havoc the exception global.
						Type exType = SootTranslationHelpers.v().getExceptionGlobal().getType();
						SootMethod havocCall = SootTranslationHelpers.v().getHavocMethod(exType);
						Local havocLocal = Jimple.v().newLocal("havoc", exType);
						body.getLocals().add(havocLocal);
						body.getUnits().add(Jimple.v().newAssignStmt(havocLocal, Jimple.v().newStaticInvokeExpr(havocCall.makeRef())));
						body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(SootTranslationHelpers.v().getExceptionGlobal().makeRef()) , havocLocal));		
						//instead of default return, we have to return HAVOC!!!!!
						if (sm.getReturnType() instanceof VoidType) {
							body.getUnits().add(SootTranslationHelpers.v().getDefaultReturnStatement(sm.getReturnType(), sm));
						} else {
							SootMethod havoc = SootTranslationHelpers.v().getHavocMethod(sm.getReturnType());
							Local ret = Jimple.v().newLocal("havoc", sm.getReturnType());
							body.getLocals().add(ret);
							body.getUnits().add(Jimple.v().newAssignStmt(ret, Jimple.v().newStaticInvokeExpr(havoc.makeRef())));
							body.getUnits().add(Jimple.v().newReturnStmt(ret));
						}
						sm.setActiveBody(body);
//						System.out.println("Stubbed " + sm.getName());
					}
				}
			}
		}
	}
}
