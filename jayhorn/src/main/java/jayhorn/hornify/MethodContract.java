package jayhorn.hornify;

import soottocfg.cfg.method.Method;

public class MethodContract {
	
	public final Method method;
	public final HornPredicate precondition;
	public final HornPredicate postcondition;
	

	public MethodContract(Method method, HornPredicate precondition, HornPredicate postcondition) {
		this.method = method;
		this.precondition = precondition;
		this.postcondition = postcondition;
	}

	public String toString() {
		return "<" + precondition + ", " + postcondition + ">";
	}

}
