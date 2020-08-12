/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soottocfg.spec.ListSimpleSpec;
import soottocfg.spec.StringBuilderSpec;

/**
 * @author schaef
 *         This transformation takes a map from SootClass to SootClass
 *         that specifies which types to replace by stubs. For example,
 *         this could contain an entry java.util.List -> jayhorn.ListStub
 *         which indicates that all uses of List should be replaced by
 *         ListStub.
 *         This requires that ListStub extends List.
 * 
 *         !!IMPORTANT!!: This assumes that Arrays have been removed already!
 */
public class SpecClassTransformer extends AbstractSceneTransformer {

	private final Map<SootClass, SootClass> replacementMap = new HashMap<SootClass, SootClass>();

	private final Map<String, SootField> fieldSubstitutionMap = new HashMap<String, SootField>();
	private final Map<String, SootMethod> methodSubstitutionMap = new HashMap<String, SootMethod>();

	public SpecClassTransformer() {

		SootClass listSpec = Scene.v().loadClass(ListSimpleSpec.class.getName(), SootClass.SIGNATURES);
		replacementMap.put(Scene.v().getSootClass(java.util.LinkedList.class.getName()), listSpec);
		replacementMap.put(Scene.v().getSootClass(java.util.List.class.getName()), listSpec);
		SootClass stringBuilderSpec = Scene.v().loadClass(StringBuilderSpec.class.getName(), SootClass.SIGNATURES);
		replacementMap.put(Scene.v().getSootClass(java.lang.StringBuilder.class.getName()), stringBuilderSpec);
//		replacementMap.put(Scene.v().getSootClass(java.lang.StringBuffer.class.getName()), stringBuilderSpec);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * soottocfg.soot.transformers.AbstractSceneTransformer#applyTransformation(
	 * )
	 */
	@Override
	public void applyTransformation() {
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		List<JimpleBody> bodies = new LinkedList<JimpleBody>();
//		List<SootMethod> entryPoints = new LinkedList<SootMethod>(Scene.v().getEntryPoints());
		// Don't transform the classes that we replace anyway.
		classes.removeAll(replacementMap.keySet());

		for (SootClass sc : classes) {
			if (sc.resolvingLevel() >= SootClass.SIGNATURES) {
				// === change the type of all fields if they are in
				// replacementMap ===.
				Set<String> changedFieldSignatures = new HashSet<String>();
				for (SootField f : sc.getFields()) {
					Type newType = updateType(f.getType());
					if (!newType.equals(f.getType())) {
						changedFieldSignatures.add(f.getSignature());
						final String oldSig = f.getSignature();
						f.setType(newType);
						fieldSubstitutionMap.put(oldSig, f);
					}
				}
				// === remove original version of changed fields ===.
				for (String s : changedFieldSignatures) {
					if (sc.declaresField(s)) {
						sc.removeField(sc.getFieldByName(s));
					}
				}

				// === now update methods ===.
				for (SootMethod sm : sc.getMethods()) {
					final String oldSignature = sm.getSignature();
					// we also have to update the refs in the EntryPoint list.
//					boolean wasMain = sm.isEntryMethod();
//					if (wasMain) {
//						entryPoints.remove(sm);
//					}

					if (sc.resolvingLevel() >= SootClass.BODIES && sm.isConcrete() && !sc.isLibraryClass()
							&& !sc.isJavaLibraryClass()) {
						// record all methods for which we found a body.
						bodies.add((JimpleBody) sm.retrieveActiveBody());
					}
					// update return type
					sm.setReturnType(updateType(sm.getReturnType()));
					// update parameter types
					List<Type> newParamTypes = new LinkedList<Type>();
					for (Type t : sm.getParameterTypes()) {
						newParamTypes.add(updateType(t));
					}
					sm.setParameterTypes(newParamTypes);

//					if (wasMain) {
//						entryPoints.add(sm);
//					}

					methodSubstitutionMap.put(oldSignature, sm);
				}

				for (JimpleBody body : bodies) {
					for (Local local : body.getLocals()) {
						local.setType(updateType(local.getType()));
					}
					for (Unit u : new LinkedList<Unit>(body.getUnits())) {
						Stmt st = (Stmt) u;

						if (st.containsFieldRef()) {
							FieldRef fr = st.getFieldRef();
							if (fieldSubstitutionMap.containsKey(fr.getField().getSignature())) {
								fr.setFieldRef(fieldSubstitutionMap.get(fr.getField().getSignature()).makeRef());
							}
						}
						if (st.containsInvokeExpr()) {
							InvokeExpr ivk = st.getInvokeExpr();
							if (replacementMap.containsKey(ivk.getMethod().getDeclaringClass())) {
								SootClass replClass = replacementMap.get(ivk.getMethod().getDeclaringClass());
								List<Type> replTypes = new LinkedList<Type>();
								for (Type t : ivk.getMethod().getParameterTypes()) {
									// TODO: it might be better to only replace
									// this one type.
									// instead of every type in the map.
									replTypes.add(updateType(t));
								}

								SootMethod replMethod = replClass.getMethod(ivk.getMethod().getName(), replTypes);
								ivk.setMethodRef(replMethod.makeRef());
								
							} else if (methodSubstitutionMap.containsKey(ivk.getMethod().getSignature())) {
								ivk.setMethodRef(methodSubstitutionMap.get(ivk.getMethod().getSignature()).makeRef());
							}
						}
					}
				}
			}
		}
	}

	private Type updateType(Type t) {
		if (t instanceof RefType) {
			SootClass sc = ((RefType) t).getSootClass();
			if (replacementMap.containsKey(sc)) {
				return RefType.v(replacementMap.get(sc));
			}
		}
		return t;
	}

}
