package benchtop.utils;

import benchtop.Classpath;
import com.google.common.io.Files;
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
import java.util.List;

/**
 * @author Martin Schaef
 * @author Huascar Sanchez
 */
public class Soot {

  private static final String FILE_SEPARATOR = File.separator;
  private static final String DOT_CLASS      = ".class";

  private Soot(){
    throw new Error("");
  }

  /**
   * Transforms Java classes in the target directory given some classpath.
   *
   * @param classpath the needed classpath.
   * @param transformed directory where transformed classes were placed.
   * @param fullyQualifiedClassNames a list of fully qualified class names
   * @throws IOException unexpected error has occurred.
   */
  public static void sootifyJavaClasses(Classpath classpath, File transformed, List<String> fullyQualifiedClassNames) throws IOException {

    Options.v().set_verbose(false);
    Options.v().set_soot_classpath(classpath.toString());

    final SootToCfg soot2cfg = new SootToCfg(fullyQualifiedClassNames);
    soot2cfg.runPreservingTransformationOnly(transformed.getAbsolutePath(), classpath.toString());

    for (SootClass sc : Scene.v().getApplicationClasses()) {
      transforms(sc, transformed);
    }
  }

  private static void transforms(SootClass sootClass, File transformedTempFolder) throws IOException {
    sootClass.validate();

    final String currentClassname = SourceLocator.v()
      .getFileNameFor(sootClass, Options.output_format_class);

    final StringBuilder content = new StringBuilder(1000);
    content.append(transformedTempFolder.getAbsolutePath());
    content.append(FILE_SEPARATOR);

    if (!sootClass.getPackageName().isEmpty()) {
      content.append(sootClass.getPackageName().replace(".", FILE_SEPARATOR));
      content.append(FILE_SEPARATOR);
    }

    content.append(Files.getNameWithoutExtension(currentClassname));
    content.append(DOT_CLASS);

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
    }
  }
}
