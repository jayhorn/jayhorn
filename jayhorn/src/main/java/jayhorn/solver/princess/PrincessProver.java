package jayhorn.solver.princess;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Verify;

import ap.DialogUtil$;
import ap.SimpleAPI;
import ap.SimpleAPI$;
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
import ap.parser.ISortedVariable;
import ap.parser.PredicateSubstVisitor$;
import ap.parser.SymbolCollector$;
import ap.terfor.ConstantTerm;
import ap.terfor.preds.Predicate;
import ap.theories.ADT;
import ap.theories.ADT$;
import ap.theories.ADT.ADTProxySort;
import ap.theories.ADT.TermMeasure$;
import ap.theories.ADT.CtorSignature;
import ap.theories.ADT.CtorArgSort;
import ap.theories.ADT.OtherSort;
import ap.theories.ADT.ADTSort;
import ap.theories.SimpleArray.ArraySort;
import ap.types.Sort;
import ap.types.Sort$;
import ap.types.Sort.Integer$;
import jayhorn.Log;
import jayhorn.Options;
import jayhorn.solver.*;
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
import scala.collection.mutable.HashSet;
import scala.util.Either;

public class PrincessProver implements Prover {

	private SimpleAPI api;

	public PrincessProver() {
		ap.util.Debug.enableAllAssertions(false);
		api = SimpleAPI.spawnNoSanitise();
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

	public ProverADT mkADT(String[]       typeNames,
                               String[]       ctorNames,
                               int[]          ctorTypes,
                               ProverType[][] ctorArgTypes,
                               String[][]     selectorNames) {
            assert(ctorNames.length == ctorTypes.length &&
                   ctorNames.length == ctorArgTypes.length &&
                   ctorNames.length == selectorNames.length);

            final ArrayBuffer<String> sortNames = new ArrayBuffer<> ();
            for (int i = 0; i < typeNames.length; ++i)
                sortNames.$plus$eq(typeNames[i]);

            final ArrayBuffer<Tuple2<String, CtorSignature>> ctors =
                new ArrayBuffer<> ();
            for (int i = 0; i < ctorNames.length; ++i) {
                assert(ctorArgTypes[i].length == selectorNames[i].length);

                final ADTSort resSort = new ADTSort(ctorTypes[i]);

                final ArrayBuffer<Tuple2<String, CtorArgSort>> args =
                    new ArrayBuffer<> ();
                for (int j = 0; j < ctorArgTypes[i].length; ++j) {
                    final ProverType type = ctorArgTypes[i][j];
                    final CtorArgSort argSort;
                    if (type instanceof ADTTempType)
                        argSort = new ADTSort(((ADTTempType)type).typeIndex);
                    else
                        argSort = new OtherSort(type2Sort(type));
                    args.$plus$eq(new Tuple2 (selectorNames[i][j], argSort));
                }

                ctors.$plus$eq(new Tuple2 (ctorNames[i],
                                           new CtorSignature(args, resSort)));
            }

            final ADT adt =
                new ADT (sortNames, ctors, TermMeasure$.MODULE$.Size());
            return new PrincessADT(adt);
        }

	public ProverType getADTTempType(int n) {
            return new ADTTempType(n);
        }

    public ProverType getTupleType(ProverType[] subTypes) {
        return new ProverTupleType(Arrays.copyOf(subTypes, subTypes.length));
    }

	public ProverExpr mkBoundVariable(int deBruijnIndex, ProverType type) {
            // TODO: handle tuples
            assert(!(type instanceof ProverTupleType));
            if (type.equals(getBooleanType())) {
                return mkEq(new TermExpr
                            (new ISortedVariable(deBruijnIndex,
                                                 Sort.Integer$.MODULE$),
                             getIntType()), mkLiteral(0));
            } else {
                return new TermExpr(new ISortedVariable(deBruijnIndex,
                                                        type2Sort(type)),
                                    type);
            }
	}

    protected static ProverType sort2Type(Sort sort) {
        if (sort == Sort.Integer$.MODULE$) {
            return IntType.INSTANCE;
        } else if (sort instanceof ADTProxySort) {
            return new PrincessADTType((ADTProxySort)sort);
        } else if (sort instanceof ArraySort) {
            return new ArrayType(((ArraySort)sort).arity());
        }
        throw new IllegalArgumentException();
    }

    protected static Sort type2Sort(ProverType type) {
        if (type == IntType.INSTANCE || type == BoolType.INSTANCE) {
            return Sort.Integer$.MODULE$;
        } else if (type instanceof PrincessADTType) {
            return ((PrincessADTType)type).sort;
        } else if (type instanceof ArrayType) {
            return new ArraySort (((ArrayType)type).arity);
        }
        throw new IllegalArgumentException();
    }

    public ProverExpr mkVariable(String name, ProverType type) {
        if (type.equals(getIntType())) {
            return new TermExpr(api.createConstant(name), type);
        } else if (type.equals(getBooleanType())) {
            return new FormulaExpr(api.createBooleanVariable(name));
        } else if (type instanceof ProverTupleType) {
            final ProverTupleType tt = (ProverTupleType)type;
            final ProverExpr[] res = new ProverExpr[tt.getArity()];
            for (int i = 0; i < tt.getArity(); ++i)
                res[i] = mkVariable(name + "_" + i, tt.getSubType(i));
            return mkTuple(res);
        } else {
            return new TermExpr(api.createConstant(name, type2Sort(type)),
                                type);
        }
    }

	public ProverFun mkUnintFunction(String name, ProverType[] argTypes, ProverType resType) {
            // TODO: handle tuples
		return new PrincessFun(api.createFunction(name, argTypes.length), resType);
	}

	/**
	 * Define a new interpreted function. The body is supposed to contain bound
	 * variables with indexes <code>0, 1, ..., (n-1)</code> representing the
	 * arguments of the function.
	 */
	public ProverFun mkDefinedFunction(String name, ProverType[] argTypes, final ProverExpr body) {
            // TODO: handle tuples
		return new ProverFun() {
			public ProverExpr mkExpr(ProverExpr[] args) {
				final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
				for (int i = 0; i < args.length; ++i) {
					argsBuf.$plus$eq(((PrincessProverExpr) args[i]).toTerm());
				}
				final List<ITerm> argsList = argsBuf.toList();

				if (body instanceof TermExpr)
					return new TermExpr(IExpression$.MODULE$.subst(((TermExpr) body).term, argsList, 0),
							body.getType());
				else
					return new FormulaExpr(IExpression$.MODULE$.subst(((FormulaExpr) body).formula, argsList, 0));
			}
		};
	}

	public ProverExpr mkAll(ProverExpr body, ProverType type) {		// TODO: type is not used, why?
            // TODO: handle tuples
		return new FormulaExpr(IExpression$.MODULE$.all(((PrincessProverExpr) body).toFormula()));
	}

	public ProverExpr mkEx(ProverExpr body, ProverType type) {
            // TODO: handle tuples
		return new FormulaExpr(IExpression$.MODULE$.ex(((PrincessProverExpr) body).toFormula()));
	}

	public ProverExpr mkTrigger(ProverExpr body, ProverExpr[] triggers) {
		final ArrayBuffer<IExpression> triggerExprs = new ArrayBuffer<IExpression>();

		for (int i = 0; i < triggers.length; ++i) {
			triggerExprs.$plus$eq(((PrincessProverExpr) triggers[i]).toExpression());
		}

		return new FormulaExpr(IExpression.trig(((FormulaExpr) body).formula, triggerExprs));
	}

    public ProverExpr mkEq(ProverExpr left, ProverExpr right) {
        if (left instanceof ProverTupleExpr) {
            ProverTupleExpr tLeft = (ProverTupleExpr)left;
            ProverTupleExpr tRight = (ProverTupleExpr)right;
            Verify.verify(tLeft.getArity() == tRight.getArity(), tLeft.getArity()+"!="+ tRight.getArity()+ " for " +left + " and " + right);

            ProverExpr[] conjuncts = new ProverExpr[tLeft.getArity()];
            for (int i = 0; i < tLeft.getArity(); ++i)
                conjuncts[i] = mkEq(tLeft.getSubExpr(i),
                                    tRight.getSubExpr(i));

            return mkAnd(conjuncts);
        }
        
        PrincessProverExpr pLeft = (PrincessProverExpr) left;
        PrincessProverExpr pRight = (PrincessProverExpr) right;
        if (pLeft.isBoolean() && pRight.isBoolean())
            return new FormulaExpr(pLeft.toFormula().$less$eq$greater(pRight.toFormula()));
        else
            return new FormulaExpr(pLeft.toTerm().$eq$eq$eq(pRight.toTerm()));
    }

	public ProverExpr mkLiteral(boolean value) {
		return new FormulaExpr(new IBoolLit(value));
	}

	public ProverExpr mkNot(ProverExpr body) {
		return new FormulaExpr(new INot(((PrincessProverExpr) body).toFormula()));
	}

	public ProverExpr mkAnd(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(new IBinFormula(IBinJunctor.And(), ((PrincessProverExpr) left).toFormula(),
				((PrincessProverExpr) right).toFormula()));
	}

	public ProverExpr mkAnd(ProverExpr ... args) {
		final ArrayBuffer<IFormula> argsBuf = new ArrayBuffer<IFormula>();
		for (int i = 0; i < args.length; ++i)
			argsBuf.$plus$eq(((PrincessProverExpr) args[i]).toFormula());
		return new FormulaExpr(IExpression$.MODULE$.and(argsBuf));
	}

	public ProverExpr mkOr(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(new IBinFormula(IBinJunctor.Or(), ((PrincessProverExpr) left).toFormula(),
				((PrincessProverExpr) right).toFormula()));
	}

	public ProverExpr mkOr(ProverExpr ... args) {
		final ArrayBuffer<IFormula> argsBuf = new ArrayBuffer<IFormula>();
		for (int i = 0; i < args.length; ++i)
			argsBuf.$plus$eq(((PrincessProverExpr) args[i]).toFormula());
		return new FormulaExpr(IExpression$.MODULE$.or(argsBuf));
	}

	public ProverExpr mkImplies(ProverExpr left, ProverExpr right) {
		return mkOr(mkNot(left), right);
	}

	public ProverExpr mkIte(ProverExpr cond, ProverExpr thenExpr, ProverExpr elseExpr) {
		if (thenExpr instanceof TermExpr)
			return new TermExpr(new ITermITE(((PrincessProverExpr) cond).toFormula(),
					((PrincessProverExpr) thenExpr).toTerm(), ((PrincessProverExpr) elseExpr).toTerm()),
					thenExpr.getType());
		else
			return new FormulaExpr(new IFormulaITE(((PrincessProverExpr) cond).toFormula(),
					((PrincessProverExpr) thenExpr).toFormula(), ((PrincessProverExpr) elseExpr).toFormula()));
	}

	public ProverExpr mkLiteral(int value) {
		return new TermExpr(new IIntLit(IdealInt$.MODULE$.apply(value)), getIntType());
	}

	public ProverExpr mkLiteral(BigInteger value) {
		return new TermExpr(new IIntLit(IdealInt$.MODULE$.apply(value.toString())), getIntType());
	}

	public ProverExpr mkPlus(ProverExpr left, ProverExpr right) {
		return new TermExpr(new IPlus(((TermExpr) left).term, ((TermExpr) right).term), getIntType());
	}

	public ProverExpr mkPlus(ProverExpr[] args) {
		final ArrayBuffer<ITerm> argsBuf = new ArrayBuffer<ITerm>();
		for (int i = 0; i < args.length; ++i)
			argsBuf.$plus$eq(((TermExpr) args[i]).term);
		return new TermExpr(IExpression$.MODULE$.sum(argsBuf), getIntType());
	}

	public ProverExpr mkMinus(ProverExpr left, ProverExpr right) {
		return new TermExpr(new IPlus(((TermExpr) left).term, ((TermExpr) right).term.unary_$minus()), getIntType());
	}

	public ProverExpr mkNeg(ProverExpr arg) {
		return new TermExpr(((TermExpr) arg).term.unary_$minus(), getIntType());
	}

	public ProverExpr mkMult(ProverExpr left, ProverExpr right) {
		return new TermExpr(api.mult(((TermExpr) left).term, ((TermExpr) right).term), getIntType());
	}

	public ProverExpr mkEDiv(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().eDiv(((TermExpr) num).term, ((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkEMod(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().eMod(((TermExpr) num).term, ((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkTDiv(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().tDiv(((TermExpr) num).term, ((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkTMod(ProverExpr num, ProverExpr denom) {
		return new TermExpr(api.mulTheory().tMod(((TermExpr) num).term, ((TermExpr) denom).term), getIntType());
	}

	public ProverExpr mkGeq(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(((TermExpr) left).term.$greater$eq(((TermExpr) right).term));
	}

	public ProverExpr mkGt(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(((TermExpr) left).term.$greater(((TermExpr) right).term));
	}

	public ProverExpr mkLeq(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(((TermExpr) left).term.$less$eq(((TermExpr) right).term));
	}

	public ProverExpr mkLt(ProverExpr left, ProverExpr right) {
		return new FormulaExpr(((TermExpr) left).term.$less(((TermExpr) right).term));
	}

	public ProverExpr mkSelect(ProverExpr ar, ProverExpr[] indexes) {
		final ArrayBuffer<ITerm> args = new ArrayBuffer<ITerm>();
		args.$plus$eq(((TermExpr) ar).term);
		for (int i = 0; i < indexes.length; ++i)
			args.$plus$eq(((TermExpr) indexes[i]).term);

		return new TermExpr(new IFunApp(api.selectFun(indexes.length), args.toSeq()), getIntType());
	}

	public ProverExpr mkStore(ProverExpr ar, ProverExpr[] indexes, ProverExpr value) {
		final ArrayBuffer<ITerm> args = new ArrayBuffer<ITerm>();
		args.$plus$eq(((TermExpr) ar).term);
		for (int i = 0; i < indexes.length; ++i)
			args.$plus$eq(((TermExpr) indexes[i]).term);
		args.$plus$eq(((TermExpr) value).term);

		return new TermExpr(new IFunApp(api.storeFun(indexes.length), args.toSeq()), getIntType());
	}

    ////////////////////////////////////////////////////////////////////////////

    public ProverExpr mkTuple(ProverExpr[] subExprs) {
        return new ProverTupleExpr(Arrays.copyOf(subExprs, subExprs.length));
    }
    
    public ProverExpr mkTupleSelect(ProverExpr tuple, int index) {
        ProverTupleExpr ttuple = (ProverTupleExpr)tuple;
        return ttuple.getSubExpr(index);
    }

    public ProverExpr mkTupleUpdate(ProverExpr tuple, int index, ProverExpr newVal) {
        ProverTupleType ptt = (ProverTupleType)tuple.getType();
        ProverExpr[] subExprs = new ProverExpr[ptt.getArity()];
        for (int i = 0; i < ptt.getArity(); i++) {
            if (i == index)
                subExprs[i] = newVal;
            else
                subExprs[i] = mkTupleSelect(tuple, i);
        }
        return mkTuple(subExprs);
    }

    ////////////////////////////////////////////////////////////////////////////

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
			assertedClauses.add((HornExpr) assertion);
		else
			api.addAssertion(((PrincessProverExpr) assertion).toFormula());
	}

	// ////////////////////////////////////////////////////////////////////////////

	private ExecutorService executor = null;
	private Future<?> futureProverResult = null;
	private PrincessSolverThread thread = null;

	
    private java.util.Map<String, String> lastSolution;
    public java.util.Map<String, String> getLastSolution() {
        return lastSolution;
    }
	
    private Dag<Tuple2<ProverFun, ProverExpr[]>>[] lastCEX;
    public Dag<Tuple2<ProverFun, ProverExpr[]>> getLastCEX() {
        return lastCEX[0];
    }

    public void prettyPrintLastCEX() {
                final Dag<String> prettyCEX =
                    getLastCEX().map(new scala.runtime.AbstractFunction1<Tuple2<ProverFun, ProverExpr[]>,
                                   String>() {
                            public String apply(Tuple2<ProverFun, ProverExpr[]> p) {
                                if (p == null) {
                                    return "false";
                                } else {
                                    final StringBuffer res = new StringBuffer();
                                    res.append(p._1());
                                    res.append("(");
                                    String sep = "";
                                    for (int i = 0; i < p._2().length; ++i) {
                                        res.append(sep);
                                        sep = ", ";
                                        res.append(p._2()[i]);
                                    }
                                    res.append(")");
                                    return res.toString();
                                }
                            }
                        });
                Log.info("Counterexample:\n"
                         + DialogUtil$.MODULE$.asString(new scala.runtime.AbstractFunction0<Integer>() {
                                 public Integer apply() {
                                     prettyCEX.prettyPrint();
                                     return 0;
                                 }
                             }));
    }

    public ProverResult checkSat(boolean block) {
        if (assertedClauses.isEmpty()) {
            return translateRes(api.checkSat(block));
        } else {
            lastSolution = new HashMap<String, String>();
            lastCEX = new Dag[1];
            if (block) {
                return runEldarica(assertedClauses, fullHornTypes,
                                   lastSolution, lastCEX);
            } else {
                this.executor = Executors.newSingleThreadExecutor();
                this.thread =
                    new PrincessSolverThread(assertedClauses,
                                             fullHornTypes,
                                             lastSolution,
                                             lastCEX);
                this.futureProverResult = executor.submit(this.thread);
                return ProverResult.Running;
            }
        }
    }

    private static ProverResult runEldarica(ArrayList<HornExpr> assertedClauses,
                                            java.util.Map<Predicate, ProverType[]> fullHornTypes,
                                            java.util.Map<String, String> lastSolution,
                                            Dag<Tuple2<ProverFun, ProverExpr[]>>[] lastCEXAr) {
        final ArrayBuffer<HornClauses.Clause> clauses = new ArrayBuffer<HornClauses.Clause>();
        for (HornExpr clause : assertedClauses)
            clauses.$plus$eq(clause.clause);

        lazabs.GlobalParameters$.MODULE$.get().assertions_$eq(false);
				
        if (Options.v().solution) {
                                	               	
            final Either<Map<Predicate, IFormula>, Dag<Tuple2<IAtom, Clause>>> result =
                SimpleWrapper.solve(clauses,
                                    scala.collection.immutable.Map$.MODULE$.<Predicate, Seq<IFormula>> empty(),
                                    Options.v().getSolverOptions().contains("abstract"),
                                    Options.v().getSolverOptions().contains("debug"),
                                    Options.v().dotCEX,
                                    Options.v().getSolverOptions().contains("abstractPO"));
            
            if (result.isLeft()) {
                StringBuffer sol = new StringBuffer();
                sol.append("Solution:\n");
                List<Tuple2<Predicate, IFormula>> ar = result.left().get().toList();
                
                while (!ar.isEmpty()) {
                    Tuple2<Predicate, IFormula> p = ar.head();
                    ar = (List<Tuple2<Predicate, IFormula>>) ar.tail();
                    sol.append("" + p._1() + ": " + SimpleAPI$.MODULE$.pp(p._2()) + "\n");
                    lastSolution.put(p._1().toString(), SimpleAPI$.MODULE$.pp(p._2()));
                }
                if (Options.v().getSolverOptions().contains("debug")) {
                	Log.info(sol.toString());
                }
                return ProverResult.Sat;
            } else {
                lastCEXAr[0] = result.right().get()
                    .map(new scala.runtime.AbstractFunction1<Tuple2<IAtom, Clause>,
                                                             Tuple2<ProverFun, ProverExpr[]>>() {
                            public Tuple2<ProverFun, ProverExpr[]> apply(Tuple2<IAtom, Clause> p) {
                                if (p._1().equals(SimpleWrapper.FALSEAtom()))
                                    // encode FALSE as null
                                    return null;
                                final Predicate pred = p._1().pred();
                                final ProverType[] types = fullHornTypes.get(pred);
                                final ProverType[] flatTypes = ProverTupleType.flatten(types);
                                assert(flatTypes.length == pred.arity());
                                final ProverFun fun = new PredicateFun(pred, types);
                                final ProverExpr[] flatArgs = new ProverExpr[pred.arity()];
                                for (int i = 0; i < flatTypes.length; ++i)
                                    flatArgs[i] = new TermExpr(p._1().args().apply(i), flatTypes[i]);
                                return new Tuple2(fun, ProverTupleExpr.unflatten(flatArgs, types));
                            }
                        });
                return ProverResult.Unsat;
            }
        } else {
            if (SimpleWrapper.isSat(clauses,
                                    scala.collection.immutable.Map$.MODULE$.<Predicate, Seq<IFormula>> empty(),
                                    Options.v().getSolverOptions().contains("abstract"),
                                    Options.v().getSolverOptions().contains("debug"),
                                    Options.v().getSolverOptions().contains("abstractPO")))
                return ProverResult.Sat;
            else
                return ProverResult.Unsat;
        }
    }

    static class PrincessSolverThread implements Runnable {
        private final ArrayList<HornExpr> hornClauses;
        private final java.util.Map<String, String> lastSolution;
        private final Dag<Tuple2<ProverFun, ProverExpr[]>>[] lastCEX;
        private final java.util.Map<Predicate, ProverType[]> fullHornTypes;
        private ProverResult status;

        public PrincessSolverThread(ArrayList<HornExpr> clauses,
                                    java.util.Map<Predicate, ProverType[]> fullHornTypes,
                                    java.util.Map<String, String> lastSolution,
                                    Dag<Tuple2<ProverFun, ProverExpr[]>>[] lastCEX) {
            this.hornClauses = clauses;
            this.fullHornTypes = fullHornTypes;
            this.lastSolution = lastSolution;
            this.lastCEX = lastCEX;
        }

        @Override
        public void run() {
            status = ProverResult.Running;
            status = runEldarica(hornClauses, fullHornTypes,
                                 lastSolution, lastCEX);
        }

        public ProverResult getStatus() {
            return this.status;
        }
    }

	private void killThread() {
		if (this.futureProverResult != null && !this.futureProverResult.isDone()) {
			this.futureProverResult.cancel(true);
		}
		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = null;
		this.futureProverResult = null;
		this.thread = null;
	}

	private ProverResult translateRes(scala.Enumeration.Value result) {
		if (result == ProverStatus$.MODULE$.Sat() || result == ProverStatus$.MODULE$.Invalid())
			return ProverResult.Sat;
		else if (result == ProverStatus$.MODULE$.Unsat() || result == ProverStatus$.MODULE$.Valid())
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
		ProverResult result;
		if (futureProverResult != null) {
			try {
				futureProverResult.get(timeout, TimeUnit.MILLISECONDS);
				result = this.thread.getStatus();
			} catch ( ExecutionException e) {
				e.printStackTrace();
				throw new RuntimeException("solver failed");
			} catch (TimeoutException | InterruptedException e) {
				result = ProverResult.Unknown;
			}
			killThread();
		} else {
			throw new RuntimeException("Start query with checkSat(false) first.");
		}
		return result;

		// return translateRes(api.getStatus(timeout));
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

		final Seq<IFormula> ints = api.getInterpolants(args.toSeq(), Long.MAX_VALUE);

		final ProverExpr[] res = new ProverExpr[partitionSeq.length - 1];
		for (int i = 0; i < partitionSeq.length - 1; ++i)
			res[i] = new FormulaExpr(ints.apply(i));

		return res;
	}

	public void addListener(ProverListener listener) {
		throw new RuntimeException();
	}

    public ProverExpr evaluate(ProverExpr expr) {
        if (((PrincessProverExpr) expr).isBoolean()) {
            return new FormulaExpr(
                     new IBoolLit(
                       api.eval(((PrincessProverExpr) expr).toFormula())));
        } else {
            return new TermExpr(
                     api.evalToTerm(((PrincessProverExpr) expr).toTerm()),
                     ((TermExpr) expr).getType());
        }
    }

	public ProverExpr[] freeVariables(ProverExpr expr) {
		final ArrayList<ProverExpr> res = new ArrayList<ProverExpr>();

		final scala.Tuple3<scala.collection.Set<IVariable>, scala.collection.Set<ConstantTerm>, scala.collection.Set<Predicate>> symTriple;
		if (expr instanceof TermExpr)
			symTriple = SymbolCollector$.MODULE$.varsConstsPreds(((TermExpr) expr).term);
		else
			symTriple = SymbolCollector$.MODULE$.varsConstsPreds(((FormulaExpr) expr).formula);

		final Iterator<IVariable> it1 = symTriple._1().iterator();
		while (it1.hasNext())
			res.add(new TermExpr(it1.next(), getIntType()));

		final Iterator<ConstantTerm> it2 = symTriple._2().iterator();
		while (it2.hasNext())
			res.add(new TermExpr(IConstant$.MODULE$.apply(it2.next()), getIntType()));

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
	public ProverExpr substitute(ProverExpr target, ProverExpr[] from, ProverExpr[] to) {
		assert (from.length == to.length);

		final scala.collection.mutable.HashMap<ConstantTerm, ITerm> constantSubst = new scala.collection.mutable.HashMap<ConstantTerm, ITerm>();
		final scala.collection.mutable.HashMap<Predicate, IFormula> predicateSubst = new scala.collection.mutable.HashMap<Predicate, IFormula>();

		for (int i = 0; i < from.length; ++i) {
			if (from[i] instanceof TermExpr) {
				final ConstantTerm c = ((IConstant) ((TermExpr) from[i]).term).c();
				final ITerm t = ((TermExpr) to[i]).term;
				constantSubst.put(c, t);
			} else {
				final Predicate p = ((IAtom) ((FormulaExpr) from[i]).formula).pred();
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
			final ITerm t2 = ConstantSubstVisitor$.MODULE$.apply(t1, constantSubst);
			final ITerm t3 = PredicateSubstVisitor$.MODULE$.apply(t2, predicateSubst);
			return new TermExpr(t3, target.getType());
		} else {
			final IFormula f1 = ((FormulaExpr) target).formula;
			final IFormula f2 = ConstantSubstVisitor$.MODULE$.apply(f1, constantSubst);
			final IFormula f3 = PredicateSubstVisitor$.MODULE$.apply(f2, predicateSubst);
			return new FormulaExpr(f3);
		}
	}

	public void shutdown() {
		api.shutDown();
	}

	public void reset() {
		api.reset();
                assertedClauses.clear();
                assertedClausesStack.clear();
                fullHornTypes.clear();
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

        private final java.util.Map<Predicate, ProverType[]> fullHornTypes =
          new HashMap<Predicate, ProverType[]> ();

    public ProverExpr mkHornVariable(String name, ProverType type) {
        if (type instanceof ProverTupleType) {
            final ProverTupleType tt = (ProverTupleType)type;
            final ProverExpr[] res = new ProverExpr[tt.getArity()];
            for (int i = 0; i < tt.getArity(); ++i)
                res[i] = mkHornVariable(name + "#" + i, tt.getSubType(i));
            return mkTuple(res);
        } else {
            // always use terms as Horn variables/arguments
            return new TermExpr(api.createConstant(name, type2Sort(type)), type);
        }
    }

    public ProverFun mkHornPredicate(String name, ProverType[] argTypes) {
        ProverType[] flatTypes = ProverTupleType.flatten(argTypes);
        ArrayBuffer<Sort> flatSorts = new ArrayBuffer<> ();
        for (int i = 0; i < flatTypes.length; ++i)
            flatSorts.$plus$eq(type2Sort(flatTypes[i]));
        Predicate pred = api.createRelation(name, flatSorts);
        ProverType[] argCopy = Arrays.copyOf(argTypes, argTypes.length);
        fullHornTypes.put(pred, argCopy);
        return new PredicateFun(pred, argCopy);
    }

	/**
	 * The head literal can either be constructed using
	 * <code>mkHornPredicate</code>, or be the formula <code>false</code>.
	 */
	public ProverHornClause mkHornClause(ProverExpr head, ProverExpr[] body, ProverExpr constraint) {
		IFormula rawHead = ((FormulaExpr) head).formula;
		if ((rawHead instanceof IBoolLit) && !((IBoolLit) rawHead).value())
			rawHead = SimpleWrapper.FALSEAtom();

		final ArrayBuffer<IAtom> rawBody = new ArrayBuffer<IAtom>();
		for (int i = 0; i < body.length; ++i)
			rawBody.$plus$eq((IAtom) ((FormulaExpr) body[i]).formula);

		final HornClauses.Clause clause = SimpleWrapper.clause((IAtom) rawHead, rawBody.toList(),
				((PrincessProverExpr) constraint).toFormula());

                // TODO: only execute this when assertions are switched on?
                SimpleWrapper.typeCheck(clause);

		return new HornExpr(clause);
	}

	@Override
	public void setHornLogic(boolean b) {
		// ignore

	}

	@Override
	public String toString() {
		return "eldarica";
	}

	public void printRules(){}
	
	////////////////////////////////////////////////////////////////////////////
	// Some functions for outputing SMT-LIB

	public String toSMTLIBDeclaration(ProverFun fun) {
		if (fun instanceof PredicateFun) {
			final PredicateFun predFun = (PredicateFun) fun;
			return predFun.toSMTLIBDeclaration();
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public String toSMTLIBFormula(ProverHornClause clause) {
		return ((HornExpr) clause).toSMTLIBFormula();
	}

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
        public String toSMTLIBScript(java.util.List<ProverHornClause> clauses) {
            ArrayBuffer<IFormula> clauseFors = new ArrayBuffer<> ();
            HashSet<Predicate> allPreds = new HashSet<> ();

            for (ProverHornClause c : clauses) {
                IFormula f = ((HornExpr)c).toFormula();
                ((HornExpr)c).collectPreds(allPreds);
                clauseFors.$plus$eq(f);
            }

            allPreds.$minus$eq(SimpleWrapper.FALSEAtom().pred());
System.out.println("all preds: " + allPreds);
            PrintStream originalOut = scala.Console.out();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream newOut = new PrintStream(baos);
            scala.Console.setOut(newOut);

            ap.parser.SMTLineariser.apply("JayHorn-clauses", "HORN", "unknown",
                                          new ArrayBuffer(),
                                          allPreds.toSeq(), // .sortBy(_.name),
                                          clauseFors);

            scala.Console.flush();
            scala.Console.setOut(originalOut);
            return baos.toString();
        }

	public void parseSMTLIBFormula(final String formula) {
//		Tuple3<Seq<IFormula>, scala.collection.immutable.Map<IFunction, SMTFunctionType>, scala.collection.immutable.Map<ConstantTerm, SMTType>> triple = 
				
		api.execSMTLIB(new StringReader(formula));	
//		api.extractSMTLIBAssertionsSymbols(new StringReader(formula));
		
	}

	@Override
	public ProverResult query(ProverExpr relation, boolean isTimed) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRule(ProverExpr hornRule) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProverExpr getCex() {
		// TODO Auto-generated method stub
		return null;
	}
}
