package benchtop;

import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * @author Huascar Sanchez
 */
public class TutorialTest {

  @Test public void testOldWay() throws Exception {
    Assert.assertThat(true, is(true));

//    // 1. target location (all classes)
//    final String classes = "/Users/hsanchez/dev/codepacking/target/classes/";
//    final File directory = new File(classes);
//
//    // 2. specify output folder
//    final File DIR = new File("/Users/hsanchez/dev/trashit/okay");
//
//    // 3. build classpath
//    final List<File> allFiles = IO.collectFiles(directory, "class");
//    final Classpath o = Classpath.environmentClasspath(
//      Classpath.of(allFiles),
//      Classpath.of(IO.localCaches()),
//      Classpath.of(directory),
//      Classpath.of(DIR)
//    );
//
//    // 4. update classpath with the randoop's output folder
//    final List<String> classList = IO.resolveFullyQualifiedNames(classes, allFiles);
//
//    // 5. run randoop
//    Benchtop.randoop(o, DIR, classList.toArray(new String[classList.size()]));
//
//    // 6. compile Randoop's tests
//    final List<File> files = IO.collectFiles(DIR, "java");
//    final List<Class<?>> listOfClasses = Classes.compileJava(o, DIR, files.toArray(new File[files.size()]));
//
//    // 7. run tests
//    for(Class<?> eachClass : listOfClasses){
//      if(eachClass.getName().contains("Regression")){
//        Benchtop.junit(o, eachClass.getCanonicalName());
//      }
//    }
  }

  @Test public void testNewWay() throws Exception {
//    Benchtop.consumes(new ExecutionBundle() {
//      @Override public void configure(Environment host) {
//        host.bundleTarget(new File("<project>/target/classes/"));
//        host.bundleOutput(new File("<project>/trashit/okay"));
//        host.bundleClasspath();
//        host.bundleTimeout(10);
//        host.bundleFocus();
//      }
//    });
    Assert.assertThat(true, is(true));
  }
}
