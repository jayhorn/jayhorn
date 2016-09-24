/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

import soot.ArrayType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StaticFieldRef;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class StaticInitializerTransformer extends AbstractSceneTransformer {

	private final Set<SootMethod> staticInitializers = new LinkedHashSet<SootMethod>();

	/**
	 * 
	 */
	public StaticInitializerTransformer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void applyTransformation() {
		for (SootClass sc : new LinkedList<SootClass>(Scene.v().getClasses())) {
			if ((sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass())
					|| sc == SootTranslationHelpers.v().getAssertionClass()) {
				initializeStaticFields(sc);
			}
		}
		addStaticInitializerCallsToMain();
	}

	private void addStaticInitializerCallsToMain() {
		Preconditions.checkNotNull(Scene.v().getMainMethod());
		SootMethod entry = Scene.v().getMainMethod();
		for (SootMethod initializer : this.madeUpSort()) {
			Unit initCall = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(initializer.makeRef()));
			JimpleBody jb = (JimpleBody) entry.getActiveBody();
			jb.getUnits().insertBefore(initCall, jb.getFirstNonIdentityStmt());
		}

	}

	private void initializeStaticFields(SootClass containingClass) {
		// find all static fields of the class.
		Set<SootField> staticFields = new LinkedHashSet<SootField>();
		for (SootField f : containingClass.getFields()) {
			if (f.isStatic()) {
				staticFields.add(f);
			}
		}
		if (staticFields.isEmpty()) {
			return; // nothing to do.
		}

		SootMethod staticInit = null;
		for (SootMethod m : containingClass.getMethods()) {
			if (m.isStaticInitializer()) {
				staticInit = m;
				break;
			}
		}

		if (staticInit == null) {
			// TODO: super hacky!
			staticInit = new SootMethod(SootMethod.staticInitializerName, new LinkedList<soot.Type>(), VoidType.v(),
					Modifier.STATIC | Modifier.PUBLIC);
			JimpleBody body = Jimple.v().newBody(staticInit);
			body.getUnits().add(Jimple.v().newReturnVoidStmt());
			staticInit.setActiveBody(body);
			containingClass.addMethod(staticInit);
		}
		staticInitializers.add(staticInit);
		for (ValueBox vb : staticInit.retrieveActiveBody().getDefBoxes()) {
			if (vb.getValue() instanceof StaticFieldRef) {
				staticFields.remove(((StaticFieldRef) vb.getValue()).getField());
			}
		}

		for (SootField f : staticFields) {
			Unit init = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(f.makeRef()),
					SootTranslationHelpers.v().getDefaultValue(f.getType()));
			staticInit.getActiveBody().getUnits().addFirst(init);
		}

	}
	
	/** TODO:
	 * These are just a few random rules to sort static initializers to prevent 
	 * some unsound cases. This is NOT THE RIGHT WAY DO THING. If we want to be sound,
	 * we have to call static initializers the first time a class is accessed. This
	 * requires some real analysis.
	 * @return
	 */
	private List<SootMethod> madeUpSort() {
		List<SootMethod> orderedInitializers = new LinkedList<SootMethod>(staticInitializers);		
		Collections.sort(orderedInitializers, new Comparator<SootMethod>() {
		    @Override
		    public int compare(SootMethod o1, SootMethod o2) {
		    	SootClass c1 = o1.getDeclaringClass();
		    	SootClass c2 = o2.getDeclaringClass();
		    	
		    	for (SootField sf : c1.getFields()) {
		    		if (hasBaseType(sf.getType(), c2)) {
		    			return -1;
		    		}
		    	}
		    	for (SootField sf : c2.getFields()) {
		    		if (hasBaseType(sf.getType(), c1)) {
		    			return 1;
		    		}
		    	}		    	
		    	
		    	if (o1.hasActiveBody()) {
		    		for (Local l : o1.getActiveBody().getLocals()) {
		    			if (hasBaseType(l.getType(), c2)) {
		    				return -1;
		    			}
		    		}
		    	} 
		    	if (o2.hasActiveBody()) {
		    		for (Local l : o2.getActiveBody().getLocals()) {
		    			if (hasBaseType(l.getType(), c1)) {
		    				return 1;
		    			}
		    		}
		    	}
//		    	if (c1.hasOuterClass() && c1.getOuterClass().equals(c2)) {
//		    		return -1;
//		    	} else if (c2.hasOuterClass() && c2.getOuterClass().equals(c1)) {
//		    		return 1;
//		    	}
		    	
		    	//otherwise we can do it last.
		        return -1;		    	
		    }
		    
		    private boolean hasBaseType(Type t, SootClass c) {
		    	if (t instanceof RefType && ((RefType)t).getSootClass().equals(c)) {
		    		return true;
		    	} else if (t instanceof ArrayType) {
		    		return hasBaseType( ((ArrayType)t).baseType, c);
		    	}
		    	return false;
		    }
		});
		return orderedInitializers;
	}

}
