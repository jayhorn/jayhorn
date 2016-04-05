package benchtop;

import benchtop.utils.IO;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

/**
 * @author Huascar Sanchez
 */
public class EnvironmentTest {
  @Test public void testBasicEnvironment() throws Exception {

    final File a = Files.createTempDir();
    final File b = Files.createTempDir();

    final DefaultEnvironment environment = new DefaultEnvironment() {
      @Override void execute() {
        testInternals(this, a, b);
      }
    };


    environment.bundleTarget(a);
    environment.bundleOutput(b);
    environment.bundleClasspath();
    environment.bundleFocus("Recursive");
    environment.bundleTransformations();
    environment.bundleTimeout(5);

    IO.deleteDirectory(a.toPath());
    IO.deleteDirectory(b.toPath());

    a.deleteOnExit();
    b.deleteOnExit();

  }

  private static void testInternals(Environment that, File a, File b) {
    try {
      Field target = that.getClass().getDeclaredField("target");
      final File aa = (File) target.get(that);

      assertEquals(aa, a);

      Field output = that.getClass().getDeclaredField("output");
      final File bb = (File) output.get(that);

      assertEquals(bb, b);

      Field prefixes = that.getClass().getDeclaredField("testPrefixes");
      final List listPrefixes = (List) prefixes.get(that);
      final String prefix = (String) listPrefixes.get(0);

      assertEquals(prefix, "Recursive");

      Field transformations = that.getClass().getDeclaredField("transformations");
      final boolean transformationValue = transformations.getBoolean(that);

      assertEquals(transformationValue, true);

      Field time = that.getClass().getDeclaredField("timeout");
      int timeout = time.getInt(that);

      assertEquals(timeout, 5);

    } catch (NoSuchFieldException | IllegalAccessException e){
      fail();
    }
  }
}
