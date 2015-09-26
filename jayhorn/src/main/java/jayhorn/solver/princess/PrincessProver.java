package jayhorn.solver.princess;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Stack;

import jayhorn.solver.ArrayType;
import jayhorn.solver.BoolType;
import jayhorn.solver.IntType;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverListener;
import jayhorn.solver.ProverResult;
import jayhorn.solver.ProverType;
import lazabs.horn.bottomup.HornClauses;
import lazabs.horn.bottomup.HornClauses.Clause;
import lazabs.horn.bottomup.SimpleWrapper;
import lazabs.horn.bottomup.Util.Dag;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.Seq;
import scala.collection.immutable.List;
import scala.collection.immutable.Map;
import scala.collection.immutable.Set;
import scala.collection.mutable.ArrayBuffer;
import scala.util.Either;
import ap.SimpleAPI;
import ap.SimpleAPI.ProverStatus$;
import ap.basetypes.IdealInt$;
import ap.parser.ConstantSubstVisitor$;
import ap.parser.IAtom;
import ap.parser.IAtom$;
import ap.parser.IBinFormula;
import ap.parser.IBinJunctor;
import ap.parser.IBoolLit;
import ap.parser.IConstant;
import ap.parser.IConstant$;
import ap.parser.IExpression;
import ap.parser.IExpression$;
import ap.parser.IFormula;
import ap.parser.IFormulaITE;
import ap.parser.IFunApp;
import ap.parser.IIntLit;
import ap.parser.INot;
import ap.parser.IPlus;
import ap.parser.ITerm;
import ap.parser.ITermITE;
import ap.parser.IVariable;
import ap.parser.PredicateSubstVisitor$;
import ap.parser.SymbolCollector$;
import ap.terfor.ConstantTerm;
import ap.terfor.preds.Predicate;

public class PrincessProver implements Prover {

	private SimpleAPI api;

	public PrincessProver() {
		ap.util.Debug.enableAllAssertions(false);
		api = SimpleAPI.spawn();
		// api = SimpleAPI.spawnWithScalaLog();
		// api = SimpleAPI.spawnWithAssertions();
	}

	public PrincessProver(String basename) {
		ap.util.Debug.enableAllAssertions(false);
		api = SimpleAPI.spawnWithLog(basename);
	}

	public ProverType getBooleanType() {
		return BoolType.INSTANCE;
	}

	public ProverType getIntType() {
		return IntType.INSTANCE;
	}

	public ProverType getArrayType(ProverType[] argTypes, ProverType resType) {
		return new ArrayType(argTypes.length);
	}

	public ProverExpr mkBoundVariable(int deBruijnIndex, ProverType type) {
		if (type.equals(getBooleanType())) {
			return mkEq(
					new TermExpr(new IVariable(deBruijnIndex), getIntType()),
					mkLiteral(0));
		} else {
			return new TermExpr(new IVariable(deBruijnIndex), type);
		}
	}

	public ProverExpr mkVariable(String name, ProverType type) {
		if (type.equals(getIntType())) {
			return new TermExpr(api.createConstant(name), type);
		}

		if (type.equals(getBooleanType())) {
			return new FormulaExpr(api.createBooleanVariable(name));
		}

		// array types
		return new TermExpr(api.createConstant(name), type);

		// throw new RuntimeException();
	}

	public ProverFun mkUnintFunction(String name, ProverType[] argTypes,
			ProverType resType) {
		return new PrincessFun(api.createFunction(name, argTypes.length),
				resType);
	}

	/**
	 * Define a new interpreted function. The body is supposed to contain bound
	 * variables with indexes <code>0, 1, ..., (n-1)</code> representing the
	 * arguments of the function.
	 */
	public ProverFun mkDefinedFunction(String name, ProverType[] argTypes,
			final ProverExpr body) {
		return new ProverFun() {
			public ProverExpr mkExpr(ProverExpr[] args) {
				final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
				for (int i = 0; i < args.length; ++i) {
					ITerm termArg;
					if (args[i].getType() == BoolType.INSTANCE)
						termArg = new ITermITE(((FormulaExpr) args[i]).formula,
								new IIntLit(IdealInt$.MODULE$.apply(0)),
								new IIntLit(IdealInt$.MODULE$.apply(1)));
					else
						termArg = ((TermExpr) args[i]).term;
					argsBuf.$plus$eq(termArg);
				}
				final List<ITerm> argsList = argsBuf.toList();

				if (body instanceof TermExpr)
					return new TermExpr(IExpression$.MODULE$.subst(
							((TermExpr) body).term, argsList, 0),
							body.getType());
				else
					return new FormulaExpr(IExpression$.MODULE$.subst(
							((FormulaExpr) body).formula, argsList, 0));
			}
		};
	}

