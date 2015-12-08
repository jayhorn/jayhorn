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
		//statistics counters 
		int normal=0, timeouts=0, interrupt=0, execException=0, outOfMemory=0, other = 0;
		int withInconsistencies = 0;
		ExecutorService executor = null;		
		try {			
			executor = Executors.newSingleThreadExecutor();
			for (Method method : program.getMethods()) {
				if (method.vertexSet().isEmpty()) {
					//ignore empty methods
					normal++;
					continue;
				}
				
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
					normal++;
					inconsistencies.addAll(thread.getInconsistentBlocks());
				} catch (TimeoutException e) {
					if (!future.cancel(true)) {
						System.err.println("failed to cancle after timeout");
					}
					timeouts++;
					inconsistencies.clear();
					System.err.println("Timeout for " + method.getMethodName());
					e.printStackTrace();
				} catch (InterruptedException e) {
					interrupt++;
					e.printStackTrace();
				} catch (ExecutionException e) {
					execException++;
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					outOfMemory++;
					e.printStackTrace();
				} catch (Throwable e) {
					other++;
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
		
		StringBuilder sb = new StringBuilder();
		sb.append("Statistics:");
		sb.append(String.format("%n  Analyzed procedure: %d", program.getMethods().length));
		sb.append(String.format("%n  Analysis terminated normally for: %d", normal));
		sb.append(String.format("%n\t With inconsistencies: %d", withInconsistencies));
		sb.append(String.format("%n  Analysis terminated with timeout after %d sec: %d", Options.v().getTimeout(), timeouts));
		sb.append(String.format("%n  Analysis terminated with intterupt exception: %d", interrupt));
		sb.append(String.format("%n  Analysis terminated with execException exception: %d", execException));
		sb.append(String.format("%n  Analysis terminated with outOfMemory exception: %d", outOfMemory));
		sb.append(String.format("%n  Analysis terminated with other exception: %d", other));
		System.out.println(sb.toString());
		return result;
	}

}
