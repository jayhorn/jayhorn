package jayhorn.hornify.encoder;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import jayhorn.hornify.HornHelper;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.Statement;

public class S2H {
	
	private final HashMap<Statement, List<ProverHornClause>> statToClause = new HashMap<Statement, List<ProverHornClause>>();
	private final List<ProverExpr> errorStates = new LinkedList<ProverExpr>();
	
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
	
	/**
	 * Get Transition Relation Clauses
	 * @return
	 */
	public List<ProverHornClause> getTransitionRelationClause(){
		List<ProverHornClause> clause = new LinkedList<ProverHornClause>();
		for (Statement s : this.statToClause.keySet())
			if (!(s instanceof AssertStatement)) {
				clause.addAll(statToClause.get(s));
			}
		return clause;
	}
	
	/**
	 * 
	 */
	public List<ProverHornClause> getPropertyClause(){
		List<ProverHornClause> clause = new LinkedList<ProverHornClause>();
		for (Statement s : this.statToClause.keySet())
			if (s instanceof AssertStatement) {
				clause.addAll(statToClause.get(s));
			}
		return clause;
	}
	
	public void setErrorState(ProverExpr errorState){
		this.errorStates.add(errorState);
	}
	
	public List<ProverExpr> getErrorState(){
		return this.errorStates;
	}
}
