	/**
 * 
 */
package soottocfg.test.translation_test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import soottocfg.soot.SootToCfg;
import soottocfg.test.Util;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class TranslationTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "translation_tests/");
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

	public TranslationTest(File source, String name) {
		this.sourceFile = source;
	}

	@Test
	public void test() {
		soot.G.reset();
		System.out.println("Running test " + this.sourceFile.getName());
		SootToCfg soot2cfg = new SootToCfg();
		File classDir = null;
		try {
			classDir = Util.compileJavaFile(this.sourceFile, System.getProperty("java.class.path"));
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		if (classDir == null) {
			Assert.fail();
		}
		soot2cfg.run(classDir.getAbsolutePath(), null);		
	}

}
