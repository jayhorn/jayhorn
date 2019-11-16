package jayhorn.hornify.encoder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jayhorn.Log;
import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornHelper;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.MethodContract;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.LiveVars;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.Variable;

//import soottocfg.Options;

public class MethodEncoder {

    private final Method method;
    private final HornPredicate precondition, postcondition;

    private final Prover p;

    private final Map<CfgBlock, HornPredicate> blockPredicates = new LinkedHashMap<CfgBlock, HornPredicate>();
    private final List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
    // TODO: What is the purpose of tsClauses?
    private final List<ProverHornClause> tsClauses = new LinkedList<ProverHornClause>(); // keep track of
    private final ExpressionEncoder expEnc;

    private final HornEncoderContext hornContext;

    public MethodEncoder(Prover p, Method method, HornEncoderContext hornContext) {
        this.p = p;
        this.method = method;

        MethodContract mc = hornContext.getMethodContract(method);
        this.precondition = mc.precondition;
        this.postcondition = mc.postcondition;
        this.expEnc = new ExpressionEncoder(p, hornContext);
        this.hornContext = hornContext;
    }

    /**
     * Encodes a method into a set of Horn clauses.
     */
    public List<ProverHornClause> encode() {
        this.clauses.clear();

        LiveVars<CfgBlock> liveVariables = method.computeBlockLiveVariables();
        makeBlockPredicates(liveVariables);

        if (method.getSource() == null) {
            encodeEmptyMethod();
            return clauses;
        }

        makeEntryPredicate();
        blocksToHorn(liveVariables);
        S2H.sh().addClause((Statement) null, tsClauses);

        encodeExtraClauses();

        return clauses;
    }

    private void encodeExtraClauses() {
        List<ProverHornClause> extraClauses = expEnc.getExtraEncodedClauses();
        tsClauses.addAll(extraClauses);
        S2H.sh().addClause((Statement) null, tsClauses);
        clauses.addAll(extraClauses);
    }

    public Map<CfgBlock, HornPredicate> getBlockPredicates() {
        return this.blockPredicates;
    }

    /**
     * Creates a trivial Horn clause:
     * pre(x,y,z) -> post(x,y,z)
     * for methods that do not have a body.
     */
    private void encodeEmptyMethod() {
        Log.debug("No implementation available for " + method.getMethodName());
        final Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
        final ProverExpr entryAtom = precondition.instPredicate(varMap);
        final ProverExpr exitAtom = postcondition.instPredicate(varMap);
        final ProverHornClause clause = p.mkHornClause(exitAtom, new ProverExpr[]{entryAtom}, p.mkLiteral(true));
        tsClauses.add(clause);
        S2H.sh().addClause((Statement) null, tsClauses);
        clauses.add(clause);
    }

    /**
     * Creates a Horn clause that makes from the method precondition to
     * the first block of the message body.
     * This method precondition args comprise all method parameters and
     * other variables that should be visible to the method. The args of
     * the predicate of the first block (entryVars) contain all local vars.
     */
    private void makeEntryPredicate() {
        // add an entry clause connecting with the precondition
        Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
        final ProverExpr preAtom = precondition.instPredicate(varMap);
        // duplicate the extra arguments, so that the pre-variables have
        // the same values as the local variables
        for (int i = 0; i < hornContext.getExtraPredicateArgs().size(); ++i)
            varMap.put(hornContext.getExtraPredicateArgs().get(i),
                    varMap.get(hornContext.getExtraPredicatePreArgs().get(i)));

        final ProverExpr entryAtom = blockPredicates.get(method.getSource()).instPredicate(varMap);
        final ProverHornClause clause = p.mkHornClause(entryAtom, new ProverExpr[]{preAtom}, p.mkLiteral(true));
        tsClauses.add(clause);
        S2H.sh().addClause((Statement) null, tsClauses);
        clauses.add(clause);
    }

