package jayhorn.test.integration_tests;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jayhorn.checker.Checker;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import scala.actors.threadpool.Arrays;
import soottocfg.soot.SootToCfg;

@RunWith(Parameterized.class)
public class CbmcTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";
	
	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> fileAndResult = new LinkedList<Object[]>();		
		final File source_dir = new File(testRoot + "cbmc-java/");
		File[] directoryListing = source_dir.listFiles();
		if (directoryListing != null) {
			Arrays.sort(directoryListing);
			for (File child : directoryListing) {
				//found a directory containing a test.
				if (child.isDirectory() && child.listFiles()!=null) {
					//check if the directory contains a test.desc that
					//states the desired outcome.

					final File[] files = child.exists() && child.listFiles() == null ? new File[0] : child.listFiles();

					assert files != null;
					for (File f : files) {
						if (f.isFile() && f.getName().endsWith(".desc")) {
							fileAndResult.add(new Object[] { child, child.getName(), parseOutcomeFromDescFile(f)});
							break;
						}
					}
				}
			}
		} else {
			throw new RuntimeException("Test directory "+source_dir+" not found.");
		}		
		return fileAndResult;
	}
	
	private final File classDir;
	private final boolean expectedResult;
	private final String description;
	
	public CbmcTest(File classDir, String name, boolean expected) {
		this.classDir = classDir;
		this.expectedResult = expected;
		this.description = name;
	}

	
	@Test
	public void testWithPrincess() {
		verifyAssertions(new PrincessProverFactory());
	}

//	@Test
//	public void testWithZ3() {		
//		verifyAssertions(new Z3ProverFactory());
//	}
		
		
	private void verifyAssertions(ProverFactory factory) {
		jayhorn.Options.v().setTimeout(15);
		System.out.println("Running test: "+this.description);
		System.out.println("\texpected result: "+ expectedResult);

		SootToCfg soot2cfg = new SootToCfg();
		soot2cfg.run(classDir.getAbsolutePath(), classDir.getAbsolutePath());	
		
		Checker checker = new Checker(factory);
		boolean result = checker.checkProgram(soot2cfg.getProgram());

//		Checker checker = new Checker(factory);
//		boolean result = checker.checkProgram(soot2cfg.getProgram());
		
		org.junit.Assert.assertTrue("Unexpected result for "+description+". Expected: "+expectedResult+" but got "+ result, expectedResult==result);		
	}
	
	/**
	 * Parses a desc file from the cbmc test cases and returns 
	 * if the verification of this file is expected to pass or fail.
	 * @param descFile
	 * @return True, if the file contains ^VERIFICATION SUCCESSFUL$, and False otherwise.
	 */
	private static boolean parseOutcomeFromDescFile(File descFile) {
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
