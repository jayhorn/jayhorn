/**
 * 
 */
package jayhorn.checker;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.hornify.Hornify;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverResult;
import jayhorn.solver.ProverType;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BooleanLiteral;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.IteExpression;
import soottocfg.cfg.expression.UnaryExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.InterProceduralPullPushOrdering;

/**
 * @author schaef
 *
 */
public class Checker {

	private static class HornPredicate {
		public final String name;
		public final List<Variable> variables;
		public final ProverFun predicate;

		public HornPredicate(String name, List<Variable> vars, ProverFun pred) {
			this.name = name;
			variables = vars;
			predicate = pred;
		}

		public String toString() {
			return "" + predicate;
		}
	}

	private static class MethodContract {
		public final Method method;
		public final HornPredicate precondition;
		public final HornPredicate postcondition;

		public MethodContract(Method method, HornPredicate precondition, HornPredicate postcondition) {
			this.method = method;
			this.precondition = precondition;
			this.postcondition = postcondition;
		}

		public String toString() {
			return "<" + precondition + ", " + postcondition + ">";
		}
	}

	private final ProverFactory factory;

	public Checker(ProverFactory fac) {
		this.factory = fac;
	}

	private final Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();
	private Map<String, MethodContract> methodContracts = new LinkedHashMap<String, MethodContract>();
	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();

	////////////////////////////////////////////////////////////////////////////

	private Map<ClassVariable, ProverFun> classInvariants = new LinkedHashMap<ClassVariable, ProverFun>();

	private ProverFun getClassInvariant(Prover p, ClassVariable sig) {
		ProverFun inv = classInvariants.get(sig);

		if (inv == null) {
			List<Variable> args = new ArrayList<Variable>();

			args.add(new Variable("ref", new ReferenceType(sig)));
			for (Variable v : sig.getAssociatedFields())
				args.add(v);

			inv = genHornPredicate(p, "inv_" + sig.getName(), args);

			classInvariants.put(sig, inv);
		}

		return inv;
	}

	////////////////////////////////////////////////////////////////////////////

	private int varNum = 0;

	private int newVarNum() {
		return varNum++;
	}

	////////////////////////////////////////////////////////////////////////////

	private class MethodEncoder {
		private final Program program;
		private final Method method;
		private final MethodContract methodContract;
		private final Prover p;

		private final Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();
		public final List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		private final List<Variable> methodPreVariables;
		private final List<ProverExpr> methodPreExprs;

		public MethodEncoder(Prover p, Program program, Method method) {
			this.p = p;
			this.program = program;
			this.method = method;
			this.methodContract = methodContracts.get(method.getMethodName());
			this.methodPreVariables = methodContract.precondition.variables;

			this.methodPreExprs = new ArrayList<ProverExpr>();
			for (Variable v : methodPreVariables)
				methodPreExprs.add(p.mkHornVariable(v.getName() + "_" + newVarNum(), getProverType(v.getType())));
		}

