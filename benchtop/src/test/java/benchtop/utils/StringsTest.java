package benchtop.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

  @Test public void testLineTraceExtraction() throws Exception {
    final String output = Joiner.on("\n").join(ImmutableList.of(
      "JUnit version 4.12", "..........E........"
    ));

    final String lineTrace = Strings.lineTrace(output);

    assertEquals(lineTrace, "..........E........");
  }

  @Test public void testArrayOfObjectsGeneration() throws Exception {
    final String[] strings = {"1", "2", "3"};
    final Object[] objects = Strings.generateArrayOfObjects(strings);

    assertArrayEquals(objects, strings);

  }

  @Test public void testStringsInstantiation() throws Exception {
    try {
      final Constructor<Strings> c = Strings.class.getDeclaredConstructor();
      c.newInstance();
      fail();
    } catch (Exception e){
      assertNotNull(e);
    }
  }
}
