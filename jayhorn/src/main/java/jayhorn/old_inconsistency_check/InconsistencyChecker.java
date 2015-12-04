/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import jayhorn.Options;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;

/**
 * @author schaef
 *
 */
public class InconsistencyChecker {

	private final ProverFactory factory = new PrincessProverFactory();

	/**
	 * 
	 */
	public InconsistencyChecker() {
		// TODO Auto-generated constructor stub
	}

	public boolean checkProgram(Program program) {
		ExecutorService executor = null;
		try {
			executor = Executors.newSingleThreadExecutor();
			for (Method method : program.getMethods()) {
				Prover prover = factory.spawn();
				prover.setHornLogic(false);

				InconsistencyThread thread = new InconsistencyThread(program, method, prover);
				final Future<?> future = executor.submit(thread);
				try {
					if (Options.v().getTimeout() <= 0) {
						future.get();
					} else {
						future.get(Options.v().getTimeout(), TimeUnit.SECONDS);
					}
				} catch (TimeoutException e) {

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					prover.stop();
					prover.shutdown();
					if (future != null && !future.isDone() && !future.cancel(true)) {
						throw new RuntimeException("Could not cancel broken thread!");
					}
				}
			}
		} finally {
			if (executor!=null) {
				executor.shutdown();
			}
			
		}
		return true;
	}

}