		public void encode() {
			// Log.info("\tEncoding method " + method.getMethodName());
			LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();
			makeBlockPredicates(liveVariables);

			if (method.getSource() == null) {
				Log.debug("No implementation available for " + method.getMethodName());
				final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
				final List<ProverExpr> exitVars = new ArrayList<ProverExpr>();
				final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
				createVarMap(methodContract.precondition.variables, entryVars, varMap);
				createVarMap(methodContract.postcondition.variables, exitVars, varMap);

				final ProverExpr entryAtom = methodContract.precondition.predicate
						.mkExpr(entryVars.toArray(new ProverExpr[0]));
				final ProverExpr exitAtom = methodContract.postcondition.predicate
						.mkExpr(exitVars.toArray(new ProverExpr[0]));

				clauses.add(p.mkHornClause(exitAtom, new ProverExpr[] { entryAtom }, p.mkLiteral(true)));

				return;
			}

			List<CfgBlock> todo = new LinkedList<CfgBlock>();
			todo.add(method.getSource());
			Set<CfgBlock> done = new HashSet<CfgBlock>();

			{
				// add an entry clause connecting with the precondition
				final HornPredicate entryPred = blockPredicates.get(method.getSource());
				final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
				final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
				createVarMap(entryPred.variables, entryVars, varMap);

				final List<ProverExpr> preVars = new ArrayList<ProverExpr>();
				createVarMap(methodPreVariables, preVars, varMap);

				final ProverExpr preAtom = methodContract.precondition.predicate
						.mkExpr(preVars.toArray(new ProverExpr[0]));

				final List<ProverExpr> allEntryArgs = new ArrayList<ProverExpr>();
				allEntryArgs.addAll(preVars);
				allEntryArgs.addAll(entryVars);

				final ProverExpr entryAtom = entryPred.predicate.mkExpr(allEntryArgs.toArray(new ProverExpr[0]));

				clauses.add(p.mkHornClause(entryAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
			}

			// translate reachable blocks
			while (!todo.isEmpty()) {
				CfgBlock current = todo.remove(0);
				// Log.info("\tEncoding block " + current);

				done.add(current);
				final HornPredicate exitPred = blockToHorn(current, liveVariables);

				// take care of return and successors
				final List<ProverExpr> exitVars = new ArrayList<ProverExpr>();
				final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
				createVarMap(exitPred.variables, exitVars, varMap);

				if (method.outgoingEdgesOf(current).isEmpty()) {
					// block ends with a return

					final List<ProverExpr> postVars = new ArrayList<ProverExpr>();
					createVarMap(methodContract.postcondition.variables, postVars, varMap);

					final ProverExpr postAtom = methodContract.postcondition.predicate
							.mkExpr(postVars.toArray(new ProverExpr[0]));

					final List<ProverExpr> allExitArgs = new ArrayList<ProverExpr>();
					allExitArgs.addAll(postVars.subList(0, methodPreVariables.size()));
					allExitArgs.addAll(exitVars);

					final ProverExpr exitAtom = exitPred.predicate.mkExpr(allExitArgs.toArray(new ProverExpr[0]));

					clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { exitAtom }, p.mkLiteral(true)));

				} else {
					// link to the successor blocks

					final ProverExpr exitAtom = instPredicate(exitPred, exitVars);

					for (CfgEdge edge : method.outgoingEdgesOf(current)) {
						CfgBlock succ = method.getEdgeTarget(edge);
						if (!todo.contains(succ) && !done.contains(succ))
							todo.add(succ);

						final ProverExpr exitCondExpr;
						if (edge.getLabel().isPresent())
							exitCondExpr = exprToProverExpr(edge.getLabel().get(), varMap);
						else
							exitCondExpr = p.mkLiteral(true);

						final HornPredicate entryPred = blockPredicates.get(succ);
						final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
						createVarMap(entryPred.variables, entryVars, varMap);

						final ProverExpr entryAtom = instPredicate(entryPred, entryVars);

						clauses.add(p.mkHornClause(entryAtom, new ProverExpr[] { exitAtom }, exitCondExpr));
					}
				}
			}
		}

		/**
		 * Creates one HornPredicate for each block. The predicate contains the
		 * list of live variables
		 * for that block sorted by names and a predicate over the types of
		 * these variables that has
		 * the same name as the block.
		 * 
		 * @param p
		 * @param method
		 */
		private void makeBlockPredicates(LiveVars<CfgBlock> liveVariables) {
			for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.liveIn.entrySet()) {
				Set<Variable> allLive = new HashSet<Variable>();
				allLive.addAll(entry.getValue());
				// sort the list of variables by name to make access
				// and reading easier.
				List<Variable> sortedVars = setToSortedList(allLive);
				String name = entry.getKey().getLabel();
				ProverFun pred = freshHornPredicate(name, sortedVars);
				blockPredicates.put(entry.getKey(), new HornPredicate(name, sortedVars, pred));
			}
		}

