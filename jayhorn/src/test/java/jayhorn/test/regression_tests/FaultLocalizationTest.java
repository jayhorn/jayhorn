/**
 * 
 */
package jayhorn.test.regression_tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jayhorn.old_inconsistency_check.InconsistencyChecker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.z3.Z3ProverFactory;
import jayhorn.test.Util;
import soottocfg.soot.SootToCfg;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class FaultLocalizationTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "faultlocalization/");
		File[] directoryListing = source_dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isFile() && child.getName().endsWith(".java")) {
					filenames.add(new Object[] { child, child.getName() });
				} else {
					// Ignore
				}
			}
		} else {
			// Handle the case where dir is not really a directory.
			// Checking dir.isDirectory() above would not be sufficient
			// to avoid race conditions with another process that deletes
			// directories.
			System.err.println("Test data in " + userDir + " not found");
			throw new RuntimeException("Test data not found!");
		}
		return filenames;
	}

	public FaultLocalizationTest(File source, String name) {
		this.sourceFile = source;
	}

//	private final ProverFactory factory = new PrincessProverFactory();
//	private final ProverFactory factory = new Z3ProverFactory();

	@Test
	public void testOldAlgorithmWithPrincess() {
		oldAlgorithm(new PrincessProverFactory());
	}

	@Test
	public void testOldAlgorithmWithZ3() {
		oldAlgorithm(new Z3ProverFactory());
	}

	
	protected void oldAlgorithm(ProverFactory factory) {
		/**
		 * TODO: for now, this just ensures that the interpolation
		 * does not crash during the fault localization. 
		 * Right now, the results of the interpolation from Z3 and
		 * Princess are too different, so it does not make sense to
		 * compare their output directly.
		 * 
		 * What we need is a better way to propagate interpolants.
		 * Then we could ensure that the result by both solvers is
		 * sort of minimal.
		 */
		System.out.println("\nRunning test " + this.sourceFile.getName() + " with "+factory.getClass()+"\n");
		File classDir = null;
		try {
			classDir = Util.compileJavaFile(this.sourceFile);
			SootToCfg soot2cfg = new SootToCfg(false, true);
			soot2cfg.run(classDir.getAbsolutePath(), null);
			InconsistencyChecker checker = new InconsistencyChecker(factory);
			checker.setDuplicatedSourceLocations(soot2cfg.getDuplicatedSourceLocations());
			
			checker.checkProgram(soot2cfg.getProgram());
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			if (classDir!=null) {
				classDir.deleteOnExit();
			}
		}	
	}
		
}
