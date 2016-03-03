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
  /**
   * Classes constructor.
   */
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
   * @throws IOException unexpected error has occurred.
   */
  public static List<Class<?>> compileJava(File destination, File... sourceFiles) throws
    IOException {

    Benchtop.javac(destination, sourceFiles);

    return loadClasses(destination);
  }

  /**
   * Loads a list of classes found in some directory.
   *
   * @param classDir the directory containing classes to load.
   * @return a list of loaded classes.
   * @throws IOException unexpected error has occurred.
   */
  public static List<Class<?>> loadClasses(File classDir) throws IOException {

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

  /**
   * Extracts the name of a fully qualified file
   *
   * @param filePathName the absolute path of a file.
   * @return the name of the file.
   */
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

}