	public ProverExpr mkAll(ProverExpr body, ProverType type) {
		return new FormulaExpr(
				IExpression$.MODULE$.all(((FormulaExpr) body).formula));
	}

	public ProverExpr mkEx(ProverExpr body, ProverType type) {
		return new FormulaExpr(
				IExpression$.MODULE$.ex(((FormulaExpr) body).formula));
	}

	public ProverExpr mkTrigger(ProverExpr body, ProverExpr[] triggers) {
		final ArrayBuffer<IExpression> triggerExprs = new ArrayBuffer<IExpression>();

		for (int i = 0; i < triggers.length; ++i) {
			if (triggers[i] instanceof TermExpr)
				triggerExprs.$plus$eq(((TermExpr) triggers[i]).term);
			else
				triggerExprs.$plus$eq(((FormulaExpr) triggers[i]).formula);
		}

		return new FormulaExpr(IExpression.trig(((FormulaExpr) body).formula,
				triggerExprs));
	}

	public ProverExpr mkEq(ProverExpr left, ProverExpr right) {
		if (left instanceof TermExpr)
			return new FormulaExpr(
					((TermExpr) left).term.$eq$eq$eq(((TermExpr) right).term));
		else
			return new FormulaExpr(
					((FormulaExpr) left).formula
							.$less$eq$greater(((FormulaExpr) right).formula));
	}

	public ProverExpr mkLiteral(boolean value) {
		return new FormulaExpr(new IBoolLit(value));
	}

	public ProverExpr mkNot(ProverExpr body) {
		return new FormulaExpr(new INot(((FormulaExpr) body).formula));
	}

	public ProverExpr mkAnd(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(new IBinFormula(IBinJunctor.And(),
				((FormulaExpr) left).formula, ((FormulaExpr) right).formula));
	}

	public ProverExpr mkAnd(ProverExpr[] args) {
		final ArrayBuffer<IFormula> argsBuf = new ArrayBuffer<IFormula>();
		for (int i = 0; i < args.length; ++i)
			argsBuf.$plus$eq(((FormulaExpr) args[i]).formula);
		return new FormulaExpr(IExpression$.MODULE$.and(argsBuf));
	}

	public ProverExpr mkOr(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(new IBinFormula(IBinJunctor.Or(),
				((FormulaExpr) left).formula, ((FormulaExpr) right).formula));
	}

	public ProverExpr mkOr(ProverExpr[] args) {
		final ArrayBuffer<IFormula> argsBuf = new ArrayBuffer<IFormula>();
		for (int i = 0; i < args.length; ++i)
			argsBuf.$plus$eq(((FormulaExpr) args[i]).formula);
		return new FormulaExpr(IExpression$.MODULE$.or(argsBuf));
	}

	public ProverExpr mkImplies(ProverExpr left, ProverExpr right) {
		return mkOr(mkNot(left), right);
	}

	public ProverExpr mkIte(ProverExpr cond, ProverExpr thenExpr,
			ProverExpr elseExpr) {
		if (thenExpr instanceof TermExpr)
			return new TermExpr(new ITermITE(((FormulaExpr) cond).formula,
					((TermExpr) thenExpr).term, ((TermExpr) elseExpr).term),
					thenExpr.getType());
		else
			return new FormulaExpr(new IFormulaITE(
					((FormulaExpr) cond).formula,
					((FormulaExpr) thenExpr).formula,
					((FormulaExpr) elseExpr).formula));
	}

