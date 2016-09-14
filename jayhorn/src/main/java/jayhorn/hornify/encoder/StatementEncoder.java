package jayhorn.hornify.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornHelper;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.MethodContract;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
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
			return assertToClause((AssertStatement) s, postPred, preAtom, varMap);
		} else if (s instanceof AssumeStatement) {
			return assumeToClause((AssumeStatement) s, postPred, preAtom, varMap);
		} else if (s instanceof AssignStatement) {
			return assignToClause((AssignStatement) s, postPred, preAtom, varMap);
		} else if (s instanceof CallStatement) {
			return callToClause((CallStatement) s, postPred, preAtom, varMap);
		} else if (s instanceof PullStatement) {
			return pullToClause((PullStatement) s, postPred, preAtom, varMap);
		} else if (s instanceof PushStatement) {
			return pushToClause((PushStatement) s, postPred, preAtom, varMap);
		}

		throw new RuntimeException("Statement type " + s + " not implemented!");
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
		clauses.add(p.mkHornClause(p.mkLiteral(false), new ProverExpr[] { preAtom }, p.mkNot(cond)));
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
		final ProverExpr cond = expEncoder.exprToProverExpr(as.getExpression(), varMap);
		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, cond));
		return clauses;
	}

	public List<ProverHornClause> assignToClause(AssignStatement as, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		Verify.verify(as.getLeft() instanceof IdentifierExpression,
				"only assignments to variables are supported, not to " + as.getLeft());
		final IdentifierExpression idLhs = (IdentifierExpression) as.getLeft();
		varMap.put(idLhs.getVariable(), expEncoder.exprToProverExpr(as.getRight(), varMap));

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		return clauses;
	}

	public List<ProverHornClause> callToClause(CallStatement cs, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		final Method calledMethod = cs.getCallTarget();
		final MethodContract contract = hornContext.getMethodContract(calledMethod);

		Verify.verify(calledMethod.getInParams().size() == cs.getArguments().size()
				&& calledMethod.getInParams().size() == contract.precondition.variables.size());
//		Verify.verify(!cs.getReceiver().isEmpty() || calledMethod.getReturnType().isEmpty());

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

		if (!cs.getReceiver().isEmpty()) {
			for (Expression lhs : cs.getReceiver()) {

				final ProverExpr callRes = HornHelper.hh().createVariable(p, "callRes_", lhs.getType());
				actualPostParams[cnt++] = callRes;

				Verify.verify(lhs instanceof IdentifierExpression,
						"only assignments to variables are supported, not to " + lhs);
				//update the receiver var to the expression that we use in the call pred.	
				varMap.put( ((IdentifierExpression) lhs).getVariable(), callRes);					
			}
		} else if (!calledMethod.getReturnType().isEmpty()) {
			for (Type tp : calledMethod.getReturnType()) {
				final ProverExpr callRes = HornHelper.hh().createVariable(p, "callRes_", tp);
				actualPostParams[cnt++] = callRes;
			}
		}

		final ProverExpr preCondAtom = contract.precondition.predicate.mkExpr(actualInParams);
		clauses.add(p.mkHornClause(preCondAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		final ProverExpr postCondAtom = contract.postcondition.predicate.mkExpr(actualPostParams);

		final ProverExpr postAtom = postPred.instPredicate(varMap);

		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, postCondAtom }, p.mkLiteral(true)));

		return clauses;
	}

	public List<ProverHornClause> pullToClause(PullStatement pull, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		final Set<ClassVariable> possibleTypes = this.hornContext.ppOrdering
				.getBrutalOverapproximationOfPossibleType(pull);

		for (ClassVariable sig : possibleTypes) {

			final HornPredicate invariant = this.hornContext.lookupInvariantPredicate(sig);
			final List<Expression> invariantArgs = new LinkedList<Expression>();
			invariantArgs.add(pull.getObject());
			invariantArgs.addAll(pull.getLeft());
			//we don't have to fill up the missing ones.
			for (int i=0; i<invariantArgs.size(); i++) {
				varMap.put(invariant.variables.get(i), expEncoder.exprToProverExpr(invariantArgs.get(i), varMap));			
			}
			for (int i=invariantArgs.size(); i<invariant.variables.size();i++) {
				final ProverExpr stub = HornHelper.hh().createVariable(p, "pullStub_",invariant.variables.get(i).getType());
				varMap.put(invariant.variables.get(i), stub);
			}
			final ProverExpr invAtom = invariant.instPredicate(varMap);
			final ProverExpr postAtom = postPred.instPredicate(varMap);
			
			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, invAtom }, p.mkLiteral(true)));
		}
		return clauses;
	}

	public List<ProverHornClause> pushToClause(PushStatement ps, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		final ClassVariable sig = ps.getClassSignature();		
		Verify.verify(sig.getAssociatedFields().length == ps.getRight().size(), "Unequal lengths: " + sig + " and " + ps.getRight());
		
		final HornPredicate invariant = this.hornContext.lookupInvariantPredicate(sig);
		final List<Expression> invariantArgs = new LinkedList<Expression>();
		invariantArgs.add(ps.getObject());
		invariantArgs.addAll(ps.getRight());
		//assign the variables of the invariant pred to the respective value.
		for (int i=0; i<invariantArgs.size(); i++) {
			varMap.put(invariant.variables.get(i), expEncoder.exprToProverExpr(invariantArgs.get(i), varMap));			
		}
		final ProverExpr invAtom = invariant.instPredicate(varMap);

		clauses.add(p.mkHornClause(invAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		return clauses;
	}

}
