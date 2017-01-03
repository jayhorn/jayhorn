/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *         Implements algorithm from Apple's Modern Compiler Construction book
 *         Chapter 17.2
 */
public class DataFlowUtils  {

	public static void main(String[] args) {
		Method m = createExampleProgram17_3();
		System.err.println(m);		
		ReachingDefinitions rd = computeReachingDefinitions(m);
		System.err.println(rd);	
	}
	
	
	
	
	public static class ReachingDefinitions {
		public Map<Statement, Set<Statement>> in, out;
		
		@Override
		public String toString() {			
			StringBuilder sb = new StringBuilder();
			sb.append("Reaching Definitions:");
			sb.append(System.getProperty("line.separator"));
			List<Statement> statementList = new LinkedList<Statement>(in.keySet());
			for (Statement s : in.keySet()) {				 
				sb.append(String.format("%1$5d:  %2$25s  ", statementList.indexOf(s), s));
				StringBuilder tmp = new StringBuilder();
				String comma = "In: ";
				for (Statement inS : in.get(s)) {
					tmp.append(comma);
					comma = ",";
					tmp.append(statementList.indexOf(inS));
				}
				sb.append(String.format("%1$15s", tmp.toString()));
				
				tmp = new StringBuilder();
				comma = "Out: ";
				for (Statement outS : out.get(s)) {
					tmp.append(comma);
					comma = ",";
					tmp.append(statementList.indexOf(outS));
				}
				sb.append(String.format("%1$15s", tmp.toString()));
				sb.append(System.getProperty("line.separator"));
			}
			
			return sb.toString();
		}
	}
	
	public static ReachingDefinitions computeReachingDefinitions(Method m) {

		Map<Statement, Set<Statement>> gen = new LinkedHashMap<Statement, Set<Statement>>();
		Map<Statement, Set<Statement>> kill = new LinkedHashMap<Statement, Set<Statement>>();
		computeGenAndKillSets(m, gen, kill);
		
		// compute the predecessor-set for all statements
		Map<Statement, Set<Statement>> predMap = getStatementPredecessorMap(m);
		
		Map<Statement, Set<Statement>> in = new LinkedHashMap<Statement, Set<Statement>>();
		Map<Statement, Set<Statement>> out = new LinkedHashMap<Statement, Set<Statement>>();
		
		
		List<Statement> allStatements = new LinkedList<Statement>(predMap.keySet());
		for (Statement s : allStatements) {
			in.put(s, new HashSet<Statement>());
			out.put(s, new HashSet<Statement>());
		}
		boolean changed = true;
		while(changed) {
			changed = false;
			for (Statement s : allStatements) {

				//update the in-sets
				Set<Statement> newIn = computeInSet(s, predMap, out);
				if (!in.get(s).equals(newIn) ) {
					changed = true;
					in.get(s).clear();
					in.get(s).addAll(newIn);
				}
				//update the out-sets
				Set<Statement> newOut = new HashSet<Statement>();
				//gen[s] \cup (in[s] - kill[s])
				newOut.addAll(in.get(s));
				newOut.removeAll(kill.get(s));
				newOut.addAll(gen.get(s));
				if (!out.get(s).equals(newOut)) {
					changed = true;
					out.get(s).clear();
					out.get(s).addAll(newOut);
				}
			}
		}
		ReachingDefinitions reach = new ReachingDefinitions();
		reach.in = in;
		reach.out = out;
		return reach;
	}

	private static Set<Statement> computeInSet(Statement s, Map<Statement, Set<Statement>> predMap, Map<Statement, Set<Statement>> out) {
		Set<Statement> res = new HashSet<Statement>();
		for (Statement pre : predMap.get(s)) {
			if (!out.containsKey(s)) {
				out.put(s, new HashSet<Statement>());
			}
			res.addAll(out.get(pre));
		}
		return res;
 	}
	
	/**
	 * Returns true if s generates an update to a variable.
	 * @param s
	 * @return
	 */
	private static boolean isGenStatement(Statement s) {
		return s instanceof AssignStatement || s instanceof CallStatement || s instanceof PullStatement || s instanceof NewStatement;
	}
	
	private static void computeGenAndKillSets(Method m, Map<Statement, Set<Statement>> gen, Map<Statement, Set<Statement>> kill) {
		Map<Variable, Set<Statement>> defs = new HashMap<Variable, Set<Statement>>();
		// compute defs and gen sets before computing kill sets.
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				// create the defs set.
				for (Variable v : s.getDefVariables()) {
					if (!defs.containsKey(v)) {
						defs.put(v, new HashSet<Statement>());
					}
					defs.get(v).add(s);
				}
				// create the gen[s] map
				Set<Statement> genSet = new HashSet<Statement>();
				gen.put(s, genSet);
				if (isGenStatement(s)) {
					gen.get(s).add(s);
				} // else do nothing.
			}
		}
		// now compute the kill sets
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				Set<Statement> killSet = new HashSet<Statement>();
				kill.put(s, killSet);
//				if (s instanceof AssignStatement || s instanceof CallStatement || s instanceof PullStatement) {
					Set<Statement> defStatements = new HashSet<Statement>();
					for (Variable v : s.getDefVariables()) {
						defStatements.addAll(defs.get(v));
					}
					defStatements.remove(s);
					kill.get(s).addAll(defStatements);
