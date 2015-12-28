/**
 * 
 */
package jayhorn.util;

import java.util.LinkedHashSet;
import java.util.Set;

import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.Statement;

/**
 * @author schaef
 *
 */
public class EdgeLabelToAssume {

	private final Method method;
	
	public EdgeLabelToAssume(Method m) {
		method = m;
	}
	
	/**
	 * For each edge labeled with a conditional, introduce a new vertex that
	 * contains this conditional as assume statement, remove the edge and add
	 * new edges to but this vertex between source and target
	 */
	public void turnLabeledEdgesIntoAssumes() {
		Set<CfgEdge> edges = new LinkedHashSet<CfgEdge>(method.edgeSet());
		for (CfgEdge edge : edges) {
			if (edge.getLabel().isPresent()) {
				CfgBlock src = method.getEdgeSource(edge);
				CfgBlock tgt = method.getEdgeTarget(edge);
				SourceLocation loc = edge.getLabel().get().getSourceLocation();
				if (!tgt.getStatements().isEmpty()) {
					loc = tgt.getStatements().iterator().next().getSourceLocation();
				} else if (!src.getStatements().isEmpty()) {
					loc = src.getStatements().get(src.getStatements().size() - 1).getSourceLocation();
				} else {
//					System.err.println(
//							"ERROR: these labeled edges without location tags will cause problems later. @Martin, fix that!");
				}
				Statement assume = new AssumeStatement(loc, edge.getLabel().get());
				method.removeEdge(edge);
				CfgBlock between = new CfgBlock(method);
				between.addStatement(assume);
				method.addEdge(src, between);
				method.addEdge(between, tgt);
			}
		}
	}
}
