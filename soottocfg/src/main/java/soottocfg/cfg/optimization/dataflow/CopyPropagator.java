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
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.expression.literal.StringLiteral;
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
public class CopyPropagator {

	public static void main(String[] args) {
		Method m = createExampleProgram();
		System.err.println(m);

		System.err.println(copyPropagate(m));

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
	public static boolean copyPropagate(Method m) {

		ReachingDefinitions rdefs = DataFlowUtils.computeReachingDefinitions(m);
		boolean changes = false;
		for (CfgBlock b : m.vertexSet()) {
			if (progateForEdgeLabels(m, b, rdefs)) {
				changes = true;
			}
			List<Statement> newStmts = new LinkedList<Statement>();
			for (Statement s : b.getStatements()) {
				Map<Variable, Variable> substitutions =
                                    createSubstitutionMap(s.getUseVariables(), rdefs.in.get(s), rdefs);
				Statement newStmt = s.substitute(substitutions);
				changes = changes || (newStmt != s);
				newStmts.add(newStmt);
			}
			if (changes) {
				b.setStatements(newStmts);
			}
		}
		return changes;
	}

	// FIXME: progateForEdgeLabels -> propagateForEdgeLabels ?
	private static boolean progateForEdgeLabels(Method m, CfgBlock b, ReachingDefinitions rdefs) {
		boolean changes = false;
		if (!b.getStatements().isEmpty()) {
			Statement lastStmt = b.getStatements().get(b.getStatements().size() - 1);
			Set<Statement> defStmts = rdefs.out.get(lastStmt);
			for (CfgEdge e : m.outgoingEdgesOf(b)) {
				if (e.getLabel().isPresent()) {
					Expression expr = e.getLabel().get();
					Map<Variable, Variable> substitutions =
                                            createSubstitutionMap(expr.getUseVariables(), defStmts, rdefs);
					if (!substitutions.isEmpty()) {
						Expression newExpr = expr.substitute(substitutions);
						if (expr != newExpr) {
							e.setLabel(newExpr);
							changes = true;
						}
					}
				}
			}
		}
		return changes;
	}

	private static Map<Variable, Variable> createSubstitutionMap(Set<Variable> useVars,
                                                                     Set<Statement> reachingStatements,
                                                                     ReachingDefinitions rdefs) {
		Map<Variable, Variable> substitutions = new HashMap<Variable, Variable>();
		mainLoop: for (Variable v : useVars) {
			Set<Statement> defStmts = getDefStatementsForVar(v, reachingStatements);
                        Variable replacement = null;

                        for (Statement s : defStmts) {
                            if (rdefs.havocStatements.contains(s))
                                continue mainLoop;
                            if (!(s instanceof AssignStatement &&
                                  (((AssignStatement)s).getRight() instanceof IdentifierExpression) &&
                                  !(((AssignStatement)s).getRight() instanceof StringLiteral)))
                                continue mainLoop;
                            AssignStatement assS = (AssignStatement)s;
                            Variable rhs = ((IdentifierExpression)assS.getRight()).getVariable();
                            if (replacement != null && !replacement.equals(rhs))
                                continue mainLoop;
                            replacement = rhs;
                        }

                        if (replacement != null)
                            substitutions.put(v, replacement);
		}
		return substitutions;
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
