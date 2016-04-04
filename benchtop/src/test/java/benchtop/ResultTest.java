package benchtop;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Huascar Sanchez
 */
public class ResultTest {
  @Test public void testResultCreation() throws Exception {
    final Result a = new Result();
    a.add("Foo", ".......");

    assertEquals(a.getOutputs("Foo").get(0), ".......");
  }

  @Test public void testResultDeletion() throws Exception {
    final Result a = new Result();
    a.add("Foo", ".......");

    a.remove("Foo");

    assertThat(a.getOutputs("Foo").isEmpty(), is(true));
  }

  @Test public void testInconsistentResult() throws Exception {
    final Result a = new Result();
    a.add("Foo", ".......");
    a.add("Foo", "...E...");

    try {
      Result.passOrThrow(a);
      fail("It should have failed already");
    } catch (RuntimeException e){
      assertThat(e.getMessage().contains("Foo#test3()"), is(true));
    }
  }
}
