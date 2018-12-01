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

import jayhorn.checker.EldaricaChecker;
import jayhorn.checker.SpacerChecker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.spacer.SpacerProverFactory;
import jayhorn.test.Util;
import java.util.Arrays;
import soottocfg.cfg.Program;
import soottocfg.soot.SootToCfg;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class HornRegressionTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "horn-encoding/regression");
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

	public HornRegressionTest(File source, String name) {
		this.sourceFile = source;
	}


	@Test
	public void testWithPrincess() {
		PrincessProverFactory factory = new PrincessProverFactory();
		Program program = getCFG(factory);
		if (program != null){
			EldaricaChecker eldarica = new EldaricaChecker(factory);
			boolean result = eldarica.checkProgram(program) == EldaricaChecker.CheckerResult.SAFE;
			boolean expected = this.sourceFile.getName().startsWith("Sat");
			Assert.assertTrue("For "+this.sourceFile.getName()+": expected "+expected + " but got "+result, expected==result);
		} else {
			Assert.fail();
		}
	}

	@Test
	public void testWithPrincessInlined() {
		PrincessProverFactory factory = new PrincessProverFactory();
		Program program = getCFG(factory, 1, -1, 100);
		if (program != null){
			EldaricaChecker eldarica = new EldaricaChecker(factory);
			boolean result = eldarica.checkProgram(program) == EldaricaChecker.CheckerResult.SAFE;
			boolean expected = this.sourceFile.getName().startsWith("Sat");
			Assert.assertTrue("For "+this.sourceFile.getName()+": expected "+expected + " but got "+result, expected==result);
		} else {
			Assert.fail();
		}
	}

	@Test
	public void testWithPrincessBounded() {
                if (this.sourceFile.getName().equals("UnsatObjectFromLib.java"))
                    return;

		PrincessProverFactory factory = new PrincessProverFactory();
		Program program = getCFG(factory, 1, 5, -1);
		if (program != null){
			EldaricaChecker eldarica = new EldaricaChecker(factory);
			EldaricaChecker.CheckerResult result = eldarica.checkProgram(program);
			EldaricaChecker.CheckerResult expected;
                        if (this.sourceFile.getName().startsWith("Sat"))
                            expected = EldaricaChecker.CheckerResult.SAFE;
                        else if (this.sourceFile.getName().startsWith("Unsat"))
                            expected = EldaricaChecker.CheckerResult.UNSAFE;
                        else
                            expected = EldaricaChecker.CheckerResult.UNKNOWN;
			Assert.assertTrue("For "+this.sourceFile.getName()+": expected "+expected + " but got "+result, expected==result);
		} else {
			Assert.fail();
		}
	}

//	@Test
	public void testWithSpacer() {
		SpacerProverFactory factory = new SpacerProverFactory();
		Program program = getCFG(factory);
		if (program != null){
			SpacerChecker spacer = new SpacerChecker(factory);
			System.out.println("Context: " + spacer.getProver());
			boolean result = spacer.checkProgram(program) == SpacerChecker.CheckerResult.SAFE;		
			boolean expected = this.sourceFile.getName().startsWith("Sat");
			Assert.assertTrue("For "+this.sourceFile.getName()+": expected "+expected + " but got "+result, expected==result);
		} else {
			Assert.fail();
		}
	}
	
	protected Program getCFG(ProverFactory factory) {
            return getCFG(factory, 1, -1, -1);
        }
	
	protected Program getCFG(ProverFactory factory,
                                 int initialHeapSize,
                                 int boundedHeapSize,
                                 int inline_size) {
		jayhorn.Options.v().setTimeout(60);
		System.out.println("\nRunning test " + this.sourceFile.getName() + " with "+factory.getClass()+"\n");
		File classDir = null;
		try {
			jayhorn.Options.v().setHeapMode(
                          boundedHeapSize >= 0 ?
                          jayhorn.Options.HeapMode.bounded :
                          jayhorn.Options.HeapMode.unbounded);
			jayhorn.Options.v().setInlineCount(inline_size);
//			jayhorn.Options.v().setInlineMaxSize(50);			
//			soottocfg.Options.v().setMemPrecision(1);
                        jayhorn.Options.v().setInitialHeapSize(initialHeapSize);
                        jayhorn.Options.v().setBoundedHeapSize(boundedHeapSize);
			classDir = Util.compileJavaFile(this.sourceFile);
			SootToCfg soot2cfg = new SootToCfg();
//			soottocfg.Options.v().setPrintCFG(true);
			soot2cfg.run(classDir.getAbsolutePath(), null);
			jayhorn.Options.v().setPrintHorn(true);

		
			return soot2cfg.getProgram();

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (classDir!=null) {
				classDir.deleteOnExit();
			}
		}	
	}
		
//	protected void verifyAssertions(ProverFactory factory) {
//		jayhorn.Options.v().setTimeout(60);
//		System.out.println("\nRunning test " + this.sourceFile.getName() + " with "+factory.getClass()+"\n");
//		File classDir = null;
//		try {
////			jayhorn.Options.v().setInlineCount(15);
////			jayhorn.Options.v().setInlineMaxSize(50);			
////			soottocfg.Options.v().setMemPrecision(1);
//			classDir = Util.compileJavaFile(this.sourceFile);
//			SootToCfg soot2cfg = new SootToCfg();
////			soottocfg.Options.v().setPrintCFG(true);
//			soot2cfg.run(classDir.getAbsolutePath(), null);
////			jayhorn.Options.v().setPrintHorn(true);
//
//			
//			Program program = soot2cfg.getProgram();
//			//boolean result;
//			
//			EldaricaChecker eldarica = new EldaricaChecker(factory);
//			boolean result = eldarica.checkProgram(program);
//
//			boolean expected = this.sourceFile.getName().startsWith("Sat");
//			Assert.assertTrue("For "+this.sourceFile.getName()+": expected "+expected + " but got "+result, expected==result);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//			Assert.fail();
//		} finally {
//			if (classDir!=null) {
//				classDir.deleteOnExit();
//			}
//		}	
//	}
		
}
