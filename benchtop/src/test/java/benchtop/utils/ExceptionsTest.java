package benchtop.utils;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * @author Huascar Sanchez
 */
public class ExceptionsTest {

  @Test public void testErrorMessageConstructions() throws Exception {
    final List<Throwable> throwables = ImmutableList.of(
      new RuntimeException("One"),
      new RuntimeException("Two"),
      new RuntimeException("Three")
    );

    final String message = Exceptions.createErrorMessage("Test", throwables);
    assertThat(!com.google.common.base.Strings.isNullOrEmpty(message), is(true));
  }

  @Test public void testExceptionsInstantiation() throws Exception {
    try {
      final Constructor<Exceptions> c = Exceptions.class.getDeclaredConstructor();
      c.newInstance();
      fail();
    } catch (Exception e){
      assertNotNull(e);
    }
  }
}
