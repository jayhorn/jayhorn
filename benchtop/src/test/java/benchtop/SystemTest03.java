package benchtop;

import benchtop.spi.RandoopConfiguration;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.fail;

/**
 * @author Huascar Sanchez
 */
public class SystemTest03 {

  private static final File DIR = RandoopConfiguration.randoopOutput();

  private static File compiledTempFolder;

  @BeforeClass public static void setup() throws Exception {
    compiledTempFolder = Files.createTempDir();

    Tests.testSetup(compiledTempFolder, Tests.JAVA_FILE6);
  }

  @Test public void testDynamicallyCreatedClasses() {
    try {
      final Result result = Tests.consumesExecutionBundle(compiledTempFolder, DIR, true);

      Result.passOrThrow(result);
    } catch (Exception e){
      fail("Failed due to : " + e.getLocalizedMessage());
    }
  }

  @AfterClass public static void tearDown() throws Exception {
    Tests.testTeardown(compiledTempFolder);

    compiledTempFolder = null;
  }

}
