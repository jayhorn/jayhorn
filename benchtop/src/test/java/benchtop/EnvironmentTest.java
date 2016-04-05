package benchtop;

import benchtop.utils.IO;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class EnvironmentTest {

  @Test public void testDeeperLevelEnvironment() throws Exception {
    final File a = Files.createTempDir();
    final File b = Files.createTempDir();

    final DefaultEnvironment environment = new DefaultEnvironment(){
      @Override List<Class<?>> runRandoopAndJUnit(Classpath copy) throws IOException {

        assertThat(!copy.isEmpty(), is(true));

        return ImmutableList.of();
      }

      @Override void transformAndTest(List<Class<?>> listOfClasses) throws IOException {
        assertThat(listOfClasses.isEmpty(), is(true));
      }
    };

    environment.bundleTarget(a);
    environment.bundleOutput(b);
    environment.bundleClasspath();

    environment.addClasses(Lists.newArrayList("goo.goo.Foo"));

    environment.bundleFocus("Recursive");
    environment.bundleTransformations();
    environment.bundleTimeout(5);

    environment.execute();


    IO.deleteDirectory(a.toPath());
    IO.deleteDirectory(b.toPath());

    a.deleteOnExit();
    b.deleteOnExit();
  }

}
