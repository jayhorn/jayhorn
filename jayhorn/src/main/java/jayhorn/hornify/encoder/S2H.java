package jayhorn.hornify.encoder;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.Variable;

public class S2H {
	
	private final HashMap<Statement, List<ProverHornClause>> statToClause = new HashMap<Statement, List<ProverHornClause>>();
	private final Map<ProverExpr, Integer> errorLineNumber = new LinkedHashMap<ProverExpr, Integer>();
	private final Map<Method, Entry<Variable, Variable>> methodHeapBounds = new HashMap<Method, Entry<Variable, Variable>>();
	
	private static S2H sh;

	public static void resetInstance() {
		sh = null;
	}
	
	public static S2H sh() {
		if (null == sh) {
			sh = new S2H();
		}
		return sh;
	}

	private S2H() {}
	
	public void addClause(Statement s, List<ProverHornClause> h){
		statToClause.put(s,h);
	}

	public Map<Statement, List<ProverHornClause>> getStatToClause() {
	    return statToClause;
    }

	/**
	 * Get Transition Relation Clauses
	 * @return
	 */
	public List<ProverHornClause> getTransitionRelationClause(){
		List<ProverHornClause> clause = new LinkedList<ProverHornClause>();
		for (Entry<Statement, List<ProverHornClause>> entry : this.statToClause.entrySet()) {
			if (!(entry.getKey() instanceof AssertStatement)) {
				clause.addAll(entry.getValue());
			}
		}
		return clause;
	}
	
	/**
	 * 
	 */
	public List<ProverHornClause> getPropertyClause(){
		List<ProverHornClause> clause = new LinkedList<ProverHornClause>();
		for (Entry<Statement, List<ProverHornClause>> entry : this.statToClause.entrySet()) {
			if (entry.getKey() instanceof AssertStatement) {
				clause.addAll(entry.getValue());
			}
		}
		return clause;
	}
	
	public void setErrorState(ProverExpr errorState, int i){
		this.errorLineNumber.put(errorState, i);
	}
	
	public Map<ProverExpr, Integer> getErrorState(){
		return this.errorLineNumber;
	}

	
	public void setHeapCounter(Method m, Variable inCounter, Variable outCounter){
		Entry<Variable, Variable> in_out = new SimpleEntry<>(inCounter, outCounter);
		methodHeapBounds.put(m, in_out);
	}
	
	public Entry<Variable, Variable> getMethodHeapVar(Method m){
		return methodHeapBounds.get(m);
	}
	
	public Entry<Variable, Variable> getMainHeapCount(){
		for (Entry<Method, Entry<Variable, Variable>> vars : methodHeapBounds.entrySet()) {
			if(vars.getKey().isProgramEntryPoint()){
				return vars.getValue();
			}
		}
		throw new RuntimeException("No Main Heap Count Found ");
	}
	
}
