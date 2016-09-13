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
	
	private final List<ProverExpr> methodPreExprs;
	private final ExpressionEncoder expEncoder;
	
	public StatementEncoder(Prover p, List<ProverExpr> methodPreExprs, ExpressionEncoder expEnc) {
		this.p = p;
		this.methodPreExprs = methodPreExprs; 		
		this.expEncoder = expEnc;
	}

	private ProverExpr instPredicate(HornPredicate pred, List<ProverExpr> args) {
		List<ProverExpr> allArgs = new ArrayList<ProverExpr>();
		allArgs.addAll(methodPreExprs);
		allArgs.addAll(args);
		return pred.predicate.mkExpr(allArgs.toArray(new ProverExpr[allArgs.size()]));
	}
	
	public List<ProverHornClause> statementToClause(Statement s, HornPredicate prePred, HornPredicate postPred) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();

		final List<ProverExpr> preVars = HornHelper.hh().findOrCreateProverVar(p, prePred.variables, varMap);
		final List<ProverExpr> postVars = HornHelper.hh().findOrCreateProverVar(p, postPred.variables, varMap);

		final ProverExpr preAtom = instPredicate(prePred, preVars);

		if (s instanceof AssertStatement) {

			final AssertStatement as = (AssertStatement) s;
			final ProverExpr cond = expEncoder.exprToProverExpr(as.getExpression(), varMap);

			clauses.add(p.mkHornClause(p.mkLiteral(false), new ProverExpr[] { preAtom }, p.mkNot(cond)));

			final ProverExpr postAtom = instPredicate(postPred, postVars);

			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		} else if (s instanceof AssumeStatement) {

			final AssumeStatement as = (AssumeStatement) s;
			final ProverExpr cond = expEncoder.exprToProverExpr(as.getExpression(), varMap);

			final ProverExpr postAtom = instPredicate(postPred, postVars);

			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, cond));

		} else if (s instanceof AssignStatement) {

			final AssignStatement as = (AssignStatement) s;
			final Expression lhs = as.getLeft();

			if (lhs instanceof IdentifierExpression) {
				final IdentifierExpression idLhs = (IdentifierExpression) lhs;
				final int lhsIndex = postPred.variables.indexOf(idLhs.getVariable());
				if (lhsIndex >= 0) {
					postVars.set(lhsIndex, expEncoder.exprToProverExpr(as.getRight(), varMap));
				}
			} else {
				throw new RuntimeException("only assignments to variables are supported, not to " + lhs);
			}

			final ProverExpr postAtom = instPredicate(postPred, postVars);

			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		} else if (s instanceof CallStatement) {

			final CallStatement cs = (CallStatement) s;
			final Method calledMethod = cs.getCallTarget();
			// final MethodContract contract =
			// methodContracts.get(calledMethod.getMethodName());
			final MethodContract contract = HornHelper.hh().getMethodContract(calledMethod.getMethodName());
			if (contract == null)
				throw new RuntimeException("Invoked method " + calledMethod.getMethodName() + " is unknown");

			assert (calledMethod.getInParams().size() == cs.getArguments().size()
					&& calledMethod.getInParams().size() == contract.precondition.variables.size());
			assert (!cs.getReceiver().isEmpty() || calledMethod.getReturnType().isEmpty());

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

					if (lhs instanceof IdentifierExpression) {
						final IdentifierExpression idLhs = (IdentifierExpression) lhs;
						final int lhsIndex = postPred.variables.indexOf(idLhs.getVariable());
						if (lhsIndex >= 0)
							postVars.set(lhsIndex, callRes);
					} else {
						throw new RuntimeException("only assignments to variables are supported, " + "not to " + lhs);
					}
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

			final ProverExpr postAtom = instPredicate(postPred, postVars);

			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, postCondAtom }, p.mkLiteral(true)));

		} else if (s instanceof PullStatement) {

			final PullStatement pull = (PullStatement) s;
			final List<IdentifierExpression> lhss = pull.getLeft();

			/*
			 * TODO
			 * Martin's hack to handle the substype problem =============
			 */
			// final Set<ClassVariable> possibleTypes =
			// ppOrdering.getBrutalOverapproximationOfPossibleType(pull);
			final Set<ClassVariable> possibleTypes = HornHelper.hh().ppOrdering
					.getBrutalOverapproximationOfPossibleType(pull);
			// final List<ProverExpr> invariantDisjunction = new
			// LinkedList<ProverExpr>();
			for (ClassVariable sig : possibleTypes) {
				// System.err.println("Possible type "+sig.getName() + " of "
				// +us.getClassSignature().getName());
				final ProverFun inv = HornHelper.hh().getClassInvariant(p, sig);

				// Verify.verify(sig.getAssociatedFields()[sig.getAssociatedFields().length-1]
				// .getName().equals(PushIdentifierAdder.LP),
				// "Class is missing " + PushIdentifierAdder.LP + " field: " +
				// sig);

				int totalFields = Math.max(sig.getAssociatedFields().length, lhss.size());

				final ProverExpr[] invArgs = new ProverExpr[1 + totalFields];
				int cnt = 0;
				invArgs[cnt++] = expEncoder.exprToProverExpr(pull.getObject(), varMap);

				for (IdentifierExpression lhs : lhss) {
					final ProverExpr lhsExpr = HornHelper.hh().createVariable(p, "pullRes_", lhs.getType());
					invArgs[cnt++] = lhsExpr;

					final int lhsIndex = postPred.variables.indexOf(lhs.getVariable());
					if (lhsIndex >= 0)
						postVars.set(lhsIndex, lhsExpr);
				}
				while (cnt < totalFields + 1) {
					// fill up the fields that are not being used
					// this should only happen if sig is a subtype of what we
					// are trying to pull (and thus declares more fields).					
					final ProverExpr lhsExpr = HornHelper.hh().createVariable(p, "pullStub_", sig.getAssociatedFields()[cnt - 1].getType());
					invArgs[cnt++] = lhsExpr;
				}
				// invariantDisjunction.add(inv.mkExpr(invArgs));

				final ProverExpr invAtom = inv.mkExpr(invArgs);
				final ProverExpr postAtom = instPredicate(postPred, postVars);
				clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom, invAtom }, p.mkLiteral(true)));

			}

		} else if (s instanceof PushStatement) {

			final PushStatement ps = (PushStatement) s;
			final ClassVariable sig = ps.getClassSignature();
			final List<Expression> rhss = ps.getRight();
			final ProverFun inv = HornHelper.hh().getClassInvariant(p, sig);

			// check that last field is "lastpush" and that lhs and rhs lengths
			// are equal
			// Verify.verify(sig.getAssociatedFields()[sig.getAssociatedFields().length-1]
			// .getName().equals(PushIdentifierAdder.LP),
			// "Class is missing " + PushIdentifierAdder.LP + " field: " + sig);
			Verify.verify(sig.getAssociatedFields().length == rhss.size(), "Unequal lengths: " + sig + " and " + rhss);

			final ProverExpr[] invArgs = new ProverExpr[1 + rhss.size()];
			int cnt = 0;
			invArgs[cnt++] = expEncoder.exprToProverExpr(ps.getObject(), varMap);

			for (Expression rhs : rhss)
				invArgs[cnt++] = expEncoder.exprToProverExpr(rhs, varMap);

			final ProverExpr invAtom = inv.mkExpr(invArgs);

			clauses.add(p.mkHornClause(invAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

			final ProverExpr postAtom = instPredicate(postPred, postVars);

			clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		} else {
			throw new RuntimeException("Statement type " + s + " not implemented!");
		}
		return clauses;
	}


}
