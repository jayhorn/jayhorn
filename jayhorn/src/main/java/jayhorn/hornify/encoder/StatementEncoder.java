package jayhorn.hornify.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.google.common.base.Verify;

import jayhorn.Options;
import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornHelper;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.MethodContract;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverType;

import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
public class StatementEncoder {

	private final Prover p;

	private final ExpressionEncoder expEncoder;
	private final HornEncoderContext hornContext;
	
	public StatementEncoder(Prover p, ExpressionEncoder expEnc) {
		this.p = p;
		this.expEncoder = expEnc;
		this.hornContext = expEncoder.getContext();
	}

	/**
	 * A statement "s" is a transition from states described by the
	 * predicate "prePred" into states described by the predicate "postPred".
	 * That is s gets translated into at least one Horn clause:
	 * 
	 * prePred(args) && guard => postPred(args')
	 * 
	 * Here, guard is the condition for transition to be feasible. The guard is
	 * only
	 * used for assume and assert statement.
	 * 
	 * The effect of a "s" on the program state is encoded by in args'. E.g., a
	 * statement
	 * x = y+1
	 * would be encoded as follows:
	 * Assume that before and after the statement only x any y are alive.
	 * Then our prePred will be prePred(x,y) and postPred(x,y).
	 * The effect of the assignment is now encoded by updating the params in
	 * postPred:
	 * 
	 * prePred(x,y) -> postPred(y+1, y)
	 * 
	 * @param s
	 *            The statement for which we want to generate Horn clauses.
	 * @param prePred
	 *            The predicated describing the pre-state.
	 * @param postPred
	 *            The predicated describing the post-state.
	 * @return The set of Horn clauses that encodes the semantics of "s".
	 */
	public List<ProverHornClause> statementToClause(Statement s, HornPredicate prePred, HornPredicate postPred) {

		final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
		// First create the atom for prePred.
		HornHelper.hh().findOrCreateProverVar(p, prePred.variables, varMap);
		final ProverExpr preAtom = prePred.instPredicate(varMap);
		HornHelper.hh().findOrCreateProverVar(p, postPred.variables, varMap);
		
		if (s instanceof AssertStatement) {
			List<ProverHornClause> clause = assertToClause((AssertStatement) s, postPred, preAtom, varMap);
			//System.out.println("Assert " + clause);
			S2H.sh().addClause(s, clause);
			return clause;
		} else if (s instanceof AssumeStatement) {
			List<ProverHornClause> clause = assumeToClause((AssumeStatement) s, postPred, preAtom, varMap);
			//System.out.println("Assume " + clause);
			S2H.sh().addClause(s, clause);
			return clause;
		} else if (s instanceof AssignStatement) {
			List<ProverHornClause> clause = assignToClause((AssignStatement) s, postPred, preAtom, varMap);
			S2H.sh().addClause(s, clause);
			//System.out.println("Assign " + clause);
			return clause;
		} else if (s instanceof CallStatement) {

			List<ProverHornClause> clause = callToClause((CallStatement) s, postPred, preAtom, varMap);
			S2H.sh().addClause(s, clause);
			//System.out.println("Call " + clause);
			return clause;
		} else if (s instanceof PullStatement) {
			
			if (assumedPushIds == null) {
				List<ProverHornClause> clause = pullToClause((PullStatement) s, postPred, preAtom, varMap, -1);
				S2H.sh().addClause(s, clause);
				//System.out.println("Pull " + clause);
				return  clause;
			} else {
				List<ProverHornClause> clause = new ArrayList<ProverHornClause>();
				for (Long id : assumedPushIds)
					clause.addAll(pullToClause((PullStatement) s, postPred, preAtom, varMap, id));
				S2H.sh().addClause(s, clause);
				//System.out.println("Pull " + clause);
				return clause;
			}
		} else if (s instanceof PushStatement) {
			List<ProverHornClause> clause = pushToClause((PushStatement) s, postPred, preAtom, varMap);
			S2H.sh().addClause(s, clause);
			//System.out.println("Push " + clause);
			return clause;
		}

		throw new RuntimeException("Statement type " + s + " not implemented!");
	}

        List<Long> assumedPushIds = null;

    private boolean isLashpushAssumption(Statement s) {
        if (!(s instanceof AssumeStatement))
            return false;
        
        AssumeStatement as = (AssumeStatement)s;

        boolean allLastpush = true;
        for (IdentifierExpression e : as.getUseIdentifierExpressions())
            allLastpush =
                allLastpush && e.getVariable().getName().contains("lastpush");

        return allLastpush;
    }

