package soottocfg.randoop;

import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class ClasspathTest {
  @Test public void testFileVarArgs() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");
    final File b = File.createTempFile("bbbb", ".tmp");

    final Classpath path = Classpath.of(a, b);
    assertNotNull(path);

    assertThat(path.getElements().isEmpty(), is(false));
    assertThat(path.isEmpty(), is(false));

    assertThat(path.contains(a), is(true));

    assertEquals(path.toString(), a.getAbsolutePath() + ":" + b.getAbsolutePath());

    a.deleteOnExit();
    b.deleteOnExit();
  }

  @Test public void testClasspathCopy() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");
    final File b = File.createTempFile("bbbb", ".tmp");

    final Classpath path = Classpath.of(a, b);
    final Classpath pathB = Classpath.of();

    pathB.addAll(path);

    assertThat(pathB.getElements().isEmpty(), is(false));
    assertThat(pathB.isEmpty(), is(false));

    assertThat(pathB.contains(a), is(true));

    assertEquals(pathB.toString(), a.getAbsolutePath() + ":" + b.getAbsolutePath());

    a.deleteOnExit();
    b.deleteOnExit();
  }

  @Test public void testClasspathCopyThruAllFiles() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");
    final File b = File.createTempFile("bbbb", ".tmp");


    final Classpath pathB = Classpath.of();

    pathB.addAll(a, b);

    assertThat(pathB.getElements().isEmpty(), is(false));
    assertThat(pathB.isEmpty(), is(false));

    assertThat(pathB.contains(a), is(true));

    assertEquals(pathB.toString(), a.getAbsolutePath() + ":" + b.getAbsolutePath());

    a.deleteOnExit();
    b.deleteOnExit();
  }

  @Test public void testClasspathEmpty() throws Exception {
    final Classpath empty = Classpath.empty();
    assertThat(empty.isEmpty(), is(true));
  }
}
