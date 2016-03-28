package benchtop;

import benchtop.spi.AbstractConfiguration;
import benchtop.spi.JavacConfiguration;
import benchtop.spi.RandoopConfiguration;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Huascar Sanchez
 */
public class BenchtopTest {

  @Test public void testNullConfiguration() throws Exception {
    try {
      Benchtop.createCommand(null);
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testNullCommandExecution() throws Exception {
    try {
      Benchtop.run(null);
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testJavacNullArrayOfSourceFiles() throws Exception {
    try {
      Benchtop.javac(new File ("boo"));
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testRandoopNullClassList() throws Exception {
    try {
      Benchtop.randoop();
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testRandoopNullClasspath() throws Exception {
    try {
      Benchtop.randoop((Classpath)null);
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testJavacNullDestination() throws Exception {
    try {
      Benchtop.javac(null);
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testRandoopNullDestination() throws Exception {
    try {
      Benchtop.randoop(Classpath.environmentClasspath(), (File)null, "Hello");
      Assert.fail("no null pointer exception thrown");
    } catch (Exception e){
      Assert.assertTrue(true);
    }
  }

  @Test public void testBasicCommandBuilding() throws Exception {

    final Command cmd = new Command(Arrays.asList("boo", "lala")){
      @Override public List<String> execute() {
        return new ArrayList<>();
      }
    };


    assertNotNull(cmd);
    assertThat(cmd.execute().isEmpty(), is(true));
    assertThat(cmd.isStarted(), is(false));

  }

  @Test public void testCommandBuildingWithConfigurations() throws Exception {

    final Command command = Benchtop.createCommand(new AbstractConfiguration() {
      @Override protected void configure() {
        arguments("boo");
        environment("cool", "no");
        maxCommandLength(100);
        permitNonZeroExitStatus();
        workingDirectory(new File("nothing.nothing"));
      }
    });

    assertNotNull(command);
    assertThat(command.isStarted(), is(false));

    assertEquals(command.toString(), "cool=no boo");

  }

  @Test public void testJavacCommand() throws Exception {

    final Command javac = Benchtop.createCommand(
      JavacConfiguration.newJavacConfiguration(Classpath.of(new File("boo")), new File("doo"), ImmutableList.of(new File("java")))
    );

    assertNotNull(javac);
    assertEquals("javac -g -classpath boo -d doo java", javac.toString());
  }

  @Test public void testRandoopCommand() throws Exception {
    final Command randoop = Benchtop.createCommand(RandoopConfiguration.defaultConfiguration(
      Classpath.of("SuperFile.txt"), new File("GEN"), 60, "foo.FooBar"
    ));

    assertNotNull(randoop);
    assertTrue(randoop.toString().contains("java -ea -classpath")
      && randoop.toString().contains("randoop.jar randoop.main.Main gentests --junit-output-dir=GEN " +
      "--testclass=foo.FooBar --only-test-public-members=true --silently-ignore-bad-class-names=true " +
      "--oom-exception=INVALID --timelimit=60"));

    ;
  }

  @Test public void testRandoopConfiguration() throws Exception {
    final File tempDirectory = Files.createTempDir();

    final Command command = Benchtop.createCommand(RandoopConfiguration.defaultConfiguration(
      Classpath.empty(),
      tempDirectory,
      60,
      "java.util.TreeSet"
    ));


    assertNotNull(command);
    assertTrue(command.toString().contains(tempDirectory.toString()));

    tempDirectory.deleteOnExit();
  }

  @Test public void testDefaultOutput() throws Exception {
    final File file = RandoopConfiguration.randoopOutput();
    assertNotNull(file);
  }

  @Test public void testRandoopConfigDefaultAttributes() throws Exception {
    assertNotNull(RandoopConfiguration.randoopClasspath());
    assertNotNull(RandoopConfiguration.randoopJar());
    assertNotNull(RandoopConfiguration.randoopOutput());
  }

  @Test public void testRandoopOOExceptionInvalid() throws Exception {
    final Command command = Benchtop.createCommand(
      new RandoopConfiguration(Classpath.empty(), new File(""), 60, "java.util.TreeSet") {
        @Override protected void configureRandoop() {
          discardOutOfMemoryErrors();
        }
    });

    final String toStringBuilder = command.toString();
    assertThat(toStringBuilder.contains("--oom-exception=INVALID"), CoreMatchers.is(true));
  }

  @Test public void testRandoopOOExceptionError() throws Exception {
    final Command command = Benchtop.createCommand(
      new RandoopConfiguration(Classpath.empty(), new File(""), 60, "java.util.TreeSet") {
        @Override protected void configureRandoop() {
          includeOutOfMemoryErrorsInErrorRevealingTests();
        }
      });

    final String toStringBuilder = command.toString();
    assertThat(toStringBuilder.contains("--oom-exception=ERROR"), CoreMatchers.is(true));
  }

  @Test public void testRandoopOOExceptionExpected() throws Exception {
    final Command command = Benchtop.createCommand(
      new RandoopConfiguration(Classpath.empty(), new File(""), 60, "java.util.TreeSet") {
        @Override protected void configureRandoop() {
          includeOutOfMemoryErrorsInRegressionTests();
        }
      });

    final String toStringBuilder = command.toString();
    assertThat(toStringBuilder.contains("--oom-exception=EXPECTED"), CoreMatchers.is(true));
  }

  @Test public void testDuplicatedOption() throws Exception {
    try {
      Benchtop.createCommand(
        new RandoopConfiguration(Classpath.empty(), new File(""), 60, "java.util.TreeSet") {
          @Override protected void configureRandoop() {
            applyDefaultOptionsOnce();
            discardOutOfMemoryErrors();
          }
        });
    } catch (Exception e){
      Assert.assertNotNull(e);
      return;
    }

    Assert.fail();
  }

  @Test public void testWorkingDirectory() throws Exception {
    final File a = File.createTempFile("aaaa", ".tmp");

    final StringBuilder wdToString = new StringBuilder();

    final Command command = Benchtop.createCommand(
      new RandoopConfiguration(Classpath.empty(), new File(""), 60, "java.util.TreeSet") {
        @Override protected void configureRandoop() {
          workingDirectory(a);

          wdToString.append(builder().toString());
        }
      });

    assertNotNull(command);
    assertTrue(wdToString.toString().contains(a.toString()));

    a.deleteOnExit();
  }
}
