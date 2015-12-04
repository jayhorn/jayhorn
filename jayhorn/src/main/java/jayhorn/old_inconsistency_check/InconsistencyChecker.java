/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.base.Preconditions;

import jayhorn.Options;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;

/**
 * @author schaef
 *
 */
public class InconsistencyChecker {

	private final ProverFactory factory;

	/**
	 * 
	 */
	public InconsistencyChecker(ProverFactory f) {
		factory = f;
	}

	public Map<String, Set<CfgBlock>> checkProgram(Program program) {
		Map<String, Set<CfgBlock>> result = new HashMap<String, Set<CfgBlock>>();
		ExecutorService executor = null;		
		try {
			executor = Executors.newSingleThreadExecutor();
			for (Method method : program.getMethods()) {
				Prover prover = factory.spawn();
				Preconditions.checkArgument(prover!=null, "Failed to initialize prover.");
				prover.setHornLogic(false);

				Set<CfgBlock> inconsistencies = new HashSet<CfgBlock>();
				result.put(method.getMethodName(), inconsistencies);
				
				InconsistencyThread thread = new InconsistencyThread(program, method, prover);
				final Future<?> future = executor.submit(thread);
				try {
					if (Options.v().getTimeout() <= 0) {
						future.get();
					} else {
						future.get(Options.v().getTimeout(), TimeUnit.SECONDS);
					}
					inconsistencies.addAll(thread.getInconsistentBlocks());
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
		return result;
	}

}
