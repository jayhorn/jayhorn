/**
 *
 */
package jayhorn.checker;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

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
import jayhorn.utils.Stats;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.variable.Variable;

/**
 * @author teme
 */
public class Checker {

  private ProverFactory factory;
  private Prover prover;

  public Checker(ProverFactory factory) {
    this.factory = factory;
  }

  private List<ProverHornClause> allClauses = new LinkedList<ProverHornClause>();

  public boolean checkProgram(Program program) {

    Log.info("Hornify  ... ");
    Hornify hf = new Hornify(factory);
    Stopwatch toHornTimer = Stopwatch.createStarted();
    HornEncoderContext hornContext = hf.toHorn(program);
    Stats.stats().add("ToHorn", String.valueOf(toHornTimer.stop()));
    prover = hf.getProver();
    allClauses.addAll(hf.clauses);
    
    if (Options.v().getPrintHorn()) {
      System.out.println(hf.writeHorn());
    }

    ProverResult result = ProverResult.Unknown;
    try {
      int verifCount = 0;
      for (Method method : program.getEntryPoints()) {
        prover.push();
        // add an entry clause from the preconditions
        final HornPredicate entryPred = hornContext.getMethodContract(method).precondition;
        final ProverExpr entryAtom = entryPred.instPredicate(new HashMap<Variable, ProverExpr>());

        final ProverHornClause entryClause = prover.mkHornClause(entryAtom, new ProverExpr[0], prover.mkLiteral(true));

        allClauses.add(entryClause);

        Hornify.hornToSMTLIBFile(allClauses, verifCount, prover);
        Hornify.hornToFile(allClauses, verifCount);

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
        Stats.stats().add("CheckSatTime", String.valueOf(satTimer.stop()));
        allClauses.remove(allClauses.size() - 1);
        prover.pop();
        ++verifCount;
      }

    } catch (Throwable t) {
      t.printStackTrace();
      throw new RuntimeException(t);
    } finally {
      prover.shutdown();
    }

    if (result == ProverResult.Sat) {
      return true;
    } else if (result == ProverResult.Unsat) {
      return false;
    }
    throw new RuntimeException("Verification failed with prover code " + result);
  }

}