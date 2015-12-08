package jayhorn.test.integration_tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.junit.Test;

import jayhorn.checker.Checker;
import soottocfg.soot.SootToCfg;

public class CbmcTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	@Test
	public void test() {
		final File source_dir = new File(testRoot + "cbmc-java/");

		File[] directoryListing = source_dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				//found a directory containing a test.
				if (child.isDirectory()) {
					//check if the directory contains a test.desc that
					//states the desired outcome.
					for (File f : child.listFiles()) {
						if (f.isFile() && f.getName().endsWith(".desc")) {
							runCbmcTest(child, parseOutcomeFromDescFile(f));
							break;
						}
					}
				}
			}
		} else {
			throw new RuntimeException("Test directory "+source_dir+" not found.");
		}
	}
	
	private void runCbmcTest(File dir, boolean expectedResult) {
		System.out.println("Checking "+ dir);
		System.out.println("\texpected result: "+ expectedResult);

		SootToCfg soot2cfg = new SootToCfg();
		soot2cfg.run(dir.getAbsolutePath(), dir.getAbsolutePath());		
		Checker checker = new Checker();
		boolean result = checker.checkProgram(soot2cfg.getProgram());
		
		org.junit.Assert.assertTrue("Unexpected result for "+dir, expectedResult==result);
		
	}
	
	/**
	 * Parses a desc file from the cbmc test cases and returns 
	 * if the verification of this file is expected to pass or fail.
	 * @param descFile
	 * @return True, if the file contains ^VERIFICATION SUCCESSFUL$, and False otherwise.
	 */
	private boolean parseOutcomeFromDescFile(File descFile) {
		Charset charset = Charset.forName("UTF8");
		CharsetDecoder decoder = charset.newDecoder();
		
		try (Reader iReader = new InputStreamReader(new FileInputStream(descFile), decoder);
			BufferedReader bReader = new BufferedReader(iReader)) {			
		    String line = null;
		    while ((line = bReader.readLine()) != null) {
		        if (line.contains("^VERIFICATION SUCCESSFUL$")) {
		        	return true;
		        }
		        if (line.contains("^VERIFICATION FAILED$")) {
		        	return false;
		        }		        
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
		throw new RuntimeException("Test "+descFile+" must contain expected outcome in .desc file.");
	}

}
