/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;

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
		sb.append(String.format("%n\t With inconsistencies: %d", result.size()));
		sb.append(String.format("%n  Analysis terminated with timeout after %d sec: %d", Options.v().getTimeout(), timeouts));
		sb.append(String.format("%n  Analysis terminated with intterupt exception: %d", interrupt));
		sb.append(String.format("%n  Analysis terminated with execException exception: %d", execException));
		sb.append(String.format("%n  Analysis terminated with outOfMemory exception: %d", outOfMemory));
		sb.append(String.format("%n  Analysis terminated with other exception: %d", other));
		System.out.println(sb.toString());
		
		printResults(result);
		
		return result;
	}

	Map<String, Set<Integer>> bla = new HashMap<String, Set<Integer>>();
	
	public void setDuplicatedSourceLocations(Set<SourceLocation> duplicates) {
		for (SourceLocation loc : duplicates) {
			if (loc.getSourceFileName()!=null) {
				if (!bla.containsKey(loc.getSourceFileName())) {
					bla.put(loc.getSourceFileName(), new HashSet<Integer>());
				}
				if (loc.getLineNumber()>0) {
					bla.get(loc.getSourceFileName()).add(loc.getLineNumber());
				}
			}
		}
	}

	
	private void printResults(Map<String, Set<CfgBlock>> result) {
		Map<String, Map<String, List<Integer>>> resultsByFile = new HashMap<String, Map<String, List<Integer>>>();
		for (Entry<String, Set<CfgBlock>> entry : result.entrySet()) {
			String sourceFileName = "Unknown";
			LinkedHashSet<Integer> lines = new LinkedHashSet<Integer>();
			for (CfgBlock b : entry.getValue()) {
				for (Statement s : b.getStatements()) {
					SourceLocation loc = s.getSourceLocation();
					if (loc!=null) {
						if (loc.getSourceFileName()!=null) {
							sourceFileName = loc.getSourceFileName();
						}
						if (bla.containsKey(entry.getKey()) && bla.get(entry.getKey()).contains(loc.getLineNumber())) {
							//ditch that warning because it contians a duplicated block
							lines.clear();
							break;
						}
						lines.add(loc.getLineNumber());
					}
				}
			}
			if (lines.isEmpty()) {
				System.err.println("Inconsistency without lines in " + sourceFileName);
				continue;
			}
			List<Integer> sortedLines = new LinkedList<Integer>(lines);
			Collections.sort(sortedLines);
			if (!resultsByFile.containsKey(sourceFileName)) {
				resultsByFile.put(sourceFileName, new HashMap<String, List<Integer>>());
			}
			Map<String, List<Integer>> r = resultsByFile.get(sourceFileName);
			if (!r.containsKey(entry.getKey())) {
				r.put(entry.getKey(), sortedLines);
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Map<String, List<Integer>>> entry : resultsByFile.entrySet()) {
			sb.append("In file: ");
			sb.append(entry.getKey());
			sb.append(System.getProperty("line.separator"));
			for (Entry<String, List<Integer>> entry2 : entry.getValue().entrySet()) {
				sb.append("  ");
				sb.append(entry2.getKey());
				sb.append(System.getProperty("line.separator"));
				String comma = "\t";
				for (Integer line : entry2.getValue()) {
					sb.append(comma);
					sb.append(line);
					comma = ", ";
				}
				sb.append(System.getProperty("line.separator"));				
			}
			sb.append(System.getProperty("line.separator"));
			sb.append(System.getProperty("line.separator"));
		}
		System.err.println(sb.toString());
	}
}