	public ProverExpr mkLiteral(int value) {
		return new TermExpr(new IIntLit(IdealInt$.MODULE$.apply(value)),
				getIntType());
	}

	public ProverExpr mkLiteral(BigInteger value) {
		return new TermExpr(new IIntLit(IdealInt$.MODULE$.apply(value
				.toString())), getIntType());
	}

	public ProverExpr mkPlus(ProverExpr left, ProverExpr right) {
		return new TermExpr(new IPlus(((TermExpr) left).term,
				((TermExpr) right).term), getIntType());
	}

	public ProverExpr mkPlus(ProverExpr[] args) {
		final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
		for (int i = 0; i < args.length; ++i)
			argsBuf.$plus$eq(((TermExpr) args[i]).term);
		return new TermExpr(IExpression$.MODULE$.sum(argsBuf), getIntType());
	}

	public ProverExpr mkMinus(ProverExpr left, ProverExpr right) {
		return new TermExpr(new IPlus(((TermExpr) left).term,
				((TermExpr) right).term.unary_$minus()), getIntType());
	}

	public ProverExpr mkNeg(ProverExpr arg) {
		return new TermExpr(((TermExpr) arg).term.unary_$minus(), getIntType());
	}

	public ProverExpr mkMult(ProverExpr left, ProverExpr right) {
		return new TermExpr(api.mult(((TermExpr) left).term,
				((TermExpr) right).term), getIntType());
	}

