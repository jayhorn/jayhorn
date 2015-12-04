/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverResult;
import jayhorn.solver.ProverType;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.expression.UnaryExpression.UnaryOperator;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.Type;

/**
 * @author schaef
 *
 */
public class InconsistencyThread implements Runnable {

	private final Method method;
	private final Prover prover;

	private final Map<Variable, Map<Integer, ProverExpr>> ssaVariableMap = new HashMap<Variable, Map<Integer, ProverExpr>>();
	private final Map<CfgBlock, ProverExpr> blockVars = new LinkedHashMap<CfgBlock, ProverExpr>();

	/**
	 * 
	 */
	public InconsistencyThread(Program prog, Method m, Prover p) {
		method = m;
		prover = p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if (method.vertexSet().isEmpty()) {
			System.out.println("Nothing to do for " + method.getMethodName());
			return;
		} else {
			System.out.println("Analyzing " + method.getMethodName());
		}
		LoopRemoval lr = new LoopRemoval(method);
		lr.removeLoops();

		turnLabeledEdgesIntoAssumes();

		SingleStaticAssignment ssa = new SingleStaticAssignment(method);
		ssa.computeSSA();

		System.out.println(method);
		createVerificationCondition();

		Map<ProverExpr, CfgBlock> blocks2cover = new HashMap<ProverExpr, CfgBlock>();
		for (Entry<CfgBlock, ProverExpr> entry : blockVars.entrySet()) {
			blocks2cover.put(entry.getValue(), entry.getKey());
		}
		Set<CfgBlock> covered = new HashSet<CfgBlock>();

		ProverResult result = prover.checkSat(true);

		Set<CfgBlock> blocksOnPath = new HashSet<CfgBlock>(); 
		while (result == ProverResult.Sat) {
			Set<ProverExpr> conj = new HashSet<ProverExpr>();
			System.err.print("Path containing ");
			for (Entry<ProverExpr, CfgBlock> entry : blocks2cover.entrySet()) {
				if (prover.evaluate(entry.getKey()).getBooleanLiteralValue()) {
					conj.add(entry.getKey());
					blocksOnPath.add(entry.getValue());
					System.err.print(entry.getValue().getLabel() + " ");
				} else {
					conj.add(prover.mkNot(entry.getKey()));
				}
			}
			System.err.println(".");
			covered.addAll(blocksOnPath);

			ProverExpr blocking = prover.mkNot(prover.mkAnd(conj.toArray(new ProverExpr[conj.size()])));
			prover.addAssertion(blocking);
			result = prover.checkSat(true);
		}

		Set<CfgBlock> notCovered = new HashSet<CfgBlock>(blockVars.keySet());
		notCovered.removeAll(covered);
		System.err.println("*** REPORT ***");
		if (!notCovered.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Not covered ");
			for (CfgBlock b : notCovered) {
				sb.append(b.getLabel());
				sb.append(", ");
			}
			System.err.println(sb.toString());
		} else {
			System.err.println("All covered");
		}
		System.err.println("**************");
	}

