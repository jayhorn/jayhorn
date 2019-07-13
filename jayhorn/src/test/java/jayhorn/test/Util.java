package jayhorn.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.io.Files;

public final class Util {

  private static final String USER_DIR  = System.getProperty("user.dir") + "/";
  private static final String TEST_ROOT = USER_DIR + "src/test/resources/";


	private Util(){
		throw new Error("Utility class");
	}

  public static String testDirectoryPath(String name){
    return TEST_ROOT + name + "/";
  }

	public static File testDirectory(String name){
    return new File(testDirectoryPath(name));
  }

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
	public static String fileToString(File f) {
		StringBuffer sb = new StringBuffer();
		try (FileReader fileRead = new FileReader(f); BufferedReader reader = new BufferedReader(fileRead);) {
			String line;
			while (true) {
				line = reader.readLine();
				if (line == null)
					break;
				sb.append(line);
				sb.append("\n");
			}
		} catch (Throwable e) {

		}
		return sb.toString();
	}

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
	public static boolean compareFiles(File out, File gold) {
		try (FileReader fR1 = new FileReader(out);
				FileReader fR2 = new FileReader(gold);
				BufferedReader reader1 = new BufferedReader(fR1);
				BufferedReader reader2 = new BufferedReader(fR2);) {
			String line1, line2;
			while (true) // Continue while there are equal lines
			{
				line1 = reader1.readLine();
				line2 = reader2.readLine();

				// End of file 1
				if (line1 == null) {
					// Equal only if file 2 also ended
					return (line2 == null ? true : false);
				}

				// Different lines, or end of file 2
				if (!line1.equalsIgnoreCase(line2)) {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

  /**
   * Compiles a sourceFile into a temp folder and returns this folder or null
   * if compilation fails.
   *
   * @param sourceFile the source file to compile
   * @return the folder that contains the class file(s) or null if compilation
   *         fails.
   * @throws IOException
   */
  public static File compileJavaFile(File sourceFile) throws IOException {
		final File tempDir = getTempDir();
		final String javac_command = String.format("javac -cp %s -g %s -d %s", sourceFile.getParent(), sourceFile.getAbsolutePath(),
			tempDir.getAbsolutePath());

		ProcessBuilder pb = new ProcessBuilder(javac_command.split(" "));
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		Process p = pb.start();

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		return tempDir;
  }

	/**
	 * Compiles a set of sourceFiles into a temp folder and returns this folder
	 * or null if compilation fails.
	 * 
	 * @param sourceFiles an array of files to compile
	 * @return the folder that contains the class file(s) or null if compilation
	 *         fails.
	 * @throws IOException
	 */
	public static File compileJavaFiles(File[] sourceFiles) throws IOException {
		final File tempDir = getTempDir();
		StringBuilder sb = new StringBuilder();
		for (File f : sourceFiles) {
			sb.append(f.getAbsolutePath());
			sb.append(" ");
		}
		final String javac_command = String.format("javac -g -d %s %s", tempDir.getAbsolutePath(), sb.toString());

		System.out.println(javac_command);

		ProcessBuilder pb = new ProcessBuilder(javac_command.split(" "));
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		Process p = pb.start();

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}

		return tempDir;
	}

	public static File getTempDir() throws IOException {
		final File tempDir = File.createTempFile("jayhorn_test_temp", Long.toString(System.nanoTime()));
		if (!(tempDir.delete())) {
			throw new IOException("Could not delete temp file: " + tempDir.getAbsolutePath());
		}
		if (!(tempDir.mkdir())) {
			throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
		}
		return tempDir;
	}

	public static List<Object[]> getData(File testDirectory){
		final Path start = Paths.get(testDirectory.toString());
		final List<Object[]> data = new CopyOnWriteArrayList<>();

		try {
			java.nio.file.Files.walkFileTree(start, new SimpleFileVisitor<Path>(){
				@Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {


					data.add(new Object[] {file.toFile(), file.toFile().getName()});

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// ignores malformed files
		}

		return data;
	}

  public static Class<?> loadClass(File classDir) throws Throwable {
    final StringBuilder classNameBuilder = new StringBuilder();
    File dir = classDir;

    while (dir.isDirectory()) {

      final File[] files = (dir.listFiles() == null
        ? new File[0]
        : dir.listFiles()
      );

      if (files != null && files.length > 0) {
				dir = files[0];
        classNameBuilder.append(Util.getNameWithoutExtension(dir.getAbsolutePath()));
        if (dir.isFile()
          && "class".equals(Util.getFileExtension(dir.getAbsolutePath()))) {
          break;
        } else {
          classNameBuilder.append(".");
        }
      }
    }

    final String className = classNameBuilder.toString();

    try (URLClassLoader classLoader = createClassLoader(classDir)) {
      return classLoader.loadClass(className);
    } catch (Throwable e) {
      e.printStackTrace(System.err);
      throw e;
    }
  }

	private static URLClassLoader createClassLoader(final File classDir) {
		return AccessController.<URLClassLoader>doPrivileged(new PrivilegedAction<URLClassLoader>() {
			@Override public URLClassLoader run() {
				try {
					return new URLClassLoader(new URL[] { classDir.toURI().toURL() });
				} catch (MalformedURLException mue){
					throw new RuntimeException("malformed URL");
				}
			}
		});
	}


	public static String getNameWithoutExtension(String fullpath){
    return Files.getNameWithoutExtension(fullpath);
  }

  public static String getFileExtension(String fullpath){
    return Files.getFileExtension(fullpath);
  }

}
