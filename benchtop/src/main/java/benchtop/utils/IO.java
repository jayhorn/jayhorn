package benchtop.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author Huascar Sanchez
 */
public class IO {

  private static final Set<File> IVY_GRADLE_MAVEN_LOCAL_CACHES = Sets.newHashSet(
    new File(System.getProperty("user.home") + "/.ivy2/cache/"),
    new File(System.getProperty("user.home") + "/.gradle/caches/modules-2/"),
    new File(System.getProperty("user.home") + "/.m2/repository")
  );

  private IO(){
    throw new Error("Utility class");
  }

  /**
   * Copies a collection of files to a target location. The {@code preserve}
   * parameter determines if file attributes should be copied/preserved.
   *
   * @param files collection of files to copy to new location.
   * @param target source file (destination)
   * @param preserve true if file attributes should be copied/preserved; false otherwise.
   */
  public static void copyFiles(Collection<File> files, Path target, boolean preserve){
    final Collection<File> nonNullFiles = Preconditions.checkNotNull(files);
    final Path nonNullTarget = Preconditions.checkNotNull(target);

    for(File eachFile : nonNullFiles){
      copyFile(eachFile.toPath(), nonNullTarget.resolve(eachFile.getName()), preserve);
    }
  }


  /**
   * thx to:
   * <p>
   * https://docs.oracle.com/javase/tutorial/essential/io/examples/Copy.java
   * </p>
   *
   * <p>Copies a file to target location. The {@code preserve}
   * parameter determines if file attributes should be copied/preserved.
   * </p>
   *
   * @param source source file
   * @param target source file (destination)
   * @param preserve true if file attributes should be copied/preserved; false otherwise.
   */
  public static void copyFile(Path source, Path target, boolean preserve) {
    CopyOption[] options = (preserve) ?
      new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING } :
      new CopyOption[] { REPLACE_EXISTING };
    try {
      Files.copy(source, target, options);
    } catch (IOException x) {
      System.err.format("Unable to copy: %s: %s%n", source, x);
    }
  }

  public static List<String> resolveFullyQualifiedNames(String location, List<File> collectedFiles){
    final List<String> namespaces = new ArrayList<>();
    for(File each : collectedFiles){
      final String absolutePath = each.getAbsolutePath();
      if(absolutePath.contains("$")) continue;

      final List<String> locationList = Lists.newArrayList(Splitter.on(File.separator)
        .split(location));

      final List<String> absPathList  = Lists.newArrayList(Splitter.on(File.separator)
        .split(absolutePath));

      //noinspection Convert2streamapi
      for(String eachDir : locationList){
        absPathList.remove(eachDir);
      }

      namespaces.add(Joiner.on(".").join(absPathList).replace(".class", ""));
    }

    return namespaces;
  }


  /**
   * Collect files in a given location.
   *
   * @param testDirectory directory to access
   * @param extension extension of files to collect
   * @return the list of files matching a given extension.
   */
  public static List<File> collectFiles(File testDirectory, String extension){
    final List<File> data = new ArrayList<>();

    try {
      IO.collectDirContent(testDirectory, extension, data);
    } catch (IOException e) {
      // ignored
    }

    return data;
  }


  private static void collectDirContent(File classDir, String extension, Collection<File> files) throws IOException {

    final Path        start   = Paths.get(classDir.toURI());
    final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*." + extension);

    try {
      Files.walkFileTree(start, new SimpleFileVisitor<Path>(){
        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws
          IOException {


          final Path fileName = file.getFileName();
          if(matcher.matches(fileName)){
            final File visitedFile = file.toFile();
            files.add(visitedFile);
          }

          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException ignored){}

  }


  /**
   * Uses the local cache directories of ivy, and gradle, and (later) maven to populate
   * Benchtop's classpath.
   *
   * @return the list directories in local caches.
   * @throws IOException unexpected error has occurred.
   */
  public static List<File> localCaches() throws IOException {
    final Set<File> files = new HashSet<>();

    if(isRunningOnWindows()){
      return new ArrayList<>();
    }

    //noinspection Convert2streamapi
    for(File each : IVY_GRADLE_MAVEN_LOCAL_CACHES){ // Java 7 compatible
      if(each.exists()){
        files.addAll(collectFiles(each, "jar"));
      }
    }

    return Lists.newArrayList(files);
  }


  /**
   * Deletes the content (e.g., files) of a given directory.
   *
   * @param path the directory to access.
   * @throws IOException unexpected error occurred.
   */
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

  private static boolean isRunningOnWindows(){
    return System.getProperty("os.name").startsWith("Windows");
  }
}
