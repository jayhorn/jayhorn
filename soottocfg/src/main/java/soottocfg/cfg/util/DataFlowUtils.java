/**
 * 
 */
package soottocfg.cfg.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.Graphs;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.IdentifierExpression;
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
                public Set<Statement> havocStatements;
		
		@Override
		public String toString() {			
			StringBuilder sb = new StringBuilder();
			sb.append("Reaching Definitions:");
			sb.append(System.getProperty("line.separator"));
			List<Statement> statementList = new LinkedList<Statement>(in.keySet());
			for (Entry<Statement, Set<Statement>> entry : in.entrySet()) {				 
				sb.append(String.format("%1$5d:  %2$25s  ", statementList.indexOf(entry.getKey()), entry.getKey()));
				StringBuilder tmp = new StringBuilder();
				String comma = "In: ";
				for (Statement inS : entry.getValue()) {
					tmp.append(comma);
					comma = ",";
					tmp.append(statementList.indexOf(inS));
				}
				sb.append(String.format("%1$15s", tmp.toString()));
				
				tmp = new StringBuilder();
				comma = "Out: ";
				for (Statement outS : out.get(entry.getKey())) {
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
//            Long start = System.currentTimeMillis();

            Map<Statement, Set<Statement>> gen = new HashMap<Statement, Set<Statement>>();
            computeGenSet(m, gen);

            // havoc expressions for the variables
            Map<Variable, Statement> havocAssignments = new HashMap<>();

            // compute the predecessor-set for all statements
            Map<Statement, Set<Statement>> predMap = getStatementPredecessorMap(m);
            
            // compute the successor-set for all statements
            Map<Statement, Set<Statement>> succMap = getStatementSuccessorMap(predMap);
		
            Map<Statement, Set<Statement>> in = new LinkedHashMap<Statement, Set<Statement>>();
            Map<Statement, Set<Statement>> out = new LinkedHashMap<Statement, Set<Statement>>();
            Map<Statement, Set<Statement>> queued = new HashMap<Statement, Set<Statement>>();
		
            List<Statement> allStatements = new LinkedList<Statement>(predMap.keySet());
            for (Statement s : allStatements) {
                in.put(s, new HashSet<Statement>());
                out.put(s, new HashSet<Statement>());
                queued.put(s, new HashSet<Statement>());
            }

            Stack<Statement> workList = new Stack<> ();
            Set<Statement> workListEls = new HashSet<> ();

            for (Statement s : allStatements)
                for (Statement r : gen.get(s)) {
                    out.get(s).add(r);
                    for (Statement t : succMap.get(s))
                        if (queued.get(t).add(r) && workListEls.add(t))
                            workList.push(t);
                }

            Set<Statement> emptySet = new HashSet<>();

            while (!workList.isEmpty()) {
                final Statement nextS = workList.pop();
                workListEls.remove(nextS);

                // update the in- and out-set
                final Set<Statement> inS = in.get(nextS);
                final Set<Statement> outS = out.get(nextS);
                final Set<Statement> queuedS = queued.get(nextS);

                queued.put(nextS, emptySet);

                for (Statement s : queuedS)
                    if (inS.add(s) && !kills(nextS, s)) {
                        Statement outStmt;
                        if (updatesUseVariables(nextS, s) && s instanceof AssignStatement) {
                            // in this case we replace the right-hand side of the assignment
                            // with a fresh havoc-variable; to make sure that the right-hand
                            // side does not contain any variables that have been updated
                            // in the meantime
                            AssignStatement assS = (AssignStatement)s;
                            Variable lhs = ((IdentifierExpression)assS.getLeft()).getVariable();
                            outStmt = havocAssignments.get(lhs);
                            if (outStmt == null) {
                                SourceLocation loc = s.getSourceLocation();
                                Variable havocV = new Variable("havoc", lhs.getType());
                                outStmt = new AssignStatement(loc, assS.getLeft(),
                                                              new IdentifierExpression(loc, havocV));
                                havocAssignments.put(lhs, outStmt);
                            }
                        } else {
                            outStmt = s;
                        }

                        if (outS.add(outStmt))
                            for (Statement t : succMap.get(nextS))
                                if (queued.get(t).add(outStmt) && workListEls.add(t))
                                    workList.push(t);
                    }

                queuedS.clear();
                emptySet = queuedS;
            }

//            System.out.println("Z2: " + (System.currentTimeMillis() - start));
            
            ReachingDefinitions reach = new ReachingDefinitions();
            reach.in = in;
            reach.out = out;

            Set<Statement> havocs = new HashSet<>();
            havocs.addAll(havocAssignments.values());
            reach.havocStatements = havocs;

            return reach;
	}

	/**
	 * Returns true if s generates an update to a variable.
	 */
	private static boolean isGenStatement(Statement s) {
		return s instanceof AssignStatement ||
                       s instanceof CallStatement ||
                       s instanceof PullStatement ||
                       s instanceof NewStatement;
	}

	/**
	 * Returns true if statement <code>killer</code> kills
	 * <code>victim</code>.
	 */
        private static boolean kills(Statement killer,
                                     Statement victim) {
            final Set<Variable> killerDefs = killer.getDefVariables();
            for (Variable v : victim.getDefVariables())
                if (!killerDefs.contains(v))
                    return false;
            return true;
        }
	
	/**
	 * Returns true if statement <code>killer</code> updates some
	 * variables used by <code>victim</code>.
	 */
        private static boolean updatesUseVariables(Statement killer,
                                                   Statement victim) {
            final Set<Variable> killerDefs = killer.getDefVariables();
            for (Variable v : victim.getUseVariables())
                if (killerDefs.contains(v))
                    return true;
            return false;
        }

	private static void computeGenSet(Method m, Map<Statement, Set<Statement>> gen) {
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				// create the gen[s] map
				Set<Statement> genSet = new HashSet<Statement>();
				gen.put(s, genSet);
				if (isGenStatement(s)) {
					gen.get(s).add(s);
				} // else do nothing.
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
		Map<Statement, Set<Statement>> pred = new HashMap<Statement, Set<Statement>>();

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

    private static Map<Statement, Set<Statement>> getStatementSuccessorMap(
                                                    Map<Statement, Set<Statement>> predMap) {
        Map<Statement, Set<Statement>> res = new HashMap<Statement, Set<Statement>>();

        for (Map.Entry<Statement, Set<Statement>> entry : predMap.entrySet()) {
            Statement succ = entry.getKey();
            if (!res.containsKey(succ))
                res.put(succ, new HashSet<Statement> ());
            for (Statement pred : entry.getValue()) {
                if (!res.containsKey(pred))
                    res.put(pred, new HashSet<Statement> ());
                res.get(pred).add(succ);
            }
        }

        return res;
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
