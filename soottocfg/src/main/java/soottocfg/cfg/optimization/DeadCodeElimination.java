package soottocfg.cfg.optimization;

import java.util.LinkedList;
import java.util.List;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.util.SetOperations;

public class DeadCodeElimination extends CfgUpdater {
	//Given a method, eliminate the dead code in it
	DeadCodeElimination(){}

	private boolean changed = false;
	public void removeDeadCode(Method m){

		do {
			LiveVars<CfgBlock> blockLiveVars = m.computeBlockLiveVariables();
			for(CfgBlock block : m.getCfg()){
				removeDeadCode(block,blockLiveVars);
			}			
		} while(changed);
	}

	protected boolean isDead(Statement stmt, LiveVars<Statement> liveVars) {
		//If a statement writes to only variables that are not live, we can remove it!
		//I.e. if intersection s.lvals, s.live is empty
		return SetOperations.intersect(stmt.getLVariables(), liveVars.liveOut.get(stmt)).isEmpty();
	}

	protected void removeDeadCode(CfgBlock block, LiveVars<CfgBlock> blockLiveVars) {
		List<Statement> rval = new LinkedList<Statement>();
		LiveVars<Statement> stmtLiveVars = block.computeLiveVariables(blockLiveVars);
		for(Statement s : block.getStatements()){
			if(!isDead(s,stmtLiveVars)){
				changed = true;
				rval.add(processStatement(s));
			}
		}	
		block.setStatements(rval);
	}
}
