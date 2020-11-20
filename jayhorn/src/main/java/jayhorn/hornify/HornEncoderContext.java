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
import jayhorn.Options;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverADT;
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
 *
 * @author schaef
 */
public class HornEncoderContext {

    private final Program program;
    private final Prover p;

    private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();
    private Map<ClassVariable, Map<Long, HornPredicate>> invariantPredicates =
        new HashMap<ClassVariable, Map<Long, HornPredicate>>();
    private Map<Method, MethodContract> methodContracts = new LinkedHashMap<Method, MethodContract>();

    private int assertionCnt = 0;

    public int getUniqueAssertionId() {
        return assertionCnt++;
    }

    // flag that can be set by the encoder classes to signal that some
    // approximate statements were replaced by an assume(false)
    private Boolean haveDroppedApproxStatements = false;

    public void droppedApproximatedStatement() {
        haveDroppedApproxStatements = true;
    }

    public Boolean encodingHasDroppedApproximatedStatements() {
        return haveDroppedApproxStatements;
    }

    public static enum GeneratedAssertions {
        // only include actual safety assertions; this will under-approximate,
        // and turn every over-approximated statement into an assume(false)
        // (you can trust result UNSAFE, but not SAFE)
        SAFETY_UNDER_APPROX,
        // only include actual safety assertions, but keep all over-approximated
        // statements
        // (you can trust result SAFE, but not UNSAFE, assuming that HEAP_BOUNDS
        // succeeded)
        SAFETY_OVER_APPROX,
        // only include assertions about heap size; this will only tell you
        // whether the heap is big enough (also include over-approximated statements)
        HEAP_BOUNDS,
        // both safety and heap bound assertions; this will over-approximate
        // (you can trust result SAFE, but not UNSAFE)
        ALL
    }

    private final int explicitHeapSize;

    private final GeneratedAssertions genAssertions;

    // List of prover types that covers all fields to be stored for any
    // class
    private final List<ProverType> unifiedClassFieldTypes =
        new ArrayList<ProverType>();
    private final Map<ClassVariable, List<Integer>> classFieldTypeIndexes =
        new HashMap<ClassVariable, List<Integer>>();
    private final ProverType unifiedClassType;

    // Additional arguments that should be included in the generated
    // predicates. By convention, each state predicate will include
    // the extraPredicateArgs, each method pre-condition will include the
    // extraPredicatePreArgs, and each method post-condition will include
    // both extraPredicateArgs and extraPredicatePreArgs.
    private final List<Variable> extraPredicateArgs =
        new ArrayList<Variable>();
    private final List<Variable> extraPredicatePreArgs =
        new ArrayList<Variable>();

    // Extra variables representing objects on the heap
    private final List<Variable> explicitHeapObjectVars =
        new ArrayList<Variable>();

    // Extra representation of static fields
    private final Map<ClassVariable, List<Variable>> explicitHeapStaticVars =
        new LinkedHashMap<>();

    public List<Variable> getExtraPredicateArgs() {
        return extraPredicateArgs;
    }

    public List<Variable> getExtraPredicatePreArgs() {
        return extraPredicatePreArgs;
    }

    public boolean useExplicitHeap() {
        return explicitHeapSize >= 0;
    }

    public int getModelledArraySize() {
        return explicitHeapSize; // currently we just use the same size
    }

    public List<Variable> getExplicitHeapObjectVariables() {
        return explicitHeapObjectVars;
    }

