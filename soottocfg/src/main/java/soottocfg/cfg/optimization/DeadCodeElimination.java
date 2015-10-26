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

	private LiveVars<CfgBlock> blockLiveVars;
	
	@Override
	public boolean updateMethod(Method m){
		blockLiveVars = m.computeBlockLiveVariables();
		changed = false;
		for(CfgBlock block : m.getCfg()){
			processCfgBlock(block);
		}
		blockLiveVars = null;
		return changed;
	}

	protected boolean isDead(Statement stmt, LiveVars<Statement> liveVars) {
		//If a statement writes to only variables that are not live, we can remove it!
		//I.e. if intersection s.lvals, s.live is empty
		return SetOperations.intersect(stmt.getLVariables(), liveVars.liveOut.get(stmt)).isEmpty();
	}

	protected void processCfgBlock(CfgBlock block) {
		setCurrentCfgBlock(block);
		List<Statement> rval = new LinkedList<Statement>();
		LiveVars<Statement> stmtLiveVars = block.computeLiveVariables(blockLiveVars);
		for(Statement s : block.getStatements()){
			if(isDead(s,stmtLiveVars)){
				//If the statements is dead, just remove it from the list
				changed = true;
			} else {
				//otherwise, it stays in the list 
				rval.add(processStatement(s));
			}
		}	
		block.setStatements(rval);
		setCurrentCfgBlock(null);
	}
}