    public void lookAhead(Statement s) {
        assumedPushIds = null;
        
        if (isLashpushAssumption(s)) {
            AssumeStatement as = (AssumeStatement)s;
            assumedPushIds = new ArrayList<Long>();

            Stack<Expression> stack = new Stack<Expression>();
            stack.push(as.getExpression());
            while (!stack.isEmpty()) {
                BinaryExpression e = (BinaryExpression)stack.pop();
                  
                if (e.getOp() == BinaryExpression.BinaryOperator.Or) {
                    stack.push(e.getLeft());
                    stack.push(e.getRight());
                } else if (e.getOp() == BinaryExpression.BinaryOperator.Eq) {
                    assumedPushIds.add(((IntegerLiteral)e.getRight()).getValue());
                }
            }
        }
    }

	/**
	 * for "assert(cond)"
	 * create two Horn clauses
	 * pre(...) && !cond -> false
	 * pre(...) -> post(...)
	 * where the first Horn clause represents the transition
	 * into the error state if the assertion doesn't hold.
	 * 
	 * @param as
	 * @param prePred
	 * @param postPred
	 * @return
	 */
	public List<ProverHornClause> assertToClause(AssertStatement as, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		final ProverExpr cond = expEncoder.exprToProverExpr(as.getExpression(), varMap); 

		final ProverExpr errorState; // For now depending on the solver we use false or a predicate
		String tag = "ErrorState@line" + as.getJavaSourceLine();
		final ProverFun errorPredicate = p.mkHornPredicate(tag, new ProverType[] {});	
		if (Options.v().getSolver().equals("spacer")){
			errorState = errorPredicate.mkExpr(new ProverExpr[]{});
			S2H.sh().setErrorState(errorState);
		}else{
			errorState = p.mkLiteral(false);
		}
		clauses.add(p.mkHornClause(errorState, new ProverExpr[] { preAtom }, p.mkNot(cond)));
		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		return clauses;
	}

