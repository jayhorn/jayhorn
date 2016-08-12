package soottocfg.test.soot_test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import soottocfg.test.Util;

public class SootTranslationTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	@Test
	public void test() {

		File classFileDir = null;
		try {

			final File source_dir = new File(testRoot + "soot_tests/");
			List<File> sourceFiles = new LinkedList<File>();
			File[] directoryListing = source_dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if (child.isFile() && child.getName().endsWith(".java")) {
						sourceFiles.add(child);
						System.out.println("Adding " + child + " to input");
					}
				}
			}

			classFileDir = Util.compileJavaFiles(sourceFiles.toArray(new File[sourceFiles.size()]));
			soottocfg.Main.main(new String[] { classFileDir.getAbsolutePath() });
		} catch (IOException e) {
			e.printStackTrace();
			fail("Translation failed.");
		} finally {
			if (classFileDir != null) {
				try {
					Util.delete(classFileDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
