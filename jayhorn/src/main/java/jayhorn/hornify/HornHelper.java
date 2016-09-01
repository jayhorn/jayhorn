package jayhorn.hornify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverType;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.InterProceduralPullPushOrdering;

public class HornHelper {

	private static HornHelper hh;

	public static void resetInstance() {
		hh = null;	
	}
		
	public static HornHelper hh() {
		if (null == hh) {
			hh = new HornHelper();
		}
		return hh;
	}

	private HornHelper() {
	}

	private Map<String, MethodContract> methodContracts = new LinkedHashMap<String, MethodContract>();
	
	private Map<ClassVariable, ProverFun> classInvariants = new LinkedHashMap<ClassVariable, ProverFun>();
	
	
	public InterProceduralPullPushOrdering ppOrdering;
	
	/**
	 * Make InterProcedural Push/Pull Ordering
	 * @param program
	 */
	public void mkPPOrdering(Program program){
		ppOrdering = new InterProceduralPullPushOrdering(program.getEntryPoints()[0]);
	}

	/**
	 * Creates a ProverType from a Type.
	 * TODO: not fully implemented.
	 * 
	 * @param p
	 * @param t
	 * @return
	 */
	public ProverType getProverType(Prover p, Type t) {
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
			//System.err.println("Warning: translating " + t + " as prover type int");
			return p.getIntType();
		}
		throw new IllegalArgumentException("don't know what to do with " + t);
	}

	
	public ProverFun freshHornPredicate(Prover p, String name, List<Variable> sortedVars) {
		return genHornPredicate(p, name, sortedVars);
	}

	public ProverFun genHornPredicate(Prover p, String name, List<Variable> sortedVars) {
		final List<ProverType> types = new LinkedList<ProverType>();
		for (Variable v : sortedVars)
			types.add(getProverType(p, v.getType()));
		return p.mkHornPredicate(name, types.toArray(new ProverType[types.size()]));
	}
	
	private int varNum = 0;

	public int newVarNum() {
		return varNum++;
	}
	

	public void createVarMap(Prover p, List<Variable> cfgVars, List<ProverExpr> proverVars,
			Map<Variable, ProverExpr> varMap) {
		for (Variable v : cfgVars) {
			ProverExpr e = varMap.get(v);
			if (e == null) {
				e = p.mkHornVariable(v.getName() + "_" + newVarNum(), getProverType(p, v.getType()));
				varMap.put(v, e);
			}
			proverVars.add(e);
		}
	}

	public List<Variable> setToSortedList(Set<Variable> set) {
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
	
	public void mkMethodContract(Program program, Prover p){	
		for (Method method : program.getMethods()) {
			final List<Variable> inParams = new ArrayList<Variable>();
			inParams.addAll(method.getInParams());
			final List<Variable> postParams = new ArrayList<Variable>();
			postParams.addAll(method.getInParams());
			if (!method.getOutParam().isEmpty()) {
				postParams.addAll(method.getOutParam());
			} else if (!method.getReturnType().isEmpty()) {
				int ctr = 0;
				for (Type tp : method.getReturnType()) {
					postParams.add(new Variable("resultVar" + (ctr++), tp));
				}
			}

			final ProverFun prePred = freshHornPredicate(p, method.getMethodName() + "_pre", inParams);
			final ProverFun postPred = freshHornPredicate(p, method.getMethodName() + "_post", postParams);

			Log.debug("method: " + method.getMethodName());
			Log.debug("pre: " + inParams);
			Log.debug("post: " + postParams);

			final HornPredicate pre = new HornPredicate(method.getMethodName() + "_pre", inParams, prePred);
			final HornPredicate post = new HornPredicate(method.getMethodName() + "_post", postParams, postPred);

			methodContracts.put(method.getMethodName(), new MethodContract(method, pre, post));
		}
	}
	
	/**
	 * Return the method contract
	 * @param methodName
	 * @return
	 */
	public MethodContract getMethodContract(String methodName){
		return methodContracts.get(methodName);
	}
	
	private int hack_counter = 0;
	
	public int getHackCounter(){
		return hack_counter++;
	}
	
	/**
	 * TODO this should in its own class
	 * @param p
	 * @param sig
	 * @return
	 */
	public ProverFun getClassInvariant(Prover p, ClassVariable sig) {
		ProverFun inv = classInvariants.get(sig);

		if (inv == null) {
			List<Variable> args = new ArrayList<Variable>();

			args.add(new Variable("ref", new ReferenceType(sig)));
			for (Variable v : sig.getAssociatedFields())
				args.add(v);

			inv = HornHelper.hh().genHornPredicate(p, "inv_" + sig.getName(), args);

			classInvariants.put(sig, inv);
		}
		return inv;
	}
}
