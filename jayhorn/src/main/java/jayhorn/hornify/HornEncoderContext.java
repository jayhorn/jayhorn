package jayhorn.hornify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.utils.GhostRegister;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.InterProceduralPullPushOrdering;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * Holds the context of the current Horn clause construction:
 * - mapping from ClassVariables to Invariant Predicates
 * - mapping from ClassVariables to unique numbers.
 * 
 * This class assigns a unique number to every ClassType
 * in a program. This is used to implement instanceof checks:
 * for "a instanceof b" we obtain the list of IDs of all 
 * subtypes of "b" using getSubtypeIDs and check if any of 
 * these is equal to the ID of "a" that we obtain with 
 * "getTypeID".
 * 
 * Make sure that there exists no more than one instance of this per 
 * Prover.
 * @author schaef
 *
 */
public class HornEncoderContext {

	private final Program program;
	private final Prover p;

	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();
	private Map<ClassVariable, Map<Long, HornPredicate>> invariantPredicates =
          new HashMap<ClassVariable, Map<Long, HornPredicate>>(); 
	private Map<Method, MethodContract> methodContracts = new LinkedHashMap<Method, MethodContract>();
	
	public HornEncoderContext(Prover p, Program prog) {
		this.program = prog;
		this.p = p;		
		for (ClassVariable var : program.getTypeGraph().vertexSet()) {
			//add +1 to make sure that no type is the
			//same number as the null constant
			typeIds.put(var, typeIds.size()+1);
		}
		mkMethodContract(program, p);
	}

	/**
	 * Get the invariant predicates for each class. This is for debugging only.
	 * @return
	 */
	public Map<ClassVariable, Map<Long, HornPredicate>> getInvariantPredicates() {
		return this.invariantPredicates;
	}
	
	/**
	 * Creates method contracts for all methods in the current scene.
	 * @param program
	 * @param p
	 */
	private void mkMethodContract(Program program, Prover p) {
		for (Method method : program.getMethods()) {
			final List<Variable> inParams = new ArrayList<Variable>(method.getInParams());
			final List<Variable> postParams = new ArrayList<Variable>();

			postParams.addAll(inParams);
			
			if (!method.getOutParams().isEmpty()) {
				Verify.verify(method.getOutParams().size()==method.getReturnType().size(), 
						method.getOutParams().size()+"!="+method.getReturnType().size());
				postParams.addAll(method.getOutParams());
			} else if (!method.getReturnType().isEmpty()) {
				int ctr = 0;
				for (Type tp : method.getReturnType()) {
					postParams.add(new Variable("resultVar" + (ctr++), tp));
				}
			}

			Log.debug("method: " + method.getMethodName());
			Log.debug("pre: " + inParams);
			Log.debug("post: " + postParams);

			final HornPredicate pre = new HornPredicate(p, method.getMethodName() + "_pre", inParams);
			final HornPredicate post = new HornPredicate(p, method.getMethodName() + "_post", postParams);

			methodContracts.put(method, new MethodContract(method, pre, post));
		}
	}

	/**
	 * Return the method contract
	 * 
	 * @param methodName
	 * @return
	 */
	public MethodContract getMethodContract(Method method) {
		return methodContracts.get(method);
	}	
	
	/**
	 * Find or create the HornPredicate that encodes the class
	 * invariant for the class represented by "sig".
	 * @param sig
	 * @return
	 */
	public HornPredicate lookupInvariantPredicate(ClassVariable sig,
                                                      long pushId) {
		if (!invariantPredicates.containsKey(sig))
                  invariantPredicates.put
                    (sig, new HashMap<Long, HornPredicate>());

                final Map<Long, HornPredicate> subMap =
                  invariantPredicates.get(sig);
                if (!subMap.containsKey(pushId)) {
			List<Variable> args = new ArrayList<Variable>();
			args.add(new Variable("ref", new ReferenceType(sig)));
			
			//add variables for the ghost fields
			//used by pull and push.
			for (Entry<String, Type> entry : GhostRegister.v().ghostVariableMap.entrySet()) {
				args.add(new Variable(entry.getKey(), entry.getValue()));
			}
			
			for (Variable v : sig.getAssociatedFields()) {
				args.add(v);
			}
                        String name = "inv_" + sig.getName();
                        if (pushId >= 0)
                          name = name + "_" + pushId;
			subMap.put(pushId, new HornPredicate(p, name, args));
		}
		return subMap.get(pushId);
	}
	
	/**
	 * Maps a ClassVariable to a unique integer.
	 * @param var
	 * @return
	 */
	public Integer getTypeID(ClassVariable var) {
		return typeIds.get(var);
	}

	/**
	 * Returns the unique IDs of all possible subtypes
	 * of var.
	 * @param var
	 * @return
	 */
	public Set<Integer> getSubtypeIDs(ClassVariable var) {
		final Set<Integer> result = new HashSet<Integer>();
		for (ClassVariable v : GraphUtil.getForwardReachableVertices(program.getTypeGraph(), var)) {
			result.add(getTypeID(v));
		}
		return result;
	}

	/**
	 * Gets a map from ClassVariable to unique ID.
	 * @return
	 */
	public Map<ClassVariable, Integer> getTypeIds() {
		return typeIds;
	}
}
