package soottocfg.test;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;

import com.google.common.io.Files;

public final class Util {

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
	public static String fileToString(File f) {
		try {
			return Files.toString(f, Charset.defaultCharset());
		} catch (IOException e) {
			return ""; // nothing was read
		}
	}

	@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(value = "DM_DEFAULT_ENCODING")
	public static boolean compareFiles(File out, File gold) {

		try {
			return Files.equal(out, gold);
		} catch (IOException e) {
			e.printStackTrace(System.err);
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
	public static File compileJavaFile(File sourceFile, String classPath) throws IOException {
		final File tempDir = Files.createTempDir();
		final String javac_command = String.format("javac -g -classpath %s %s -d %s", classPath, sourceFile.getAbsolutePath(),
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

	public static File compileJavaFile(File sourceFile) throws IOException {
		return compileJavaFile(sourceFile, ".");
	}

	
	
	protected static void printJavaCVersion() {
		final String javac_command = "javac -version";

		ProcessBuilder pb = new ProcessBuilder(javac_command.split(" "));
		pb.redirectOutput(Redirect.INHERIT);
		pb.redirectError(Redirect.INHERIT);
		try {
			Process p = pb.start();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Compiles a set of sourceFiles into a temp folder and returns this folder
	 * or null if compilation fails.
	 * 
	 * @param sourceFiles the source files to compile
	 * @return the folder that contains the class file(s) or null if compilation
	 *         fails.
	 * @throws IOException
	 */
	public static File compileJavaFiles(File[] sourceFiles) throws IOException {
		final File tempDir = Files.createTempDir();
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

	public static void delete(File f) throws IOException {
		if (f.isDirectory() && f.listFiles()!= null) {
			final File[] files = f.exists() && f.listFiles() == null ? new File[0] : f.listFiles();

			assert files != null;
			for (File c : files)
				delete(c);
		}
		if (!f.delete()) {
			throw new IOException("Failed to delete file: " + f);
		}
	}
}
