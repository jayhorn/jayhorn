/**
 * 
 */
package soottocfg.test.parser_test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import soottocfg.cfg.Program;
import soottocfg.cfg.ast2cfg.Ast2Cfg;
import soottocfg.cfg.ast2cfg.Cfg2AstPrinter;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class PrettyPrinterTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File astFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "parser_tests");
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
				if (child.isFile() && child.getName().endsWith(".jh")) {
					filenames.add(new Object[] { child, child.getName() });
				} else if (child.isDirectory()) {
					collectFileNamesRecursively(child, filenames);
				} else {
					// Ignore
				}
			}
		}
	}

	public PrettyPrinterTest(File source, String name) {
		this.astFile = source;
	}

	@Test
	public void testWithPrincess() {
		Program prog = new Program();
		Ast2Cfg a2c = new Ast2Cfg(prog);
		Assert.assertNotNull(a2c.loadFile(this.astFile));
		System.out.println(Cfg2AstPrinter.printProgramToString(prog));
	}

}
