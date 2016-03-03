package benchtop.utils;

import benchtop.Tests;
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
}
