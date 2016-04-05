package benchtop;

import benchtop.utils.Classes;
import benchtop.utils.IO;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class SystemTest01 {

  @Test public void testOldWay() throws Exception {
    final String cpString = "..the path to some libraries";
    final Classpath cp = Classpath.of(cpString.split(":"));

//		final String classes = "/Users/schaef/git/graphgen/build/classes/main";
    final String classes = "somethingMeaningful";
    final File directory = new File(classes);

    final List<File> allFiles = IO.collectFiles(directory, "class");

    final List<String> classList = IO.resolveFullyQualifiedNames(classes, allFiles);
    File randoopOutput = new File("randoop");
    Benchtop.randoop(cp, randoopOutput, 2, classList.toArray(new String[classList.size()]));
    cp.addAll(randoopOutput);

    // add junit and hamcrest to the cp before we run the tests.
    for (String each : System.getProperty("java.class.path").split(":")) {
      if (each.contains("hamcrest-core")) {
        cp.addAll(new File(each));
      } else if (each.contains("junit")) {
        cp.addAll(new File(each));
      }
    }

    for (Class<?> eachClass : compileRandoopTests(cp, randoopOutput)) {
      Benchtop.junit(cp, eachClass.getCanonicalName());
    }

  }

  @Test public void testNewWay() throws Exception {
    Benchtop.consumes(new ExecutionBundle() {
      @Override public void configure(Environment host) {
        host.bundleTarget(new File("path/to/project/classes"));
        host.bundleOutput(new File("path/to/output"));
        // classpath is auto resolved..(.ivy2, .m2, and .gradle are ignored if on Windows)
        host.bundleClasspath();
        host.bundleTimeout(60);
        host.bundleFocus(); // default is regression tests
        // disabled for now. This call throws a Soot error: Stack height is negative.
        // disabling this call until it can be solved. This error is related to
        // ill-transformed classes. We believe the use of namespace (or packages) is
        // preventing Soot from properly transforming these classes.
//				host.bundleTransformations();
      }
    });
  }

  private List<Class<?>> compileRandoopTests(Classpath classpath, File DIR) throws Exception {
    final List<File> files = IO.collectFiles(DIR, "java");
    return Classes.compileJava(classpath, DIR, files.toArray(new File[files.size()]));
  }

}
