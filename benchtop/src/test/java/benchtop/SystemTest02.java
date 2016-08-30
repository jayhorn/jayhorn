package benchtop;

import benchtop.spi.RandoopConfiguration;
import benchtop.utils.Soot;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Huascar Sanchez
 */
public class SystemTest02 {

  private static final File DIR = RandoopConfiguration.randoopOutput();

  private static File compiledTempFolder;

  @BeforeClass public static void setup() throws Exception {
    compiledTempFolder = Files.createTempDir();

    Tests.testAllSetup(compiledTempFolder);
  }


  @Test public void testDynamicallyCreatedClasses() {
    try {
      Tests.consumesExecutionBundle(compiledTempFolder, DIR, false);
    } catch (Exception e){
      fail("Failed due to : " + e.getLocalizedMessage());
    }
  }

  @Test public void testDynamicallyCreatedAndTransformedClasses(){
    try {
      Tests.consumesExecutionBundle(compiledTempFolder, DIR, true);
    } catch (Exception e){
      fail("Failed due to : " + e.getLocalizedMessage());
    }
  }


  @Ignore @Test public void testFailureOfDynamicallyCreatedAndTransformedClasses(){
    Soot.withSanityCheck();
    try {
      Tests.consumesExecutionBundle(compiledTempFolder, DIR, true);
      fail("Expected an exception (e.g., NullpointerException) to be thrown.");
    } catch (Exception e){
      assertNotNull(e);
    }
  }


  @AfterClass public static void tearDown() throws Exception {
    Tests.testTeardown(compiledTempFolder);

    compiledTempFolder = null;
  }
}
