package benchtop.utils;

import benchtop.Classpath;
import benchtop.Tests;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class ClassesTest {

  @Test public void testParsing() throws Exception {
    IO.cleanDirectory(Tests.defaultWorkingDirectory());
    final File javaFile = Tests.createJavaFile(Tests.defaultDestination());

    final List<Class<?>> classes = Classes.compileJava(Tests.defaultWorkingDirectory(), javaFile);
    assertNotNull(classes);

    assertThat(classes.size() == 1, is(true));

    final Class<?> clazz = classes.get(0);
    assertNotNull(clazz);
  }


  @Test public void testAllFiles() throws Exception {
    IO.cleanDirectory(Tests.defaultWorkingDirectory());

    final List<File> javaFiles = Tests.createJavaFiles(Tests.defaultDestination(), Tests.JAVA_FILE, Tests.JAVA_FILE2);

    final List<Class<?>> classes = Classes.compileJava(
      Classpath.environmentClasspath(), Tests.defaultWorkingDirectory(), javaFiles
    );

    assertThat(classes.isEmpty(), is(false));
    assertThat(classes.size() == 3, is(true));
  }
}
