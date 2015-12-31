/**
 * 
 */
package jayhorn.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graphs;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;

import com.google.common.base.Verify;

import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.util.Dominators;
import soottocfg.cfg.util.EffectualSet;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.PostDominators;

/**
 * @author schaef
 *
 */
public class ConvertToDiamondShape {

	public void convert(Method method) {
		CfgBlock sink = GraphUtil.getSink(method);
		CfgBlock mergePoint = null;
		int round = 0;
		while (!sink.equals(mergePoint)) {
			try {
				CfgBlock source = GraphUtil.getSource(method);
				Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, source);
				PostDominators<CfgBlock> pdom = new PostDominators<CfgBlock>(method, sink);
				EffectualSet<CfgBlock> effSet = new EffectualSet<CfgBlock>(dom, pdom);
				mergePoint = enforceDiamondStructure(method, source, effSet);
			} catch (GraphDirtyException e) {
				sink = GraphUtil.getSink(method);
			} catch (Throwable e) {
				graphToDot(new File("foobar" + (++round) + ".dot"), method);
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Check if the graph is in proper diamond shape. If not, we find a diamond
	 * with two different join points where one join dominates the other. We
	 * duplicate all paths between the fork point and the (post)dominant join
	 * point and throw an exception to force the 'convert' method to recompute
	 * the dominators, etc.
	 * 
	 * @param graph
	 * @param block
	 * @param effSet
	 * @return
	 */
	private CfgBlock enforceDiamondStructure(Method graph, CfgBlock block, EffectualSet<CfgBlock> effSet) {
		Set<CfgBlock> latticeElement = effSet.findInLattice(block);
		while (latticeElement.contains(block)) {
			List<CfgBlock> successors = Graphs.successorListOf(graph, block);
			CfgBlock next = null;
			if (successors.size() == 1) {
				next = successors.get(0);
			} else if (successors.isEmpty()) {
				return block;
			} else {
				Verify.verify(successors.size() == 2);
				CfgBlock join = null;
				for (CfgBlock suc : successors) {
					Verify.verify(effSet.isBelowInLattice(suc, latticeElement));
					CfgBlock subGraphJoin = enforceDiamondStructure(graph, suc, effSet);
					if (join == null) {
						join = subGraphJoin;
					} else {
						if (subGraphJoin != join) {
							/*
							 * This is the case where we have to do something:
							 * There is a join that is not from a diamond. We
							 * have to check which of the two possible joins
							 * dominates the other and create a new path TODO
							 */
							Verify.verifyNotNull(subGraphJoin);
							if (effSet.getPostDominators().isDominatedBy(subGraphJoin, join)) {
								duplicatePathsBetween(effSet, graph, block, join, subGraphJoin);
							} else {
								Verify.verify(effSet.getPostDominators().isDominatedBy(join, subGraphJoin));
								duplicatePathsBetween(effSet, graph, block, subGraphJoin, join);
							}
							throw new GraphDirtyException();
						}
						Verify.verify(subGraphJoin == join);
					}
				}
				Verify.verify(join != null);
				next = join;
			}

			if (latticeElement.contains(next)) {
				block = next;
			} else {
				Verify.verify(effSet.isAboveInLattice(next, latticeElement));
				return next;
			}
		}
		throw new RuntimeException("Should not be here. " + block.getLabel());
	}

	/** @formatter:off
	 * We found a non-diamond structure (see comments below) where
	 * 'through' is shared between to almost diamonds and we convert
	 * it back to a diamond structure by duplicating the paths between
	 * 'through' and 'to'.
		// Now we have something of the form
		// ......... root ..................
		// ......... / \ ...................
		// ........ ? from .................
		// ........ |. /. \ ................
		// ........ \ / .. | ...............
		// .......through. | ...............
		// .......... \.. / ................
		// ............ to .................
		// and we want to convert it back into diamond
		// shape.
		// We do this by duplicating the subgraph between
		// 'through' and 'to' (excluding 'to') and re-directing
		// the edge from 'from' to the copy of 'through'.
		// The result looks as follows:
		// .......... root ..................
		// ......... /... \ ...................
		// ........ ?.... from .................
		// ........ |..... |.. \ ................
		// ........ \..... | ... \ ...............
		// ..... through through' |...............
		// .......... \..... |.  / ................
		// ........... \..../ ../ .........
		// ............... to .................
	 * @param effSet
	 * @param graph
	 * @param from
	 * @param to
	 * @param through
	 */
	private void duplicatePathsBetween(EffectualSet<CfgBlock> effSet, Method graph, CfgBlock from, CfgBlock to,
			CfgBlock through) {


		// copy the vertices between 'through' and 'to'
		Set<CfgBlock> toCopy = GraphUtil.getVerticesBetween(graph, through, to);
		StringBuilder sb = new StringBuilder();

		toCopy.remove(to);
		Map<CfgBlock, CfgBlock> copies = new HashMap<CfgBlock, CfgBlock>();
		for (CfgBlock orig : toCopy) {
			sb.append(orig.getLabel() + ", ");
			CfgBlock cpy = new CfgBlock(graph);
			cpy.setStatements(orig.getStatements());
			copies.put(orig, cpy);
		}
		// copy the edges between 'through' and 'to'.
		for (CfgEdge e : new HashSet<CfgEdge>(graph.edgeSet())) {
			if (toCopy.contains(graph.getEdgeSource(e)) && toCopy.contains(graph.getEdgeTarget(e))) {
				graph.addEdge(copies.get(graph.getEdgeSource(e)), copies.get(graph.getEdgeTarget(e)));
			} else if (toCopy.contains(graph.getEdgeSource(e)) && graph.getEdgeTarget(e).equals(to)) {
				graph.addEdge(copies.get(graph.getEdgeSource(e)), to);
			}
		}
		// now remove the edge that goes through 'from' into 'through' and
		// redirect it into the copy of 'through'
		for (CfgBlock pre : new LinkedList<CfgBlock>(Graphs.predecessorListOf(graph, through))) {
			if (effSet.getDominators().isDominatedBy(pre, from)) {
				graph.removeEdge(pre, through);
				graph.addEdge(pre, copies.get(through));
			}
		}
	}

	private static class GraphDirtyException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	private void graphToDot(File dotFile, DirectedGraph<CfgBlock, CfgEdge> graph) {
		try (FileOutputStream fileStream = new FileOutputStream(dotFile);
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");) {
			DOTExporter<CfgBlock, CfgEdge> dot = new DOTExporter<CfgBlock, CfgEdge>(new StringNameProvider<CfgBlock>() {
				@Override
				public String getVertexName(CfgBlock vertex) {
					StringBuilder sb = new StringBuilder();
					sb.append("\"");
					sb.append(vertex.getLabel());
					sb.append("\"");
					return sb.toString();
				}
			}, null, null);
			dot.export(writer, graph);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