	/**
	 * For each edge labeled with a conditional, introduce a new vertex that
	 * contains this conditional as assume statement, remove the edge and add
	 * new edges to but this vertex between source and target
	 */
	private void turnLabeledEdgesIntoAssumes() {
		Set<CfgEdge> edges = new HashSet<CfgEdge>(method.edgeSet());
		for (CfgEdge edge : edges) {
			if (edge.getLabel().isPresent()) {
				CfgBlock src = method.getEdgeSource(edge);
				CfgBlock tgt = method.getEdgeTarget(edge);
				SourceLocation loc = null;
				if (!tgt.getStatements().isEmpty()) {
					loc = tgt.getStatements().iterator().next().getSourceLocation();
				} else if (!src.getStatements().isEmpty()) {
					loc = src.getStatements().get(src.getStatements().size() - 1).getSourceLocation();
				} else {					
					System.err.println("ERROR: these labeled edges without location tags will cause problems later. @Martin, fix that!");
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

	private void createVerificationCondition() {		
		System.err.println("Creating transition relation");
		
		// first create a boolean variable for each block.
		for (CfgBlock b : method.vertexSet()) {
			blockVars.put(b, prover.mkVariable(b.getLabel(), prover.getBooleanType()));
		}
		//assert that the boolean var for the root must be true
		prover.addAssertion(blockVars.get(method.getSource()));
		
		for (CfgBlock b : method.vertexSet()) {
			List<ProverExpr> conj = new LinkedList<ProverExpr>();
			for (Statement s : b.getStatements()) {
				if (statementToTransitionRelation(s) == null)
					continue; // TOOD: hack, remove later
				conj.add(statementToTransitionRelation(s));
			}
			List<ProverExpr> disj = new LinkedList<ProverExpr>();
			for (CfgBlock succ : method.getSuccsOf(b)) {
				// This assumes that all edge labels have been turned into
				// assumes.
				disj.add(blockVars.get(succ));
			}
			if (!disj.isEmpty()) {
				conj.add(prover.mkOr(disj.toArray(new ProverExpr[disj.size()])));
			}
			ProverExpr tr;
			if (conj.isEmpty()) {
				tr = prover.mkLiteral(true);
			} else {
				tr = prover.mkAnd(conj.toArray(new ProverExpr[conj.size()]));
			}
			ProverExpr blockTransitionFormula = prover.mkImplies(blockVars.get(b), tr);
			if (method.inDegreeOf(b) == 0) {
				System.err.print("(source)");
			}
			if (method.outDegreeOf(b) == 0) {
				System.err.print("(sink)");
			}

			System.err.println(b.getLabel() + ": " + blockTransitionFormula.toString());
			prover.addAssertion(blockTransitionFormula);
		}
		System.err.println("done");
	}

	private ProverExpr statementToTransitionRelation(Statement s) {
		if (s instanceof AssertStatement) {
			return expressionToProverExpr(((AssertStatement) s).getExpression());
		} else if (s instanceof AssignStatement) {
			ProverExpr l = expressionToProverExpr(((AssignStatement) s).getLeft());
			ProverExpr r = expressionToProverExpr(((AssignStatement) s).getRight());
			if (l == null || r == null) {
				return null; // TODO: these are hacks. Later, this must not
								// return null.
			}
			return prover.mkEq(l, r);
		} else if (s instanceof AssumeStatement) {
			return expressionToProverExpr(((AssumeStatement) s).getExpression());
		} else if (s instanceof CallStatement) {
			// TODO: should be eliminated earlier
		} else if (s instanceof ArrayReadStatement) {
			// TODO
		} else if (s instanceof ArrayStoreStatement) {
			// TODO
		} else {
			// TODO ignore all other statements?
			return null;
		}
		return null; // TODO: these are hacks. Later, this must not return null.
	}

	private ProverExpr expressionToProverExpr(Expression e) {
		if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) e;
			ProverExpr left = expressionToProverExpr(be.getLeft());
			ProverExpr right = expressionToProverExpr(be.getRight());
			if (left == null || right == null) {
				return null; // TODO: these are hacks. Later, this must not
								// return null.
			}
			switch (be.getOp()) {
			case Plus:
				return prover.mkPlus(left, right);
			case Minus:
				return prover.mkMinus(left, right);
			case Mul:
				return prover.mkMult(left, right);
			case Div:
				return prover.mkTDiv(left, right);
			case Mod:
				return prover.mkTMod(left, right);

			case Eq:
				return prover.mkEq(left, right);
			case Ne:
				return prover.mkNot(prover.mkEq(left, right));
			case Gt:
				return prover.mkGt(left, right);
			case Ge:
				return prover.mkGeq(left, right);
			case Lt:
				return prover.mkLt(left, right);
			case Le:
				return prover.mkLeq(left, right);
			default: {
				throw new RuntimeException("Not implemented for " + be.getOp());
			}
			}
		} else if (e instanceof BooleanLiteral) {
			return prover.mkLiteral(((BooleanLiteral) e).getValue());
		} else if (e instanceof IdentifierExpression) {
			IdentifierExpression ie = (IdentifierExpression) e;
			ie.getVariable();
			ie.getIncarnation();
			if (!ssaVariableMap.containsKey(ie.getVariable())) {
				ssaVariableMap.put(ie.getVariable(), new HashMap<Integer, ProverExpr>());
			}
			if (!ssaVariableMap.get(ie.getVariable()).containsKey(ie.getIncarnation())) {
				ProverExpr ssaVar = prover.mkVariable(ie.getLVariables() + "__" + ie.getIncarnation(),
						lookupProverType(ie.getType()));
				ssaVariableMap.get(ie.getVariable()).put(ie.getIncarnation(), ssaVar);
			}
			return ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation());
		} else if (e instanceof InstanceOfExpression) {
			// TODO:
			return prover.mkVariable("$randomBool" + UUID.randomUUID().toString(), prover.getBooleanType());
		} else if (e instanceof IntegerLiteral) {
			return prover.mkLiteral(BigInteger.valueOf(((IntegerLiteral) e).getValue()));
		} else if (e instanceof IteExpression) {
			IteExpression ie = (IteExpression) e;
			return prover.mkIte(expressionToProverExpr(ie.getCondition()), expressionToProverExpr(ie.getThenExpr()),
					expressionToProverExpr(ie.getElseExpr()));
		} else if (e instanceof UnaryExpression) {
			UnaryExpression ue = (UnaryExpression) e;
			ProverExpr expr = expressionToProverExpr(ue.getExpression());
			if (expr == null) {
				return null; // TODO: these are hacks. Later, this must not
								// return null.
			}

			if (ue.getOp() == UnaryOperator.LNot) {
				return prover.mkNot(expr);
			} else {
				assert(ue.getOp() == UnaryOperator.Neg);
				return prover.mkMult(prover.mkLiteral(-1), expr);
			}
		} else {
			throw new RuntimeException("unexpected expression type: " + e);
		}
	}

	private ProverType lookupProverType(Type t) {
		if (t == BoolType.instance()) {
			return prover.getBooleanType();
		}
		return prover.getIntType();
	}

}