	public ProverExpr mkEDiv(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().eDiv(((TermExpr) num).term,
				((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkEMod(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().eMod(((TermExpr) num).term,
				((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkTDiv(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().tDiv(((TermExpr) num).term,
				((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkTMod(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().tMod(((TermExpr) num).term,
				((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkGeq(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(
				((TermExpr) left).term.$greater$eq(((TermExpr) right).term));
	}

	public ProverExpr mkGt(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(
				((TermExpr) left).term.$greater(((TermExpr) right).term));
	}

	public ProverExpr mkLeq(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(
				((TermExpr) left).term.$less$eq(((TermExpr) right).term));
	}

	public ProverExpr mkLt(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(
				((TermExpr) left).term.$less(((TermExpr) right).term));
	}

	public ProverExpr mkSelect(ProverExpr ar, ProverExpr[] indexes) {
		final ArrayBuffer<ITerm> args = new ArrayBuffer<ITerm>();
		args.$plus$eq(((TermExpr) ar).term);
		for (int i = 0; i < indexes.length; ++i)
			args.$plus$eq(((TermExpr) indexes[i]).term);

		return new TermExpr(new IFunApp(api.selectFun(indexes.length),
				args.toSeq()), getIntType());
	}

	public ProverExpr mkStore(ProverExpr ar, ProverExpr[] indexes,
			ProverExpr value) {
		final ArrayBuffer<ITerm> args = new ArrayBuffer<ITerm>();
		args.$plus$eq(((TermExpr) ar).term);
		for (int i = 0; i < indexes.length; ++i)
			args.$plus$eq(((TermExpr) indexes[i]).term);
		args.$plus$eq(((TermExpr) value).term);

		return new TermExpr(new IFunApp(api.storeFun(indexes.length),
				args.toSeq()), getIntType());
	}

	// ////////////////////////////////////////////////////////////////////////////

	public void push() {
		api.push();
                assertedClausesStack.push(assertedClauses.size());
	}

	public void pop() {
		api.pop();
                int n = assertedClausesStack.pop();
                while (assertedClauses.size() > n)
                    assertedClauses.remove(assertedClauses.size() - 1);
	}

	public void addAssertion(ProverExpr assertion) {
            if (assertion instanceof HornExpr)
                assertedClauses.add((HornExpr)assertion);
            else
		api.addAssertion(((FormulaExpr) assertion).formula);
	}

	// ////////////////////////////////////////////////////////////////////////////

	public ProverResult checkSat(boolean block) {
            if (assertedClauses.isEmpty()) {
                return translateRes(api.checkSat(block));
            } else {
                assert(block); // only implemented case so far

                final ArrayBuffer<HornClauses.Clause> clauses =
                    new ArrayBuffer<HornClauses.Clause>();
                for (HornExpr clause : assertedClauses)
                    clauses.$plus$eq(clause.clause);

                final Either<Map<Predicate, IFormula>, Dag<Tuple2<IAtom, Clause>>> result =
                    SimpleWrapper.solve
                    (clauses,
                     scala.collection.immutable.Map$.MODULE$.<Predicate, Seq<IFormula>>empty(),
                     false,
                     false);

                if (result.isLeft())
                  return ProverResult.Sat;
                else
                  return ProverResult.Unsat;
            }
	}

	private ProverResult translateRes(scala.Enumeration.Value result) {
		if (result == ProverStatus$.MODULE$.Sat()
				|| result == ProverStatus$.MODULE$.Invalid())
			return ProverResult.Sat;
		else if (result == ProverStatus$.MODULE$.Unsat()
				|| result == ProverStatus$.MODULE$.Valid())
			return ProverResult.Unsat;
		else if (result == ProverStatus$.MODULE$.Unknown())
			return ProverResult.Unknown;
		else if (result == ProverStatus$.MODULE$.Running())
			return ProverResult.Running;
		else
			return ProverResult.Error;
	}

	public ProverResult getResult(boolean block) {
		return translateRes(api.getStatus(block));
	}

	public ProverResult getResult(long timeout) {
		return translateRes(api.getStatus(timeout));
	}

	public ProverResult nextModel(boolean block) {
		return translateRes(api.nextModel(block));
	}

	public ProverResult stop() {
		return translateRes(api.stop());
	}

	public void setConstructProofs(boolean b) {
		api.setConstructProofs(b);
	}

	public void setPartitionNumber(int num) {
		api.setPartitionNumber(num);
	}

	public ProverExpr[] interpolate(int[][] partitionSeq) {
		final ArrayBuffer<Set<Object>> args = new ArrayBuffer<Set<Object>>();
		for (int i = 0; i < partitionSeq.length; ++i) {
			final ArrayBuffer<Object> indexes = new ArrayBuffer<Object>();
			for (int j = 0; j < partitionSeq[i].length; ++j)
				indexes.$plus$eq(Integer.valueOf(partitionSeq[i][j]));
			args.$plus$eq(indexes.toSet());
		}

		final Seq<IFormula> ints = api.getInterpolants(args.toSeq());

		final ProverExpr[] res = new ProverExpr[partitionSeq.length - 1];
		for (int i = 0; i < partitionSeq.length - 1; ++i)
			res[i] = new FormulaExpr(ints.apply(i));

		return res;
	}

	public void addListener(ProverListener listener) {
		throw new RuntimeException();
	}

	public ProverExpr evaluate(ProverExpr expr) {
		if (expr instanceof TermExpr)
			return new TermExpr(new IIntLit(api.eval(((TermExpr) expr).term)),
					((TermExpr) expr).getType());
		else
			return new FormulaExpr(new IBoolLit(
					api.eval(((FormulaExpr) expr).formula)));
	}

	public ProverExpr[] freeVariables(ProverExpr expr) {
		final ArrayList<ProverExpr> res = new ArrayList<ProverExpr>();

		final scala.Tuple3<scala.collection.Set<IVariable>, scala.collection.Set<ConstantTerm>, scala.collection.Set<Predicate>> symTriple;
		if (expr instanceof TermExpr)
			symTriple = SymbolCollector$.MODULE$
					.varsConstsPreds(((TermExpr) expr).term);
		else
			symTriple = SymbolCollector$.MODULE$
					.varsConstsPreds(((FormulaExpr) expr).formula);

		final Iterator<IVariable> it1 = symTriple._1().iterator();
		while (it1.hasNext())
			res.add(new TermExpr(it1.next(), getIntType()));

		final Iterator<ConstantTerm> it2 = symTriple._2().iterator();
		while (it2.hasNext())
			res.add(new TermExpr(IConstant$.MODULE$.apply(it2.next()),
					getIntType()));

		final Iterator<Predicate> it3 = symTriple._3().iterator();
		final List<ITerm> emptyArgs = (new ArrayBuffer<ITerm>()).toList();
		while (it3.hasNext())
			res.add(new FormulaExpr(IAtom$.MODULE$.apply(it3.next(), emptyArgs)));

		return res.toArray(new ProverExpr[0]);
	}

	/**
	 * Simultaneously substitute <code>from</code> with <code>to</code> in
	 * <code>target</code>. <code>from</code> has to be an array of free or
	 * bound variables.
	 */
	public ProverExpr substitute(ProverExpr target, ProverExpr[] from,
			ProverExpr[] to) {
		assert (from.length == to.length);

		final scala.collection.mutable.HashMap<ConstantTerm, ITerm> constantSubst = new scala.collection.mutable.HashMap<ConstantTerm, ITerm>();
		final scala.collection.mutable.HashMap<Predicate, IFormula> predicateSubst = new scala.collection.mutable.HashMap<Predicate, IFormula>();

		for (int i = 0; i < from.length; ++i) {
			if (from[i] instanceof TermExpr) {
				final ConstantTerm c = ((IConstant) ((TermExpr) from[i]).term)
						.c();
				final ITerm t = ((TermExpr) to[i]).term;
				constantSubst.put(c, t);
			} else {
				final Predicate p = ((IAtom) ((FormulaExpr) from[i]).formula)
						.pred();
				assert (p.arity() == 0);
				final IFormula f = ((FormulaExpr) to[i]).formula;
				predicateSubst.put(p, f);
			}
		}

		// We currently just assume that there are no clashes between
		// substituted
		// terms and predicates/formulae, and that the substitutions can be
		// carried out in sequence

		if (target instanceof TermExpr) {
			final ITerm t1 = ((TermExpr) target).term;
			final ITerm t2 = ConstantSubstVisitor$.MODULE$.apply(t1,
					constantSubst);
			final ITerm t3 = PredicateSubstVisitor$.MODULE$.apply(t2,
					predicateSubst);
			return new TermExpr(t3, target.getType());
		} else {
			final IFormula f1 = ((FormulaExpr) target).formula;
			final IFormula f2 = ConstantSubstVisitor$.MODULE$.apply(f1,
					constantSubst);
			final IFormula f3 = PredicateSubstVisitor$.MODULE$.apply(f2,
					predicateSubst);
			return new FormulaExpr(f3);
		}
	}

	public void shutdown() {
		api.shutDown();
	}

	public void reset() {
		api.reset();
	}

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
	public String proverExprToSMT(ProverExpr exp) {
		PrintStream originalOut = scala.Console.out();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream newOut = new PrintStream(baos);
		scala.Console.setOut(newOut);
		ap.parser.SMTLineariser.apply(((FormulaExpr) exp).formula);
		scala.Console.flush();
		scala.Console.setOut(originalOut);
		return baos.toString();
	}


    ////////////////////////////////////////////////////////////////////////////
    // Horn clause interface

    private final ArrayList<HornExpr> assertedClauses =
        new ArrayList<HornExpr>();

    private final Stack<Integer> assertedClausesStack =
        new Stack<Integer>();

    public ProverFun mkHornPredicate(String name, ProverType[] argTypes) {
	return new PredicateFun(api.createRelation(name, argTypes.length));
    }
    
    /**
     * The head literal can either be constructed using
     * <code>mkHornPredicate</code>, or be the formula <code>false</code>.
     */
    public ProverHornClause mkHornClause(ProverExpr head, ProverExpr[] body,
                                         ProverExpr constraint) {
        IFormula rawHead = ((FormulaExpr)head).formula;
        if ((rawHead instanceof IBoolLit) && !((IBoolLit)rawHead).value())
            rawHead = SimpleWrapper.FALSEAtom();

        final ArrayBuffer<IAtom> rawBody = new ArrayBuffer<IAtom>();
        for (int i = 0; i < body.length; ++i)
            rawBody.$plus$eq((IAtom)((FormulaExpr)body[i]).formula);

        final HornClauses.Clause clause =
            SimpleWrapper.clause((IAtom)rawHead,
                                 rawBody.toList(),
                                 ((FormulaExpr)constraint).formula);

        return new HornExpr(clause);
    }

	@Override
	public void setHornLogic(boolean b) {
		// ignore		
	}
}
