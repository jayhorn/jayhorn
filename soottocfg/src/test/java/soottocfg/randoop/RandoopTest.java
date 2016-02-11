package soottocfg.randoop;

import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class RandoopTest {

  @Test public void testRandoopConfiguration() throws Exception {
    final File tempDirectory = Files.createTempDir();
    Randoop.RandoopBuilder builder = Randoop.configure(Classpath.empty(), tempDirectory)
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

  @Test public void testRandoopOOExceptionInvalid() throws Exception {
    Randoop.RandoopBuilder builder = Randoop.configure()
      .discardOutOfMemoryErrors();

    final String toStringBuilder = builder.builder().build().toString();
    assertThat(toStringBuilder.contains("--oom-exception=INVALID"), is(true));
  }

  @Test public void testRandoopOOExceptionError() throws Exception {
    Randoop.RandoopBuilder builder = Randoop.configure()
      .includeOutOfMemoryErrorsInErrorRevealingTests();

    final String toStringBuilder = builder.builder().build().toString();
    assertThat(toStringBuilder.contains("--oom-exception=ERROR"), is(true));
  }

  @Test public void testRandoopOOExceptionExpected() throws Exception {
    Randoop.RandoopBuilder builder = Randoop.configure()
      .includeOutOfMemoryErrorsInRegressionTests();

    final String toStringBuilder = builder.builder().build().toString();
    assertThat(toStringBuilder.contains("--oom-exception=EXPECTED"), is(true));
  }

  @Test public void testDuplicatedOption() throws Exception {
    try {
      Randoop.RandoopBuilder builder = Randoop.configure()
        .jayhorn()
        .silentlyIgnoreBadClassNames().silentlyIgnoreBadClassNames();
    } catch (Exception e){
      Assert.assertTrue(true);
      return;
    }

    Assert.fail();
  }

  @Test public void testWorkingDirectory() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");

    Randoop.RandoopBuilder builder = Randoop.configure(a);

    assertNotNull(builder);

    a.deleteOnExit();
  }

  @Test public void testRandoopUpdatedConfiguration() throws Exception {

    Randoop.RandoopBuilder builder = Randoop.configure()
      .jayhorn()
      .silentlyIgnoreBadClassNames()
      .jUnitPackageName("super.cool")
      .discardOutOfMemoryErrors()
      .permitNonZeroExitStatus()
      .timeLimit(60);

    final String toStringBuilder = builder.builder().build().toString();

    assertThat(toStringBuilder.contains("--junit-package-name=super.cool"), is(true));
    assertThat(toStringBuilder.contains("--silently-ignore-bad-class-names=true"), is(true));
    assertThat(toStringBuilder.contains("--oom-exception=INVALID"), is(true));
    assertThat(toStringBuilder.contains("--timelimit=60"), is(true));
  }

}