	/**
	 * for "assume(cond)"
	 * create Horn clause
	 * pre(...) && cond -> post(...)
	 * 
	 * @param as
	 * @param postPred
	 * @param preAtom
	 * @param varMap
	 * @return
	 */
	public List<ProverHornClause> assumeToClause(AssumeStatement as, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		final ProverExpr cond =
                    isLashpushAssumption(as) ?
                    p.mkLiteral(true) :
                    expEncoder.exprToProverExpr(as.getExpression(), varMap);
		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, cond));
		return clauses;
	}

        private long lastpushId = -1;

	public List<ProverHornClause> assignToClause(AssignStatement as, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		Verify.verify(as.getLeft() instanceof IdentifierExpression,
				"only assignments to variables are supported, not to " + as.getLeft());
		final IdentifierExpression idLhs = (IdentifierExpression) as.getLeft();
		varMap.put(idLhs.getVariable(), expEncoder.exprToProverExpr(as.getRight(), varMap));

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

                if (idLhs.getVariable().getName().contains("lastpush") &&
                    (as.getRight() instanceof IntegerLiteral))
                  lastpushId = ((IntegerLiteral)as.getRight()).getValue();

		return clauses;
	}

	/**
	 * Translates a call statement of the form:
	 * r1, ... rN = callee(arg1, ... argM)
	 * We assume that there exists a MethodContract that
	 * has two predicates:
	 * precondtion of arity M, and postcondition of arity N+M
	 * We generate two Horn clauses: 
	 * 
	 * pre(...) -> precondition(...)
	 * postcondition(...) -> post(...)
	 * 
	 * @param cs
	 * @param postPred
	 * @param preAtom
	 * @param varMap
	 * @return
	 */
	public List<ProverHornClause> callToClause(CallStatement cs, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		final Method calledMethod = cs.getCallTarget();
		final MethodContract contract = hornContext.getMethodContract(calledMethod);

		Verify.verify(calledMethod.getInParams().size() == cs.getArguments().size()
				&& calledMethod.getInParams().size() == contract.precondition.variables.size());

		final List<Variable> receiverVars = new ArrayList<Variable>();
		for (Expression e : cs.getReceiver()) {
			receiverVars.add(((IdentifierExpression) e).getVariable());
		}
		// final List<ProverExpr> receiverExprs =
		HornHelper.hh().findOrCreateProverVar(p, receiverVars, varMap);

		final ProverExpr[] actualInParams = new ProverExpr[calledMethod.getInParams().size()];
		final ProverExpr[] actualPostParams = new ProverExpr[calledMethod.getInParams().size()
				+ calledMethod.getReturnType().size()];

		int cnt = 0;
		for (Expression e : cs.getArguments()) {
			final ProverExpr expr = expEncoder.exprToProverExpr(e, varMap);
			actualInParams[cnt] = expr;
			actualPostParams[cnt] = expr;
			++cnt;
		}

                for (int i = 0; i < calledMethod.getReturnType().size(); ++i) {
                    Type tp = calledMethod.getReturnType().get(i);
                    final ProverExpr callRes = HornHelper.hh().createVariable(p, "callRes_", tp);
                    actualPostParams[cnt++] = callRes;
                    if (i < cs.getReceiver().size()) {
                        Expression lhs = cs.getReceiver().get(i);
                        Verify.verify(lhs instanceof IdentifierExpression,
                                      "only assignments to variables are supported, not to " + lhs);
                        //update the receiver var to the expression that we use in the call pred.	
                        varMap.put( ((IdentifierExpression) lhs).getVariable(), callRes);
                    }
                }

		final ProverExpr preCondAtom = contract.precondition.predicate.mkExpr(actualInParams);
		clauses.add(p.mkHornClause(preCondAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		final ProverExpr postCondAtom = contract.postcondition.predicate.mkExpr(actualPostParams);

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		
	
		
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, postCondAtom }, p.mkLiteral(true)));

		return clauses;
	}


	/**
	 * Translates a pull statement of the form:
	 * l1, l2, ..., ln = pull(obj)
	 * into Horn clauses.
	 * First, we look up all possible subtypes of obj and get the
	 * Horn Predicate that corresponds to it's invariant. Then,
	 * for each invariant of the form:
	 * inv(x1, ... xk) with k>=n
	 * We create a Horn clause
	 * pre(l1...ln) && inv(l1,...ln, x_(n+1)...xk) -> post(l1...ln)
	 * 
	 * Not that k may be greater than n if we look at a subtype invariant
	 * of obj.
	 * 
	 * @param pull
	 * @param postPred
	 * @param preAtom
	 * @param varMap
	 * @return
	 */
	public List<ProverHornClause> pullToClause(PullStatement pull, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap,
                        long pushId) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		// get the possible (sub)types of the object used in pull.
		// TODO: find a more precise implementation.
		final Set<ClassVariable> possibleTypes = this.hornContext.ppOrdering
				.getBrutalOverapproximationOfPossibleType(pull);
		
		for (ClassVariable sig : possibleTypes) {
			// get the invariant for this subtype.
			final HornPredicate invariant = this.hornContext.lookupInvariantPredicate(sig, pushId);

			// the first argument is always the reference
                        // to the object

                        // PR: once the reference becomes a vector, we
                        // have to properly map between the elements
                        // of the vector and the fields of the object
                        varMap.put(invariant.variables.get(0),
                                   expEncoder.exprToProverExpr(pull.getObject(), varMap));

                        // introduce fresh prover variables for all
                        // the other invariant parameters, and map
                        // them to the post-state
                        for (int i = 1; i < invariant.variables.size(); i++) {
                            final ProverExpr var =
                                HornHelper.hh().createVariable(p, "pullVar_",
                                                               invariant.variables.get(i).getType());
                            varMap.put(invariant.variables.get(i), var);
                            if (i <= pull.getLeft().size())
                                varMap.put(pull.getLeft().get(i - 1).getVariable(), var);
                            /*
                             * If our current invariant is a subtype
                             * of what the orginial pull used, it
                             * might have more fields (that were added
                             * in the subtype). For this case, we have
                             * to fill up our args with fresh, unbound
                             * variables to match the number of
                             * arguments.
                             */
                        }

			// now we can instantiate the invariant.
			final ProverExpr invAtom = invariant.instPredicate(varMap);
			final ProverExpr postAtom = postPred.instPredicate(varMap);
                        final ProverHornClause clause =
                            p.mkHornClause(postAtom, new ProverExpr[] { preAtom, invAtom }, p.mkLiteral(true));
			clauses.add(clause);
		}
		return clauses;
	}

	/**
	 * Works like an assert of the invariant of the type that is
	 * being pushed.
	 * First we get the invariant HornPredicate and we instantiate it
	 * with the current object and all expressions that are being pushed.
	 * Then we generate two Horn clauses. One assertion Horn clause
	 * pre(...) -> inv(...)
	 * and one for the transition.
	 * pre(...) -> post(...)
	 * 
	 * @param ps
	 * @param postPred
	 * @param preAtom
	 * @param varMap
	 * @return
	 */
	public List<ProverHornClause> pushToClause(PushStatement ps, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		final ClassVariable sig = ps.getClassSignature();
		Verify.verify(sig.getAssociatedFields().length == ps.getRight().size(),
				"Unequal lengths: " + sig + " and " + ps.getRight() + " in " + ps);
		// get the invariant for the ClassVariable
		final HornPredicate invariant = this.hornContext.lookupInvariantPredicate(sig, lastpushId);
		final List<Expression> invariantArgs = new LinkedList<Expression>();
		invariantArgs.add(ps.getObject());
		invariantArgs.addAll(ps.getRight());
		// assign the variables of the invariant pred to the respective value.
		for (int i = 0; i < invariantArgs.size(); i++) {
			varMap.put(invariant.variables.get(i), expEncoder.exprToProverExpr(invariantArgs.get(i), varMap));
		}
		final ProverExpr invAtom = invariant.instPredicate(varMap);
		clauses.add(p.mkHornClause(invAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		return clauses;
	}

}
