/**
 * 
 */
package jayhorn.test.soundness;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jayhorn.checker.Checker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.test.Util;
import soottocfg.cfg.Program;
import soottocfg.soot.SootToCfg;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class BigSoundnessTests {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	private static int resultCorrect = 0;
	private static int resultImprecise = 0;
	private static int resultUnsound = 0;
	private static int resultException = 0;
	private static String unsoundFileNames = ""; 

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		// collect benchmarks from multiple places
		collectFileNamesRecursively(new File(testRoot + "cbmc-src"), filenames);		
		collectFileNamesRecursively(new File(testRoot + "horn-encoding/regression"), filenames);
		collectFileNamesRecursively(new File(testRoot + "horn-encoding/backlog"), filenames);
		collectFileNamesRecursively(new File(testRoot + "horn-encoding/mem_precision"), filenames);
		collectFileNamesRecursively(new File(testRoot + "horn-encoding/classics"), filenames);
		if (filenames.isEmpty()) {
			throw new RuntimeException("Test data not found!");
		}
		return filenames;
	}

	private static void collectFileNamesRecursively(File file, List<Object[]> filenames) {
		File[] directoryListing = file.listFiles();
		if (directoryListing != null) {
			Arrays.sort(directoryListing);
			for (File child : directoryListing) {
				if (child.isFile() && child.getName().endsWith(".java")) {
					filenames.add(new Object[] { child, child.getName() });
				} else if (child.isDirectory()) {
					collectFileNamesRecursively(child, filenames);
				} else {
					// Ignore
				}
			}
		}
	}

	public BigSoundnessTests(File source, String name) {
		this.sourceFile = source;
	}

	@Test
	public void testWithPrincess() throws IOException {
		verifyAssertions(new PrincessProverFactory());
	}

	// @Test
	// public void testWithZ3() {
	// verifyAssertions(new Z3ProverFactory());
	// }

	protected void verifyAssertions(ProverFactory factory) throws IOException {
		System.out.println("\nRunning test " + this.sourceFile.getName() + " with " + factory.getClass() + "\n");
		File classDir = null;
		try {
//			soottocfg.Options.v().passCallerIdIntoMethods(true);
//			 soottocfg.Options.v().setPrintCFG(true);
			// soottocfg.Options.v().setExcAsAssert(true);
			classDir = Util.compileJavaFile(this.sourceFile);
			SootToCfg soot2cfg = new SootToCfg();
//			soottocfg.Options.v().setMemPrecision(3);
//			soottocfg.Options.v().setInlineCount(3);
//			soottocfg.Options.v().setInlineMaxSize(20);
//			soottocfg.Options.v().setArrayInv(true);
			soottocfg.Options.v().setExactArrayElements(0);
			boolean expected = this.sourceFile.getName().startsWith("Sat");
			boolean result = false;
			try {
				soot2cfg.run(classDir.getAbsolutePath(), null);
				jayhorn.Options.v().setTimeout(30);
//				jayhorn.Options.v().setPrintHorn(true);
				jayhorn.Options.v().setSolverOptions("abstract");

				Program program = soot2cfg.getProgram();
				Checker hornChecker = new Checker(factory);
				result = hornChecker.checkProgram(program);

				if (expected == result) {
					resultCorrect++;
				} else if (expected == true) {
					resultImprecise++;
				} else {
					resultUnsound++;
					StringBuilder sb = new StringBuilder();
					sb.append(unsoundFileNames);
					sb.append("  ");
					sb.append(this.sourceFile.getAbsolutePath());
					sb.append("\n");
					unsoundFileNames = sb.toString();
				}
			} catch (Exception e) {
				resultException++;
				e.printStackTrace();
				throw new RuntimeException(e.toString());
			} finally {
				StringBuilder sb = new StringBuilder();
				sb.append("**************************************\n");
				final String stats = String.format("unsound: %1$-5d exception: %2$-5d imprecise: %3$-5d correct: %4$-5d", resultUnsound,
						resultException, resultImprecise, resultCorrect);
				sb.append(stats);
				sb.append("\n");
				if (unsoundFileNames.length()>0) {
				sb.append("\nUnsound results:\n");
				sb.append(unsoundFileNames);
				}
				sb.append("**************************************\n");
				System.err.println(sb.toString());
			}
			Assert.assertTrue("For " + this.sourceFile.getName() + ": expected " + expected + " but got " + result,
					expected == result);

		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (classDir != null) {
				classDir.deleteOnExit();
			}
		}
	}

}
