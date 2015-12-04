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

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverType;
import jayhorn.solver.princess.PrincessProverFactory;
import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
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
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.Type;

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
    }

	private final ProverFactory factory = new PrincessProverFactory();
	private Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();

	public boolean checkProgram(Program program) {
		Log.info("Starting verification for " + program.getEntryPoints().length + " entry points.");

		for (Method method : program.getEntryPoints()) {

			Prover p = factory.spawn();
			p.setHornLogic(true);
			try {
				checkEntryPoint(p, program, method); // TODO give this one a
														// return value.
			} catch (Throwable t) {
				t.printStackTrace();
			} finally {
				p.shutdown();
			}
		}

		return true;
	}

    private void checkEntryPoint(Prover p, Program program, Method method) {
        Log.info("\tVerification from entry " + method.getMethodName());
        p.push();

        makeBlockPredicates(p, method);

        List<CfgBlock> todo = new LinkedList<CfgBlock>();
        todo.add(method.getSource());
        Set<CfgBlock> done = new HashSet<CfgBlock>();
        List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

        {
            // add an entry clause
            final HornPredicate entryPred = blockPredicates.get(method.getSource());
            final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
            final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
            createVarMap(p, entryPred, entryVars, varMap);
            
            final ProverExpr entryAtom =
                entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));
            
            clauses.add(p.mkHornClause(entryAtom,
                                       new ProverExpr[0],
                                       p.mkLiteral(true)));
        }

        // translate reachable blocks
        while (!todo.isEmpty()) {
            CfgBlock current = todo.remove(0);
            done.add(current);
            final HornPredicate exitPred = blockToHorn(p, current, clauses);

            
            // take care of successors
            if (!method.outgoingEdgesOf(current).isEmpty()) {
                final List<ProverExpr> exitVars = new ArrayList<ProverExpr>();
                final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
                createVarMap(p, exitPred, exitVars, varMap);

                final ProverExpr exitAtom =
                    exitPred.predicate.mkExpr(exitVars.toArray(new ProverExpr[0]));

                for (CfgEdge edge : method.outgoingEdgesOf(current)) {
                	CfgBlock succ = method.getEdgeTarget(edge);
                    if (!todo.contains(succ) && !done.contains(succ))
                        todo.add(succ);

                    final Expression exitCond = edge.getLabel().get();
                    final ProverExpr exitCondExpr;
                    if (exitCond == null)
                        exitCondExpr = p.mkLiteral(true);
                    else
                        exitCondExpr = exprToProverExpr(p, exitCond, varMap);
                    
                    // assume that the live entry variable are the same as the exit variables
                    final ProverExpr entryAtom =
                        blockPredicates.get(succ).predicate
                                       .mkExpr(exitVars.toArray(new ProverExpr[0]));

                    clauses.add(p.mkHornClause(entryAtom,
                                               new ProverExpr[] { exitAtom },
                                               exitCondExpr));
                }
            }
        }

        // for the time being, just check whether all clauses together are satisfiable
        Log.info("\tNumber of clauses:  " + clauses.size());

        for (ProverHornClause clause : clauses) {
            Log.info("\t\t" + clause);
            p.addAssertion(clause);
        }

        Log.info("\tResult:  " + p.checkSat(true));

        p.pop();
    }

	/**
	 * Creates one HornPredicate for each block. The predicate contains the list of live variables
	 * for that block sorted by names and a predicate over the types of these variables that has
	 * the same name as the block.
	 * @param p
	 * @param method
	 */
    private void makeBlockPredicates(Prover p, Method method) {
        LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();
        //Todo is this live in or live out
        for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.liveOut.entrySet()) {
            // First sort the list of variables by name to make access and
            // reading easier.
            List<Variable> sortedVars = setToSortedList(entry.getValue());
            String name = entry.getKey().getLabel();
            ProverFun pred = freshHornPredicate(p, name, sortedVars);
            blockPredicates.put(entry.getKey(), new HornPredicate(name, sortedVars, pred));
        }
    }

    private ProverFun freshHornPredicate(Prover p,
                                         String name,
                                         List<Variable> sortedVars) {
        final List<ProverType> types = new LinkedList<ProverType>();
        for (Variable v : sortedVars)
            types.add(getProverType(p, v.getType()));
        return p.mkHornPredicate(name,
                                 types.toArray(new ProverType[types.size()]));
    }

	/**
	 * Creates a ProverType from a Type.
	 * TODO: not fully implemented.
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
                throw new IllegalArgumentException("don't know what to do with " + t);
	}

    private HornPredicate blockToHorn(Prover p, CfgBlock block,
                                      List<ProverHornClause> clauseBuffer) {
        final HornPredicate initPred = blockPredicates.get(block);
        final String initName = initPred.name;
        HornPredicate prePred = initPred;
        int counter = 0;

        for (Statement s : block.getStatements()) {
            final String postName = initName + "_" + (++counter);
            final HornPredicate postPred =
                new HornPredicate(postName,
                                  initPred.variables,
                                  freshHornPredicate(p, postName, initPred.variables));
            clauseBuffer.add(statementToClause(p, s, prePred, postPred));
            prePred = postPred;
        }

        return prePred;
    }

    private void createVarMap(Prover p,
                              HornPredicate pred,
                              List<ProverExpr> vars,
                              Map<Variable, ProverExpr> varMap) {
        for (Variable v : pred.variables) {
            final ProverExpr e = p.mkHornVariable(v.getName(), getProverType(p, v.getType()));
            vars.add(e);
            varMap.put(v, e);
        }
    }
	
    private ProverHornClause statementToClause(Prover p, Statement s,
                                               HornPredicate prePred,
                                               HornPredicate postPred) {
        final List<ProverExpr> preVars = new ArrayList<ProverExpr>();
        final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
        createVarMap(p, prePred, preVars, varMap);

        final ProverExpr preAtom =
            prePred.predicate.mkExpr(preVars.toArray(new ProverExpr[0]));

        if (s instanceof AssertStatement) {
            final AssertStatement as = (AssertStatement)s;
            final ProverExpr cond = exprToProverExpr(p, as.getExpression(), varMap);

            return p.mkHornClause(p.mkLiteral(false),
                                  new ProverExpr[] { preAtom },
                                  p.mkNot(cond));
        } else if (s instanceof AssumeStatement) {
            throw new RuntimeException("Statement type " + s + " not implemented!");
        } else if (s instanceof AssignStatement) {
            final AssignStatement as = (AssignStatement)s;
            final Expression lhs = as.getLeft();
            final int lhsIndex;

            if (lhs instanceof IdentifierExpression) {
                final IdentifierExpression idLhs = (IdentifierExpression)lhs;
                lhsIndex = prePred.variables.indexOf(idLhs.getVariable());
                if (lhsIndex < 0)
                    throw new RuntimeException
                        ("left-hand side " + lhs + " could not be resolved");
            } else {
                throw new RuntimeException
                    ("only assignments to variables are supported, not to " + lhs);
            }

            final List<ProverExpr> postVars = new ArrayList<ProverExpr>();
            postVars.addAll(preVars);
            postVars.set(lhsIndex, exprToProverExpr(p, as.getRight(), varMap));

            final ProverExpr postAtom =
                postPred.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));
            
            return p.mkHornClause(postAtom,
                                  new ProverExpr[] { preAtom },
                                  p.mkLiteral(true));
        } else if (s instanceof CallStatement) {
            throw new RuntimeException("Statement type " + s + " not implemented!");
        } else {
            throw new RuntimeException("Statement type " + s + " not implemented!");
        }
    }
	
    private ProverExpr exprToProverExpr(Prover p, Expression e,
                                        Map<Variable, ProverExpr> varMap) {
        if (e instanceof IdentifierExpression) {
            return varMap.get(((IdentifierExpression)e).getVariable());
        } else if (e instanceof IntegerLiteral) {
            return p.mkLiteral(BigInteger.valueOf(((IntegerLiteral)e).getValue()));
        } else if (e instanceof BinaryExpression) {
            final BinaryExpression be = (BinaryExpression)e;
            final ProverExpr left = exprToProverExpr(p, be.getLeft(), varMap);
            final ProverExpr right = exprToProverExpr(p, be.getRight(), varMap);

            // TODO: the following choices encode Java semantics of various
            // operators; need a good schema to choose how precise the encoding
            // should be (probably configurable)
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
                
            default: {
                throw new RuntimeException("Not implemented for " + be.getOp());
            }
            }
        } else if (e instanceof UnaryExpression) {
            final UnaryExpression ue = (UnaryExpression)e;
            final ProverExpr subExpr = exprToProverExpr(p, ue.getExpression(), varMap);

            // TODO: the following choices encode Java semantics of various
            // operators; need a good schema to choose how precise the encoding
            // should be (probably configurable)
            switch (ue.getOp()) {
            case Neg:
                return p.mkNeg(subExpr);
            case LNot:
                return p.mkNot(subExpr);
            }
        } else if (e instanceof IteExpression) {
            final IteExpression ie = (IteExpression)e;
            final ProverExpr condExpr = exprToProverExpr(p, ie.getCondition(), varMap);
            final ProverExpr thenExpr = exprToProverExpr(p, ie.getThenExpr(), varMap);
            final ProverExpr elseExpr = exprToProverExpr(p, ie.getElseExpr(), varMap);
            return p.mkIte(condExpr, thenExpr, elseExpr);
        }
        throw new RuntimeException("Expression type " + e + " not implemented!");
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
