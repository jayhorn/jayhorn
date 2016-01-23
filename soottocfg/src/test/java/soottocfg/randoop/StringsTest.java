package soottocfg.randoop;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class StringsTest {
  @Test public void testBasicOperations() throws Exception {
    String[] output = Strings.generateArrayOfStrings(Arrays.asList(1, 2, 3, 4));
    assertThat(output.length, is(4));

    final String joined = Strings.joinCollection(":", Arrays.asList(1, 2, 3, 4));
    assertEquals(joined, "1:2:3:4");

    final String[] out2 = Strings.generateArrayOfStrings(new Object[]{1, 2, 3});
    assertThat(out2.length, is(3));
  }
}