    /**
     * Creates one HornPredicate for each block. The predicate contains the
     * list of live variables
     * for that block sorted by names and a predicate over the types of
     * these variables that has
     * the same name as the block.
     *
     * Note that the ProverFun in the predicate also contains all variables
     * in precondition.variables. So the arity of the prover fun is
     * |precondition.variables| + |sortedVars|
     *
     * @param liveVariables the list of live variables at the beginning of the block
     */
    private void makeBlockPredicates(LiveVars<CfgBlock> liveVariables) {
        for (Entry<CfgBlock, Set<Variable>> entry : liveVariables.liveIn.entrySet()) {
            Set<Variable> allLive = new HashSet<Variable>();
            allLive.addAll(entry.getValue());
            // sort the list of variables by name to make access
            // and reading easier.
            List<Variable> sortedVars = new LinkedList<Variable>();

            sortedVars.addAll(HornHelper.hh().setToSortedList(allLive));
            blockPredicates.put(entry.getKey(),
                    freshHornPredicate(method.getMethodName() + "_" + entry.getKey().getLabel(), sortedVars));
        }
    }

    /**
     * Creates a fresh HornPredicate with arity of
     * |precondition.variables| + |sortedVars| + 2*|extraPredicateArgs|.
     *
     * This is because we need to move the precondition variables through
     * all Horn clauses until the exit.
     *
     * @param name
     * @param sortedVars
     * @return Horn Predicate of arity |precondition.variables| + |sortedVars|
     */
    private HornPredicate freshHornPredicate(String name, List<Variable> sortedVars) {
        // add types for the method arguments, which
        // are later needed for the post-conditions
        final List<Variable> allArgs = new LinkedList<Variable>();
        allArgs.addAll(precondition.variables); // note that this includes ExtraPredicatePreArgs
        allArgs.addAll(sortedVars);
        allArgs.addAll(hornContext.getExtraPredicateArgs());
        return new HornPredicate(p, name, allArgs);
    }


    /**
     * Creates Horn clauses for all CfgBlocks in a method.
     *
     * @param liveVariables
     */
    private void blocksToHorn(LiveVars<CfgBlock> liveVariables) {
        List<CfgBlock> todo = new LinkedList<CfgBlock>();
        todo.add(method.getSource());
        Set<CfgBlock> done = new HashSet<CfgBlock>();
        // translate reachable blocks
        while (!todo.isEmpty()) {
            CfgBlock current = todo.remove(0);
            done.add(current);
            /*
             * Translate the body of the CfgBlock using blockToHorn.
             * This gives us the exitPred which is the last predicate
             * used in this basic block.
             */
            final HornPredicate exitPred = blockToHorn(current, liveVariables.liveOut.get(current));
            //reset the varMap here.
            Map<Variable, ProverExpr> varMap = new HashMap<Variable, ProverExpr>();
            /*
             * Now distinguish two cases:
             * THEN-CASE: Our block has no successors and leaves the method.
             *   In this case, we have to connect exitPred to the predicate
             *   associated with the postcondition of the method. That is,
             *   we generate a Horn clause of the form
             *   exitPred(...) -> postPred(...)
             * ELSE-CASE: Our block has at least one successor. In this case,
             *   we have to create a clause that connects exitPred with the
             *   predicate associated with the entry into the next block: succPred.
             *   And we have to add the next block to the todo list if we haven't
             *   processed it already.
             */
            if (method.outgoingEdgesOf(current).isEmpty()) {
                // block ends with a return
                final ProverExpr postAtom = postcondition.instPredicate(varMap);
                final ProverExpr exitAtom = exitPred.instPredicate(varMap);
                ProverHornClause clause = p.mkHornClause(postAtom, new ProverExpr[]{exitAtom}, p.mkLiteral(true));
                tsClauses.add(clause);
                S2H.sh().addClause((Statement) null, tsClauses);
                clauses.add(clause);
            } else {
                // link to the successor blocks
                final ProverExpr exitAtom = exitPred.instPredicate(varMap);
                for (CfgEdge edge : method.outgoingEdgesOf(current)) {
                    CfgBlock succ = method.getEdgeTarget(edge);
                    if (!todo.contains(succ) && !done.contains(succ)) {
                        todo.add(succ);
                    }
                    ProverExpr exitCondExpr;
                    if (edge.getLabel().isPresent()) {
                        try {
                            exitCondExpr = expEnc.exprToProverExpr(edge.getLabel().get(), varMap);
                        } catch (ExpressionEncoder.OverApproxException e) {
                            Log.info("Removing imprecisely handled block transition");
                            hornContext.droppedApproximatedStatement();
                            exitCondExpr = p.mkLiteral(false);
                        }
                    } else {
                        exitCondExpr = p.mkLiteral(true);
                    }
                    final ProverExpr succAtom = blockPredicates.get(succ).instPredicate(varMap);
                    ProverHornClause clause = p.mkHornClause(succAtom, new ProverExpr[]{exitAtom}, exitCondExpr);
                    tsClauses.add(clause);
                    S2H.sh().addClause((Statement) null, tsClauses);
                    clauses.add(clause);
                }
            }
        }
    }