		private ProverExpr instPredicate(HornPredicate pred, List<ProverExpr> args) {
			List<ProverExpr> allArgs = new ArrayList<ProverExpr>();
			allArgs.addAll(methodPreExprs);
			allArgs.addAll(args);
			return pred.predicate.mkExpr(allArgs.toArray(new ProverExpr[0]));
		}

		private ProverFun freshHornPredicate(String name, List<Variable> sortedVars) {
			final List<Variable> allArgs = new LinkedList<Variable>();
			// add types for the method arguments, which
			// are later needed for the post-conditions
			allArgs.addAll(methodPreVariables);
			allArgs.addAll(sortedVars);
			return genHornPredicate(p, method.getMethodName() + "_" + name, allArgs);
		}

		private ProverType getProverType(Type t) {
			return Checker.this.getProverType(p, t);
		}

		private HornPredicate blockToHorn(CfgBlock block, LiveVars<CfgBlock> liveVariables) {
			final HornPredicate initPred = blockPredicates.get(block);

			if (block.getStatements().isEmpty())
				return initPred;

			final Set<Variable> liveOutVars = liveVariables.liveOut.get(block);

			final Set<Variable>[] interVars = new Set[block.getStatements().size()];
			interVars[interVars.length - 1] = new HashSet<Variable>();
			interVars[interVars.length - 1].addAll(liveOutVars);

			// add variables used in the outgoing guards, and the
			// method arguments
			for (CfgEdge edge : method.outgoingEdgesOf(block))
				if (edge.getLabel().isPresent())
					interVars[interVars.length - 1].addAll(edge.getLabel().get().getUseVariables());

			for (int i = interVars.length - 1; i > 0; --i) {
				final Statement s = block.getStatements().get(i);
				interVars[i - 1] = new HashSet<Variable>();
				interVars[i - 1].addAll(interVars[i]);
				interVars[i - 1].removeAll(s.getDefVariables());
				interVars[i - 1].addAll(s.getUseVariables());
			}

			final String initName = initPred.name;
			HornPredicate prePred = initPred;
			int counter = 0;

			for (Statement s : block.getStatements()) {
				final String postName = initName + "_" + (++counter);
				final List<Variable> interVarList = setToSortedList(interVars[counter - 1]);
				final HornPredicate postPred = new HornPredicate(postName, interVarList,
						freshHornPredicate(postName, interVarList));
				statementToClause(s, prePred, postPred);
				prePred = postPred;
			}

			return prePred;
		}

		private void createVarMap(List<Variable> cfgVars, List<ProverExpr> proverVars,
				Map<Variable, ProverExpr> varMap) {
			for (Variable v : cfgVars) {
				ProverExpr e = varMap.get(v);
				if (e == null) {
					e = p.mkHornVariable(v.getName() + "_" + newVarNum(), getProverType(v.getType()));
					varMap.put(v, e);
				}
				proverVars.add(e);
			}
		}

