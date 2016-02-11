package soottocfg.randoop;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class JUnitTestRunnerTest {

  @Test public void testSupportBasicTest() throws Exception {
    final TestRunner runner = new JUnit4TestRunner(BasicTest.class);

    assertThat(runner.isClassSupported(BasicTest.class), is(true));
  }

  @Test public void testRunBasicTest() throws Exception {
    assertThat(JUnit4TestRunner.runUnitTests(BasicTest.class), is(true));
  }


  public static class BasicTest {
    @Test public void testDoSomething() {}
  }

}
