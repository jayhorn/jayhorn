/**
 * 
 */
package soottocfg.cfg;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class Program {

	private final Map<String, Variable> globalVariables = new LinkedHashMap<String, Variable>();
	private final Map<String, Method> methods = new LinkedHashMap<String, Method>();

	private final Collection<Method> entryPoints = new HashSet<Method>();

	private Variable exceptionGlobal;
	
	public Variable[] getGlobalVariables() {
		return this.globalVariables.values().toArray(new Variable[this.globalVariables.size()]);
	}

	public Variable createFreshGlobal(String prefix, Type t) {
		final String vname = prefix + this.globalVariables.size();
		Preconditions.checkArgument(!globalVariables.containsKey(vname));
		Variable v = new Variable(vname, t);
		this.globalVariables.put(vname, v);
		return v;
	}

	public Variable loopupGlobalVariable(String varName, Type t) {
		return loopupGlobalVariable(varName, t, false, false);
	}

	public Variable loopupGlobalVariable(String varName, Type t, boolean constant, boolean unique) {
		if (!this.globalVariables.containsKey(varName)) {
			this.globalVariables.put(varName, new Variable(varName, t, constant, unique));
		}
		return this.globalVariables.get(varName);
	}

	public Method loopupMethod(String methodSignature) {
		return methods.get(methodSignature);
	}
	
	public void addMethod(Method m) {
		this.methods.put(m.getMethodName(), m);
	}

	public void addEntryPoint(Method entry) {
		this.entryPoints.add(entry);
	}

	public Method[] getEntryPoints() {
		return entryPoints.toArray(new Method[entryPoints.size()]);
	}

	public Method[] getMethods() {
		return methods.values().toArray(new Method[methods.size()]);
	}

	public Variable getExceptionGlobal() {
		Verify.verify(exceptionGlobal!=null, "Exception global never set");
		return exceptionGlobal;
	}
	
	public void setExceptionGlobal(Variable exGlobal) {
		Verify.verify(exceptionGlobal==null, "Do not set this variable twice");
		exceptionGlobal = exGlobal;
	}
}
