package benchtop.utils;

import benchtop.Benchtop;
import benchtop.Classpath;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

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

  public static List<Class<?>> compileJava(Classpath classpath, File destination, File... sourceFiles) throws
    IOException {

    Benchtop.javac(classpath, destination, sourceFiles);

    return loadClasses(classpath, destination);
  }

  public static List<Class<?>> compileJava(Classpath classpath, int depth, File destination, File... sourceFiles) throws
    IOException {

    Benchtop.javac(classpath, destination, sourceFiles);

    return loadClasses(classpath, depth, destination);
  }


  public static List<Class<?>> compileJava(Classpath classpath, File destination, Collection<File> sourceFiles) throws
    IOException {

    Benchtop.javac(classpath, destination, sourceFiles);

    return loadClasses(classpath, destination);
  }

  public static List<Class<?>> loadClasses(File classDir) throws IOException {
    return loadClasses(Classpath.empty(), classDir);
  }

  /**
   * Loads a list of classes found in some directory.
   *
   * @param classDir the directory containing classes to load.
   * @return a list of loaded classes.
   * @throws IOException unexpected error has occurred.
   */
  public static List<Class<?>> loadClasses(final Classpath classpath, final File classDir) throws IOException {
    return loadClasses(classpath, Integer.MAX_VALUE, classDir);
  }

  public static List<Class<?>> loadClasses(final Classpath classpath, int depth, final File classDir) throws IOException {

    final Path start = Paths.get(classDir.toURI());

    final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.class");

    final List<Class<?>> classes = new ArrayList<>();

    final EnumSet<FileVisitOption> options = EnumSet.noneOf(FileVisitOption.class);

    try {
      Files.walkFileTree(start, options, depth, new SimpleFileVisitor<Path>(){
        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws
          IOException {


          final Path fileName = file.getFileName();
          if(matcher.matches(fileName)){
            final File visitedFile = file.toFile();

            try (URLClassLoader classLoader = createClassLoader(classpath, classDir)) {
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

  private static URLClassLoader createClassLoader(Classpath classpath, final File classDir) {
    final Classpath updated = Classpath.union(classpath, Classpath.of(classDir));

    return AccessController.<URLClassLoader>doPrivileged(new PrivilegedAction<URLClassLoader>() {
      @Override public URLClassLoader run() {
        try {
          final List<URL> urls = new ArrayList<>();
          for(File eachFile : updated.getElements()){
            urls.add(eachFile.toURI().toURL());
          }

          return new URLClassLoader(urls.toArray(new URL[urls.size()]));
        } catch (MalformedURLException mue){
          throw new RuntimeException("malformed URL");
        }
      }
    });
  }

}
