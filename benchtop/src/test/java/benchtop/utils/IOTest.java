package benchtop.utils;

import benchtop.Tests;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class IOTest {
  @BeforeClass public static void setup() throws Exception {
    IO.cleanDirectory(Tests.defaultWorkingDirectory());
  }

  @Test public void testFileCollection() throws Exception {

    final File javaFile = Tests.createJavaFile(Tests.defaultDestination());

    final List<File> files = IO.collectFiles(Tests.defaultWorkingDirectory(), "java");

    assertNotNull(files);

    assertThat(files.size() > 0, is(true));

    final File file = files.get(0);
    assertNotNull(file);

    assertEquals(file, javaFile);

    javaFile.deleteOnExit();
  }

  @Test public void testNameResolution() throws Exception {
    IO.cleanDirectory(Tests.defaultWorkingDirectory());

    final List<File> javaFiles = Tests.createJavaFiles(Tests.defaultDestination(), Tests.JAVA_FILE4, Tests.JAVA_FILE5);
    IO.resolveFullyQualifiedNames(Tests.defaultDestination(), javaFiles);

    assertThat(javaFiles.size() == 2, is(true));
  }

  @Test public void testLocalCaches() throws Exception {
    final List<File> local = IO.localCaches();
    assertThat(local.isEmpty(), is(false));
    assertThat(local.size() > 10, is(true));
  }

  @Test public void testCleanup() throws Exception {
    IO.cleanDirectory(Tests.defaultWorkingDirectory());

    Tests.createJavaFile(Tests.defaultDestination());

    assertThat(IO.collectFiles(Tests.defaultWorkingDirectory(), "java").size() == 1, is(true));

    IO.cleanDirectory(Tests.defaultWorkingDirectory());
    assertThat(IO.collectFiles(Tests.defaultWorkingDirectory(), "java").isEmpty(), is(true));
  }

  @Test public void testDeletion() throws Exception {
    final File directory = Files.createTempDir();
    assertThat(directory.exists(), is(true));

    IO.deleteDirectory(directory.toPath());
    assertThat(directory.exists(), is(false));
  }


  @AfterClass public static void tearDown() throws Exception {
    IO.cleanDirectory(Tests.defaultWorkingDirectory());
  }



}
