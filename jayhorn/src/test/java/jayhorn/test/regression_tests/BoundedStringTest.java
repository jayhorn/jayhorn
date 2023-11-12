/**
 * 
 */
package jayhorn.test.regression_tests;

import jayhorn.checker.Checker;
import jayhorn.checker.EldaricaChecker;
import jayhorn.hornify.encoder.StringEncoder;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.test.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import soottocfg.cfg.Program;
import soottocfg.soot.SootToCfg;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class BoundedStringTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "horn-encoding/strings");
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

	public BoundedStringTest(File source, String name) {
		this.sourceFile = source;
	}

//	@Test
//	public void testWithPrincessRecursiveLTR() {
//		jayhorn.Options.v().setStringEncoding(StringEncoder.StringEncoding.recursive);
//		jayhorn.Options.v().setStringDirection(StringEncoder.StringDirection.ltr);
//		verifyAssertions(new PrincessProverFactory());
//	}

//	@Test
//	public void testWithPrincessRecursiveWithPrecLTR() {
//		jayhorn.Options.v().setStringEncoding(StringEncoder.StringEncoding.recursiveWithPrec);
//		jayhorn.Options.v().setStringDirection(StringEncoder.StringDirection.ltr);
//		verifyAssertions(new PrincessProverFactory());
//	}

//	@Test
//	public void testWithPrincessIterativeLTR() {
//		jayhorn.Options.v().setStringEncoding(StringEncoder.StringEncoding.iterative);
//		jayhorn.Options.v().setStringDirection(StringEncoder.StringDirection.ltr);
//		verifyAssertions(new PrincessProverFactory());
//	}

//	@Test
//	public void testWithPrincessRecursiveRTL() {
//		jayhorn.Options.v().setStringEncoding(StringEncoder.StringEncoding.recursive);
//		jayhorn.Options.v().setStringDirection(StringEncoder.StringDirection.rtl);
//		verifyAssertions(new PrincessProverFactory());
//	}

	@Test
	public void testWithPrincessRecursiveWithPrecRTL() {
		jayhorn.Options.v().setStringEncoding(StringEncoder.StringEncoding.recursiveWithPrec);
		jayhorn.Options.v().setStringDirection(StringEncoder.StringDirection.rtl);
		verifyAssertions(new PrincessProverFactory());
	}

//	@Test
//	public void testWithPrincessIterativeRTL() {
//		jayhorn.Options.v().setStringEncoding(StringEncoder.StringEncoding.iterative);
//		jayhorn.Options.v().setStringDirection(StringEncoder.StringDirection.rtl);
//		verifyAssertions(new PrincessProverFactory());
//	}

	public void verifyAssertions(ProverFactory factory) {
		Program program = getCFG(factory);
		if (program != null) {
			EldaricaChecker eldarica = new EldaricaChecker(factory);
			Checker.CheckerResult result = eldarica.checkProgram(program);
			Checker.CheckerResult expected;
			if (this.sourceFile.getName().startsWith("Sat")) {
				expected = EldaricaChecker.CheckerResult.SAFE;
			} else if (this.sourceFile.getName().startsWith("Unsat")) {
				expected = EldaricaChecker.CheckerResult.UNSAFE;
			} else {
				expected = EldaricaChecker.CheckerResult.UNKNOWN;
			}
			Assert.assertTrue("For " + this.sourceFile.getName() + ": expected " + expected + " but got " + result, result == expected);
		} else {
			Assert.fail();
		}
	}

	protected Program getCFG(ProverFactory factory) {
            return getCFG(factory, 1, 10, -1);
        }

	protected Program getCFG(ProverFactory factory,
                                 int initialHeapSize,
                                 int boundedHeapSize,
                                 int inline_size) {
		jayhorn.Options.v().setTimeout(120);
		System.out.println("\nRunning test " + this.sourceFile.getName() + " with "+factory.getClass()+"\n");
		File classDir = null;
		try {
			jayhorn.Options.v().setHeapMode(
					boundedHeapSize >= 0 ?
							jayhorn.Options.HeapMode.bounded :
							jayhorn.Options.HeapMode.unbounded);
			jayhorn.Options.v().setInlineCount(inline_size);
			jayhorn.Options.v().setInlineMaxSize(50);
//			soottocfg.Options.v().setMemPrecision(1);
			jayhorn.Options.v().setInitialHeapSize(initialHeapSize);
			jayhorn.Options.v().setBoundedHeapSize(boundedHeapSize);
			jayhorn.Options.v().useSpecs = true;
			classDir = Util.compileJavaFile(this.sourceFile);
			SootToCfg soot2cfg = new SootToCfg();
//			soottocfg.Options.v().setPrintCFG(true);
			soottocfg.Options.v().setBuiltInSpecs(true);
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
		
}
