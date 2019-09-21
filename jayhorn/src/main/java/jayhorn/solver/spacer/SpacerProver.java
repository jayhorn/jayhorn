
package jayhorn.solver.spacer;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Verify;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.ArrayExpr;
import com.microsoft.z3.ArraySort;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Fixedpoint;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.InterpolationContext;
import com.microsoft.z3.Model;
import com.microsoft.z3.Params;
import com.microsoft.z3.Quantifier;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import jayhorn.Options;
import jayhorn.solver.BoolType;
import jayhorn.solver.IntType;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverListener;
import jayhorn.solver.ProverResult;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverTupleType;
import jayhorn.solver.ProverType;

/**
 * @author teme
 *
 */
public class SpacerProver implements Prover {

	private Context ctx;
	private Solver solver;

//	private boolean initStringHornClauses = false;

	private HashMap<String, String> cfg = new HashMap<String, String>();
	private Fixedpoint fx;

	static class SpacerSolverThread implements Runnable {
		private final Fixedpoint fx;
		private Status status;
		private BoolExpr tgt;

		public SpacerSolverThread(Fixedpoint fx, BoolExpr tgt) {
			this.fx = fx;
			this.tgt = tgt;
		}

		@Override
		public void run() {
			try {
				this.status = this.fx.query(this.tgt);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public Status getStatus() {
			return this.status;
		}
	}

	public SpacerProver(){
		this.cfg.put("model", "true");
		try {
			this.ctx = new Context(this.cfg);
			createSolver();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private void createSolver() {
		try {
				this.solver = this.ctx.mkSolver();
				this.fx = this.ctx.mkFixedpoint();
				Params params = this.ctx.mkParams();
				params.add(":engine", "spacer");
				params.add (":use_heavy_mev", true);
				params.add (":reset_obligation_queue", true);
				params.add (":pdr.flexible_trace", false);
				if (Options.v().getSolverOptions().contains("spacer_no_pp")){
					// No pre-processing
					params.add (":xform.slice", false);
					params.add (":xform.inline-linear", false);
					params.add (":xform.inline-eager", false);
				}
				params.add (":pdr.utvpi", false);
			    //params.set (":pdr.flexible_trace", FlexTrace);

			    // -- disable propagate_variable_equivalences in tail_simplifier
			    params.add (":xform.tail_simplifier_pve", false);
			    //params.set (":xform.subsumption_checker", Subsumption);
//			    params.add (":order_children", HornChildren ? 1U : 0U);
//			    params.add (":pdr.max_num_contexts", PdrContexts);
				//this.solver.setParameters(params);
				this.fx.setParameters(params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	protected Expr unpack(ProverExpr exp) {
		if (exp instanceof SpacerTermExpr) {
			return ((SpacerTermExpr) exp).getExpr();
		} else if (exp instanceof SpacerBoolExpr) {
			return ((SpacerBoolExpr) exp).getExpr();
		}
		throw new RuntimeException("not implemented "
				+ exp.getType().toString());
	}

	protected Expr[] unpack(ProverExpr[] exp) {
		Expr[] res = new Expr[exp.length];
		for (int i = 0; i < exp.length; i++) {
			res[i] = unpack(exp[i]);
		}
		return res;
	}

	protected ProverExpr pack(Expr e) throws Z3Exception {
		if (e instanceof BoolExpr) {
			return new SpacerBoolExpr((BoolExpr) e);
		} else {
			return new SpacerTermExpr(e, pack(e.getSort()));
		}
	}

	protected ProverExpr[] pack(Expr[] e) throws Z3Exception {
		ProverExpr[] res = new ProverExpr[e.length];
		for (int i = 0; i < e.length; i++) {
			res[i] = pack(e[i]);
		}
		return res;
	}

	protected Sort unpack(ProverType type) throws Z3Exception {
		if (type instanceof IntType) {
			return ctx.getIntSort();
		} else if (type instanceof BoolType) {
			return ctx.getBoolSort();
		} else if (type instanceof SpacerArrayType) {
			return ((SpacerArrayType) type).getSort();
		}
		throw new RuntimeException( type + "not implemented");
	}

	protected ProverType pack(Sort type) throws Z3Exception{
		if (type.equals(ctx.getIntSort())) {
			return this.getIntType();
		} else if (type.equals(ctx.getBoolSort())) {
			return this.getBooleanType();
		} else if (type.equals(ctx.getRealSort())) {
			throw new RuntimeException("not implemented");
		} else if (type instanceof ArraySort) {
			ArraySort as = (ArraySort) type;
			return new SpacerArrayType(as, pack(as.getRange()),
					pack(as.getDomain()));
		} else {
			throw new RuntimeException("not implemented");
		}
	}

	@Override
	public ProverType getBooleanType() {
		return BoolType.INSTANCE;
	}

	@Override
	public ProverType getIntType() {
		return IntType.INSTANCE;
	}

	@Override
	public ProverType getArrayType(ProverType[] argTypes, ProverType resType) {
		try{
		if (argTypes.length == 0) {
			throw new RuntimeException("Array should have at one dimension");
		}
		ProverType t = resType;
		Sort sort = unpack(resType);
		// fold everything but the last iteration.
		for (int i = argTypes.length - 1; i > 0; i--) {
			sort = lookupArraySort(unpack(argTypes[i]), sort);
		}
		return new SpacerArrayType(lookupArraySort(unpack(argTypes[0]), sort),
				argTypes[0], t);
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private ArraySort lookupArraySort(Sort idx, Sort val) throws Z3Exception{
		return this.ctx.mkArraySort(idx, val);
	}

	@Override
	public ProverExpr mkBoundVariable(int deBruijnIndex, ProverType type) {
		try{
			return new SpacerTermExpr(ctx.mkBound(deBruijnIndex, unpack(type)), type);
		}catch (Z3Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkVariable(String name, ProverType type){
		try{
			Expr exp = null;
			if (type instanceof IntType) {
				exp = ctx.mkIntConst(name);
			} else if (type instanceof BoolType) {
				exp = ctx.mkBoolConst(name);
			} else if (type instanceof SpacerArrayType) {
				exp = ctx.mkArrayConst(name,
						unpack(((SpacerArrayType) type).getIndexType()),
						unpack(((SpacerArrayType) type).getValueType()));
			} else if (type instanceof ProverTupleType){
				 final ProverTupleType tt = (ProverTupleType)type;
		            final ProverExpr[] res = new ProverExpr[tt.getArity()];
		            for (int i = 0; i < tt.getArity(); ++i)
		                res[i] = mkVariable(name + "_" + i, tt.getSubType(i));
		            return mkTuple(res);
			} else {
				throw new RuntimeException("not implemented");
			}
			return new SpacerTermExpr(exp, type);
		}catch (Z3Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}


	@Override
	public SpacerFun mkUnintFunction(String name, ProverType[] argTypes,
			ProverType resType) {
		try{
			ProverType[] flatType = ProverTupleType.flatten(argTypes);
			Sort[] argSorts = new Sort[flatType.length];
			for (int i = 0; i < flatType.length; i++) {
						argSorts[i] = unpack(flatType[i]);
			}
			
			return new SpacerFun(ctx.mkFuncDecl(name, argSorts, unpack(resType)), ctx,
					resType);
		}catch (Z3Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * TODO: Define a new interpreted function. The body is supposed to contain
	 * bound variables with indexes <code>0, 1, ..., (n-1)</code> representing
	 * the arguments of the function.
	 */
	@Override
	public ProverFun mkDefinedFunction(String name, ProverType[] argTypes,
			ProverExpr body){
			final ProverExpr b = body;
			return new ProverFun() {
				public ProverExpr mkExpr(ProverExpr[] args){
					final Expr[] z3args = new Expr[args.length];
					try {
						for (int i = 0; i < args.length; i++) {
							if (args[i] instanceof SpacerTermExpr) {
								z3args[i] = ((SpacerTermExpr) args[i]).getExpr();
							} else if (args[i] instanceof SpacerBoolExpr) {
								z3args[i] = ctx.mkITE(((SpacerBoolExpr) args[i]).getExpr(),
										ctx.mkInt(0), ctx.mkInt(1));
							}
						}
						if (b instanceof SpacerTermExpr) {
							return new SpacerTermExpr(unpack(b).substituteVars(z3args),
									b.getType());
						} else {
							return new SpacerBoolExpr((BoolExpr) unpack(b).substituteVars(
									z3args));
						}
					} catch (Exception e) {
						throw new RuntimeException(e.getMessage());
					}
				}
				
			};
	}

	@Override
	public ProverExpr mkAll(ProverExpr body, ProverType type) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverExpr mkEx(ProverExpr body, ProverType type) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverExpr mkTrigger(ProverExpr body, ProverExpr[] triggers) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverExpr mkEq(ProverExpr left, ProverExpr right)  {
		try{
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
			return new SpacerBoolExpr(ctx.mkEq(unpack(left), unpack(right)));
		}catch (Exception e){
			throw  new RuntimeException (e.getMessage());
		}
	}

	@Override
	public ProverExpr mkLiteral(boolean value) {
		try{
			return new SpacerBoolExpr(ctx.mkBool(value));
		} catch (Exception e){
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkNot(ProverExpr body){
		try{
			return new SpacerBoolExpr(ctx.mkNot((BoolExpr) unpack(body)));
		}catch (Exception e){
				throw new RuntimeException(e.getMessage());
			}
		}

		@Override
		public ProverExpr mkAnd(ProverExpr left, ProverExpr right)   {
			try
			{
				return new SpacerBoolExpr(ctx.mkAnd((BoolExpr) unpack(left),
						(BoolExpr) unpack(right)));
			}catch (Exception e){
				throw new RuntimeException(e.getMessage());
			}

		}


	@Override
	public ProverExpr mkAnd(ProverExpr ... args)  {
		try {
			BoolExpr[] bargs = new BoolExpr[args.length];
			for (int i = 0; i < args.length; i++) {
				bargs[i] = (BoolExpr) unpack(args[i]);
			}
			return new SpacerBoolExpr(ctx.mkAnd(bargs));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkOr(ProverExpr left, ProverExpr right)  {
		try {
			return new SpacerBoolExpr(ctx.mkOr((BoolExpr) unpack(left),
					(BoolExpr) unpack(right)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkOr(ProverExpr ... args) {
		try {
			BoolExpr[] bargs = new BoolExpr[args.length];
			for (int i = 0; i < args.length; i++) {
				bargs[i] = (BoolExpr) unpack(args[i]);
			}
			return new SpacerBoolExpr(ctx.mkOr(bargs));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkImplies(ProverExpr left, ProverExpr right)  {
		try {
			return new SpacerBoolExpr(ctx.mkImplies((BoolExpr) unpack(left),
					(BoolExpr) unpack(right)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkIte(ProverExpr cond, ProverExpr thenExpr,
			ProverExpr elseExpr)   {
		try {
			return new SpacerTermExpr(ctx.mkITE((BoolExpr) unpack(cond),
					unpack(thenExpr), unpack(elseExpr)), thenExpr.getType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkLiteral(int value)   {
		try {
			return new SpacerTermExpr(ctx.mkInt(value), this.getIntType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkLiteral(BigInteger value) {
		try {
			return new SpacerTermExpr(ctx.mkInt(value.longValue()), this.getIntType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkPlus(ProverExpr left, ProverExpr right)  {
		try {
			return new SpacerTermExpr(ctx.mkAdd((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)), left.getType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkPlus(ProverExpr[] args)  {
		try {
			ArithExpr[] aargs = new ArithExpr[args.length];
			for (int i = 0; i < args.length; i++) {
				aargs[i] = (ArithExpr) unpack(args[i]);
			}
			return new SpacerTermExpr(ctx.mkAdd(aargs), this.getBooleanType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkMinus(ProverExpr left, ProverExpr right)  {
		try {
			return new SpacerTermExpr(ctx.mkSub((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)), left.getType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkNeg(ProverExpr arg)  {
		try {
			return new SpacerTermExpr(ctx.mkUnaryMinus((ArithExpr) unpack(arg)),
					arg.getType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkEDiv(ProverExpr num, ProverExpr denom)  {
		try {
			return new SpacerTermExpr(ctx.mkDiv((ArithExpr) unpack(num),
					(ArithExpr) unpack(num)), this.getIntType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkEMod(ProverExpr num, ProverExpr denom)  {
		try {
			return new SpacerTermExpr(ctx.mkMod((IntExpr) unpack(num),
					(IntExpr) unpack(num)), this.getIntType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkTDiv(ProverExpr num, ProverExpr denom) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverExpr mkTMod(ProverExpr num, ProverExpr denom) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverExpr mkMult(ProverExpr left, ProverExpr right)  {
		try {
			return new SpacerTermExpr(ctx.mkMul((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)), left.getType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkGeq(ProverExpr left, ProverExpr right) {
		try {
			return new SpacerBoolExpr(ctx.mkGe((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkGt(ProverExpr left, ProverExpr right) {
		try {
			return new SpacerBoolExpr(ctx.mkGt((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkLeq(ProverExpr left, ProverExpr right) {
		try {
			return new SpacerBoolExpr(ctx.mkLe((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkLt(ProverExpr left, ProverExpr right) {
		try {
			return new SpacerBoolExpr(ctx.mkLt((ArithExpr) unpack(left),
					(ArithExpr) unpack(right)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkSelect(ProverExpr ar, ProverExpr[] indexes) {
		try {
			if (indexes.length == 1) {
				return new SpacerTermExpr(ctx.mkSelect((ArrayExpr) unpack(ar),
						unpack(indexes[0])), ar.getType());
			}
			throw new RuntimeException("not implemented");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr mkStore(ProverExpr ar, ProverExpr[] indexes,
			ProverExpr value) {
		try {
			if (indexes.length == 1) {
				return new SpacerTermExpr(ctx.mkStore((ArrayExpr) unpack(ar),
						unpack(indexes[0]), unpack(value)), ar.getType());
			}
//			List<Expr> idxs = new LinkedList<Expr>();
//			for (ProverExpr e : indexes) {
//				idxs.add(unpack(e));
//			}
			throw new RuntimeException("not implemented");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void push() {
		try {
			this.solver.push();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void pop() {
		try {
			this.solver.pop();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private SortedMap<Integer, List<BoolExpr>> interpolationPattern = new TreeMap<Integer, List<BoolExpr>>();
	private int interpolationPartition = -1;

	@Override
	public void addAssertion(ProverExpr assertion) {
		try {
			if (assertion instanceof SpacerHornExpr) {
				SpacerHornExpr hc = (SpacerHornExpr) assertion;
				
				BoolExpr head = (BoolExpr) unpack(hc.getHead());
				BoolExpr body = (BoolExpr) unpack(hc.getConstraint());
				
				Set<Expr> freeVars = new HashSet<Expr>();
				freeVars.addAll(freeVariables(head));
				
				if (hc.getBody().length>0) {
					BoolExpr[] conj = new BoolExpr[hc.getBody().length];
					int i=0;
					for (Expr e : unpack(hc.getBody())) {
						freeVars.addAll(freeVariables(e));
						conj[i]=(BoolExpr)e;
						i++;
					}
					BoolExpr b = (conj.length==1) ? conj[0] : ctx.mkAnd(conj); 
					if (body.equals(ctx.mkTrue())) {
						body = b;
					} else {
						body = ctx.mkAnd(b, body);
					}
				} 
				//from Nikolajs example 			
				BoolExpr asrt;
				if (freeVars.size()>0) {				
					asrt =  ctx.mkForall(freeVars.toArray(new Expr[freeVars.size()]), ctx.mkImplies(body, head), 1, null, null, null, null);				
					this.solver.add(asrt);
				} else {
					asrt =  ctx.mkImplies(body, head);
				}
				this.solver.add(asrt);
			} else if (assertion instanceof SpacerBoolExpr) {
				BoolExpr asrt = (BoolExpr) unpack(assertion);
				this.solver.add(asrt);

				if (interpolationPartition >= 0
						&& ctx instanceof InterpolationContext) {
					if (!this.interpolationPattern
							.containsKey(this.interpolationPartition)) {
						this.interpolationPattern.put(this.interpolationPartition,
								new LinkedList<BoolExpr>());
					}
					this.interpolationPattern.get(this.interpolationPartition).add(
							asrt);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private ExecutorService executor = null;
	private Future<?> future = null;
	private SpacerSolverThread thread = null;

	@Override
	public ProverResult checkSat(boolean block) {
		throw new UnsupportedOperationException();
	}

	private ProverResult translateResult(Status status) {
		if (status == Status.SATISFIABLE) {
			return ProverResult.Sat;
		} else if (status == Status.UNSATISFIABLE) {
			return ProverResult.Unsat;
		} else {
			return ProverResult.Unknown;
		}
	}

	private void killThread() {
		if (this.future != null && !this.future.isDone()) {
			this.future.cancel(true);
		}
		if (this.executor != null) {
			this.executor.shutdown();
		}
		this.executor = null;
		this.future = null;
		this.thread = null;
	}

	@Override
	public ProverResult nextModel(boolean block) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverResult getResult(boolean block) {
		ProverResult result;
		if (future != null) {
			if (block) {
				try {
					future.get();
				} catch (InterruptedException | ExecutionException e) {
					throw new RuntimeException("solver failed");
				}
				result = translateResult(this.thread.getStatus());
				killThread();
			} else {
				if (future.isDone()) {
					result = translateResult(this.thread.getStatus());
					killThread();
				} else {
					result = ProverResult.Running;
				}
			}
		} else {
			throw new RuntimeException("Start query with check sat first.");
		}
		return result;
	}

	@Override
	public ProverResult getResult(long timeoutInSeconds) {
		ProverResult result;
		if (future != null) {
			try {
				future.get(timeoutInSeconds, TimeUnit.SECONDS);
				result = translateResult(this.thread.getStatus());
			} catch (InterruptedException | ExecutionException e) {
				throw new RuntimeException("solver failed");
			} catch (TimeoutException e) {
				result = ProverResult.Unknown;
			}
			killThread();
		} else {
			throw new RuntimeException("Start query with check sat first.");
		}
		return result;
	}

	@Override
	public ProverResult stop() {
		// TODO: what is this used for?
		ProverResult res = getResult(false);
		killThread();
		if (res == ProverResult.Running)
			return ProverResult.Unknown;
		return res;
	}

	@Override
	public void setConstructProofs(boolean b) {
		try {
			killThread();
			if (b) {
				cfg.put("proof", "true");
				this.ctx = new InterpolationContext(this.cfg);
			} else {
				cfg.put("proof", "false");
				this.ctx = new Context(this.cfg);

			}
			createSolver();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	
	@Override
	public void setPartitionNumber(int num) {
		if (!(ctx instanceof InterpolationContext)) {
			throw new RuntimeException("call setConstructProofs(true) first");
		}
		if (num < 0) {
			throw new RuntimeException("only positive partition numbers please");
		}
		this.interpolationPartition = num;
	}

	@Override
	public ProverExpr[] interpolate(int[][] partitionSeq) {
		if (!(ctx instanceof InterpolationContext)) {
			throw new RuntimeException("call setConstructProofs(true) first");
		}
		InterpolationContext ictx = (InterpolationContext) ctx;
		// BoolExpr iA = ictx.MkInterpolant(A);
		// BoolExpr AB = ictx.mkAnd(A, B);
		// BoolExpr pat = ictx.mkAnd(iA, B);
		
		List<BoolExpr> patternList = new LinkedList<BoolExpr>();
		try {
			for (Entry<Integer, List<BoolExpr>> entry : this.interpolationPattern
					.entrySet()) {
				BoolExpr conj = ictx.mkAnd(entry.getValue().toArray(
						new BoolExpr[entry.getValue().size()]));
				patternList.add(ictx.MkInterpolant(conj));
			}
			patternList.add(ictx.mkTrue());

			BoolExpr pat = ictx.mkAnd(patternList.toArray(new BoolExpr[patternList
					.size()]));
			Params params = ictx.mkParams();
			Expr proof = this.solver.getProof();
			
			Expr[] interps = ictx.GetInterpolant(proof, pat, params);
			
			List<SpacerBoolExpr> result = new LinkedList<SpacerBoolExpr>();
			for (int i = 0; i < interps.length; i++) {
				result.add(new SpacerBoolExpr((BoolExpr) interps[i]));
			}
			return result.toArray(new ProverExpr[result.size()]);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void addListener(ProverListener listener) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public ProverExpr evaluate(ProverExpr expr) {
		try {
			Model m = this.solver.getModel();
			if (m == null) {
				throw new RuntimeException("no model :(");
			}
			Expr e = unpack(expr);

			if (e.isConst()) {
				return new SpacerTermExpr(m.getConstInterp(e), expr.getType());
			}

			return new SpacerTermExpr(m.evaluate(e, false), expr.getType());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public ProverExpr[] freeVariables(ProverExpr expr) {
		try {
			List<Expr> freeVars = freeVariables(unpack(expr));
			List<ProverExpr> freeProverVars = new LinkedList<ProverExpr>();
			for (Expr e : freeVars) {
				freeProverVars.add(pack(e));
			}
			return freeProverVars.toArray(new ProverExpr[freeProverVars.size()]);
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	private List<Expr> freeVariables(Expr e) {
		try {
			List<Expr> result = new LinkedList<Expr>();
			if (e.isConst() && !e.equals(ctx.mkTrue()) && !e.equals(ctx.mkFalse())) {

				result.add(e);
			} else if (e.isQuantifier()) {
				Quantifier q = (Quantifier) e;
				
				q.getBoundVariableNames();
				freeVariables(((Quantifier) e).getBody());
				throw new RuntimeException("not implemented");
			} else if (e.isApp()) {
				
				for (Expr child : e.getArgs()) {
					result.addAll(freeVariables(child));
				}
			} else if (e.isNumeral()) {
				// ignore
			} else {
				throw new RuntimeException("not implemented "
						+ e.getClass().toString());
			}

			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	private List<Expr> freeVariablesHead(Expr e) {
		try {
			List<Expr> result = new LinkedList<Expr>();
				if (e.isQuantifier()) {
				Quantifier q = (Quantifier) e;
				
				q.getBoundVariableNames();
				freeVariables(((Quantifier) e).getBody());
				throw new RuntimeException("not implemented");
			} else if (e.isApp()) {
				
				for (Expr child : e.getArgs()) {
					result.addAll(freeVariables(child));
				}
			} else if (e.isNumeral()) {
				// ignore
			} else {
				throw new RuntimeException("not implemented "
						+ e.getClass().toString());
			}

			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	@Override
	public ProverExpr substitute(ProverExpr target, ProverExpr[] from,
			ProverExpr[] to) {
		try {
			Expr e = unpack(target).substitute(unpack(from), unpack(to));
			if (target instanceof SpacerBoolExpr) {
				return new SpacerBoolExpr((BoolExpr) e);
			} else {
				return new SpacerTermExpr(e, target.getType());
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void shutdown() {
		// TODO Is this true?
		killThread();
		try {
			this.solver.reset();
			this.solver.dispose();
			ctx.dispose();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	@Override
	public void reset() {
		killThread();
		try {
			this.solver.reset();
			this.interpolationPattern = new TreeMap<Integer, List<BoolExpr>>();
			this.interpolationPartition = -1;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	
	// //////////////////////////////////////////////////////////////////////////
	// Horn clause interface



	@Override
	public ProverExpr mkHornVariable(String name, ProverType type) {
		return mkVariable(name, type);
	}
	
	private String replaceName(String name){
		String new_name = name;
		new_name = new_name.replaceAll(" ", "_");
		new_name = new_name.replaceAll("\\(|\\)|<|>|:|/", "");
		 return new_name;
	}
	
	/**
	 * Make Horn Predicate
	 */
	public SpacerFun mkHornPredicate(String name, ProverType[] argTypes) {
		try {
			SpacerFun fun = this.mkUnintFunction(this.replaceName(name), argTypes, this.getBooleanType());
			this.fx.registerRelation(fun.getFun());
			return fun;
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * The head literal can either be constructed using
	 * <code>mkHornPredicate</code>, or be the formula <code>false</code>.
	 */
	@Override
	public ProverHornClause mkHornClause(ProverExpr head, ProverExpr[] body, ProverExpr constraint
//			, String name
	) {
		return new SpacerHornExpr(head, body, constraint);
	}

//	@Override
//	public ProverHornClause mkHornClause(ProverExpr head, ProverExpr[] body, ProverExpr constraint) {
//		return mkHornClause(head, body, constraint, null);
//	}

//	@Override
//	public void initializeStringHornClauses(Iterable<ProverHornClause> stringHornClauses) {
//		if (!initStringHornClauses) {
//			for (ProverHornClause hc : stringHornClauses) {
//				this.addAssertion(hc);
//			}
//			initStringHornClauses = true;
//		}
//	}
//
//	@Override
//	public boolean isInitializedStringHornClauses() {
//		return initStringHornClauses;
//	}

	@Override
	public void setHornLogic(boolean b) {
		createSolver();		
	}
	
	public String toString(){
		return "spacer";
	}
	
	
	/**
	 * Add Fact
	 **/
	public void addFact(SpacerFun fact){
		try {
			this.fx.addFact(fact.getFun());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}
	/**
	 * Add a Horn rule
	 * @param relation
	 */
	public void addRule(ProverExpr relation){
		try {
			BoolExpr asrt = null;
			if (relation instanceof SpacerHornExpr) {
				SpacerHornExpr hc = (SpacerHornExpr) relation;
				
				BoolExpr head = (BoolExpr) unpack(hc.getHead());
				BoolExpr body = (BoolExpr) unpack(hc.getConstraint());
				//System.out.println("Head -> " + head);
				//System.out.println("Body -> " + body);
				Set<Expr> freeVars = new HashSet<Expr>();
				
				freeVars.addAll(freeVariablesHead(head));
				
				if (hc.getBody().length>0) {
					BoolExpr[] conj = new BoolExpr[hc.getBody().length];
					int i=0;
					for (Expr e : unpack(hc.getBody())) {
						freeVars.addAll(freeVariables(e));
						conj[i]=(BoolExpr)e;
						i++;
					}
					BoolExpr b = (conj.length==1) ? conj[0] : ctx.mkAnd(conj); 
					if (body.equals(ctx.mkTrue())) {
						body = b;
					} else {
						body = ctx.mkAnd(b, body);
					}
				} 
				//from Nikolajs example 			
				
				if (freeVars.size()>0) {
					asrt =  ctx.mkForall(freeVars.toArray(new Expr[freeVars.size()]), 
							ctx.mkImplies(body, head), 1, null, null, null, null);	
					//System.out.println("ASRT -> " + asrt);

				} else {
					asrt =  ctx.mkImplies(body, head);
				}
			} else if (relation instanceof SpacerBoolExpr) {
				asrt = (BoolExpr) unpack(relation);	

			}

			this.fx.addRule(asrt, null);
//			System.out.println(this.fx);
//			System.out.println("----------------");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		
	}
	
	
	/**
	 * Query Rechability
	 */
	public ProverResult query(ProverExpr tgt, boolean isTimed){
		try{
			if (isTimed){
				return this.queryThread(tgt);
			} else{
				Status status = null;
				BoolExpr asrt = (BoolExpr) unpack(tgt);
				status = this.fx.query(asrt);
				return this.translateResult(status);
			}
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	/**
	 * A thread to query for reachability
	 * @param tgt
	 * @return
	 */
	private ProverResult queryThread(ProverExpr tgt) {
		try {
			if (future != null && !future.isDone()) {
					throw new RuntimeException("Another check is still running.");
				}
				BoolExpr asrt = (BoolExpr) unpack(tgt);
				this.executor = Executors.newSingleThreadExecutor();
				this.thread = new SpacerSolverThread(this.fx, asrt);
				//System.out.println(this.thread.toString());
				this.future = executor.submit(this.thread);
				return ProverResult.Running;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	  /**
     * Retrieve satisfying instance or instances of solver, or definitions for
     * the recursive predicates that show unsatisfiability.
     *
     * TODO Fix change to SpacerBoolExpr
     **/
    public Expr getAnswer() 
    {
    	try {
    		return this.fx.getAnswer();
		} catch (Z3Exception e) {
			throw new RuntimeException(e.getMessage());
		}
    	
    }

    public ProverExpr getCex(){
    	throw new UnsupportedOperationException();
     }
    /**
     * Retrieve explanation why fixedpoint engine returned status Unknown.
     **/
    public String getReasonUnknown()
    {
       try {
    	   return this.fx.getReasonUnknown();
       } catch (Z3Exception e) {
    	   throw new RuntimeException(e.getMessage());
       }
    }

    /**
     * Retrieve the number of levels explored for a given predicate.
     **/
    public int getNumLevels(SpacerFun predicate) throws Z3Exception
    {
    	try {
    		return this.fx.getNumLevels(predicate.getFun());
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
    }

    /**
     * Retrieve the cover of a predicate.
     *
     **/
    public Expr getCoverDelta(int level, SpacerFun predicate) throws Z3Exception
    {
    	try {
    		return this.fx.getCoverDelta(level, predicate.getFun());
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
    }

    /**
     * Add <tt>property</tt> about the <tt>predicate</tt>. The property is added
     * at <tt>level</tt>.
     **/
    public void addCover(int level, SpacerFun predicate, SpacerTermExpr property)
    {
    	try {
    		this.fx.addCover(level, predicate.getFun(), property.getExpr());
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
       
    }


    /**
     * Get Ground SAT values
     * 
     * TODO Change return type
     **/
    public Expr getGroundSatAnswer()
    {
    	try {
    		return this.fx.getGroundSatAnswer();
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}

    }

    /**
     * Get Rules Along Trace
     *
     * TODO Change return type
     **/
    public Expr getRulesAlongTrace() 
    {
    	try {
    		return this.fx.getRulesAlongTrace();
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}    
    }


    /**
     * Get Rule Names Along Trace
     *
     * TODO Change return type
     **/
    public Expr getRuleNamesAlongTrace() 
    {
    	try {
    		return this.fx.getRuleNamesAlongTrace();
    	} catch (Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}    
    }  
    
////////////////////////////////////////////////////////////////////////////
// Some functions for outputing SMT-LIB

    public String toSMTLIBDeclaration(ProverFun fun) {
    	throw new UnsupportedOperationException();
    }

    public String toSMTLIBFormula(ProverHornClause clause) {
    	throw new UnsupportedOperationException();
    }

    public void parseSMTLIBFormula(final String formula) {
    	throw new UnsupportedOperationException();
    }

    public String toSMTLIBScript(List<ProverHornClause> clauses) {
    	throw new UnsupportedOperationException();
    }

    public void printRules() {
    	try{
    	BoolExpr[] rules = this.fx.getRules();
    	System.out.println("; Generated by JayHorn" );
    	System.out.println("; Number of rules:" + rules.length);
    	System.out.println(this.fx);
    	}catch (Exception e){
    		throw new RuntimeException(e.getMessage());
    	}
    }
    
    public java.util.Map<String, String> getLastSolution() {
    	throw new UnsupportedOperationException();
	}


	 public ProverType getTupleType(ProverType[] subTypes) {
	        return new ProverTupleType(Arrays.copyOf(subTypes, subTypes.length));
	    }
	 
	
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

}



