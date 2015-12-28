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

import org.jgrapht.Graphs;

import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverResult;
import jayhorn.solver.ProverType;
import jayhorn.util.EdgeLabelToAssume;
import jayhorn.util.LoopRemoval;
import jayhorn.util.SsaPrinter;
import jayhorn.util.SsaTransformer;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.ArrayLengthExpression;
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
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.UnreachableNodeRemover;

/**
 * @author schaef
 *
 */
public class InconsistencyThread implements Runnable {

	public boolean debugMode = false;

	private final Method method;
	private final Prover prover;
	private final Program program;

	private final Map<Variable, Map<Integer, ProverExpr>> ssaVariableMap = new HashMap<Variable, Map<Integer, ProverExpr>>();
	private final Map<CfgBlock, ProverExpr> blockVars = new LinkedHashMap<CfgBlock, ProverExpr>();

	private final Set<CfgBlock> inconsistentBlocks = new HashSet<CfgBlock>();

	public Set<CfgBlock> getInconsistentBlocks() {
		return this.inconsistentBlocks;
	}

	/**
	 * 
	 */
	public InconsistencyThread(Program prog, Method m, Prover p) {
		method = m;
		prover = p;
		program = prog;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		inconsistentBlocks.clear();		
		if (method.vertexSet().isEmpty()) {
			System.out.println("Nothing to do for " + method.getMethodName());
			return;
		} else {
			System.out.println("Analyzing " + method.getMethodName());
		}		
		
//		System.out.println(method);
		
		UnreachableNodeRemover<CfgBlock, CfgEdge> unr = new UnreachableNodeRemover<CfgBlock, CfgEdge>(method, method.getSource(), method.getSink());
		if (unr.pruneUnreachableNodes()) {
			System.err.println("removed unreachable nodes for "+method.getMethodName());
		}
		
		EdgeLabelToAssume etoa = new EdgeLabelToAssume(method);
		etoa.turnLabeledEdgesIntoAssumes();

		LoopRemoval lr = new LoopRemoval(method);
		lr.removeLoops();
		
		lr.verifyLoopFree();// TODO: run only in debug mode.

		SsaTransformer ssa = new SsaTransformer(program, method);
		ssa.eliminatePhiStatements();

//		{
//		SsaPrinter printer = new SsaPrinter();
//		StringBuilder sb = new StringBuilder();
//		printer.printMethod(sb, method);
//		System.out.println(sb);
//		}

		createVerificationCondition();

		Set<ProverExpr> enablingClause = new HashSet<ProverExpr>();
		Map<ProverExpr, CfgBlock> blocks2cover = new HashMap<ProverExpr, CfgBlock>();
		for (Entry<CfgBlock, ProverExpr> entry : blockVars.entrySet()) {
			blocks2cover.put(entry.getValue(), entry.getKey());
			enablingClause.add(entry.getValue());
		}
		Set<CfgBlock> covered = new HashSet<CfgBlock>();

		ProverResult result = prover.checkSat(true);
		// prover.push();

		while (result == ProverResult.Sat) {
			// prover.pop();
			Set<ProverExpr> conj = new HashSet<ProverExpr>();
			if (debugMode)
				System.err.print("Path containing ");
			for (Entry<ProverExpr, CfgBlock> entry : blocks2cover.entrySet()) {
				if (prover.evaluate(entry.getKey()).getBooleanLiteralValue()) {
					conj.add(entry.getKey());
					covered.add(entry.getValue());
					enablingClause.remove(entry.getKey());
					if (debugMode)
						System.err.print(entry.getValue().getLabel() + " ");
				} else {
					conj.add(prover.mkNot(entry.getKey()));
				}
			}
			if (debugMode)
				System.err.println(".");
			// if (enablingClause.isEmpty()) break;

			// prover.push();
			// ProverExpr enabling = prover.mkOr(enablingClause.toArray(new
			// ProverExpr[enablingClause.size()]));
			// prover.addAssertion(enabling);
			ProverExpr blocking = prover.mkNot(prover.mkAnd(conj.toArray(new ProverExpr[conj.size()])));
			prover.addAssertion(blocking);			
			result = prover.checkSat(true);
		}
		// prover.pop();
		
		Set<CfgBlock> notCovered = new HashSet<CfgBlock>(blockVars.keySet());
		notCovered.removeAll(covered);

		inconsistentBlocks.addAll(notCovered);

		if (!inconsistentBlocks.isEmpty()) {
			System.err.println("*** REPORT ***");
			StringBuilder sb = new StringBuilder();
			sb.append("Not covered ");
			for (CfgBlock b : inconsistentBlocks) {
				sb.append(b.getLabel());
				sb.append("\n");
//				for (Statement s : b.getStatements()) {
//					sb.append("\t");
//					sb.append(s);
//					sb.append("\n");
//				}
			}
			
			System.err.println(sb.toString());
			System.err.println("**************");
		}

		return;
	}



	ProverFun arrayLength;

	private void createHelperFunctions() {
		// TODO: change the type of this
		arrayLength = prover.mkUnintFunction("$arrayLength", new ProverType[] { prover.getIntType() },
				prover.getIntType());
	}

