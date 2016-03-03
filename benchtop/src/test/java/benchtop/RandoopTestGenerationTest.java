package benchtop;

import benchtop.configs.RandoopConfiguration;
import benchtop.utils.Classes;
import benchtop.utils.IO;
import com.google.common.io.Files;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import soot.Scene;
import soot.SootClass;
import soot.SourceLocator;
import soot.jimple.JasminClass;
import soot.options.Options;
import soot.util.JasminOutputStream;
import soottocfg.soot.SootToCfg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class RandoopTestGenerationTest {

  private static final File DIR = RandoopConfiguration.randoopOutput();
  private static final List<Class<?>> TEST_CLASSES = new ArrayList<>();

  @BeforeClass public static void setup() throws Exception {

    if(DIR.exists()){
      IO.deleteDirectoryContent(DIR);
    }

    final File javaFile = Tests.createJavaFile(DIR.getAbsolutePath() + "/");

    final List<Class<?>> javaFileClass = Classes.compileJava(
      DIR, javaFile
    );

    assert javaFileClass.get(0) != null;

    final Classpath env = Classpath.union(
      Classpath.environmentClasspath(),
      Classpath.of(DIR)
    );


    Benchtop.randoop(
      env, DIR, "JavaFile"
    );

    final List<File> files = IO.collectFiles(DIR, "java");
    final List<Class<?>> classes = Classes.compileJava(DIR, files.toArray(new File[files.size()]));

    TEST_CLASSES.addAll(classes);
  }

  @Test public void testRandoopClasses() throws Exception {

    for (Class<?> eachClass : TEST_CLASSES){
      if("RegressionTest".equals(eachClass.getName()) || "JavaFile".equals(eachClass.getName())){
        continue;
      }

      final Classpath cp = Classpath.of(DIR);

      Benchtop.junit(cp, eachClass.getCanonicalName());
    }
  }

  @Test public void testTransformedClazz() throws Exception {

    final Classpath cp = Classpath.union(Classpath.environmentClasspath(), Classpath.of(DIR));

    Options.v().set_verbose(false);
    Options.v().set_soot_classpath(cp.toString());

    final File compiledTempFolder = Files.createTempDir();
    final File javaFile = Tests.createJavaFile(compiledTempFolder.getAbsolutePath() + "/");

    Classes.compileJava(
      compiledTempFolder, javaFile
    );

    final File transformedTempFolder = Files.createTempDir();

    final SootToCfg soot2cfg = new SootToCfg();
    soot2cfg.runPreservingTransformationOnly(compiledTempFolder.getAbsolutePath(), null);

    final List<SootClass> suiteClasses = new LinkedList<>();

    // write out the transformed classes
    for (SootClass sc : Scene.v().getApplicationClasses()) {
      transformClass(sc, transformedTempFolder);
    }




    compiledTempFolder.deleteOnExit();
    transformedTempFolder.deleteOnExit();
  }

  private void transformClass(SootClass sootClass, File transformedTempFolder) {
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
  }

}