    /**
     * Creates the Horn clauses for the statements in a single block.
     *
     * @param block       The block that is to be translated.
     * @param liveOutVars The set of variables that are live after the block.
     * @return
     */
    private HornPredicate blockToHorn(CfgBlock block, Set<Variable> liveOutVars) {
        //get the predicate that is associated with the entry of the block.
        final HornPredicate initPred = blockPredicates.get(block);

        if (block.getStatements().isEmpty()) {
            return initPred;
        }

        Map<Statement, Set<Variable>> liveAfter = computeLiveAfterVariables(block, liveOutVars);

        final String initName = initPred.name;
        HornPredicate prePred = initPred;
        int counter = 0;

        StatementEncoder senc = new StatementEncoder(p, this.expEnc);

        List<Statement> stmts = block.getStatements();
        for (int i = 0; i < stmts.size(); ++i) {
            final Statement s = stmts.get(i);
            final String postName = initName + "_" + (++counter);
            final List<Variable> interVarList = HornHelper.hh().setToSortedList(liveAfter.get(s));
            final HornPredicate postPred = freshHornPredicate(postName, interVarList);
            this.clauses.addAll(senc.statementToClause(s, prePred, postPred, this.method));

            prePred = postPred;
        }

        return prePred;
    }

    /**
     * Compute for each statement the set of variables
     * that are live after the statement.
     *
     * @param block       The current CfgBlock.
     * @param liveOutVars The set of vars that are live after block.
     * @return A map that stores for each statement the
     * set of variables that are live after the execution
     * of the statement.
     */
    private Map<Statement, Set<Variable>> computeLiveAfterVariables(CfgBlock block, Set<Variable> liveOutVars) {

        Map<Statement, Set<Variable>> liveMap = new HashMap<Statement, Set<Variable>>();

        @SuppressWarnings("unchecked") final Set<Variable>[] interVars = new Set[block.getStatements().size()];
        interVars[interVars.length - 1] = new HashSet<Variable>();
        interVars[interVars.length - 1].addAll(liveOutVars);

        // add variables used in the outgoing guards, and the
        // method arguments
        for (CfgEdge edge : method.outgoingEdgesOf(block))
            if (edge.getLabel().isPresent())
                interVars[interVars.length - 1].addAll(edge.getLabel().get().getUseVariables());

        for (int i = interVars.length - 1; i > 0; --i) {
            final Statement s = block.getStatements().get(i);
            interVars[i - 1] = new HashSet<Variable>();
            interVars[i - 1].addAll(interVars[i]);
            interVars[i - 1].removeAll(s.getDefVariables());
            interVars[i - 1].addAll(s.getUseVariables());
            liveMap.put(s, interVars[i]);
        }
        liveMap.put(block.getStatements().get(0), interVars[0]);
        return liveMap;
    }

}