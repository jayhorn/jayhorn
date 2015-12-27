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
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.ReferenceType;
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

    private static class MethodContract {
        public final Method method;
        public final HornPredicate precondition;
        public final HornPredicate postcondition;

        public MethodContract(Method method,
                              HornPredicate precondition,
                              HornPredicate postcondition) {
            this.method = method;
            this.precondition = precondition;
            this.postcondition = postcondition;
        }
    }
    
    private final ProverFactory factory = new PrincessProverFactory();

    private final Map<CfgBlock, HornPredicate> blockPredicates =
        new LinkedHashMap<CfgBlock, HornPredicate>();
    private Map<Method, MethodContract> methodContracts =
        new LinkedHashMap<Method, MethodContract>();

    ////////////////////////////////////////////////////////////////////////////

    private class MethodEncoder {
        private final Method method;
        private final MethodContract methodContract;
        private final Prover p;
        
        private final Map<CfgBlock, HornPredicate> blockPredicates =
            new LinkedHashMap<CfgBlock, HornPredicate>();
        public final List<ProverHornClause> clauses =
            new LinkedList<ProverHornClause>();

	private final List<Variable> methodPreVariables;

        public MethodEncoder(Prover p, Method method) {
            this.p = p;
            this.method = method;
            this.methodContract = methodContracts.get(method);
	    this.methodPreVariables = methodContract.precondition.variables;
        }
        
        public void encode() {
            Log.info("\tEncoding method " + method.getMethodName());
            LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();
            makeBlockPredicates(liveVariables);
            
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

		final ProverExpr entryAtom =
		    entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));
		final ProverExpr preAtom =
		    methodContract.precondition.predicate.mkExpr(preVars.toArray(new ProverExpr[0]));
            
		clauses.add(p.mkHornClause(entryAtom,
					   new ProverExpr[] { preAtom },
					   p.mkLiteral(true)));
	    }

            // translate reachable blocks
            while (!todo.isEmpty()) {
                CfgBlock current = todo.remove(0);
		Log.info("\tEncoding block " + current);

                done.add(current);
                final HornPredicate exitPred = blockToHorn(current, liveVariables);
            
                // take care of return and successors
		final List<ProverExpr> exitVars = new ArrayList<ProverExpr>();
		final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
		createVarMap(exitPred.variables, exitVars, varMap);
                    
		final ProverExpr exitAtom =
		    exitPred.predicate.mkExpr(exitVars.toArray(new ProverExpr[0]));

                if (method.outgoingEdgesOf(current).isEmpty()) {
		    // block ends with a return

		    final List<ProverExpr> postVars = new ArrayList<ProverExpr>();
		    createVarMap(methodContract.postcondition.variables, postVars, varMap);
		    
		    final ProverExpr postAtom =
			methodContract.postcondition.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));

		    clauses.add(p.mkHornClause(postAtom,
					       new ProverExpr[] { exitAtom },
					       p.mkLiteral(true)));
		    
		} else {
		    // link to the successor blocks

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
                    
                        final ProverExpr entryAtom =
                            entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));

                        clauses.add(p.mkHornClause(entryAtom,
                                                   new ProverExpr[] { exitAtom },
                                                   exitCondExpr));
                    }
                }
            }
        }

	/**
	 * Creates one HornPredicate for each block. The predicate contains the list of live variables
	 * for that block sorted by names and a predicate over the types of these variables that has
	 * the same name as the block.
	 * @param p
	 * @param method
	 */
        private void makeBlockPredicates(LiveVars<CfgBlock> liveVariables) {
            for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.liveIn.entrySet()) {
		// method arguments are always considered as live,
		// since we need them for the post-conditions
		Set<Variable> allLive = new HashSet<Variable>();
		allLive.addAll(entry.getValue());
		allLive.addAll(methodPreVariables);
                // sort the list of variables by name to make access
                // and reading easier.
                List<Variable> sortedVars = setToSortedList(allLive);
                String name = entry.getKey().getLabel();
                ProverFun pred = freshHornPredicate(name, sortedVars);
                blockPredicates.put(entry.getKey(), new HornPredicate(name, sortedVars, pred));
            }
        }

        private ProverFun freshHornPredicate(String name,
                                             List<Variable> sortedVars) {
            return Checker.this.freshHornPredicate(p, name, sortedVars);
        }

	/**
	 * Creates a ProverType from a Type.
	 * TODO: not fully implemented.
	 * @param p
	 * @param t
	 * @return
	 */
	private ProverType getProverType(Type t) {
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
                System.err.println("Warning: translating " + t + " as prover type int");
                return p.getIntType();
            }
            throw new IllegalArgumentException("don't know what to do with " + t);
	}

        private HornPredicate blockToHorn(CfgBlock block,
                                          LiveVars<CfgBlock> liveVariables) {
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
		    interVars[interVars.length - 1]
			.addAll(edge.getLabel().get().getUseVariables());
	    interVars[interVars.length - 1].addAll(methodPreVariables);

            for (int i = interVars.length - 1; i > 0; --i) {
                final Statement s = block.getStatements().get(i);
                interVars[i - 1] = new HashSet<Variable> ();
                interVars[i - 1].addAll(interVars[i]);
                interVars[i - 1].removeAll(s.getDefVariables());
                interVars[i - 1].addAll(s.getUseVariables());
            }

            final String initName = initPred.name;
            HornPredicate prePred = initPred;
            int counter = 0;

            for (Statement s : block.getStatements()) {
                final String postName = initName + "_" + (++counter);
		final List<Variable> interVarList =
		    setToSortedList(interVars[counter - 1]);
                final HornPredicate postPred =
                    new HornPredicate(postName,
                                      interVarList,
                                      freshHornPredicate(postName, interVarList));
                statementToClause(s, prePred, postPred);
                prePred = postPred;
            }

            return prePred;
        }

        private void createVarMap(List<Variable> cfgVars,
				  List<ProverExpr> proverVars,
                                  Map<Variable, ProverExpr> varMap) {
            Checker.this.createVarMap(p, cfgVars, proverVars, varMap);
        }
	
        private void statementToClause(Statement s,
                                       HornPredicate prePred,
                                       HornPredicate postPred) {
            final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();

            final List<ProverExpr> preVars = new ArrayList<ProverExpr>();
            createVarMap(prePred.variables, preVars, varMap);

            final List<ProverExpr> postVars = new ArrayList<ProverExpr>();
            createVarMap(postPred.variables, postVars, varMap);
        
            final ProverExpr preAtom =
                prePred.predicate.mkExpr(preVars.toArray(new ProverExpr[0]));

            if (s instanceof AssertStatement) {

                final AssertStatement as = (AssertStatement)s;
                final ProverExpr cond = exprToProverExpr(as.getExpression(), varMap);

                clauses.add(p.mkHornClause(p.mkLiteral(false),
                                           new ProverExpr[] { preAtom },
                                           p.mkNot(cond)));

                final ProverExpr postAtom =
                    postPred.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));
            
                clauses.add(p.mkHornClause(postAtom,
                                           new ProverExpr[] { preAtom },
                                           p.mkLiteral(true)));

            } else if (s instanceof AssumeStatement) {

                throw new RuntimeException("Statement type " + s + " not implemented!");

            } else if (s instanceof AssignStatement) {

                final AssignStatement as = (AssignStatement)s;
                final Expression lhs = as.getLeft();

                if (lhs instanceof IdentifierExpression) {
                    final IdentifierExpression idLhs = (IdentifierExpression)lhs;
		    final int lhsIndex = postPred.variables.indexOf(idLhs.getVariable());
                    if (lhsIndex >= 0)
			postVars.set(lhsIndex, exprToProverExpr(as.getRight(), varMap));
                } else {
                    throw new RuntimeException
                        ("only assignments to variables are supported, not to " + lhs);
                }

                final ProverExpr postAtom =
                    postPred.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));
            
                clauses.add(p.mkHornClause(postAtom,
                                           new ProverExpr[] { preAtom },
                                           p.mkLiteral(true)));

            } else if (s instanceof CallStatement) {

                final CallStatement cs = (CallStatement)s;
		final Method calledMethod = cs.getCallTarget();
		final MethodContract contract = methodContracts.get(calledMethod);

		if (contract == null)
		    throw new RuntimeException("Invoked method " +
					       calledMethod.getMethodName() +
					       " is unknown");

		System.out.println(calledMethod.getInParams());
		System.out.println(cs.getReceiver());
		System.out.println(cs.getArguments());

		assert(calledMethod.getInParams().size() ==
		       cs.getArguments().size());
		assert(calledMethod.getOutParams().size() == ((cs.getReceiver().isPresent())?1:0));

		final List<Variable> receiverVars = new ArrayList<Variable>();
		if (cs.getReceiver().isPresent())
		    receiverVars.add(((IdentifierExpression)cs.getReceiver().get()).getVariable());

		final List<ProverExpr> receiverExprs = new ArrayList<ProverExpr>();
		createVarMap(receiverVars, receiverExprs, varMap);

		final ProverExpr[] actualInParams =
		    new ProverExpr[calledMethod.getInParams().size()];
		final ProverExpr[] actualPostParams =
		    new ProverExpr[calledMethod.getInParams().size() +
				   calledMethod.getOutParams().size()];

		int cnt = 0;
		for (Expression e : cs.getArguments()) {
		    final ProverExpr expr = exprToProverExpr(e, varMap);
		    actualInParams[cnt] = expr;
		    actualPostParams[cnt] = expr;
		    ++cnt;
		}

		if (cs.getReceiver().isPresent()) {
		    actualPostParams[cnt++] = exprToProverExpr(cs.getReceiver().get(), varMap);
		}

		final ProverExpr preCondAtom =
		    contract.precondition.predicate.mkExpr(actualInParams);
                clauses.add(p.mkHornClause(preCondAtom,
                                           new ProverExpr[] { preAtom },
                                           p.mkLiteral(true)));

		final ProverExpr postCondAtom =
		    contract.postcondition.predicate.mkExpr(actualPostParams);

                final ProverExpr postAtom =
                    postPred.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));

                clauses.add(p.mkHornClause(postAtom,
                                           new ProverExpr[] { preAtom, postCondAtom },
                                           p.mkLiteral(true)));

            } else {
                throw new RuntimeException("Statement type " + s + " not implemented!");
            }
        }
	
        private ProverExpr exprToProverExpr(Expression e,
                                            Map<Variable, ProverExpr> varMap) {
            if (e instanceof IdentifierExpression) {
                return varMap.get(((IdentifierExpression)e).getVariable());
            } else if (e instanceof IntegerLiteral) {
                return p.mkLiteral(BigInteger.valueOf(((IntegerLiteral)e).getValue()));
            } else if (e instanceof BinaryExpression) {
                final BinaryExpression be = (BinaryExpression)e;
                final ProverExpr left = exprToProverExpr(be.getLeft(), varMap);
                final ProverExpr right = exprToProverExpr(be.getRight(), varMap);

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
                final ProverExpr subExpr = exprToProverExpr(ue.getExpression(), varMap);

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
                final ProverExpr condExpr = exprToProverExpr(ie.getCondition(), varMap);
                final ProverExpr thenExpr = exprToProverExpr(ie.getThenExpr(), varMap);
                final ProverExpr elseExpr = exprToProverExpr(ie.getElseExpr(), varMap);
                return p.mkIte(condExpr, thenExpr, elseExpr);
            } else if (e instanceof BooleanLiteral) {
                return p.mkLiteral(((BooleanLiteral)e).getValue());
	    }
	    throw new RuntimeException("Expression type " + e + " not implemented!");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    
    public boolean checkProgram(Program program) {
        Log.info("Starting verification for " + program.getEntryPoints().length + " entry points.");

        Prover p = factory.spawn();
        p.setHornLogic(true);

        try {
            Log.info("Generating method contracts");

            for (Method method : program.getMethods()) {
                final List<Variable> inParams = new ArrayList<Variable>();
                inParams.addAll(method.getInParams());
                final List<Variable> postParams = new ArrayList<Variable>();
                postParams.addAll(method.getInParams());
                postParams.addAll(method.getOutParams());
                
                final ProverFun prePred =
                    freshHornPredicate(p, method.getMethodName() + "_pre", inParams);
                final ProverFun postPred =
                    freshHornPredicate(p, method.getMethodName() + "_post", postParams);
		Log.info("pre: " + inParams);
		Log.info("post: " + postParams);
                final HornPredicate pre =
                    new HornPredicate (method.getMethodName() + "_pre", inParams, prePred);
                final HornPredicate post =
                    new HornPredicate (method.getMethodName() + "_post", postParams, postPred);

                methodContracts.put(method, new MethodContract(method, pre, post));
            }
        
            Log.info("Encoding methods as Horn clauses");

	    List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

            for (Method method : program.getMethods()) {
		
		// hack
		if (method.getMethodName().contains("init"))
		    continue;

		final MethodEncoder encoder = new MethodEncoder(p, method);
		encoder.encode();
		clauses.addAll(encoder.clauses);
		
		Log.info("\tNumber of clauses:  " + encoder.clauses.size());
		for (ProverHornClause clause : encoder.clauses)
		    Log.info("\t\t" + clause);
	    }

            for (Method method : program.getEntryPoints()) {
		Log.info("\tVerification from entry " + method.getMethodName());

		p.push();
		for (ProverHornClause clause : clauses)
		    p.addAssertion(clause);

		// add an entry clause from the preconditions
		final HornPredicate entryPred = methodContracts.get(method).precondition;
		final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
		final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
		createVarMap(p, entryPred.variables, entryVars, varMap);
            
		final ProverExpr entryAtom =
		    entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));
            
		p.addAssertion(p.mkHornClause(entryAtom,
					      new ProverExpr[0],
					      p.mkLiteral(true)));

		Log.info("\tResult:  " + p.checkSat(true));
		
		p.pop();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            p.shutdown();
        }

        return true;
    }

    private void checkEntryPoint(Prover p, Program program, Method method) {
        Log.info("\tVerification from entry " + method.getMethodName());
        p.push();
        LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();

        makeBlockPredicates(p, liveVariables);

        List<CfgBlock> todo = new LinkedList<CfgBlock>();
        todo.add(method.getSource());
        Set<CfgBlock> done = new HashSet<CfgBlock>();
        List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

        {
            // add an entry clause
            final HornPredicate entryPred = blockPredicates.get(method.getSource());
            final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
            final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
            createVarMap(p, entryPred.variables, entryVars, varMap);
            
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
            final HornPredicate exitPred = blockToHorn(p, current, clauses, liveVariables);
            
            // take care of successors
            if (!method.outgoingEdgesOf(current).isEmpty()) {
                final List<ProverExpr> exitVars = new ArrayList<ProverExpr>();
                final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
                createVarMap(p, exitPred.variables, exitVars, varMap);

                final ProverExpr exitAtom =
                    exitPred.predicate.mkExpr(exitVars.toArray(new ProverExpr[0]));

                for (CfgEdge edge : method.outgoingEdgesOf(current)) {
                    CfgBlock succ = method.getEdgeTarget(edge);
                    if (!todo.contains(succ) && !done.contains(succ))
                        todo.add(succ);

                    final ProverExpr exitCondExpr;
                    if (edge.getLabel().isPresent())
                        exitCondExpr = exprToProverExpr(p, edge.getLabel().get(), varMap);
                    else
                        exitCondExpr = p.mkLiteral(true);

                    final HornPredicate entryPred = blockPredicates.get(succ);
                    final List<ProverExpr> entryVars = new ArrayList<ProverExpr>();
                    createVarMap(p, entryPred.variables, entryVars, varMap);
                    
                    final ProverExpr entryAtom =
                        entryPred.predicate.mkExpr(entryVars.toArray(new ProverExpr[0]));

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
    private void makeBlockPredicates(Prover p, LiveVars<CfgBlock> liveVariables) {
        for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.liveIn.entrySet()) {
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
                if (t instanceof ReferenceType) {
                    return p.getIntType();
                }
                if (t instanceof MapType) {
                    System.err.println("Warning: translating " + t + " as prover type int");
                    return p.getIntType();
                }
                throw new IllegalArgumentException("don't know what to do with " + t);
	}

    private HornPredicate blockToHorn(Prover p, CfgBlock block,
                                      List<ProverHornClause> clauseBuffer,
                                      LiveVars<CfgBlock> liveVariables) {
        //        System.out.println("to horn: " + block);

        final HornPredicate initPred = blockPredicates.get(block);

        if (block.getStatements().isEmpty())
            return initPred;
            
        final Set<Variable> liveOutVars = liveVariables.liveOut.get(block);

        final Set<Variable>[] interVars = new Set[block.getStatements().size()];
        interVars[interVars.length - 1] = liveOutVars;

        for (int i = interVars.length - 1; i > 0; --i) {
            final Statement s = block.getStatements().get(i);
            interVars[i - 1] = new HashSet<Variable> ();
            interVars[i - 1].addAll(interVars[i]);
            interVars[i - 1].removeAll(s.getDefVariables());
            interVars[i - 1].addAll(s.getUseVariables());
        }

        final String initName = initPred.name;
        HornPredicate prePred = initPred;
        int counter = 0;

        for (Statement s : block.getStatements()) {
            final String postName = initName + "_" + (++counter);
            final HornPredicate postPred =
                new HornPredicate(postName,
                                  initPred.variables,
                                  freshHornPredicate(p, postName, initPred.variables));
            statementToClause(p, s, prePred, postPred, clauseBuffer);
            prePred = postPred;
        }

        return prePred;
    }

    private void createVarMap(Prover p,
                              List<Variable> cfgVars,
                              List<ProverExpr> proverVars,
                              Map<Variable, ProverExpr> varMap) {
        for (Variable v : cfgVars) {
            ProverExpr e = varMap.get(v);
            if (e == null) {
                e = p.mkHornVariable(v.getName(), getProverType(p, v.getType()));
                varMap.put(v, e);
            }
            proverVars.add(e);
        }
    }
	
    private void statementToClause(Prover p, Statement s,
                                   HornPredicate prePred,
                                   HornPredicate postPred,
                                   List<ProverHornClause> clauseBuffer) {
        final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();

        final List<ProverExpr> preVars = new ArrayList<ProverExpr>();
        createVarMap(p, prePred.variables, preVars, varMap);

        final List<ProverExpr> postVars = new ArrayList<ProverExpr>();
        createVarMap(p, postPred.variables, postVars, varMap);
        
        final ProverExpr preAtom =
            prePred.predicate.mkExpr(preVars.toArray(new ProverExpr[0]));

        if (s instanceof AssertStatement) {
            final AssertStatement as = (AssertStatement)s;
            final ProverExpr cond = exprToProverExpr(p, as.getExpression(), varMap);

            clauseBuffer.add(p.mkHornClause(p.mkLiteral(false),
                                            new ProverExpr[] { preAtom },
                                            p.mkNot(cond)));

            final ProverExpr postAtom =
                postPred.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));
            
            clauseBuffer.add(p.mkHornClause(postAtom,
                                            new ProverExpr[] { preAtom },
                                            p.mkLiteral(true)));
        } else if (s instanceof AssumeStatement) {
            throw new RuntimeException("Statement type " + s + " not implemented!");
        } else if (s instanceof AssignStatement) {
            final AssignStatement as = (AssignStatement)s;
            final Expression lhs = as.getLeft();
            final int lhsIndex;

            if (lhs instanceof IdentifierExpression) {
                final IdentifierExpression idLhs = (IdentifierExpression)lhs;
                lhsIndex = postPred.variables.indexOf(idLhs.getVariable());
                if (lhsIndex < 0)
                    throw new RuntimeException
                        ("left-hand side " + lhs + " could not be resolved");
            } else {
                throw new RuntimeException
                    ("only assignments to variables are supported, not to " + lhs);
            }

            postVars.set(lhsIndex, exprToProverExpr(p, as.getRight(), varMap));

            final ProverExpr postAtom =
                postPred.predicate.mkExpr(postVars.toArray(new ProverExpr[0]));
            
            clauseBuffer.add(p.mkHornClause(postAtom,
                                            new ProverExpr[] { preAtom },
                                            p.mkLiteral(true)));
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
