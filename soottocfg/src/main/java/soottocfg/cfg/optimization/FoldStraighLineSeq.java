/**
 * 
 */
package soottocfg.cfg.optimization;

import java.util.HashSet;

import org.jgrapht.Graphs;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;

/**
 * @author schaef
 *
 */
public class FoldStraighLineSeq {

	/**
	 * 
	 */
	public FoldStraighLineSeq() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Searches for two blocks b1 -> b2 s.t. there is
	 * exactly one edge between b1 and b2. Then it folds
	 * b2 into b1, by copying all its statements and outgoing
	 * edges.
	 * @param m
	 */
	public void fold(Method m) {
		boolean fixedpoint = false;
		while (!fixedpoint) {
			fixedpoint = true;
			for (CfgBlock b : new HashSet<CfgBlock>(m.vertexSet())) {
				if (m.outDegreeOf(b)==1) {
					CfgBlock suc = Graphs.successorListOf(m, b).get(0);
					if (foldIfPossible(m, b, suc)) {
						m.removeVertex(suc);
						fixedpoint = false;
						break;
					} 
				}
			}
		}
	}
		
	private boolean foldIfPossible(Method m, CfgBlock b1, CfgBlock b2) {
		if (m.outDegreeOf(b1)==1 && Graphs.successorListOf(m, b1).contains(b2)) {
			if (m.inDegreeOf(b2)==1 && Graphs.predecessorListOf(m, b2).contains(b1)) {
				//there is exactly one edge between b1 and b2
				for (Statement s : b2.getStatements()) {
					b1.addStatement(s.deepCopy());
				}
				for (CfgBlock suc : Graphs.successorListOf(m, b2)) {
					CfgEdge newEdge = new CfgEdge();
					CfgEdge oldEdge = m.getEdge(b2, suc);
					if (oldEdge.getLabel().isPresent()) {
						newEdge.setLabel(oldEdge.getLabel().get());
					}

					m.addEdge(b1, suc, newEdge);
					m.removeEdge(b2, suc);
				}
				return true;
			}
		}
		return false;
	}

}
