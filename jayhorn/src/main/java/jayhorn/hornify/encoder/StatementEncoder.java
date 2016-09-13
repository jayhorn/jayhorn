package jayhorn.hornify.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import jayhorn.hornify.HornHelper;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.MethodContract;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
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

	public StatementEncoder(Prover p, ExpressionEncoder expEnc) {
		this.p = p;
		this.expEncoder = expEnc;
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
		final MethodContract contract = HornHelper.hh().getMethodContract(calledMethod.getMethodName());

		Verify.verify(calledMethod.getInParams().size() == cs.getArguments().size()
				&& calledMethod.getInParams().size() == contract.precondition.variables.size());
		Verify.verify(!cs.getReceiver().isEmpty() || calledMethod.getReturnType().isEmpty());

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

		final List<IdentifierExpression> lhss = pull.getLeft();

		final Set<ClassVariable> possibleTypes = HornHelper.hh().ppOrdering
				.getBrutalOverapproximationOfPossibleType(pull);

		for (ClassVariable sig : possibleTypes) {

			final ProverFun inv = HornHelper.hh().getClassInvariant(p, sig);
			int totalFields = Math.max(sig.getAssociatedFields().length, lhss.size());

			final ProverExpr[] invArgs = new ProverExpr[1 + totalFields];
			int cnt = 0;
			invArgs[cnt++] = expEncoder.exprToProverExpr(pull.getObject(), varMap);

			for (IdentifierExpression lhs : lhss) {
				final ProverExpr lhsExpr = HornHelper.hh().createVariable(p, "pullRes_", lhs.getType());
				invArgs[cnt++] = lhsExpr;

				varMap.put(lhs.getVariable(), lhsExpr);
			}
			while (cnt < totalFields + 1) {
				// fill up the fields that are not being used
				// this should only happen if sig is a subtype of what we
				// are trying to pull (and thus declares more fields).
				final ProverExpr lhsExpr = HornHelper.hh().createVariable(p, "pullStub_",
						sig.getAssociatedFields()[cnt - 1].getType());
				invArgs[cnt++] = lhsExpr;
			}
			// invariantDisjunction.add(inv.mkExpr(invArgs));

			final ProverExpr invAtom = inv.mkExpr(invArgs);
			final ProverExpr postAtom = postPred.instPredicate(varMap);
			
			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, invAtom }, p.mkLiteral(true)));

		}
		return clauses;
	}

	public List<ProverHornClause> pushToClause(PushStatement ps, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		final ClassVariable sig = ps.getClassSignature();
		final List<Expression> rhss = ps.getRight();
		final ProverFun inv = HornHelper.hh().getClassInvariant(p, sig);

		Verify.verify(sig.getAssociatedFields().length == rhss.size(), "Unequal lengths: " + sig + " and " + rhss);

		final ProverExpr[] invArgs = new ProverExpr[1 + rhss.size()];
		int cnt = 0;
		invArgs[cnt++] = expEncoder.exprToProverExpr(ps.getObject(), varMap);

		for (Expression rhs : rhss)
			invArgs[cnt++] = expEncoder.exprToProverExpr(rhs, varMap);

		final ProverExpr invAtom = inv.mkExpr(invArgs);

		clauses.add(p.mkHornClause(invAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		final ProverExpr postAtom = postPred.instPredicate(varMap);

		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		return clauses;
	}

}
