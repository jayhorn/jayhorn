/**
 * 
 */
package jayhorn.soot;

import java.util.HashMap;
import java.util.Map;

import jayhorn.cfg.Variable;
import jayhorn.cfg.method.Method;
import jayhorn.cfg.type.Type;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.ClassConstant;

/**
 * @author schaef
 *
 */
public enum SootTranslationHelpers {
	INSTANCE;
	
	public static SootTranslationHelpers v() {
		return INSTANCE;
	}

	private final Map<SootMethod, Method> methods = new HashMap<SootMethod, Method>();
	private final Map<SootClass, Variable> classVariables = new HashMap<SootClass, Variable>();
	private final Map<soot.Type, jayhorn.cfg.type.Type> types = new HashMap<soot.Type, jayhorn.cfg.type.Type>();
	
	public Method loopupMethod(SootMethod m) {
		if (!methods.containsKey(m)) {
			Method method = new Method(m);
			methods.put(m, method);
		}
		return methods.get(m);
	}
	
	public Type lookupType(soot.Type t) {
		if (!types.containsKey(t)) {
			//TODO:
		}
		return types.get(t);
	}
	
	public Variable lookupClassConstant(ClassConstant cc) {
		throw new RuntimeException("Not implemented");
	}
	
	public Variable lookupClassVariable(SootClass sc) {
		if (!classVariables.containsKey(sc)) {
			Variable var = new Variable(sc.getName(), lookupType(sc.getType()));
			classVariables.put(sc, var);
			
		}
		return classVariables.get(sc);
	}
	
}
