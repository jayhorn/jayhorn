/**
 * 
 */
package jayhorn.test.assorted_tests;

import java.io.File;
import java.io.IOException;
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
import scala.actors.threadpool.Arrays;
import soottocfg.cfg.Program;
import soottocfg.soot.SootToCfg;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class MutliFileTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: {2}")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "multi-test");
		collectProjects(source_dir, filenames);
		if (filenames.isEmpty()) {
			throw new RuntimeException("Test data not found!");
		}
		return filenames;
	}
	
	private static void collectProjects(File file, List<Object[]> filenames) {
		File[] directoryListing = file.listFiles();
		if (directoryListing != null) {
			Arrays.sort(directoryListing);
			for (File child : directoryListing) {
				//iterate over all projects.
				if (child.isDirectory()) {
					File[] childListing = child.listFiles();
					if (childListing!=null) {
						Arrays.sort(childListing);
						for (File grandChild : childListing) {
							if (grandChild.isFile() && grandChild.getName().endsWith("Main.java")) {
								filenames.add(new Object[] { grandChild, grandChild.getName(), child.getName() });
							}						
						}
					}
				}
			}
		}
	}

	private final String benchmarkName;
	
	public MutliFileTest(File source, String name, String benchmarkName) {
		this.sourceFile = source;
		this.benchmarkName = benchmarkName;
	}

	@Test
	public void testWithPrincess() {
		verifyAssertions(new PrincessProverFactory());
	}

//	@Test
//	public void testWithZ3() {
//		verifyAssertions(new Z3ProverFactory());
//	}

	
	protected void verifyAssertions(ProverFactory factory) {
		jayhorn.Options.v().setTimeout(60);
		System.out.println("\nRunning test " + this.sourceFile.getName() + " with "+factory.getClass()+"\n");
		File classDir = null;
		try {
			classDir = Util.compileJavaFile(this.sourceFile);
			SootToCfg soot2cfg = new SootToCfg();			
			soottocfg.Options.v().setMemPrecision(3);
			soot2cfg.run(classDir.getAbsolutePath(), null);
			
			Program program = soot2cfg.getProgram();
	  		Checker hornChecker = new Checker(factory);
	  		boolean result = hornChecker.checkProgram(program);

			boolean expected = benchmarkName.startsWith("Sat");
			Assert.assertTrue("For "+this.sourceFile.getName()+": expected "+expected + " but got "+result, expected==result);

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