		private void statementToClause(Statement s, HornPredicate prePred, HornPredicate postPred) {
			final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();

			final List<ProverExpr> preVars = new ArrayList<ProverExpr>();
			createVarMap(prePred.variables, preVars, varMap);

			final List<ProverExpr> postVars = new ArrayList<ProverExpr>();
			createVarMap(postPred.variables, postVars, varMap);

			final ProverExpr preAtom = instPredicate(prePred, preVars);

			if (s instanceof AssertStatement) {

				final AssertStatement as = (AssertStatement) s;
				final ProverExpr cond = exprToProverExpr(as.getExpression(), varMap);

				clauses.add(p.mkHornClause(p.mkLiteral(false), new ProverExpr[] { preAtom }, p.mkNot(cond)));

				final ProverExpr postAtom = instPredicate(postPred, postVars);

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

			} else if (s instanceof AssumeStatement) {

				final AssumeStatement as = (AssumeStatement) s;
				final ProverExpr cond = exprToProverExpr(as.getExpression(), varMap);

				final ProverExpr postAtom = instPredicate(postPred, postVars);

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, cond));

			} else if (s instanceof AssignStatement) {

				final AssignStatement as = (AssignStatement) s;
				final Expression lhs = as.getLeft();

				if (lhs instanceof IdentifierExpression) {
					final IdentifierExpression idLhs = (IdentifierExpression) lhs;
					final int lhsIndex = postPred.variables.indexOf(idLhs.getVariable());
					if (lhsIndex >= 0) {
						postVars.set(lhsIndex, exprToProverExpr(as.getRight(), varMap));
					}
				} else {
					throw new RuntimeException("only assignments to variables are supported, not to " + lhs);
				}

				final ProverExpr postAtom = instPredicate(postPred, postVars);

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

			} else if (s instanceof CallStatement) {

				final CallStatement cs = (CallStatement) s;
				final Method calledMethod = cs.getCallTarget();
				final MethodContract contract = methodContracts.get(calledMethod.getMethodName());

				if (contract == null)
					throw new RuntimeException("Invoked method " + calledMethod.getMethodName() + " is unknown");

				assert (calledMethod.getInParams().size() == cs.getArguments().size()
						&& calledMethod.getInParams().size() == contract.precondition.variables.size());
				assert (!cs.getReceiver().isEmpty() || calledMethod.getReturnType().isEmpty());

				final List<Variable> receiverVars = new ArrayList<Variable>();
				for (Expression e : cs.getReceiver()) {
					receiverVars.add(((IdentifierExpression) e).getVariable());
				}
				final List<ProverExpr> receiverExprs = new ArrayList<ProverExpr>();
				createVarMap(receiverVars, receiverExprs, varMap);

				final ProverExpr[] actualInParams = new ProverExpr[calledMethod.getInParams().size()];
				final ProverExpr[] actualPostParams = new ProverExpr[calledMethod.getInParams().size()
						+ calledMethod.getReturnType().size()];

				int cnt = 0;
				for (Expression e : cs.getArguments()) {
					final ProverExpr expr = exprToProverExpr(e, varMap);
					actualInParams[cnt] = expr;
					actualPostParams[cnt] = expr;
					++cnt;
				}

				if (!cs.getReceiver().isEmpty()) {
					for (Expression lhs : cs.getReceiver()) {

						final ProverExpr callRes = p.mkHornVariable("callRes_" + newVarNum(),
								getProverType(lhs.getType()));
						actualPostParams[cnt++] = callRes;

						if (lhs instanceof IdentifierExpression) {
							final IdentifierExpression idLhs = (IdentifierExpression) lhs;
							final int lhsIndex = postPred.variables.indexOf(idLhs.getVariable());
							if (lhsIndex >= 0)
								postVars.set(lhsIndex, callRes);
						} else {
							throw new RuntimeException(
									"only assignments to variables are supported, " + "not to " + lhs);
						}
					}
				} else if (!calledMethod.getReturnType().isEmpty()) {
					for (Type tp : calledMethod.getReturnType()) {
						final ProverExpr callRes = p.mkHornVariable("callRes_" + newVarNum(), getProverType(tp));
						actualPostParams[cnt++] = callRes;
					}
				}

				final ProverExpr preCondAtom = contract.precondition.predicate.mkExpr(actualInParams);
				clauses.add(p.mkHornClause(preCondAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

				final ProverExpr postCondAtom = contract.postcondition.predicate.mkExpr(actualPostParams);

				final ProverExpr postAtom = instPredicate(postPred, postVars);

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, postCondAtom }, p.mkLiteral(true)));

			} else if (s instanceof PullStatement) {

				final PullStatement pull = (PullStatement) s;
				final List<IdentifierExpression> lhss = pull.getLeft();

				/*
				 * TODO
				 * Martin's hack to handle the substype problem =============
				 */
				final Set<ClassVariable> possibleTypes = ppOrdering.getBrutalOverapproximationOfPossibleType(pull);
//				final List<ProverExpr> invariantDisjunction = new LinkedList<ProverExpr>();
				for (ClassVariable sig : possibleTypes) {
//					System.err.println("Possible type "+sig.getName() + " of " +us.getClassSignature().getName());
					final ProverFun inv = getClassInvariant(p, sig);
					
//					Verify.verify(sig.getAssociatedFields()[sig.getAssociatedFields().length-1]
//							.getName().equals(PushIdentifierAdder.LP), 
//							"Class is missing " + PushIdentifierAdder.LP + " field: " + sig);

					int totalFields = Math.max(sig.getAssociatedFields().length, lhss.size());
					
					final ProverExpr[] invArgs = new ProverExpr[1 + totalFields];
					int cnt = 0;
					invArgs[cnt++] = exprToProverExpr(pull.getObject(), varMap);

					for (IdentifierExpression lhs : lhss) {
						final ProverExpr lhsExpr = p.mkHornVariable("pullRes_" + lhs + "_" + newVarNum(),
								getProverType(lhs.getType()));
						invArgs[cnt++] = lhsExpr;

						final int lhsIndex = postPred.variables.indexOf(lhs.getVariable());
						if (lhsIndex >= 0)
							postVars.set(lhsIndex, lhsExpr);
					}
					while (cnt<totalFields+1) {
						//fill up the fields that are not being used
						//this should only happen if sig is a subtype of what we 
						//are trying to pull (and thus declares more fields).
						final ProverExpr lhsExpr = p.mkHornVariable("pullRes_stub" + cnt + "_" + newVarNum(),
								getProverType(sig.getAssociatedFields()[cnt-1].getType() ));
						invArgs[cnt++] = lhsExpr;
					}
//					invariantDisjunction.add(inv.mkExpr(invArgs));
					
					final ProverExpr invAtom = inv.mkExpr(invArgs);
					final ProverExpr postAtom = instPredicate(postPred, postVars);
					clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, invAtom }, p.mkLiteral(true)));

				}
