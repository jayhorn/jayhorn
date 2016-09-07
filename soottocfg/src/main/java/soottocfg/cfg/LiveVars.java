package soottocfg.cfg;

import java.util.Map;
import java.util.Set;

import soottocfg.cfg.variable.Variable;

public class LiveVars<T> {
	public final Map<T, Set<Variable>> liveIn;
	public final Map<T, Set<Variable>> liveOut;

	public LiveVars(Map<T, Set<Variable>> in, Map<T, Set<Variable>> out) {
		liveIn = in;
		liveOut = out;
	}
}
