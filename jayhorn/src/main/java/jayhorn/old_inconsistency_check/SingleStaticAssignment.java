/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.traverse.TopologicalOrderIterator;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;

/**
 * @author schaef
 *
 */
public class SingleStaticAssignment {

	private final Method method;
	/**
	 * 
	 */
	public SingleStaticAssignment(Method m) {
		method = m;
	}

	public void computeSSA() {		
		Set<CfgBlock> done = new HashSet<CfgBlock>();
		//this is like bfs, but ensures that all predecessors are visited before 
		//the node is visited.
		TopologicalOrderIterator<CfgBlock, CfgEdge> iterator = new TopologicalOrderIterator<CfgBlock, CfgEdge>(method);
		while (iterator.hasNext()) {
			CfgBlock b = iterator.next();
			//------------ sanity check for the iterator TODO: remove later
			for (CfgBlock pre : method.getPredsOf(b)) {
				if (!done.contains(pre)) {
					throw new RuntimeException("TopologicalOrderIterator does not work as expected");
				}
			}
			if (done.contains(b)) {
				throw new RuntimeException("TopologicalOrderIterator does not work as expected");
			} 
			//------------
			computeSSA(b);
			done.add(b);
		}
	}
	
	Map<CfgBlock, Map<Variable, Integer>> blockIncarnationMap = new HashMap<CfgBlock, Map<Variable, Integer>>();
	
	private void computeSSA(CfgBlock b) {
		Map<Variable, Integer> incarnationMap = createIncarnationMapAndFrames(b);
		for (Statement s : b.getStatements()) {
			//first set the current incarnation
			for (IdentifierExpression ie : s.getIdentifierExpressions()) {
				if (!incarnationMap.containsKey(ie.getVariable())) {
					incarnationMap.put(ie.getVariable(), 0);
				} 
				ie.setIncarnation(incarnationMap.get(ie.getVariable()));
			}
			//then update the incarnation if necessary.
			if (s instanceof AssignStatement) {
				AssignStatement as = (AssignStatement)s;
				if (as.getLeft() instanceof IdentifierExpression) {
					IdentifierExpression ie =(IdentifierExpression)as.getLeft();
					incarnationMap.put(ie.getVariable(), incarnationMap.get(ie.getVariable())+1);
					ie.setIncarnation(incarnationMap.get(ie.getVariable()));
				} else {
					System.err.println("TODO: " + as.getLeft().getClass());
				}
			} //TODO do the same for heap assignments

		}
				
	}
	
	private Map<Variable, Integer> createIncarnationMapAndFrames(CfgBlock b) {
		Map<Variable, Integer> incarnationMap = new HashMap<Variable, Integer>();
		//first find the max incarnation.
		for (CfgBlock pre : method.getPredsOf(b)) {
			for (Entry<Variable, Integer> entry : blockIncarnationMap.get(pre).entrySet()) {
				if (!incarnationMap.containsKey(entry.getKey())) {
					incarnationMap.put(entry.getKey(), entry.getValue());
				} else {
					incarnationMap.put(entry.getKey(), Math.max(entry.getValue(), incarnationMap.get(entry.getKey())));
				}
			}
		}
		//then add frames
		for (CfgBlock pre : method.getPredsOf(b)) {			
			Map<Variable, Integer> mapUpdate = new HashMap<Variable, Integer>();
			for (Entry<Variable, Integer> entry : blockIncarnationMap.get(pre).entrySet()) {
				if (incarnationMap.get(entry.getKey())<entry.getValue()) {
					mapUpdate.put(entry.getKey(), entry.getValue());
				}
			}
			if (!mapUpdate.isEmpty()) {
				updateMap(pre, mapUpdate);
			}
		}
		
		blockIncarnationMap.put(b, incarnationMap);
		return incarnationMap;
	}
	
	private void updateMap(CfgBlock b, Map<Variable, Integer> mapUpdate) {
		for (Entry<Variable, Integer> entry : mapUpdate.entrySet()) {
			SourceLocation loc = b.getStatements().get(b.getStatements().size()-1).getSourceLocation();
			//create an assignment v_newIdx = v_oldIdx 
			IdentifierExpression l = new IdentifierExpression(loc, entry.getKey(), entry.getValue());			
			IdentifierExpression r = new IdentifierExpression(loc, entry.getKey(), blockIncarnationMap.get(b).get(entry.getKey()));			
			b.addStatement(new AssignStatement(loc , l, r)); 
		}
		//then update the incarnation map for b
		blockIncarnationMap.get(b).putAll(mapUpdate);
	}
	
}
