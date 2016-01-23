/**
 * 
 */
package jayhorn.old_inconsistency_check;

import java.util.Collections;
import java.util.Comparator;
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

import jayhorn.Log;
import jayhorn.Options;
import jayhorn.old_inconsistency_check.faultlocalization.LocalizationThread;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.Dominators;

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

	// statistics counters
	int normal = 0, timeouts = 0, interrupt = 0, execException = 0, outOfMemory = 0, other = 0;
	private final Map<String, Set<CfgBlock>> inconsistentBlocksPerMethod = new HashMap<String, Set<CfgBlock>>();
	private final Map<Inconsistency, Set<Statement>> localizedInconsistencies = new HashMap<Inconsistency, Set<Statement>>();

	public Map<String, Set<CfgBlock>> getInconsistentBlocksPerMethod() {
		return inconsistentBlocksPerMethod;
	}

	public Map<Inconsistency, Set<Statement>> getLocalizedInconsistencies() {
		return localizedInconsistencies;
	}

	/**
	 * Main method to check for inconsistencies. Takes a program as input
	 * and checks for each procedure if it contains inconsistent CfgBlocks.
	 * If so, it adds an entry to 'inconsistentBlocksPerMethod' (which can
	 * be retrieved using 'getInconsistentBlocksPerMethod()'), and applies
	 * fault localization to obtain the list of relevant statements which gets
	 * stored in 'localizedInconsistencies' (which can be retrieved using
	 * getLocalizedInconsistencies()).
	 * 
	 * @param program
	 */
	public void checkProgram(Program program) {
		ExecutorService executor = null;
		try {
			executor = Executors.newSingleThreadExecutor();

			for (Method method : program.getMethods()) {
				Set<Inconsistency> inconsistencies = findInconsistenciesInMethod(executor, program, method);

				// if we found inconsistencies, do the fault localization.
				if (!inconsistencies.isEmpty()) {
					System.out.println("Running fault localization.");
					for (Inconsistency inconsistency : inconsistencies) {
						localizedInconsistencies.put(inconsistency,
								localizeInconsistency(executor, program, inconsistency));
					}
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
			// throw new RuntimeException(e);
		} finally {
			if (executor != null) {
				executor.shutdownNow();
			}

		}

		StringBuilder sb = new StringBuilder();
		sb.append("Statistics:");
		sb.append(String.format("%n  Analyzed procedure: %d", program.getMethods().length));
		sb.append(String.format("%n  Analysis terminated normally for: %d", normal));
		sb.append(String.format("%n\t With inconsistencies: %d", inconsistentBlocksPerMethod.size()));
		sb.append(String.format("%n  Analysis terminated with timeout after %d sec: %d", Options.v().getTimeout(),
				timeouts));
		sb.append(String.format("%n  Analysis terminated with intterupt exception: %d", interrupt));
		sb.append(String.format("%n  Analysis terminated with execException exception: %d", execException));
		sb.append(String.format("%n  Analysis terminated with outOfMemory exception: %d", outOfMemory));
		sb.append(String.format("%n  Analysis terminated with other exception: %d", other));
		Log.info(sb.toString());

		printResults(inconsistentBlocksPerMethod);

		Log.info(printLocalizedInconsistencies(localizedInconsistencies));
	}

	/**
	 * Finds inconsistencies in a given 'method' and returns a set of
	 * inconsistencies that is "minimal" in the sense that no block given
	 * by the Inconsistency is dominated by a block that is also inconsistent.
	 * 
	 * This starts a new thread (with optional timeout given by
	 * Options.v().getTimeout()).
	 * 
	 * WARNING: This method DOES modify the program. It unwinds loops and
	 * adds additional blocks during SSA transformation!
	 * 
	 * @param executor
	 *            ExecutorService used to start the thread.
	 * @param program
	 *            The current program.
	 * @param method
	 *            A method from 'program' that we want to analyze.
	 * @return Set of inconsistencies.
	 */
	private Set<Inconsistency> findInconsistenciesInMethod(ExecutorService executor, Program program, Method method) {
		Set<Inconsistency> inconsistencies = new HashSet<Inconsistency>();
		if (method.vertexSet().isEmpty()) {
			// ignore empty methods
			normal++;
			return inconsistencies;
		}
		Prover prover = factory.spawn();
		Preconditions.checkArgument(prover != null, "Failed to initialize prover.");
		prover.setHornLogic(false);

		Set<CfgBlock> inconsistentBlocks = new HashSet<CfgBlock>();

		InconsistencyThread thread = new InconsistencyThread(program, method, prover);
		final Future<?> future = executor.submit(thread);
		try {
			 
			if (Options.v().getTimeout() <= 0) {
				future.get();
			} else {
				future.get(Options.v().getTimeout(), TimeUnit.SECONDS);
			}
			normal++;
			inconsistencies.addAll(getInconsistencies(method, thread.getInconsistentBlocks()));

			inconsistentBlocks.addAll(thread.getInconsistentBlocks());
			if (!inconsistentBlocks.isEmpty()) {
				inconsistentBlocksPerMethod.put(method.getMethodName(), inconsistentBlocks);
			}
		} catch (TimeoutException e) {
			if (!future.cancel(true)) {
				System.err.println("failed to cancel after timeout");
			}
			timeouts++;
			Log.error("Timeout for " + method.getMethodName());
		} catch (InterruptedException e) {
			interrupt++;
			e.printStackTrace();
		} catch (ExecutionException e) {
			execException++;
			throw new RuntimeException(e);
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
		return inconsistencies;
	}

	/**
	 * Uses the algorithm form "Explaining Inconsistent Code" (FSE'13) to find a
	 * small set of statements that is sufficient to explain the inconsistency.
	 * 
	 * This starts a new thread (with optional timeout given by
	 * Options.v().getTimeout()).
	 * 
	 * Note that this algorithm does not modify the program.
	 * 
	 * @param executor
	 *            ExecutorService used to start the thread.
	 * @param inconsistency
	 *            The inconsistency which we want to localize
	 * @return A (not necessarily minimal) subset of statements needed to
	 *         understand the inconsistency.
	 */
	private Set<Statement> localizeInconsistency(ExecutorService executor, Program program,
			Inconsistency inconsistency) {
		Set<Statement> relevantStmts = new HashSet<Statement>();
		Prover prover = factory.spawn();
		Preconditions.checkArgument(prover != null, "Failed to initialize prover.");
		prover.setHornLogic(false);
		LocalizationThread localizationThread = new LocalizationThread(inconsistency, prover);
		final Future<?> inconsistencyFuture = executor.submit(localizationThread);
		try {
//			while (!inconsistencyFuture.isDone() || executor.awaitTermination(Options.v().getTimeout(), TimeUnit.SECONDS));
			if (Options.v().getTimeout() <= 0) {
				inconsistencyFuture.get();
			} else {
				inconsistencyFuture.get(Options.v().getTimeout(), TimeUnit.SECONDS);
			}
			relevantStmts.addAll(localizationThread.getRelevantStatements());
		} catch (TimeoutException e) {			
			if (!inconsistencyFuture.cancel(true)) {
				System.err.println("failed to cancel after timeout");
			}
			Log.error("Localization timeout for " + inconsistency.getMethod().getMethodName());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			prover.stop();
			prover.shutdown();
			if (inconsistencyFuture != null && !inconsistencyFuture.isDone() && !inconsistencyFuture.cancel(true)) {
				throw new RuntimeException("Could not cancel broken thread!");
			}
		}
		return relevantStmts;
	}

	/**
	 * Gets the set of inconsistent blocks in a 'method' and returns a
	 * set of inconsistencies that is minimal in the sense that no inconsistent
	 * block in an inconsistency in this set is dominated by another
	 * inconsistent
	 * block. Inconsistent blocks that are dominated by other inconsistent
	 * blocks
	 * are dropped.
	 * 
	 * @param method
	 *            Method with inconsistencies.
	 * @param inconsistentBlocks
	 *            Set of blocks that are inconsistent in 'method'.
	 * @return Minimal set of inconsistencies.
	 */
	private Set<Inconsistency> getInconsistencies(Method method, Set<CfgBlock> inconsistentBlocks) {
		Set<Inconsistency> inconsistencies = new HashSet<Inconsistency>();
		if (inconsistentBlocks != null && !inconsistentBlocks.isEmpty()) {
			Dominators<CfgBlock> dom = new Dominators<CfgBlock>(method, method.getSource());
			for (CfgBlock b : inconsistentBlocks) {
				if (!dom.isStrictlyDominatedByAny(b, inconsistentBlocks)) {
					inconsistencies.add(new Inconsistency(method, b));
				}
			}
		}
		return inconsistencies;
	}

	Map<String, Set<Integer>> lineNumbersWithDuplicates = new HashMap<String, Set<Integer>>();

	public void setDuplicatedSourceLocations(Set<SourceLocation> duplicates) {
		for (SourceLocation loc : duplicates) {
			if (loc.getSourceFileName() != null) {
				if (!lineNumbersWithDuplicates.containsKey(loc.getSourceFileName())) {
					lineNumbersWithDuplicates.put(loc.getSourceFileName(), new HashSet<Integer>());
				}
				if (loc.getLineNumber() > 0) {
					lineNumbersWithDuplicates.get(loc.getSourceFileName()).add(loc.getLineNumber());
				}
			}
		}
	}

	/**
	 * This is just awful hacking to print sth to stdout. Will be replaced
	 * by proper reporting some day in the future.
	 * 
	 * @param localizedInconsistencies
	 */
	private String printLocalizedInconsistencies(Map<Inconsistency, Set<Statement>> localizedInconsistencies) {
		Set<String> methodNames = new HashSet<String>();
		for (Entry<Inconsistency, Set<Statement>> entry : localizedInconsistencies.entrySet()) {
			methodNames.add(entry.getKey().getMethod().getMethodName());
		}
		StringBuilder sb = new StringBuilder();
		for (String methodName : methodNames) {
			sb.append("Method: ");
			sb.append(methodName);
			sb.append(System.getProperty("line.separator"));
			for (Entry<Inconsistency, Set<Statement>> entry : localizedInconsistencies.entrySet()) {
				if (entry.getKey().getMethod().getMethodName().equals(methodName)) {
					Set<Integer> lineNumberSet = new HashSet<Integer>();
					boolean hasClonedLine = false;
					for (Statement s : entry.getValue()) {
						SourceLocation loc = s.getSourceLocation();
						if (loc == null) {
							System.err.println("please add source location for " + s);
						} else {
							if (lineNumbersWithDuplicates.containsKey(methodName)
									&& lineNumbersWithDuplicates.get(methodName).contains(loc.getLineNumber())) {
								hasClonedLine = true;
							}
							lineNumberSet.add(loc.getLineNumber());
						}
					}
					List<Integer> sortedLineNumbers = new LinkedList<Integer>(lineNumberSet);
					Collections.sort(sortedLineNumbers);
					sb.append("  Line numbers:");
					if (hasClonedLine) {
						sb.append("(false positive)");
					}
					String comma = "";
					for (Integer i : sortedLineNumbers) {
						sb.append(comma);
						comma = ", ";
						sb.append(i);
					}
					sb.append(System.getProperty("line.separator"));
					List<Statement> sortedStmts = new LinkedList<Statement>(entry.getValue());
					Collections.sort(sortedStmts, new Comparator<Statement>() {
						public int compare(Statement s1, Statement s2) {
							return Integer.compare(s1.getJavaSourceLine(), s2.getJavaSourceLine());
						}
					});
					for (Statement s : sortedStmts) {
						sb.append("\t");
						if (s.getSourceLocation() != null) {
							sb.append(s.getSourceLocation().getLineNumber());
						} else {
							sb.append("??");
						}
						sb.append(": ");
						sb.append(s);
						sb.append(System.getProperty("line.separator"));
					}
				}
			}
		}
		return sb.toString();
	}

	private void printResults(Map<String, Set<CfgBlock>> result) {
		Map<String, Map<String, List<Integer>>> resultsByFile = new HashMap<String, Map<String, List<Integer>>>();
		for (Entry<String, Set<CfgBlock>> entry : result.entrySet()) {
			String sourceFileName = "Unknown";
			LinkedHashSet<Integer> lines = new LinkedHashSet<Integer>();
			for (CfgBlock b : entry.getValue()) {
				for (Statement s : b.getStatements()) {
					SourceLocation loc = s.getSourceLocation();
					if (loc != null) {
						if (loc.getSourceFileName() != null) {
							sourceFileName = loc.getSourceFileName();
						}
						if (lineNumbersWithDuplicates.containsKey(entry.getKey())
								&& lineNumbersWithDuplicates.get(entry.getKey()).contains(loc.getLineNumber())) {
							// ditch that warning because it contians a
							// duplicated block
							Log.error("Suppressed lines");
							lines.clear();
							break;
						}
						lines.add(loc.getLineNumber());
					}
				}
			}
			if (lines.isEmpty()) {
				Log.error("Inconsistency without lines in " + sourceFileName);
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
		Log.info(sb.toString());
	}
}
