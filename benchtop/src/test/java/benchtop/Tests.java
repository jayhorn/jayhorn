package benchtop;

import benchtop.utils.Classes;
import benchtop.utils.IO;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Huascar Sanchez
 */
public class Tests {
  private static final String DESTINATION = System.getProperty("user.dir")
    + "/build/test/resources/";

  private static final File WORKING_DIR = new File(DESTINATION);

  public static final String JAVA_FILE  = "JavaFile";
  public static final String JAVA_FILE2 = "JavaFile2";
  public static final String JAVA_FILE3 = "JavaFile3";
  public static final String JAVA_FILE4 = "JavaFile4";
  public static final String JAVA_FILE5 = "JavaFile5";

  private static final Map<String, List<String>> NAME_TO_FILE_CONTENT = Maps.newHashMap();
  static {
    NAME_TO_FILE_CONTENT.put(JAVA_FILE, simpleJavaFile());
    NAME_TO_FILE_CONTENT.put(JAVA_FILE2, javaFileWithStaticNestedClass());
    NAME_TO_FILE_CONTENT.put(JAVA_FILE3, javaFileWithStaticNestedClassAndNamespace());
    NAME_TO_FILE_CONTENT.put(JAVA_FILE4, firstClass());
    NAME_TO_FILE_CONTENT.put(JAVA_FILE5, secondClass());
  }


  private Tests(){
    throw new Error("Utility class");
  }

  public static String defaultDestination(){
    return DESTINATION;
  }

  public static File defaultWorkingDirectory(){
    return WORKING_DIR;
  }


  public static void testSingleSetup(File directory) throws Exception {
    Tests.testSetup(directory, JAVA_FILE);
  }


  public static void testAllSetup(File directory) throws Exception {
    Tests.testSetup(
      directory, JAVA_FILE,
      JAVA_FILE2, JAVA_FILE3,
      JAVA_FILE4, JAVA_FILE5
    );
  }



  public static void consumesExecutionBundle(final File target, final File output,
                                              final boolean withTransformations) throws Exception {

    Preconditions.checkNotNull(target);
    Preconditions.checkNotNull(output);

    Benchtop.consumes(new ExecutionBundle() {
      @Override public void configure(Environment host) {
        host.bundleTarget(target);
        host.bundleOutput(output);
        host.bundleClasspath();
        host.bundleFocus("Regression");
        if(withTransformations) host.bundleTransformations();
      }
    });
  }


  public static void testSetup(File directory, String... names) throws Exception {
    if(directory.exists()){
      IO.cleanDirectory(directory);
    }

    final List<File> javaFiles = Tests.createJavaFiles(directory.getAbsolutePath() + "/", names);

    Classes.compileJava(
      Classpath.environmentClasspath(), directory, javaFiles
    );
  }


  public static void testTeardown(File directory) throws Exception {
    if(directory.exists()){
      IO.cleanDirectory(directory);
    }

    IO.deleteDirectory(directory.toPath());

  }


  public static File createJavaFile(String destination) throws IOException {

    final List<File> singleton = createJavaFiles(destination, JAVA_FILE);

    assert singleton.size() == 1;

    return singleton.get(0);
  }

  public static List<File> createJavaFiles(String destination, String... names) throws IOException {

    final List<File> files = new ArrayList<>();

    for(String each : names){
      if(NAME_TO_FILE_CONTENT.containsKey(each) && !each.endsWith(".java")){
        final Path          path  = Paths.get(destination + each + ".java");
        final List<String>  lines = NAME_TO_FILE_CONTENT.get(each);

        files.add(Files.write(path, lines, Charset.forName("UTF-8")).toFile());
      }
    }

    return files;
  }


