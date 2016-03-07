package benchtop;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.io.Files;

import benchtop.configs.RandoopConfiguration;
import benchtop.utils.Classes;
import benchtop.utils.IO;
import soot.Scene;
import soot.SootClass;
import soot.SourceLocator;
import soot.jimple.JasminClass;
import soot.options.Options;
import soot.util.JasminOutputStream;
import soottocfg.soot.SootToCfg;

/**
 * @author Huascar Sanchez
 */
public class SoundnessTest {
  private static final File DIR = RandoopConfiguration.randoopOutput();
  private static final List<Class<?>> TEST_CLASSES = new ArrayList<>();

  private static File compiledTempFolder;
  private static File compiledJavaFile;


  @BeforeClass public static void setup() throws Exception {

    compiledTempFolder = Files.createTempDir();

    Tests.randoopSetup(DIR);

    final List<File> files = IO.collectFiles(DIR, "java");
    final List<Class<?>> classes = Classes.compileJava(DIR, files.toArray(new File[files.size()]));

    final List<File> classFiles = IO.collectFiles(DIR, "class");

    compiledJavaFile = Iterables.get(classFiles, 0, null);

    assert compiledJavaFile != null;

    TEST_CLASSES.addAll(classes);
  }


  @Test public void testTransformedClass() throws Exception {
    sootifyJavaClass();

    final Classpath cp = Classpath.of(DIR);

    for (Class<?> eachClass : TEST_CLASSES){
      if("RegressionTest".equals(eachClass.getName())
        || "JavaFile".equals(eachClass.getName())
        || "JayHornAssertions".equals(eachClass.getName())){

        continue;
      }



      Benchtop.junit(cp, eachClass.getCanonicalName());
    }

    assertThat(cp.isEmpty(), is(false));

  }

  private static void sootifyJavaClass() throws Exception {
    final Classpath cp = Classpath.union(Classpath.environmentClasspath(), Classpath.of(DIR));

    Options.v().set_verbose(false);
    Options.v().set_soot_classpath(cp.toString());

    // 1. copy JavaFile.class to temp folder
    IO.copyFile(compiledJavaFile.toPath(), compiledTempFolder.toPath().resolve(compiledJavaFile.getName()), true);

    // 2. transformed JavaFile.class using preserving transformations
    final SootToCfg soot2cfg = new SootToCfg();
    soot2cfg.runPreservingTransformationOnly(compiledTempFolder.getAbsolutePath(), null);


    // 3. write out the transformed classes
    for (SootClass sc : Scene.v().getApplicationClasses()) {
      transformClass(sc, compiledTempFolder);
    }

    final List<File> interestingFiles = IO.collectFiles(compiledTempFolder, "class");

    // 4. copy all Randoop test classes to a new location
    IO.copyFiles(interestingFiles, DIR.toPath(), true);
  }

  private static void transformClass(SootClass sootClass, File transformedTempFolder) {
    sootClass.validate();

    final String currentClassname = SourceLocator.v()
      .getFileNameFor(sootClass, Options.output_format_class);

    final StringBuilder content = new StringBuilder(1000);
    content.append(transformedTempFolder.getAbsolutePath());
    content.append(File.separator);

    if (!sootClass.getPackageName().isEmpty()) {
      content.append(sootClass.getPackageName().replace(".", File.separator));
      content.append(File.separator);
    }

    content.append(Files.getNameWithoutExtension(currentClassname));
    content.append(".class");

    final File modifiedClassFile = new File(content.toString());

    if (!modifiedClassFile.getParentFile().mkdirs()) {
      System.out.println("no folders needed");
    }

    final String fileName = modifiedClassFile.getAbsolutePath();
    try (OutputStream streamOut = new JasminOutputStream(new FileOutputStream(fileName));
         PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut, "UTF-8"))) {
      final JasminClass jasminClass = new JasminClass(sootClass);
      jasminClass.print(writerOut);
      writerOut.flush();
    } catch (IOException e) {
      e.printStackTrace(System.err);
    }

  }

  @AfterClass public static void tearDown() throws Exception {

    TEST_CLASSES.clear();

    if(DIR.exists()){
      IO.deleteDirectoryContent(DIR);
    }

    if(compiledTempFolder!=null){
      compiledTempFolder.deleteOnExit();
    }

    compiledTempFolder = null;
    compiledJavaFile   = null;
  }
}
