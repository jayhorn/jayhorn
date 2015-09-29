/**
 * 
 */
package jayhorn.test.callgraph_test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jayhorn.soot.SootRunner.CallgraphAlgorithm;
import jayhorn.soot.SootToCfg;
import jayhorn.util.Util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class CallGraphTest {

	protected static final String userDir = System.getProperty("user.dir") + "/";
	protected static final String testRoot = userDir + "src/test/resources/";
	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "callgraph_tests/");
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

	public CallGraphTest(File source, String name) {
		this.sourceFile = source;
	}

	@Test
	public void test_cha() {
		testWithCallgraphAlgorithm(CallgraphAlgorithm.None);
	}

	// @Test
	// public void test_spark() {
	// testWithCallgraphAlgorithm("spark");
	// }

	// TODO: VTA and RTA are not yet working.
	// @Test
	// public void test_vta() {
	// testWithCallgraphAlgorithm("vta");
	// }

	protected void testWithCallgraphAlgorithm(CallgraphAlgorithm algorithm) {
		soot.G.reset();
		System.out.println("Running test " + this.sourceFile.getName() + " with algorithm " + algorithm);
		SootToCfg soot2cfg = new SootToCfg();
		File classDir = null;
		try {
			classDir = Util.compileJavaFile(this.sourceFile);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		if (classDir == null) {
			Assert.fail();
		}
		soot2cfg.run(classDir.getAbsolutePath(), null, algorithm);
	}

}
