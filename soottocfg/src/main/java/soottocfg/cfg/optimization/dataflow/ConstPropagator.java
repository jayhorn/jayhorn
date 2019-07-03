/**
 * 
 */
package soottocfg.cfg.optimization.dataflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.Literal;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.DataFlowUtils;
import soottocfg.cfg.util.DataFlowUtils.ReachingDefinitions;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *         Implements copy propagation algorithm from
 *         Apple's Modern Compiler Construction book page 359
 */
public class ConstPropagator {

	private static boolean cfgEquals(Expression s1, Expression s2) {
		// TODO: equals currently broken
		return s1.toString().equals(s2.toString());
	}

	private static boolean cfgEquals(Statement s1, Statement s2) {
		// TODO: equals currently broken
		return s1.toString().equals(s2.toString());
	}

	public static void main(String[] args) {
		Method m = createExampleProgram();
		System.err.println(m);
		System.err.println("Running constant propagator");
		while (constPropagate(m)) {
			System.err.println("Running propagator again");
		}
		System.err.println(m);
	}

	/**
	 * Copy propagation of local variables. Returns true
	 * if anything has changed and false otherwise.
	 * 
	 * @param m
	 *            The method to which copy propagation should be applied.
	 * @return True, if anything changed. Otherwise false.
	 */
	public static boolean constPropagate(Method m) {
		ReachingDefinitions rdefs = DataFlowUtils.computeReachingDefinitions(m);
//            Long start = System.currentTimeMillis();
		
		boolean changes = false;

		for (CfgBlock b : m.vertexSet()) {
			if (propagateForEdgeLabels(m, b, rdefs)) {
				changes = true;
			}
			List<Statement> newStmts = new LinkedList<Statement>();
			for (Statement s : b.getStatements()) {
				Map<Variable, Expression> substitutions = createSubstitutionMap(s.getUseVariables(), rdefs.in.get(s));
				Statement newStmt = s.substituteVarWithExpression(substitutions);
				changes = changes || !cfgEquals(newStmt, s);
				newStmts.add(newStmt);
			}
			if (changes) {
				b.setStatements(newStmts);
			}
		}
//            System.out.println("A: " + (System.currentTimeMillis() - start));
		return changes;
	}

	private static boolean propagateForEdgeLabels(Method m, CfgBlock b, ReachingDefinitions rdefs) {
		boolean changes = false;
		if (!b.getStatements().isEmpty()) {
			Statement lastStmt = b.getStatements().get(b.getStatements().size() - 1);
			Set<Statement> defStmts = rdefs.out.get(lastStmt);
			for (CfgEdge e : m.outgoingEdgesOf(b)) {
				if (e.getLabel().isPresent()) {
					Expression expr = e.getLabel().get();
					Map<Variable, Expression> substitutions = createSubstitutionMap(expr.getUseVariables(), defStmts);
					if (!substitutions.isEmpty()) {
						Expression newExpr = expr.substituteVarWithExpression(substitutions);
						if (!cfgEquals(expr, newExpr)) {
							e.setLabel(newExpr);
							changes = true;
						}
					}
				}
			}
		}
		return changes;
	}

	private static Map<Variable, Expression> createSubstitutionMap(Set<Variable> useVars,
			Set<Statement> reachingStatements) {
		Map<Variable, Expression> subsitutions = new HashMap<Variable, Expression>();
		for (Variable v : useVars) {
//			boolean debug = false;
//			if (v.getName().contains("cp_$ex__9")) debug=true;
			
			Set<Statement> defStmts = getDefStatementsForVar(v, reachingStatements);
//			if (debug) System.err.println("1 " + reachingStatements);
			if (defStmts.size() == 1) {
				Statement stmt = defStmts.iterator().next();
//				if (debug) System.err.println("2");
				if (stmt instanceof AssignStatement && ((AssignStatement) stmt).getRight() instanceof Literal) {
					Expression literal = (Expression) ((AssignStatement) stmt).getRight();
//					if (debug) System.err.println("3");
					subsitutions.put(v, literal);
				}
			}
		}
		return subsitutions;
	}

	/**
	 * Returns the subset of statements that def the variable v.
	 * This can be assignments, calls, or pulls.
	 * 
	 * @param v
	 * @param reachingStatements
	 * @return
	 */
	private static Set<Statement> getDefStatementsForVar(Variable v, Set<Statement> reachingStatements) {
		Set<Statement> res = new HashSet<Statement>();
		for (Statement s : reachingStatements) {
			if (s.getDefVariables().contains(v)) {
				res.add(s);
			}
		}
		return res;
	}

	/**
	 * Creates example program from Listing 17.3 on page 356
	 * 
	 * @return
	 */
	private static Method createExampleProgram() {
		Program p = new Program();
		SootTranslationHelpers.initialize(p);
		SourceLocation loc = SourceLocation.ANALYSIS;
		Method m = Method.createMethodForTestingOnly(p, "test", new LinkedList<Variable>(), new LinkedList<Type>(),
				loc);

		Variable a = new Variable("a", IntType.instance());
		Variable b = new Variable("b", IntType.instance());
		Variable c = new Variable("c", IntType.instance());
		m.addLocalVariable(a);
		m.addLocalVariable(b);
		m.addLocalVariable(c);
		CfgBlock l0 = new CfgBlock(m);

		l0.addStatement(new AssignStatement(loc, a.mkExp(loc), new IntegerLiteral(loc, 5)));
		l0.addStatement(new AssignStatement(loc, b.mkExp(loc), a.mkExp(loc)));
		l0.addStatement(new AssignStatement(loc, c.mkExp(loc), b.mkExp(loc)));

		return m;
	}
}
