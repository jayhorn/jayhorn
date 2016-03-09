package benchtop;

import benchtop.spi.RandoopConfiguration;
import benchtop.utils.Classes;
import benchtop.utils.IO;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class RandoopTestGenerationTest {

  private static final File DIR = RandoopConfiguration.randoopOutput();
  private static final List<Class<?>> TEST_CLASSES = new ArrayList<>();

  @BeforeClass public static void setup() throws Exception {

    /**
     * Set us all the right parts to run randoop.
     */
    Tests.randoopSetup(DIR);

    final List<File> files = IO.collectFiles(DIR, "java");
    final List<Class<?>> classes = Classes.compileJava(DIR, files.toArray(new File[files.size()]));

    TEST_CLASSES.addAll(classes);
  }

  @Test public void testRandoopClasses() throws Exception {

    for (Class<?> eachClass : TEST_CLASSES){
      if("RegressionTest".equals(eachClass.getName()) || "JavaFile".equals(eachClass.getName())){
        continue;
      }

      final Classpath cp = Classpath.of(DIR);

      Benchtop.junit(cp, eachClass.getCanonicalName());
    }
  }


  @AfterClass public static void tearDown() throws Exception {
    TEST_CLASSES.clear();

    if(DIR.exists()){
      IO.deleteDirectoryContent(DIR);
    }
  }

}