  private static List<String> simpleJavaFile(){
    final List<String> lines = new ArrayList<>();
    lines.add("public class JavaFile {");
    lines.add("	public String simpleTest01() {");
    lines.add("		StringBuilder sb = new StringBuilder(); ");
    lines.add("		sb.append(simpleTestMethod01(0));       ");
    lines.add("		sb.append(simpleTestMethod02(0));       ");
    lines.add("		sb.append(simpleTestMethod01(2));       ");
    lines.add("		sb.append(simpleTestMethod02(2));		");
    lines.add("		sb.append(simpleTestMethod01(3));       ");
    lines.add("		sb.append(simpleTestMethod02(3));		");
    lines.add("		return sb.toString();");
    lines.add("	}");
    lines.add("		");
    lines.add("	private int simpleTestMethod01(int i) {");
    lines.add("		if (i == 0) {");
    lines.add("			return 1;");
    lines.add("		} else if (i == 2) {");
    lines.add("			return 2;");
    lines.add("		}");
    lines.add("		return 3;");
    lines.add("	}");
    lines.add("");
    lines.add("	String s1=\"Foo\";");
    lines.add("	");
    lines.add("	private String simpleTestMethod02(int i) {");
    lines.add("		Object o;");
    lines.add("		if (i>0) {");
    lines.add("			o = s1;");
    lines.add("		} else {");
    lines.add("			o = new JavaFile();");
    lines.add("		}");
    lines.add("		return o.toString(); // may not be null.");
    lines.add("	}");
    lines.add("	@Override");
    lines.add("	public String toString() {return \"Bar\";}");
    lines.add("}");
    return lines;
  }


  private static List<String> javaFileWithStaticNestedClass(){
    final List<String> lines = new ArrayList<>();
    lines.add("public class JavaFile2 {");
    lines.add("	");
    lines.add("	public static JavaFile2 get(){");
    lines.add("		return Installer.INSTANCE;");
    lines.add("	}");
    lines.add("	");
    lines.add("	public String simpleTest01() {");
    lines.add("		final StringBuilder sb = new StringBuilder();");
    lines.add("		sb.append(simpleTestMethod01(0));");
    lines.add("		return sb.toString();");
    lines.add("	}");
    lines.add("	");
    lines.add("	private int simpleTestMethod01(int i){");
    lines.add("		if(i == 0) return 1;");
    lines.add("		if(i == 2) return 2;");
    lines.add("		");
    lines.add("		return 3;");
    lines.add("	}");
    lines.add("	");
    lines.add("	private static class Installer {");
    lines.add("		static final JavaFile2 INSTANCE = new JavaFile2();");
    lines.add("	}");
    lines.add("}");
    return lines;
  }

  private static List<String> javaFileWithStaticNestedClassAndNamespace(){
    final List<String> lines = new ArrayList<>();
    lines.add("package goo.foo;");
    lines.add("	");
    lines.add("public class JavaFile3 {");
    lines.add("	");
    lines.add("	public static JavaFile3 get(){");
    lines.add("		return Installer.INSTANCE;");
    lines.add("	}");
    lines.add("	");
    lines.add("	public String simpleTest01() {");
    lines.add("		final StringBuilder sb = new StringBuilder();");
    lines.add("		sb.append(simpleTestMethod01(0));");
    lines.add("		return sb.toString();");
    lines.add("	}");
    lines.add("	");
    lines.add("	private int simpleTestMethod01(int i){");
    lines.add("		if(i == 0) return 1;");
    lines.add("		if(i == 2) return 2;");
    lines.add("		");
    lines.add("		return 3;");
    lines.add("	}");
    lines.add("	");
    lines.add("	private static class Installer {");
    lines.add("		static final JavaFile3 INSTANCE = new JavaFile3();");
    lines.add("	}");
    lines.add("}");
    return lines;
  }

  private static List<String> firstClass(){
    return ImmutableList.of(
      "package goo.foo;",
      "	",
      "public class JavaFile4 {",
      "	public String simpleTest01() {",
      "		return new JavaFile5().toString();",
      " }",
      "}"
    );
  }
  private static List<String> secondClass(){
    return ImmutableList.of(
      "package goo.foo;",
      "	",
      "public class JavaFile5 {",
      "	@Override public String toString() {",
      "		return super.toString();",
      " }",
      "}"
    );
  }

}