    public boolean genSafetyAssertions() {
        if (!useExplicitHeap())
            return true;

        switch (genAssertions) {
            case SAFETY_UNDER_APPROX:
            case SAFETY_OVER_APPROX:
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

    /**
     * Should over-approximated statements be kept, or turned into
     * assume(false)?
     */
    public boolean elimOverApprox() {
        if (!useExplicitHeap())
            return false;

        switch (genAssertions) {
            case SAFETY_UNDER_APPROX:
                return true;
            default:
                return false;
        }
    }

    public HornEncoderContext(Prover p, Program prog, ProverADT stringADT, int explicitHeapSize, GeneratedAssertions generatedAssertions) {
        this.program = prog;
        this.p = p;
        HornHelper.hh().setStringADT(stringADT);
        this.explicitHeapSize = explicitHeapSize;
        this.genAssertions = generatedAssertions;
        for (ClassVariable var : program.getTypeGraph().vertexSet()) {
            //add +1 to make sure that no type gets the
            //same number as the null constant
            typeIds.put(var, typeIds.size() + 1);
        }

        if (useExplicitHeap()) {
            mkUnifiedFieldTypes();

            for (List<Variable> fields : explicitHeapStaticVars.values())
                extraPredicateArgs.addAll(fields);

            unifiedClassType =
                p.getTupleType(unifiedClassFieldTypes
                                   .toArray(new ProverType[0]));
            final Type wrappedType =
                new WrappedProverType(unifiedClassType);
            for (int i = 0; i < explicitHeapSize; ++i)
                explicitHeapObjectVars.add(new Variable("object" + i,
                                                        wrappedType));

            extraPredicateArgs.addAll(explicitHeapObjectVars);
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

        Log.debug("unified class field types: " + unifiedClassFieldTypes);
        Log.debug("field indexes: " + classFieldTypeIndexes);
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
     * Determine whether the given type is a class representing static fields.
     */
    public boolean isStaticFieldsClass(ClassVariable var) {
        return var.getName().startsWith("$StaticFields_");
    }

    /**
     * Add fields for the given class to <code>unifiedClassFieldTypes</code>
     */
    private void addFieldsFor(ClassVariable var) {
        if (classFieldTypeIndexes.containsKey(var) ||
            explicitHeapStaticVars.containsKey(var))
            return;
        final List<Variable> fields = getInvariantArgs(var);

        if (isArrayInv(var)) {
            Log.debug("adding array type " + var);

            if (Options.v().strictlySound() &&
                var.getName().startsWith("JayArray_JayArray_"))
                throw new RuntimeException("Multi-dimensional arrays not fully supported yet");

            Variable elemVar = null;
            for (Variable v : fields)
                if (v.getName().equals(ArrayTransformer.AElem))
                    elemVar = v;
            if (elemVar == null)
                throw new RuntimeException(
                            "could not determine array element type");

            final List<Variable> arrayFields = new ArrayList<> ();
            
            for (int i = 0; i < getModelledArraySize(); ++i)
                arrayFields.add(elemVar);

            addFieldTypeIndexes(var, arrayFields);

            return;
        }

        Log.debug("adding fields of class " + var + ": " + fields);

        // we know that the first field is the actual object reference,
        // which does not have to be recorded
        fields.remove(0);

        if (isStaticFieldsClass(var)) {
            final List<Variable> extraVars = new ArrayList<>();
            for (Variable v : fields)
                extraVars.add(new Variable(v.getName() + "_static", v.getType()));
            explicitHeapStaticVars.put(var, extraVars);
            return;
        }

        addFieldTypeIndexes(var, fields);
    }

    private void addFieldTypeIndexes(ClassVariable var, List<Variable> fields) {
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
     *
     * @return
     */
    public Map<ClassVariable, Map<Long, HornPredicate>> getInvariantPredicates() {
        return this.invariantPredicates;
    }

    /**
     * Creates method contracts for all methods in the current scene.
     *
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
                Verify.verify(method.getOutParams().size() == method.getReturnType().size(),
                              method.getOutParams().size() + "!=" + method.getReturnType().size());
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

    public MethodContract getMethodContract(Method method) {
        return methodContracts.get(method);
    }

    /**
     * Find or create the HornPredicate that encodes the class
     * invariant for the class represented by "sig".
     *
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
     * Check whether the given class results from the array transformation
     */
    public boolean isArrayInv(ClassVariable sig) {
      return
          soottocfg.Options.v().arrayInv() &&
          (sig.getName().contains(ArrayTransformer.arrayTypeName));
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

        if (isArrayInv(sig)) {
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

        return p.mkTuple(unifiedArgs);
    }

    /**
     * Initialise all fields of an array represented explicitly
     */
    public ProverExpr initUnifiedArrayType(ClassVariable sig,
                                           ProverExpr elemValue,
                                           ProverExpr defaultValues) {
        Verify.verify(isArrayInv(sig),
                      "method only works for array invariants, not for " +
                      sig);

        final List<Integer> fieldIndexes = classFieldTypeIndexes.get(sig);

        final ProverExpr[] unifiedArgs =
            new ProverExpr[unifiedClassFieldTypes.size()];
        for (int i = 0; i < fieldIndexes.size(); ++i)
            unifiedArgs[fieldIndexes.get(i)] = elemValue;

        for (int i = 0; i < unifiedArgs.length; ++i)
            if (unifiedArgs[i] == null)
                unifiedArgs[i] = p.mkTupleSelect(defaultValues, i);

        return p.mkTuple(unifiedArgs);
    }

    /**
     * Assign a value to some element of an array represented explicitly
     */
    public ProverExpr setUnifiedArrayField(ClassVariable sig,
                                           int index,
                                           ProverExpr elemValue,
                                           ProverExpr defaultValues) {
        Verify.verify(isArrayInv(sig),
                      "method only works for array invariants, not for " +
                      sig);

        final List<Integer> fieldIndexes = classFieldTypeIndexes.get(sig);
        final int unifiedIndex = fieldIndexes.get(index);

        final ProverExpr[] unifiedArgs =
            new ProverExpr[unifiedClassFieldTypes.size()];
        unifiedArgs[unifiedIndex] = elemValue;

        for (int i = 0; i < unifiedArgs.length; ++i)
            if (unifiedArgs[i] == null)
                unifiedArgs[i] = p.mkTupleSelect(defaultValues, i);

        return p.mkTuple(unifiedArgs);
    }

    /**
     * Update <code>varMap</code> to contain the values of static fields
     * referred to by the invariant.
     */
    public void assignStaticVars(ClassVariable sig,
                                 HornPredicate invPredicate,
                                 Map<Variable, ProverExpr> varMap) {
        final ProverExpr[] invArgs = invPredicate.compileArguments(varMap);
        final List<Variable> staticVars = explicitHeapStaticVars.get(sig);

        Verify.verify(invArgs.length == staticVars.size() + 1,
                      "Inconsistent number of static class fields");

        for (int i = 0; i < staticVars.size(); ++i)
            varMap.put(staticVars.get(i), invArgs[i + 1]);
    }

    /**
     * Read the values of fields of a <code>sig</code> from the given
     * <code>unifiedObjectFields</code>.
     */
    public void substObjectFields(ClassVariable sig,
                                  HornPredicate invPredicate,
                                  Map<Variable, ProverExpr> varMap,
                                  ProverExpr unifiedObjectFields) {
        final ProverExpr[] invArgs = invPredicate.compileArguments(varMap);
        final List<Integer> fieldIndexes = classFieldTypeIndexes.get(sig);

        Verify.verify(invArgs.length == fieldIndexes.size() + 1,
                      "Inconsistent number of class fields");

        final Map<ProverExpr, ProverExpr> subst = new HashMap<> ();

        for (int i = 0; i < fieldIndexes.size(); ++i)
            subst.put(invArgs[i + 1], p.mkTupleSelect(unifiedObjectFields, fieldIndexes.get(i)));

        HornHelper.hh().substitute(varMap, subst);
    }

    /**
     * Read the value of an array element from the given
     * <code>unifiedObjectFields</code>.
     */
    public void substArrayElem(ClassVariable sig,
                               int index,
                               ProverExpr elemTargetExpr,
                               Map<Variable, ProverExpr> varMap,
                               ProverExpr unifiedObjectFields) {
        final List<Integer> fieldIndexes = classFieldTypeIndexes.get(sig);
        final int unifiedIndex = fieldIndexes.get(index);

        final Map<ProverExpr, ProverExpr> subst = new HashMap<> ();

        subst.put(elemTargetExpr,
                  p.mkTupleSelect(unifiedObjectFields, unifiedIndex));

        HornHelper.hh().substitute(varMap, subst);
    }

    /**
     * Read the values of static fields of a <code>sig</code>, and put them in the
     * <code>varMap</code>.
     */
    public void substStaticFields(ClassVariable sig,
                                  HornPredicate invPredicate,
                                  Map<Variable, ProverExpr> varMap) {
        final ProverExpr[] invArgs = invPredicate.compileArguments(varMap);
        final List<Variable> staticVars = explicitHeapStaticVars.get(sig);

        Verify.verify(invArgs.length == staticVars.size() + 1,
                      "Inconsistent number of static class fields");

        final Map<ProverExpr, ProverExpr> subst = new HashMap<> ();

        for (int i = 0; i < staticVars.size(); ++i)
            subst.put(invArgs[i + 1], varMap.get(staticVars.get(i)));

        HornHelper.hh().substitute(varMap, subst);
    }

    /**
     * Maps a ClassVariable to a unique integer.
     *
     * @param var
     * @return
     */
    public Integer getTypeID(ClassVariable var) {
        return typeIds.get(var);
    }

    /**
     * Returns the unique IDs of all possible subtypes
     * of var.
     *
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
     *
     * @return
     */
    public Map<ClassVariable, Integer> getTypeIds() {
        return typeIds;
    }
}
