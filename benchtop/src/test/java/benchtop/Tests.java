package benchtop;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class Tests {
  private static final String DESTINATION = System.getProperty("user.dir")
    + "/build/test/resources/";

  private static final File WORKING_DIR = new File(DESTINATION);


  private Tests(){
    throw new Error("Utility class");
  }

  public static String defaultDestination(){
    return DESTINATION;
  }

  public static File defaultWorkingDirectory(){
    return WORKING_DIR;
  }


  public static File createJavaFile(String destination) throws IOException {
    final Path path = Paths.get(destination + "JavaFile.java");
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

    return Files.write(path, lines, Charset.forName("UTF-8")).toFile();
  }
}
