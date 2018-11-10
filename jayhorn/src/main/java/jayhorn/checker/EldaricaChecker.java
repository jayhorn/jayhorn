package jayhorn.checker;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.Options;
import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.Hornify;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverResult;
import jayhorn.utils.GhostRegister;
import jayhorn.utils.HeapCounterTransformer;
import jayhorn.utils.Stats;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author teme
 */
public class EldaricaChecker extends Checker {

    private ProverFactory factory;
    private Prover prover;

    public EldaricaChecker(ProverFactory factory) {
        this.factory = factory;
    }

    private List<ProverHornClause> allClauses = new LinkedList<ProverHornClause>();

    public boolean checkProgram(Program program) {
        Preconditions.checkNotNull(program.getEntryPoint(),
                                   "The program has no entry points and thus is trivially verified.");

        GhostRegister.reset();

        if (soottocfg.Options.v().memPrecision() >= 2) {
            GhostRegister.v().ghostVariableMap.put("pushID", IntType.instance());
        }

        if (Options.v().useCallIDs) {
            Log.info("Inserting call IDs  ... ");
            Verify.verify(false, "Don't run this for now!");
//			CallingContextTransformer cct = new CallingContextTransformer();
//			cct.transform(program);
        }

        HeapCounterTransformer hct = new HeapCounterTransformer();
        hct.transform(program);

        if (Options.v().printCFG) {
            System.out.println(program);
        }

        ProverResult result = ProverResult.Unknown;
        if (Options.v().getBoundedHeapSize() == -1) {
            result = generateAndCheckHornClauses(program,
                                                 -1,
                                                 HornEncoderContext.GeneratedAssertions.ALL);

        } else {
            final int offset = Options.v().getInitialHeapSize();
            final int step = Options.v().getStepHeapSize();
            final int max = Options.v().getBoundedHeapSize();

            Log.info("Trying to verify with max explicit heap size " + max);
            for (int k = offset; k <= max; k += step) {
                Log.info("Round " + (k - offset + 1) + ": heap size " + k);
                // try to verify program with heap size k and only safety assertions
                result = generateAndCheckHornClauses(program, k, HornEncoderContext.GeneratedAssertions.SAFETY);
                if (result == ProverResult.Unsat) {
                    // definitely unsafe: found counterexample with bounded heap
                    return false;
                } else {
                    // try to verify program with heap size k and only heap bound assertions
                    result = generateAndCheckHornClauses(program, k, HornEncoderContext.GeneratedAssertions.HEAP_BOUNDS);
                    if (result == ProverResult.Sat) {
                        return true;
                    }
                }
            }
            throw new RuntimeException("Failed to verify the program with max heap size " + max);
        }
        if (result == ProverResult.Sat) {
            return true;
        } else if (result == ProverResult.Unsat) {
            return false;
        }
        throw new RuntimeException("Verification failed with prover code " + result);
    }

    private ProverResult generateAndCheckHornClauses(final Program program,
                                                     int explicitHeapSize,
                                                     HornEncoderContext.GeneratedAssertions generatedAssertions) {
        Log.info("Hornify  ... ");
        Hornify hf = new Hornify(factory);
        Stopwatch toHornTimer = Stopwatch.createStarted();
        HornEncoderContext hornContext = hf.toHorn(program, explicitHeapSize, generatedAssertions);
        Stats.stats().add("ToHorn", String.valueOf(toHornTimer.stop()));
        prover = hf.getProver();
        allClauses.addAll(hf.clauses);

        if (Options.v().getPrintHorn()) {
            System.out.println(hf.writeHorn());
        }

        ProverResult result = ProverResult.Unknown;
        try {
            final Method entryPoint = program.getEntryPoint();

            Log.info("Running from entry point: " + entryPoint.getMethodName());
            prover.push();
            // add an entry clause from the preconditions
            final HornPredicate entryPred = hornContext.getMethodContract(entryPoint).precondition;
            Map<Variable, ProverExpr> initialState = new HashMap<Variable, ProverExpr>();
            //Set the heap counter initially to one (because 0 is reserved for null)

            final ProverExpr entryAtom = entryPred.instPredicate(initialState);

            final ProverHornClause entryClause = prover.mkHornClause(entryAtom, new ProverExpr[0],
                                                                     prover.mkLiteral(true));

            allClauses.add(entryClause);

            Hornify.hornToSMTLIBFile(allClauses, 0, prover);
            Hornify.hornToFile(allClauses, 0);

            for (ProverHornClause clause : allClauses)
                prover.addAssertion(clause);

            Stopwatch satTimer = Stopwatch.createStarted();
            if (jayhorn.Options.v().getTimeout() > 0) {
                int timeoutInMsec = (int) TimeUnit.SECONDS.toMillis(jayhorn.Options.v().getTimeout());
                prover.checkSat(false);
                result = prover.getResult(timeoutInMsec);
            } else {
                result = prover.checkSat(true);
            }
            if (Options.v().solution) {
                Log.info(printHeapInvariants(hornContext));
            }
            Stats.stats().add("CheckSatTime", String.valueOf(satTimer.stop()));

            allClauses.remove(allClauses.size() - 1);
            prover.pop();
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        } finally {
            prover.shutdown();
        }

        return result;
//
//        if (result == ProverResult.Sat) {
//            return true;
//        } else if (result == ProverResult.Unsat) {
//            return false;
//        }
//        throw new RuntimeException("Verification failed with prover code " + result);
    }


    private String printHeapInvariants(HornEncoderContext hornContext) {
        StringBuilder sb = new StringBuilder();
        if (prover.getLastSolution() != null) {
            sb.append("No assertion can fail using the following heap invariants:\n");

            Map<ClassVariable, TreeMap<Long, String>> heapInvariants = new LinkedHashMap<ClassVariable, TreeMap<Long, String>>();

            for (Entry<String, String> entry : prover.getLastSolution().entrySet()) {
                boolean found = false;
                for (Entry<ClassVariable, Map<Long, HornPredicate>> pentry : hornContext.getInvariantPredicates().entrySet()) {

                    for (Entry<Long, HornPredicate> predEntry : pentry.getValue().entrySet()) {
                        HornPredicate hp = predEntry.getValue();
                        if (hp.predicate.toString().contains(entry.getKey())) {
                            //we found one.
                            if (!heapInvariants.containsKey(pentry.getKey())) {
                                heapInvariants.put(pentry.getKey(), new TreeMap<Long, String>());
                            }
                            String readable = entry.getValue();
                            for (int i = 0; i < hp.variables.size(); i++) {
                                readable = readable.replace("_" + i, hp.variables.get(i).getName());
                            }
                            heapInvariants.get(pentry.getKey()).put(predEntry.getKey(), readable);
                        }
                    }
                    if (found) {
                        break;
                    }
                }
            }
            for (Entry<ClassVariable, TreeMap<Long, String>> entry : heapInvariants.entrySet()) {
                sb.append(entry.getKey());
                sb.append("\n  ");
                for (Variable v : entry.getKey().getAssociatedFields()) {
                    sb.append(", ");
                    sb.append(v.getName());
                }
                sb.append(":\n");
                for (Entry<Long, String> e2 : entry.getValue().entrySet()) {
                    sb.append("\t");
                    sb.append(e2.getKey());
                    sb.append(":  ");
                    sb.append(e2.getValue());
                    sb.append("\n");
                }
                sb.append("--\n");
            }
            sb.append("----\n");
            System.err.println(sb.toString());
        }
        return sb.toString();
    }
}