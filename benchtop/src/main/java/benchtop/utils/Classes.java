package benchtop.utils;

import benchtop.Benchtop;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author Huascar Sanchez
 */
public class Classes {
  private Classes(){
    throw new Error("Utility class");
  }

  /**
   * Compiles an array of sourceFiles into a temp folder and returns this folder or null
   * if compilation fails.
   *
   * @param destination where the .class files will be placed.
   * @param sourceFiles the source files to compile
   * @return the list of loaded classes.
   */
  public static List<Class<?>> compileJava(File destination, File... sourceFiles) throws
    IOException {

    Benchtop.javac(destination, sourceFiles);

    return loadClasses(destination);
  }

  public static List<File> collectFiles(File testDirectory, String extension){
    final List<File> data = new ArrayList<>();

    try {
      collectDirectoryContent(testDirectory, extension, data);
    } catch (IOException e) {
      // ignored
    }

    return data;
  }

  static List<Class<?>> loadClasses(File classDir) throws IOException {

    final Path start = Paths.get(classDir.toURI());

    final List<Class<?>> classes = new ArrayList<>();

    try {
      Files.walkFileTree(start, new SimpleFileVisitor<Path>(){
        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws
          IOException {

          final File visitedFile = file.toFile();

          if(!Objects.isNull(visitedFile) && visitedFile.getName().endsWith("class")){
            try (URLClassLoader classLoader = createClassLoader(classDir)) {
              final String className = extractFileName(visitedFile.getAbsolutePath());
              classes.add(classLoader.loadClass(className));
            } catch (Throwable e) {
              throw new IOException("Unable to load class");
            }

          }

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException ignored){}

    return classes;
  }

  public static String extractFileName(String filePathName) {
    if ( filePathName == null )
      return null;

    int dotPos      = filePathName.lastIndexOf( '.' );
    int slashPos    = filePathName.lastIndexOf( '\\' );

    if (slashPos == -1){
      slashPos = filePathName.lastIndexOf( '/' );
    }

    if ( dotPos > slashPos ){
      return filePathName.substring(
          slashPos > 0 ? slashPos + 1 : 0, dotPos
        );
    }

    return filePathName.substring( slashPos > 0 ? slashPos + 1 : 0 );
  }

  private static URLClassLoader createClassLoader(final File classDir) {
    return AccessController.doPrivileged((PrivilegedAction<URLClassLoader>) () -> {
      try {
        return new URLClassLoader(new URL[] { classDir.toURI().toURL() });
      } catch (MalformedURLException mue){
        throw new RuntimeException("malformed URL");
      }
    });
  }

  private static void collectDirectoryContent(File path, String extension, Collection<File> collector) throws IOException {

    if(path.exists()) {
      File[] files = path.listFiles();
      assert files != null;

      for (File file : files) {
        if (file.isDirectory()) {
          collectDirectoryContent(file, extension, collector);
        } else {
          if(file.getName().endsWith(extension)){
            collector.add(file);
          }
        }
      }
    }
  }

  public static void deleteDirectoryContent(File path) throws IOException {

    if(path.exists()) {
      File[] files = path.listFiles();
      assert files != null;

      for (File file : files) {
        if (file.isDirectory()) {
          deleteDirectoryContent(file);
        } else {

          if(!file.delete()){
            throw new IOException("Failed to delete file: " + file);
          }
        }
      }
    }
  }
}