//				} // else do nothing.
			}
		}		
	}
 	
	/**
	 * Compute the predecessor map that maps each statement in the method 
	 * body to the set of its predecessor statements.
	 * @param m
	 * @return Map from statement to set of predecessor statements 
	 */
	private static Map<Statement, Set<Statement>> getStatementPredecessorMap(Method m) {
		Map<Statement, Set<Statement>> pred = new LinkedHashMap<Statement, Set<Statement>>();

		Map<CfgBlock, Set<Statement>> lastStmt = new HashMap<CfgBlock, Set<Statement>>();

		List<CfgBlock> blockWithLastStatement = new LinkedList<CfgBlock>();
		for (CfgBlock b : m.vertexSet()) {
			lastStmt.put(b, new HashSet<Statement>());
			if (!b.getStatements().isEmpty()) {
				Statement last = b.getStatements().get(b.getStatements().size() - 1);
				lastStmt.get(b).add(last);

				Statement previousStmt = null;
				for (Statement s : b.getStatements()) {
					pred.put(s, new HashSet<Statement>());
					if (previousStmt != null) {
						pred.get(s).add(previousStmt);
					}
					previousStmt = s;
				}
			} else {
				blockWithLastStatement.add(b);
			}
		}
		
		while (!blockWithLastStatement.isEmpty()) {
			CfgBlock current = blockWithLastStatement.remove(0);
			lastStmt.get(current)
					.addAll(getPredecessorStatementRecursively(m, lastStmt, current, new HashSet<CfgBlock>()));
		}
		// now, the lastStmt map is complete and we can complete the pred map.
		for (CfgBlock b : m.vertexSet()) {
			for (CfgBlock pre : Graphs.predecessorListOf(m, b)) {
				if (!b.getStatements().isEmpty()) {
					pred.get(b.getStatements().get(0)).addAll(lastStmt.get(pre));
				}
			}
		}

		return pred;
	}

	private static Set<Statement> getPredecessorStatementRecursively(Method m, Map<CfgBlock, Set<Statement>> lastStmt,
			CfgBlock current, Set<CfgBlock> visited) {
		Set<Statement> ret = new HashSet<Statement>();
		if (lastStmt.containsKey(current) && !lastStmt.get(current).isEmpty()) {
			return lastStmt.get(current);
		} else if (visited.contains(current)) {
			//we are in a loop
			return ret;
		}

		Set<CfgBlock> rec_visited = new HashSet<CfgBlock>(visited);
		rec_visited.add(current);
		for (CfgBlock pre : Graphs.predecessorListOf(m, current)) {
			ret.addAll(getPredecessorStatementRecursively(m, lastStmt, pre, rec_visited));
		}
		if (!lastStmt.containsKey(current)) {
			lastStmt.put(current, new HashSet<Statement>());
			lastStmt.get(current).addAll(ret);
		}
		return ret;
	}


	/**
	 * Creates example program from Listing 17.3 on page 356
	 * @return
	 */
	private static Method createExampleProgram17_3() {
		Program p = new Program();
		SootTranslationHelpers.initialize(p);
		SourceLocation loc = SourceLocation.ANALYSIS;
		Method m = Method.createMethodForTestingOnly(p, "test", new LinkedList<Variable>(), new LinkedList<Type>(), loc);

		Variable a = new Variable("a", IntType.instance());
		Variable b = new Variable("b", IntType.instance());
		Variable c = new Variable("c", IntType.instance());
		m.addLocalVariable(a);
		m.addLocalVariable(b);
		m.addLocalVariable(c);
		CfgBlock l0 = new CfgBlock(m);
		CfgBlock l1 = new CfgBlock(m);
		CfgBlock loop = new CfgBlock(m);
		CfgBlock l2 = new CfgBlock(m);
		
		CfgEdge l1Tol2 = new CfgEdge();
		l1Tol2.setLabel(new BinaryExpression(loc, BinaryOperator.Gt, c.mkExp(loc), a.mkExp(loc)));
		CfgEdge l1Toloop = new CfgEdge();
		l1Toloop.setLabel(new BinaryExpression(loc, BinaryOperator.Le, c.mkExp(loc), a.mkExp(loc)));
		
		m.addEdge(l0, l1);
		m.addEdge(loop, l1);
		m.addEdge(l1, l2, l1Tol2);
		m.addEdge(l1, loop, l1Toloop);
		
		l0.addStatement(new AssignStatement(loc, a.mkExp(loc), new IntegerLiteral(loc, 5)));
		l0.addStatement(new AssignStatement(loc, c.mkExp(loc), new IntegerLiteral(loc, 1)));
				
		loop.addStatement(new AssignStatement(loc, c.mkExp(loc), new BinaryExpression(loc, BinaryOperator.Plus, c.mkExp(loc), c.mkExp(loc))));
		
		l2.addStatement(new AssignStatement(loc, a.mkExp(loc), new BinaryExpression(loc, BinaryOperator.Minus, c.mkExp(loc), a.mkExp(loc))));
		l2.addStatement(new AssignStatement(loc, c.mkExp(loc), new IntegerLiteral(loc, 0)));
		return m;
	}

}
