package benchtop.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author Huascar Sanchez
 */
public class IO {

  private IO(){
    throw new Error("Utility class");
  }

  /**
   * Copies a collection of files to a target location. The {@code preserve}
   * parameter determines if file attributes should be copied/preserved.
   */
  public static void copyFiles(Collection<File> files, Path target, boolean preserve){
    final Collection<File> nonNullFiles = Objects.requireNonNull(files);
    final Path nonNullTarget = Objects.requireNonNull(target);

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

  public static List<File> collectFiles(File testDirectory, String extension){
    final List<File> data = new ArrayList<>();

    try {
      IO.collectDirectoryContent(testDirectory, extension, data);
    } catch (IOException e) {
      // ignored
    }

    return data;
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
