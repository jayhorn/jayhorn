package jayhorn.hornify.encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import soottocfg.cfg.expression.BinaryExpression;

import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.literal.IntegerLiteral;

import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverTupleType;
import soottocfg.cfg.expression.literal.NullLiteral;

import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssertStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.NewStatement;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

import soottocfg.soot.transformers.ArrayTransformer;


public class StatementEncoder {

	private final Prover p;

	private final ExpressionEncoder expEncoder;
	private final HornEncoderContext hornContext;


	public final static String LP = "_lastpush";
	private int nextLP = 0;

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
	public List<ProverHornClause> statementToClause(Statement s, HornPredicate prePred, HornPredicate postPred, Method m) {

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
			S2H.sh().addClause(s, clause);
			return clause;
		} else if (s instanceof AssignStatement) {
			List<ProverHornClause> clause = assignToClause((AssignStatement) s, postPred, preAtom, varMap);
			S2H.sh().addClause(s, clause);
			return clause;
		} else if (s instanceof NewStatement) {
			List<ProverHornClause> clause = newToClause((NewStatement) s, postPred, preAtom, varMap);
			S2H.sh().addClause(s, clause);
			return clause;
		} else if (s instanceof CallStatement) {
			List<ProverHornClause> clause = callToClause((CallStatement) s, postPred, preAtom, varMap);
			S2H.sh().addClause(s, clause);
			return clause;
		} else if (s instanceof PullStatement) {
				List<ProverHornClause> clause = pullToClause((PullStatement) s, postPred, preAtom, varMap, m);
				S2H.sh().addClause(s, clause);
				return  clause;
		} else if (s instanceof PushStatement) {
			List<ProverHornClause> clause = pushToClause((PushStatement) s, postPred, preAtom, varMap, m);
			S2H.sh().addClause(s, clause);
			return clause;
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

		final ProverExpr errorState; // For now depending on the solver we use false or a predicate
		String tag = "ErrorState@line" + as.getJavaSourceLine();
		final ProverFun errorPredicate = p.mkHornPredicate(tag, new ProverType[] {});
	
		if (p.solverName().equals("spacer")){
			errorState = errorPredicate.mkExpr(new ProverExpr[]{});
			S2H.sh().setErrorState(errorState, as.getJavaSourceLine());
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

		ProverExpr right = expEncoder.exprToProverExpr(as.getRight(), varMap);
		if (as.getRight() instanceof NullLiteral) {
			ProverTupleType pttLeft = (ProverTupleType) HornHelper.hh().getProverType(p, idLhs.getType());
			right = HornHelper.hh().mkNullExpression(p, pttLeft.getSubTypes());
		} else if (right instanceof ProverTupleExpr) {
			if (!(idLhs.getType() instanceof ReferenceType)) {
				Verify.verify(false, "Chances are that this is a cast from java.lang.Integer to int");
			}
			ProverTupleType pttLeft = (ProverTupleType) HornHelper.hh().getProverType(p, idLhs.getType());
			if (pttLeft.getArity() > ((ProverTupleExpr) right).getArity()) {
				throw new RuntimeException("Oops, not implemented");
			} else if (pttLeft.getArity() > ((ProverTupleExpr) right).getArity()) {
				throw new RuntimeException("Oops, not implemented");
			}
		}

		varMap.put(idLhs.getVariable(), right);

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		return clauses;
	}

	public List<ProverHornClause> newToClause(NewStatement ns, HornPredicate postPred, ProverExpr preAtom,
			Map<Variable, ProverExpr> varMap) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		Verify.verify(ns.getLeft() instanceof IdentifierExpression,
				"only assignments to variables are supported, not to " + ns.getLeft());
		final IdentifierExpression idLhs = (IdentifierExpression) ns.getLeft();

		ReferenceType rightType = new ReferenceType(ns.getClassVariable());
		ProverExpr[] tupleElements = new ProverExpr[rightType.getElementTypeList().size()];

		tupleElements[0] = expEncoder
				.exprToProverExpr(new IdentifierExpression(ns.getSourceLocation(), ns.getCounterVar()), varMap);
		tupleElements[1] = p.mkLiteral(hornContext.getTypeID(ns.getClassVariable()));
		for (int i = 2; i < tupleElements.length; i++) {
			tupleElements[i] = p.mkVariable("$new" + i,
					HornHelper.hh().getProverType(p, rightType.getElementTypeList().get(i)));
		}
		varMap.put(idLhs.getVariable(), p.mkTuple(tupleElements));

		// final ProverExpr ctr = expEncoder.exprToProverExpr(new
		// IdentifierExpression(ns.getSourceLocation(), ns.getCounterVar()),
		// varMap);
		// // set the ref to the current heap counter.
		// varMap.put(idLhs.getVariable(), ctr);

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

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

		List<Expression> callArgs = new LinkedList<Expression>(cs.getArguments());

		List<Variable> inParams = new LinkedList<Variable>(calledMethod.getInParams());

		Verify.verify(inParams.size() == callArgs.size(), inParams.size() + "!=" + callArgs.size());
		Verify.verify(inParams.size() == contract.precondition.variables.size(),
				inParams.size() + "!=" + contract.precondition.variables.size());

		final List<Variable> receiverVars = new ArrayList<Variable>();
		for (Expression e : cs.getReceiver()) {
			receiverVars.add(((IdentifierExpression) e).getVariable());
		}
		// final List<ProverExpr> receiverExprs =
		HornHelper.hh().findOrCreateProverVar(p, receiverVars, varMap);

		List<Type> retTypes = new LinkedList<Type>(calledMethod.getReturnType());

		final ProverExpr[] actualInParams = new ProverExpr[inParams.size()];
		final ProverExpr[] actualPostParams = new ProverExpr[inParams.size() + retTypes.size()];

		int cnt = 0;
		for (Expression e : callArgs) {
			final ProverExpr expr;
			if (e instanceof NullLiteral) {
				ReferenceType rt = (ReferenceType) inParams.get(cnt).getType();
				ProverTupleType ptt = (ProverTupleType) HornHelper.hh().getProverType(p, rt);
				expr = HornHelper.hh().mkNullExpression(p, ptt.getSubTypes());
			} else {
				expr = expEncoder.exprToProverExpr(e, varMap);
			}
			actualInParams[cnt] = expr;
			actualPostParams[cnt] = expr;
			++cnt;
		}

		List<Expression> receiver = new LinkedList<Expression>(cs.getReceiver());

		for (int i = 0; i < retTypes.size(); ++i) {
			Type tp = retTypes.get(i);
			final ProverExpr callRes = HornHelper.hh().createVariable(p, "callRes_", tp);
			actualPostParams[cnt++] = callRes;
			if (i < receiver.size()) {
				Expression lhs = receiver.get(i);
				Verify.verify(lhs instanceof IdentifierExpression,
						"only assignments to variables are supported, not to " + lhs);
				// update the receiver var to the expression that we use in the
				// call pred.
				varMap.put(((IdentifierExpression) lhs).getVariable(), callRes);
			}
		}

		final ProverExpr preCondAtom = contract.precondition.predicate.mkExpr(actualInParams);
		clauses.add(p.mkHornClause(preCondAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		if (actualPostParams.length != contract.postcondition.variables.size()) {
			StringBuilder sb = new StringBuilder();
			sb.append(actualPostParams.length + "!=" + contract.postcondition.variables.size() + "\n");
			sb.append(cs.toString());
			sb.append("  [");
			String comma = "";
			for (Type t : cs.getCallTarget().getReturnType()) {
				sb.append(comma);
				comma = ", ";
				sb.append(t);
			}
			sb.append("]");
			Verify.verify(false, sb.toString());
		}

		for (int i = 0; i < contract.postcondition.variables.size(); i++) {
			ProverType t_ = actualPostParams[i].getType();
			ProverType vt = HornHelper.hh().getProverType(p, contract.postcondition.variables.get(i).getType());
			if (!vt.equals(t_)) {
				System.err.println("*********** at pos "+i+": ");
				System.err.println(cs);
				System.err.println(t_ + "\t" + vt);
				throw new RuntimeException("Return type and receiver type don't match: " + vt + " and " + t_);
			}
		}

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
			Map<Variable, ProverExpr> varMap, Method m) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		Set<PushStatement> affecting = pull.getAffectingPushes();
		Verify.verify(!affecting.isEmpty(),
				"The set of pushes affecting this pull is empty, this would create an assume false");
		for (PushStatement push : pull.getAffectingPushes()) {
			ClassVariable sig = push.getClassSignature();
			if (sig.subclassOf(pull.getClassSignature())) {
				long pushid = -1;
				if (soottocfg.Options.v().memPrecision() >= soottocfg.Options.MEMPREC_LASTPUSH)
					pushid = push.getID();
				HornPredicate invariant = this.hornContext.lookupInvariantPredicate(sig, pushid);
				clauses.addAll(pullToIndividualClause(pull, postPred, preAtom, varMap, invariant, m));
			}
		}
		return clauses;
	}

	private List<ProverHornClause> pullToIndividualClause(PullStatement pull, HornPredicate postPred,
			ProverExpr preAtom, Map<Variable, ProverExpr> varMap, HornPredicate invariant, Method m) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();

		// the first argument is always the reference
		// to the object

		int i = 0;
		// PR: once the reference becomes a vector, we
		// have to properly map between the elements
		// of the vector and the fields of the object
		varMap.put(invariant.variables.get(i++), expEncoder.exprToProverExpr(pull.getObject(), varMap));

		// RK: we should probably elimate these ghostExpressions altogether
		// MS: add all global ghosts
		for (Expression e : pull.getGhostExpressions()) {
			// System.err.println(invariant.variables.get(i)+" = "+e);
			varMap.put(invariant.variables.get(i++), expEncoder.exprToProverExpr(e, varMap));
		}

		// RK: add last push variable
		if (soottocfg.Options.v().memPrecision() >= soottocfg.Options.MEMPREC_LASTPUSH) {
			Variable freshLp = new Variable(LP + nextLP, IntType.instance());
			Expression e = new IdentifierExpression(pull.getSourceLocation(), freshLp);
			varMap.put(invariant.variables.get(i++), expEncoder.exprToProverExpr(e, varMap));
		}
		
		// RK: add index variable for array classes
		if (soottocfg.Options.v().arrayInv() 
				&& pull.getObject().getType().toString().contains(ArrayTransformer.arrayTypeName)
				) {
			System.out.println("Found pull in array get or set " + pull);
			Variable arIndex = m.getInParam(1);
			Expression e = new IdentifierExpression(pull.getSourceLocation(), arIndex);
			varMap.put(invariant.variables.get(i++), expEncoder.exprToProverExpr(e, varMap));
		}

		// introduce fresh prover variables for all
		// the other invariant parameters, and map
		// them to the post-state
		int j = 0;
		for (; i < invariant.variables.size(); i++) {
			final ProverExpr var = HornHelper.hh().createVariable(p, "pullVar_", invariant.variables.get(i).getType());
			varMap.put(invariant.variables.get(i), var);
			if (j < pull.getLeft().size()) {
				varMap.put(pull.getLeft().get(j++).getVariable(), var);
			}
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
		final ProverHornClause clause = p.mkHornClause(postAtom, new ProverExpr[] { preAtom, invAtom },
				p.mkLiteral(true));
		clauses.add(clause);
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
			Map<Variable, ProverExpr> varMap, Method m) {
		List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		final ClassVariable sig = ps.getClassSignature();
		Verify.verify(sig.getAssociatedFields().length == ps.getRight().size(),
				"Unequal lengths: " + sig + " and " + ps.getRight() + " in " + ps);
		int pushid = -1;

		final List<Expression> invariantArgs = new LinkedList<Expression>();
		// TODO: unpack once we have tuples
		invariantArgs.add(ps.getObject());

		// RK: eliminate and handle callerID here instead?
		// MS: add all global ghosts
		invariantArgs.addAll(ps.getGhostExpressions());

		if (soottocfg.Options.v().memPrecision() >= soottocfg.Options.MEMPREC_LASTPUSH) {
			Variable lp = new Variable(LP + nextLP++, IntType.instance());
			Expression lastpush = new IdentifierExpression(ps.getSourceLocation(), lp);
			invariantArgs.add(lastpush);
			pushid = ps.getID();
		}
		
		// Rody: add index variable for array classes
		if (soottocfg.Options.v().arrayInv() 
				&& ps.getObject().getType().toString().contains(ArrayTransformer.arrayTypeName)) {
			System.out.println("Found push in array get or set " + ps);
			Variable arIndex = m.getInParam(1);
			Expression e = new IdentifierExpression(ps.getSourceLocation(), arIndex);
			invariantArgs.add(e);
		}

		invariantArgs.addAll(ps.getRight());

		// get the invariant for the ClassVariable
		final HornPredicate invariant = this.hornContext.lookupInvariantPredicate(sig, pushid);

		// assign the variables of the invariant pred to the respective value.
		for (int i = 0; i < invariantArgs.size(); i++) {
			final ProverExpr right;
			if (invariantArgs.get(i) instanceof NullLiteral) {
				ProverTupleType ptt = (ProverTupleType) HornHelper.hh().getProverType(p,
						invariant.variables.get(i).getType());
				right = HornHelper.hh().mkNullExpression(p, ptt.getSubTypes());
			} else {
				right = expEncoder.exprToProverExpr(invariantArgs.get(i), varMap);
			}

			varMap.put(invariant.variables.get(i), right);
			
//			System.out.println("invariantArgs = " + invariantArgs.get(i));
//			System.err.println(invariant.variables.get(i)+ " = "+varMap.get(invariant.variables.get(i)));
		}
		final ProverExpr invAtom = invariant.instPredicate(varMap);
		clauses.add(p.mkHornClause(invAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));

		final ProverExpr postAtom = postPred.instPredicate(varMap);
		clauses.add(p.mkHornClause(postAtom, new ProverExpr[] { preAtom }, p.mkLiteral(true)));
		return clauses;
	}

}