	private void createVerificationCondition() {
		System.err.println("Creating transition relation");
		createHelperFunctions();

		// first create a boolean variable for each block.
		for (CfgBlock b : method.vertexSet()) {
			blockVars.put(b, prover.mkVariable(b.getLabel(), prover.getBooleanType()));
		}
		// assert that the boolean var for the root must be true
		prover.addAssertion(blockVars.get(method.getSource()));

		for (CfgBlock b : method.vertexSet()) {
			List<ProverExpr> conj = new LinkedList<ProverExpr>();

			// ensure that only complete paths can be in a model
			List<ProverExpr> comeFrom = new LinkedList<ProverExpr>();
			for (CfgBlock pre : Graphs.predecessorListOf(method, b)) {
				comeFrom.add(blockVars.get(pre));
			}
			if (!comeFrom.isEmpty()) {
				conj.add(prover.mkOr(comeFrom.toArray(new ProverExpr[comeFrom.size()])));
			}
			// ---------

			// transition relation of the statements
			for (Statement s : b.getStatements()) {
				if (statementToTransitionRelation(s) == null)
					continue; // TOOD: hack, remove later
				conj.add(statementToTransitionRelation(s));
			}
			List<ProverExpr> disj = new LinkedList<ProverExpr>();
			for (CfgBlock succ : Graphs.successorListOf(method, b)) {
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
			// if (method.inDegreeOf(b) == 0) {
			// System.err.print("(source)");
			// }
			// if (method.outDegreeOf(b) == 0) {
			// System.err.print("(sink)");
			// }
			//
			// System.err.println(b.getLabel() + ": " +
			// blockTransitionFormula.toString());
			prover.addAssertion(blockTransitionFormula);
		}

		// now add assertions to ensure that all unique variables are different.
		int superHackIntCounter = 0;
		for (ProverExpr var : usedUniqueVariables) {
			// TODO: this is a hack
			prover.addAssertion(prover.mkEq(var, prover.mkLiteral(superHackIntCounter++)));
		}

		System.err.println("done");
	}

	private int dummyvarcounter = 0;
	
	private ProverExpr statementToTransitionRelation(Statement s) {
		if (s instanceof AssertStatement) {
			return expressionToProverExpr(((AssertStatement) s).getExpression());
		} else if (s instanceof AssignStatement) {
			ProverExpr l = expressionToProverExpr(((AssignStatement) s).getLeft());
			ProverExpr r = expressionToProverExpr(((AssignStatement) s).getRight());

			return prover.mkEq(l, r);
		} else if (s instanceof AssumeStatement) {
			return expressionToProverExpr(((AssumeStatement) s).getExpression());
		} else if (s instanceof CallStatement) {
			CallStatement cs = (CallStatement)s;
			if (cs.getReceiver().isPresent()) {
				return prover.mkEq(expressionToProverExpr(cs.getReceiver().get()), prover.mkVariable("dummy"+(dummyvarcounter++), lookupProverType(cs.getReceiver().get().getType())));		
			}
			return null;
		} else if (s instanceof ArrayReadStatement) {
			ArrayReadStatement ar = (ArrayReadStatement)s;
			MapType mt = (MapType)ar.getBase().getType();			
			return prover.mkEq(expressionToProverExpr(ar.getLeftValue()), prover.mkVariable("dummy"+(dummyvarcounter++), lookupProverType(mt.getValueType())));
		} else if (s instanceof ArrayStoreStatement) {
			// TODO
		} else {
			// TODO ignore all other statements?
			return null;
		}
		return null; // TODO: these are hacks. Later, this must not return null.
	}

	private Set<ProverExpr> usedUniqueVariables = new HashSet<ProverExpr>();

	private ProverExpr expressionToProverExpr(Expression e) {
		if (e instanceof ArrayLengthExpression) {
			return arrayLength
					.mkExpr(new ProverExpr[] { expressionToProverExpr(((ArrayLengthExpression) e).getExpression()) });
		} else if (e instanceof BinaryExpression) {
			BinaryExpression be = (BinaryExpression) e;
			ProverExpr left = expressionToProverExpr(be.getLeft());
			ProverExpr right = expressionToProverExpr(be.getRight());
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
				ProverExpr ssaVar = prover.mkVariable(ie.getDefVariables() + "__" + ie.getIncarnation(),
						lookupProverType(ie.getType()));
				ssaVariableMap.get(ie.getVariable()).put(ie.getIncarnation(), ssaVar);
			}
			if (ie.getVariable().isUnique()) {
				// If this is a unique variable, remember it and add axioms
				// later that ensure that
				// all unique variables are different.
				usedUniqueVariables.add(ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation()));
			}
			return ssaVariableMap.get(ie.getVariable()).get(ie.getIncarnation());
		} else if (e instanceof InstanceOfExpression) {
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

			if (ue.getOp() == UnaryOperator.LNot) {
				return prover.mkNot(expr);
			} else {
				assert (ue.getOp() == UnaryOperator.Neg);
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
