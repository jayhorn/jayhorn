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
import jayhorn.solver.princess.PrincessProver;
import jayhorn.solver.princess.CexPrinter;
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

    private int hornOutputNum = 0;

    public EldaricaChecker(ProverFactory factory) {
        this.factory = factory;
    }

    public CheckerResult checkProgram(Program program) {
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
        solutionOutput = "";

        switch(Options.v().getHeapMode()) {
        case auto:
        case unbounded:
            Log.info("Trying to verify with unbounded heap");
            result =
                generateAndCheckHornClauses(program, -1,
                  HornEncoderContext.GeneratedAssertions.ALL);

            //            Log.info("Prover code " + result);
            
            if (result == ProverResult.Sat) {
                Log.info("Program is SAFE");
                if (!"".equals(solutionOutput))
                    Log.info(solutionOutput);
                return CheckerResult.SAFE;
            }
            break;
        default:
            break;
        }

        switch(Options.v().getHeapMode()) {
        case auto:
        case bounded:
            return boundedChecking(program,
                                   Options.v().getInitialHeapSize(),
                                   Options.v().getStepHeapSize(),
                                   Options.v().getBoundedHeapSize());
        default:
            break;
        }

        return CheckerResult.UNKNOWN;
    }

    private CheckerResult boundedChecking(Program program,
                                          int offset, int step, int max) {
        Log.info("Trying to verify with max explicit heap size " + max);
        ProverResult result = ProverResult.Unknown;
        for (int k = offset; k <= max; k += step) {
            solutionOutput = "";
            Log.info("======== Round " + (k - offset + 1) + ": heap size " + k);
            // try to verify program with heap size k and only safety assertions
            Log.info("- Searching for counterexamples ...");
            result =
                generateAndCheckHornClauses(program, k,
                  HornEncoderContext.GeneratedAssertions.SAFETY_UNDER_APPROX);
            if (result == ProverResult.Unsat) {
                // definitely unsafe: found counterexample with bounded heap
                Log.info("Program is UNSAFE");
                if (!"".equals(solutionOutput))
                    Log.info(solutionOutput);
                return CheckerResult.UNSAFE;
            } else {
                // try to verify program with heap size k and only
                // heap bound assertions
                Log.info("No counterexamples, checking heap bounds ...");
                final Boolean approx = hasDroppedStatements;
                result =
                    generateAndCheckHornClauses(program, k,
                      HornEncoderContext.GeneratedAssertions.HEAP_BOUNDS);
                if (result == ProverResult.Sat) {
                    if (!approx) {
                        Log.info("Program is bounded and therefore SAFE");
                        return CheckerResult.SAFE;
                    }
                    
                    Log.info("- program is bounded, checking full safety ...");
                    result =
                        generateAndCheckHornClauses(program, k,
                          HornEncoderContext.GeneratedAssertions.SAFETY_OVER_APPROX);
                    if (result == ProverResult.Sat) {
                        Log.info("Program is SAFE");
                        if (!"".equals(solutionOutput))
                            Log.info(solutionOutput);
                        return CheckerResult.SAFE;
                    } else {
                        Log.info("Could not prove safety, giving up");
                        return CheckerResult.UNKNOWN;
                    }
                } else {
                    Log.info("- insufficient heap, increasing size");
                }
            }
        }
        Log.info("Failed to verify the program with max heap size " + max);
        return CheckerResult.UNKNOWN;
    }

    private String solutionOutput = "";
    private Boolean hasDroppedStatements = false;

    private ProverResult generateAndCheckHornClauses(final Program program,
                                                     int explicitHeapSize,
                                                     HornEncoderContext.GeneratedAssertions generatedAssertions) {
        List<ProverHornClause> allClauses = new LinkedList<ProverHornClause>();
        Log.info("Hornify  ... ");
        ProverResult result = ProverResult.Unknown;

        try {
            Hornify hf = new Hornify(factory);
            Stopwatch toHornTimer = Stopwatch.createStarted();
            HornEncoderContext hornContext =
                hf.toHorn(program, explicitHeapSize, generatedAssertions);
            hasDroppedStatements =
                hornContext.encodingHasDroppedApproximatedStatements();
            Stats.stats().add("ToHorn", String.valueOf(toHornTimer.stop()));
            prover = hf.getProver();
            allClauses.addAll(hf.clauses);

            if (Options.v().getPrintHorn()) {
                System.out.println(hf.writeHorn());
            }

            final Method entryPoint = program.getEntryPoint();

            Log.info("Running from entry point: " + entryPoint.getMethodName());
            prover.push();
            // add an entry clause from the preconditions
            final HornPredicate entryPred = hornContext.getMethodContract(entryPoint).precondition;
            Map<Variable, ProverExpr> initialState = new HashMap<Variable, ProverExpr>();

            final ProverExpr entryAtom = entryPred.instPredicate(initialState);

            final ProverHornClause entryClause = prover.mkHornClause(entryAtom, new ProverExpr[0],
                                                                     prover.mkLiteral(true));

            allClauses.add(entryClause);

            Hornify.hornToSMTLIBFile(allClauses, hornOutputNum, prover);
            Hornify.hornToFile(allClauses, hornOutputNum);
            hornOutputNum = hornOutputNum + 1;

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
                if (result == ProverResult.Sat) {
                    // solutionOutput = printHeapInvariants(hornContext);
                } else if (result == ProverResult.Unsat) {
                    Log.info("Possible violation at " +
                             ((PrincessProver)prover).getLastCEX().apply(1).productElement(0));
                }
            }

            if (Options.v().fullCEX && result == ProverResult.Unsat)
                ((PrincessProver) prover).prettyPrintLastCEX();

            if (Options.v().trace && result == ProverResult.Unsat) {
                final CexPrinter cexPrinter = new CexPrinter();
                solutionOutput =
                    cexPrinter.proverCexToCext(
                       ((PrincessProver) prover).getLastCEX(),
                       hornContext);
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
                        if (entry.getKey().contains(hp.predicate.toString())) {
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
                String comma = "";
                for (Variable v : entry.getKey().getAssociatedFields()) {
                    sb.append(comma);
                    comma = ", ";
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
//            System.err.println(sb.toString());
        }
        return sb.toString();
    }
}