//				final ProverExpr invAtom = p.mkOr(invariantDisjunction.toArray(new ProverExpr[invariantDisjunction.size()]));

				/*
				 * Old code that only checked one invariant.
				 * final PullStatement us = (PullStatement) s;
				 * final ClassVariable sig = us.getClassSignature();
				 * final List<IdentifierExpression> lhss = us.getLeft();
				 * final ProverFun inv = getClassInvariant(p, sig);
				 * 
				 * 
				 * final ProverExpr[] invArgs = new ProverExpr[1 + lhss.size()];
				 * int cnt = 0;
				 * invArgs[cnt++] = exprToProverExpr(us.getObject(), varMap);
				 * 
				 * for (IdentifierExpression lhs : lhss) {
				 * final ProverExpr lhsExpr = p.mkHornVariable("unpackRes_" +
				 * lhs + "_" + newVarNum(),
				 * getProverType(lhs.getType()));
				 * invArgs[cnt++] = lhsExpr;
				 * 
				 * final int lhsIndex =
				 * postPred.variables.indexOf(lhs.getVariable());
				 * if (lhsIndex >= 0)
				 * postVars.set(lhsIndex, lhsExpr);
				 * }
				 * 
				 * final ProverExpr invAtom = inv.mkExpr(invArgs);
				 * final ProverExpr postAtom = instPredicate(postPred,
				 * postVars);
				 * 
				 * clauses.add(p.mkHornClause(postAtom, new ProverExpr[] {
				 * preAtom, invAtom }, p.mkLiteral(true)));
				 */

			} else if (s instanceof PushStatement) {

				final PushStatement ps = (PushStatement) s;
				final ClassVariable sig = ps.getClassSignature();
				final List<Expression> rhss = ps.getRight();
				final ProverFun inv = getClassInvariant(p, sig);
				
				// check that last field is "lastpush" and that lhs and rhs lengths are equal
//				Verify.verify(sig.getAssociatedFields()[sig.getAssociatedFields().length-1]
//						.getName().equals(PushIdentifierAdder.LP), 
//						"Class is missing " + PushIdentifierAdder.LP + " field: " + sig);
				Verify.verify(sig.getAssociatedFields().length == rhss.size(), 
						"Unequal lengths: " + sig + " and " + rhss);

				final ProverExpr[] invArgs = new ProverExpr[1 + rhss.size()];
				int cnt = 0;
				invArgs[cnt++] = exprToProverExpr(ps.getObject(), varMap);

				for (Expression rhs : rhss)
					invArgs[cnt++] = exprToProverExpr(rhs, varMap);

				final ProverExpr invAtom = inv.mkExpr(invArgs);

				clauses.add(p.mkHornClause(invAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

				final ProverExpr postAtom = instPredicate(postPred, postVars);

				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

			} else {
				throw new RuntimeException("Statement type " + s + " not implemented!");
			}
		}

		private ProverExpr exprToProverExpr(Expression e, Map<Variable, ProverExpr> varMap) {
			if (e instanceof IdentifierExpression) {
				Variable var = ((IdentifierExpression) e).getVariable();
				if (var instanceof ClassVariable) {
					return p.mkLiteral(typeIds.get(var));
				} else {
					ProverExpr res = varMap.get(var);
					if (res == null)
						throw new RuntimeException("Could not resolve variable " + e);
					return res;
				}
			} else if (e instanceof IntegerLiteral) {
				return p.mkLiteral(BigInteger.valueOf(((IntegerLiteral) e).getValue()));
			} else if (e instanceof BinaryExpression) {
				final BinaryExpression be = (BinaryExpression) e;
				final ProverExpr left = exprToProverExpr(be.getLeft(), varMap);
				final ProverExpr right = exprToProverExpr(be.getRight(), varMap);

				// TODO: the following choices encode Java semantics
				// of various operators; need a good schema to choose
				// how precise the encoding should be (probably
				// configurable)
				switch (be.getOp()) {
				case Plus:
					return p.mkPlus(left, right);
				case Minus:
					return p.mkMinus(left, right);
				case Mul:
					return p.mkMult(left, right);
				case Div:
					return p.mkTDiv(left, right);
				case Mod:
					return p.mkTMod(left, right);
				case Eq:
					return p.mkEq(left, right);
				case Ne:
					return p.mkNot(p.mkEq(left, right));
				case Gt:
					return p.mkGt(left, right);
				case Ge:
					return p.mkGeq(left, right);
				case Lt:
					return p.mkLt(left, right);
				case Le:
					return p.mkLeq(left, right);
				case PoLeq:
					if ((be.getRight() instanceof IdentifierExpression)
							&& (((IdentifierExpression) be.getRight()).getVariable() instanceof ClassVariable)) {

						final ClassVariable var = (ClassVariable) ((IdentifierExpression) be.getRight()).getVariable();

						final Set<ClassVariable> subTypes = GraphUtil
								.getForwardReachableVertices(program.getTypeGraph(), var);

						ProverExpr disj = p.mkLiteral(false);
						for (ClassVariable st : subTypes) {
							disj = p.mkOr(disj, p.mkEq(left, p.mkLiteral(typeIds.get(st))));
						}

						return disj;
					} else {
						throw new RuntimeException("instanceof is only supported for concrete types");
					}
				case And:
					return p.mkAnd(left, right);
				case Or:
					return p.mkOr(left, right);
				case Implies:
					return p.mkImplies(left, right);
				case Shl:
				case Shr:
				case BAnd:
				case BOr:
				case Xor:
					return p.mkVariable("HACK_FreeVar" + hack_counter++, p.getIntType());
				// Verify.verify(left.getType()==p.getIntType() &&
				// right.getType()==p.getIntType());
				// return binopFun.mkExpr(new ProverExpr[]{left, right});
				default: {
					throw new RuntimeException("Not implemented for " + be.getOp());
				}
				}
			} else if (e instanceof UnaryExpression) {
				final UnaryExpression ue = (UnaryExpression) e;
				final ProverExpr subExpr = exprToProverExpr(ue.getExpression(), varMap);

				// TODO: the following choices encode Java semantics
				// of various operators; need a good schema to choose
				// how precise the encoding should be (probably
				// configurable)
				switch (ue.getOp()) {
				case Neg:
					return p.mkNeg(subExpr);
				case LNot:
					return p.mkNot(subExpr);
				}
			} else if (e instanceof IteExpression) {
				final IteExpression ie = (IteExpression) e;
				final ProverExpr condExpr = exprToProverExpr(ie.getCondition(), varMap);
				final ProverExpr thenExpr = exprToProverExpr(ie.getThenExpr(), varMap);
				final ProverExpr elseExpr = exprToProverExpr(ie.getElseExpr(), varMap);
				return p.mkIte(condExpr, thenExpr, elseExpr);
			} else if (e instanceof BooleanLiteral) {
				return p.mkLiteral(((BooleanLiteral) e).getValue());
			}
			throw new RuntimeException("Expression type " + e + " not implemented!");
		}
	}

	private int hack_counter = 0;

	////////////////////////////////////////////////////////////////////////////
	protected InterProceduralPullPushOrdering ppOrdering;

	public boolean checkProgram(Program program) {

		/*
		 * The checker assumes that the program is closed. I.e., no
		 * uninitialized variables
		 * are being used (e.g. in main(String[] args), the array args[] is not
		 * initialized).
		 * These need to be initialized to an unknown value. Further, we need to
		 * add non-det
		 * assignments to unknown library calls and add some other stuff.
		 */
//		CfgStubber stubber = new CfgStubber();
//		stubber.stubUnboundFieldsAndMethods(program);
		// TODO **********************
		/*
		 * We have to build that up elsewhere. The problem is that we currently
		 * modify the cfg while generating the
		 * Horn clauses. For this to work to correctly, we need to create a
		 * final version of the cfg first; then
		 * build up this ordering; and finally create the Horn clauses.
		 */
		Verify.verify(program.getEntryPoints().length == 1,
				"Currently, we only support programs with one entry point. However, its easy to extend that.");
		ppOrdering = new InterProceduralPullPushOrdering(program.getEntryPoints()[0]);
		// *************************

		Log.info("Starting verification for " + program.getEntryPoints().length + " entry points.");

		Prover p = factory.spawn();
		p.setHornLogic(true);
		ProverResult result = ProverResult.Unknown;

		try {
			Log.info("Building type hierarchy");

			for (ClassVariable var : program.getTypeGraph().vertexSet()) {
				typeIds.put(var, typeIds.size());

			}

			Log.info("Generating method contracts");

			for (Method method : program.getMethods()) {
				final List<Variable> inParams = new ArrayList<Variable>();
				inParams.addAll(method.getInParams());
				final List<Variable> postParams = new ArrayList<Variable>();
				postParams.addAll(method.getInParams());
				if (!method.getOutParam().isEmpty()) {
					postParams.addAll(method.getOutParam());
				} else if (!method.getReturnType().isEmpty()) {
					int ctr = 0;
					for (Type tp : method.getReturnType()) {
						postParams.add(new Variable("resultVar" + (ctr++), tp));
					}
				}

				final ProverFun prePred = freshHornPredicate(p, method.getMethodName() + "_pre", inParams);
				final ProverFun postPred = freshHornPredicate(p, method.getMethodName() + "_post", postParams);

				Log.debug("method: " + method.getMethodName());
				Log.debug("pre: " + inParams);
				Log.debug("post: " + postParams);

				final HornPredicate pre = new HornPredicate(method.getMethodName() + "_pre", inParams, prePred);
				final HornPredicate post = new HornPredicate(method.getMethodName() + "_post", postParams, postPred);

				methodContracts.put(method.getMethodName(), new MethodContract(method, pre, post));
			}

			Log.info("Encoding methods as Horn clauses");

			List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

			for (Method method : program.getMethods()) {
//				System.err.println(method);

				final MethodEncoder encoder = new MethodEncoder(p, program, method);
				encoder.encode();
				clauses.addAll(encoder.clauses);
			}

                        int verifCount = 0;
			for (Method method : program.getEntryPoints()) {
				Log.info("\tVerification from entry " + method.getMethodName());
				// Log.info("\t Number of clauses: " + clauses.size());
				p.push();

				// add an entry clause from the preconditions
				final HornPredicate entryPred = methodContracts.get(method.getMethodName()).precondition;
				final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
				final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
				createVarMap(p, entryPred.variables, entryVars, varMap);

				final ProverExpr entryAtom = entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));
                                final ProverHornClause entryClause =
                                    p.mkHornClause(entryAtom, new ProverExpr[0], p.mkLiteral(true));

                                clauses.add(entryClause);

				// print Horn clauses
				if (jayhorn.Options.v().getPrintHorn()) {
                                    // Log.info("\tNumber of clauses: " +
                                    // encoder.clauses.size());
                                    for (ProverHornClause clause : clauses)
                                        Log.info("\t\t" + clause);
				}

                                // write Horn clauses to file
                                Hornify.hornToSMTLIBFile(clauses, verifCount, p);
                                Hornify.hornToFile(clauses, verifCount);

				for (ProverHornClause clause : clauses)
					p.addAssertion(clause);

				// result = p.checkSat(true);
				if (jayhorn.Options.v().getTimeout() > 0) {
					int timeoutInMsec = (int) TimeUnit.SECONDS.toMillis(jayhorn.Options.v().getTimeout());
					p.checkSat(false);

					result = p.getResult(timeoutInMsec);
				} else {
					result = p.checkSat(true);
				}

                                clauses.remove(clauses.size() - 1);

				p.pop();

                                ++verifCount;
			}
		} catch (Throwable t) {
			t.printStackTrace();
			throw new RuntimeException(t);
		} finally {
			p.shutdown();
		}
		// Log.info("\tResult: " + result);
		if (result == ProverResult.Sat) {
			return true;
		} else if (result == ProverResult.Unsat) {
			return false;
		}
		throw new RuntimeException("Verification failed with prover code " + result);
	}

	private ProverFun freshHornPredicate(Prover p, String name, List<Variable> sortedVars) {
		return genHornPredicate(p, name, sortedVars);
	}

	private ProverFun genHornPredicate(Prover p, String name, List<Variable> sortedVars) {
		final List<ProverType> types = new LinkedList<ProverType>();
		for (Variable v : sortedVars)
			types.add(getProverType(p, v.getType()));
		return p.mkHornPredicate(name, types.toArray(new ProverType[types.size()]));
	}

	/**
	 * Creates a ProverType from a Type.
	 * TODO: not fully implemented.
	 * 
	 * @param p
	 * @param t
	 * @return
	 */
	private ProverType getProverType(Prover p, Type t) {
		if (t == IntType.instance()) {
			return p.getIntType();
		}
		if (t == BoolType.instance()) {
			return p.getBooleanType();
		}
		if (t instanceof ReferenceType) {
			return p.getIntType();
		}
		if (t instanceof MapType) {
			// System.err.println("Warning: translating " + t + " as prover type
			// int");
			return p.getIntType();
		}
		throw new IllegalArgumentException("don't know what to do with " + t);
	}

	private void createVarMap(Prover p, List<Variable> cfgVars, List<ProverExpr> proverVars,
			Map<Variable, ProverExpr> varMap) {
		for (Variable v : cfgVars) {
			ProverExpr e = varMap.get(v);
			if (e == null) {
				e = p.mkHornVariable(v.getName() + "_" + newVarNum(), getProverType(p, v.getType()));
				varMap.put(v, e);
			}
			proverVars.add(e);
		}
	}

	private List<Variable> setToSortedList(Set<Variable> set) {
		List<Variable> res = new LinkedList<Variable>(set);
		if (!res.isEmpty()) {
			Collections.sort(res, new Comparator<Variable>() {
				@Override
				public int compare(final Variable object1, final Variable object2) {
					return object1.getName().compareTo(object2.getName());
				}
			});
		}
		return res;
	}
}
