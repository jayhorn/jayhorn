package benchtop;

import benchtop.configs.RandoopConfiguration;
import benchtop.utils.Classes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class FunctionalTest {

  private static final File DIR = RandoopConfiguration.randoopOutput();
  private static final List<Class<?>> TEST_CLASSES = new ArrayList<>();

  @BeforeClass public static void setup() throws Exception {

    if(DIR.exists()){
      Classes.deleteDirectoryContent(DIR);
    }

    final File javaFile = Tests.createJavaFile(DIR.getAbsolutePath() + "/");

    final List<Class<?>> javaFileClass = Classes.compileJava(
      DIR, javaFile
    );

    assert javaFileClass.get(0) != null;

    final Classpath env = Classpath.union(
      Classpath.environmentClasspath(),
      Classpath.of(DIR)
    );


    Benchtop.randoop(
      env, DIR, "JavaFile"
    );

    final List<File> files = Classes.collectFiles(DIR, "java");
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
      Classes.deleteDirectoryContent(DIR);
    }
  }


}
