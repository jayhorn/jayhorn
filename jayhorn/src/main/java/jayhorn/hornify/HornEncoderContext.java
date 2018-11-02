package jayhorn.hornify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.BitSet;

import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverType;
import jayhorn.solver.ProverExpr;
import jayhorn.utils.GhostRegister;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.CfgScanner;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.soot.transformers.ArrayTransformer;

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

        public static enum GeneratedAssertions {
            SAFETY,        // only include actual safety assertions; this will under-approximate
                           // (you can trust result UNSAFE, but not SAFE)
            HEAP_BOUNDS,   // only include assertions about heap size; this will only tell you
                           // whether the heap is big enough
            ALL            // both safety and heap bound assertions; this will over-approximate
                           // (you can trust result SAFE, but not UNSAFE)
        }

        private static final int explicitHeapSize = -1;
    
        private static final GeneratedAssertions genAssertions = GeneratedAssertions.ALL;

        // List of prover types that covers all fields to be stored for any
        // class
        private final List<ProverType> unifiedClassFieldTypes =
          new ArrayList<ProverType> ();
        private final Map<ClassVariable, List<Integer>> classFieldTypeIndexes =
          new HashMap<ClassVariable, List<Integer>> ();
        private final ProverType unifiedClassType;

        // Additional arguments that should be included in the generated
        // predicates. By convention, each state predicate will include
        // the extraPredicateArgs, each method pre-condition will include the
        // extraPredicatePreArgs, and each method post-condition will include
        // both extraPredicateArgs and extraPredicatePreArgs.
        private final List<Variable> extraPredicateArgs =
          new ArrayList<Variable> ();
        private final List<Variable> extraPredicatePreArgs =
          new ArrayList<Variable> ();

        public List<Variable> getExtraPredicateArgs() {
          return extraPredicateArgs;
        }

        public List<Variable> getExtraPredicatePreArgs() {
          return extraPredicatePreArgs;
        }

        public boolean useExplicitHeap() {
          return explicitHeapSize >= 0;
        }

        public List<Variable> getExplicitHeapVariables() {
          return extraPredicateArgs;
        }

        public boolean genSafetyAssertions() {
            if (!useExplicitHeap())
                return true;

            switch (genAssertions) {
            case SAFETY:
            case ALL:
                return true;

            default:
                return false;
            }
        }
    
        public boolean genHeapBoundAssertions() {
            if (!useExplicitHeap())
                return false;

            switch (genAssertions) {
            case HEAP_BOUNDS:
            case ALL:
                return true;

            default:
                return false;
            }
        }
    
	public HornEncoderContext(Prover p, Program prog) {
		this.program = prog;
		this.p = p;		
		for (ClassVariable var : program.getTypeGraph().vertexSet()) {
			//add +1 to make sure that no type is the
			//same number as the null constant
			typeIds.put(var, typeIds.size()+1);
		}

                if (useExplicitHeap()) {
                    mkUnifiedFieldTypes();
                    unifiedClassType =
                        p.getTupleType(unifiedClassFieldTypes
                                       .toArray(new ProverType [0]));
                    final Type wrappedType =
                        new WrappedProverType(unifiedClassType);
                    for (int i = 0; i < explicitHeapSize; ++i)
                        extraPredicateArgs.add(new Variable("object" + i,
                                                            wrappedType));
                } else {
                    unifiedClassType = null;
                }

                for (Variable v : extraPredicateArgs)
                    extraPredicatePreArgs.add(
                      new Variable(v.getName() + "_in", v.getType()));

		mkMethodContract(program, p);
        }

    /**
     * Scan the program for push/pull statements, and add the fields of
     * each pulled/pushed class to <code>unifiedClassFieldTypes</code>
     */
    private void mkUnifiedFieldTypes() {
        final FieldTypeScanner scanner = new FieldTypeScanner();
        for (Method method : program.getMethods())
            scanner.scanMethod(method);
                    
        Log.info("unified class field types: " + unifiedClassFieldTypes);
        Log.info("field indexes: " + classFieldTypeIndexes);
    }

    private class FieldTypeScanner extends CfgScanner {
	@Override
	protected Statement processStatement(PullStatement s) { 
            addFieldsFor(s.getClassSignature());
            return s;
        }
	@Override
	protected Statement processStatement(PushStatement s) {
            addFieldsFor(s.getClassSignature());
            return s;
        }
    }

    /**
     * Add fields for the given class to <code>unifiedClassFieldTypes</code>
     */
    private void addFieldsFor(ClassVariable var) {
            Log.info("adding fields of class " + var);

            final List<Variable> fields = getInvariantArgs(var);

            // we know that the first field is the actual object reference,
            // which does not have to be recorded
            fields.remove(0);

            final List<Integer> indexes = new ArrayList<Integer>();
            final BitSet usedTypes = new BitSet();

            for (Variable field : fields) {
                final ProverType t = 
                    HornHelper.hh().getProverType(p, field.getType());
                boolean found = false;
                for (int i = 0;
                     !found && i < unifiedClassFieldTypes.size();
                     ++i)
                    if (!usedTypes.get(i) &&
                        unifiedClassFieldTypes.get(i).equals(t)) {
                        indexes.add(i);
                        usedTypes.set(i);
                        found = true;
                    }
                if (!found) {
                    final int n = unifiedClassFieldTypes.size();
                    unifiedClassFieldTypes.add(t);
                    indexes.add(n);
                    usedTypes.set(n);
                }
            }
            
            classFieldTypeIndexes.put(var, indexes);
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
                        inParams.addAll(extraPredicatePreArgs);
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

                        postParams.addAll(extraPredicateArgs);

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
                    List<Variable> args = getInvariantArgs(sig);
				
                    String name = "inv_" + sig.getName();
                    if (pushId >= 0)
                        name = name + "_" + pushId;
                    subMap.put(pushId, new HornPredicate(p, name, args));
		}
		return subMap.get(pushId);
	}
	
    /**
     * Determine the arguments to be included in the invariant of the given
     * class.
     */
    private List<Variable> getInvariantArgs(ClassVariable sig) {
            List<Variable> args = new ArrayList<Variable>();
            args.add(new Variable("ref", new ReferenceType(sig)));
			
            //add variables for the ghost fields
            //used by pull and push.
            for (Entry<String, Type> entry : GhostRegister.v().ghostVariableMap.entrySet()) {
                args.add(new Variable(entry.getKey(), entry.getValue()));
            }
			
            if (soottocfg.Options.v().arrayInv() && 
                (sig.getName().contains(ArrayTransformer.arrayTypeName))) {
                args.add(new Variable("array_index", IntType.instance()));
            }
			
            for (Variable v : sig.getAssociatedFields()) {
                args.add(v);
            }

            return args;
        }

    /**
     * Generate a constraint stating that the current heap counter has not
     * exceeded the heap size bound.
     */
    public ProverExpr validHeapCounterConstraint(ProverExpr heapCounter) {
      if (useExplicitHeap())
        // we assume that the heap counter starts at 1
        return p.mkLeq(heapCounter, p.mkLiteral(explicitHeapSize));
      else
        return p.mkLiteral(true);
    }

    /**
     * Embed the fields of an object in the unified class type, resulting in
     * a tuple of the right arity.
     */
    public ProverExpr toUnifiedClassType(ClassVariable sig,
                                         HornPredicate invPredicate,
                                         Map<Variable, ProverExpr> varMap,
                                         ProverExpr defaultValues) {
        final ProverExpr[] invArgs = invPredicate.compileArguments(varMap);
        final List<Integer> fieldIndexes = classFieldTypeIndexes.get(sig);

        Verify.verify(invArgs.length == fieldIndexes.size() + 1,
                      "Inconsistent number of class fields");

        final ProverExpr[] unifiedArgs =
            new ProverExpr[unifiedClassFieldTypes.size()];
        for (int i = 0; i < fieldIndexes.size(); ++i)
            unifiedArgs[fieldIndexes.get(i)] = invArgs[i + 1];

        for (int i = 0; i < unifiedArgs.length; ++i)
            if (unifiedArgs[i] == null)
                unifiedArgs[i] = p.mkTupleSelect(defaultValues, i);
                                 //p.mkHornVariable("field_" + i,
                                 //                 unifiedClassFieldTypes.get(i));

        return p.mkTuple(unifiedArgs);
    }

    /**
     * Read the values of fields of a <code>sig</code> from the given
     * <code>unifiedObjectFields</code>.
     */
    public ProverExpr createFieldEquations(ClassVariable sig,
                                           HornPredicate invPredicate,
                                           Map<Variable, ProverExpr> varMap,
                                           ProverExpr unifiedObjectFields) {
        final ProverExpr[] invArgs = invPredicate.compileArguments(varMap);
        final List<Integer> fieldIndexes = classFieldTypeIndexes.get(sig);

        Verify.verify(invArgs.length == fieldIndexes.size() + 1,
                      "Inconsistent number of class fields");

        ProverExpr res = p.mkLiteral(true);
        for (int i = 0; i < fieldIndexes.size(); ++i)
            res = p.mkAnd(res,
                          p.mkEq(invArgs[i + 1],
                                 p.mkTupleSelect(unifiedObjectFields, fieldIndexes.get(i))));

        return res;
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

	public Program getProgram() {
		return this.program;
	}
	
	/**
	 * Gets a map from ClassVariable to unique ID.
	 * @return
	 */
	public Map<ClassVariable, Integer> getTypeIds() {
		return typeIds;
	}
}
