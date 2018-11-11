/**
 * 
 */
package jayhorn.test.integration_tests;

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

import jayhorn.checker.EldaricaChecker;
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
public class FailingTests {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "horn-encoding/backlog");
		collectFileNamesRecursively(source_dir, filenames);
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

	public FailingTests(File source, String name) {
		this.sourceFile = source;
	}

//	@Test
//	public void testWithSpacer() {
//		verifyAssertions(new SpacerProverFactory());
//	}
	
	@Test
	public void testWithPrincess() {
		verifyAssertions(new PrincessProverFactory());
	}

//	@Test
//	public void testWithZ3() {
//		verifyAssertions(new Z3ProverFactory());
//	}

	protected void verifyAssertions(ProverFactory factory) {
		System.out.println("\nRunning test " + this.sourceFile.getName() + " with "+factory.getClass()+"\n");
		File classDir = null;
		try {			
			classDir = Util.compileJavaFile(this.sourceFile);
			SootToCfg soot2cfg = new SootToCfg();

			soottocfg.Options.v().setMemPrecision(3);
			soottocfg.Options.v().setExcAsAssert(true);

			soottocfg.Options.v().setPrintCFG(true);
//			soottocfg.Options.v().setArrayInv(true);
//			soottocfg.Options.v().setExactArrayElements(0);
			soot2cfg.run(classDir.getAbsolutePath(), null);
			Program program = soot2cfg.getProgram();
			

			jayhorn.Options.v().setTimeout(30);
//			jayhorn.Options.v().setPrintHorn(true);	
//			jayhorn.Options.v().setOut("/tmp/SatArrayInit");
	  		EldaricaChecker hornChecker = new EldaricaChecker(factory);

	  		boolean result = hornChecker.checkProgram(program) == EldaricaChecker.CheckerResult.SAFE;
	  		
			boolean expected = this.sourceFile.getName().startsWith("Sat");
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
