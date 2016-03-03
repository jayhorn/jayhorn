package benchtop.utils;

import benchtop.Tests;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class ClassesTest {

  @Test public void testFileCollection() throws Exception {
    final File javaFile = Tests.createJavaFile(Tests.defaultDestination());

    final List<File> files = Classes.collectFiles(Tests.defaultWorkingDirectory(), "java");

    assertNotNull(files);

    assertThat(files.size() > 0, is(true));

    final File file = files.get(0);
    assertNotNull(file);

    assertEquals(file, javaFile);

    javaFile.deleteOnExit();
  }

  @Test public void testParsing() throws Exception {
    final File javaFile = Tests.createJavaFile(Tests.defaultDestination());

    final List<Class<?>> classes = Classes.compileJava(Tests.defaultWorkingDirectory(), javaFile);
    assertNotNull(classes);

    assertThat(classes.size() == 1, is(true));

    final Class<?> clazz = classes.get(0);
    assertNotNull(clazz);

    deleteClassFiles(javaFile);
  }

  private static void deleteClassFiles(File javaFile) throws IOException {
    javaFile.deleteOnExit();
    Files.delete(new File(Tests.defaultDestination() + "JavaFile.class").toPath());
  }
}
