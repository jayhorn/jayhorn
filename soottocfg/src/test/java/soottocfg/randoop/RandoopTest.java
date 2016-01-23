package soottocfg.randoop;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

/**
 * @author Huascar Sanchez
 */
public class RandoopTest {

  @Test public void testRandoopConfiguration() throws Exception {
    final File tempDirectory = Files.createTempDir();
    Randoop.RandoopBuilder builder = Randoop.configure(tempDirectory)
      .testClass("java.util.TreeSet")
      .silentlyIgnoreBadClassNames()
      .timeLimit(60);

    assertNotNull(builder);

    tempDirectory.deleteOnExit();
  }

  @Test public void testDefaultOutput() throws Exception {
    final File file = Randoop.defaultOutput();
    assertNotNull(file);
  }

  @Test public void testRandoopConfig() throws Exception {
    assertNotNull(Randoop.configure());
  }

  @Test public void testRandoopClasspath() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");
    final File b = File.createTempFile("bbbb", ".tmp");

    final String aaa = a.getAbsolutePath();
    final String bbb = b.getAbsolutePath();

    Randoop.RandoopBuilder builder = new Randoop().classpath(aaa, bbb);

    assertNotNull(builder);

    a.deleteOnExit();
    b.deleteOnExit();
  }

  @Test public void testClasslist() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");
    assertNotNull(Randoop.configure().classList(a));

    a.deleteOnExit();
  }
}
