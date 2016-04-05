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
        try {
          Field target = this.getClass().getDeclaredField("target");
          final File aa = (File) target.get(this);

          assertEquals(aa, a);

          Field output = this.getClass().getDeclaredField("output");
          final File bb = (File) output.get(this);

          assertEquals(bb, b);

          Field prefixes = this.getClass().getDeclaredField("testPrefixes");
          final List listPrefixes = (List) prefixes.get(this);
          final String prefix = (String) listPrefixes.get(0);

          assertEquals(prefix, "Recursive");

          Field transformations = this.getClass().getDeclaredField("transformations");
          final boolean transformationValue = transformations.getBoolean(this);

          assertEquals(transformationValue, true);

          Field time = this.getClass().getDeclaredField("timeout");
          int timeout = time.getInt(this);

          assertEquals(timeout, 5);

        } catch (Exception e){
          fail();
        }
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
}
