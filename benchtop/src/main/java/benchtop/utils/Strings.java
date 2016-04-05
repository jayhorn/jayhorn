package benchtop.utils;

import com.google.common.base.Splitter;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @author Huascar Sanchez
 */
public class Strings {

  /**
   * Utility class. Not meant to be instantiated.
   */
  private Strings(){
    throw new Error("Utility class");
  }

  /**
   * Joins a collection of objects, which override toString, using a
   * delimiter.
   *
   * @param delimiter delimiter between entries in a collection.
   * @param entries collection to join using a given delimiter.
   * @param <T> type parameter of elements in entries collection.
   * @return joined collection represented as a String
   */
  public static <T> String joinCollection(String delimiter, Collection<T> entries){
    return java6LikeJoin(delimiter, entries);
  }

  /**
   * Generates an array of strings from an array of objects.
   *
   * @param objects array of objects to stringify.
   * @return an array of strings.
   */
  public static String[] generateArrayOfStrings(Object[] objects) {
    String[] result = new String[objects.length];
    int i = 0;

    for (Object o : objects) {
      result[i++] = o.toString();
    }
    return result;
  }

  /**
   * Generates an array of objects from an array of Strings.
   * @param objects array of strings
   * @return array of objects.
   */
  public static Object[] generateArrayOfObjects(String[] objects) {
    Object[] result = new Object[objects.length];
    int i = 0;

    for (String o : objects) {
      result[i++] = Object.class.cast(o);
    }

    return result;
  }

  /**
   * Generates an array of strings from a collection of objects.
   *
   * @param objects collection of objects to stringify.
   * @return an array of strings.
   */
  public static String[] generateArrayOfStrings(Collection<?> objects) {
    return generateArrayOfStrings(objects.toArray());
  }

  /**
   * Implements a generic method for joining a collection of objects. This
   * method is intended to work on Java6+ versions.
   *
   * @param delimiter delimiter between entries in a collection.
   * @param data collection to join using a given delimiter.
   * @return joined collection represented as a String
   */
  private static <T> String java6LikeJoin(String delimiter, Collection<T> data){
    final Iterator<T> iterator = data.iterator();
    final StringBuilder stringBuilder = new StringBuilder();

    if (iterator.hasNext()) {
      stringBuilder.append(iterator.next());

      while (iterator.hasNext()) {
        stringBuilder.append(delimiter).append(iterator.next());
      }
    }

    return stringBuilder.toString();
  }


  /**
   * Returns JUnit's produced line trace.
   *
   * @param output complete JUnit's output.
   * @return the extracted line trace.
   */
  public static String lineTrace(String output){
    final int lastIndex = output.lastIndexOf("JUnit version 4.12");
    final String truncated = output.substring(lastIndex, output.length());

    final List<String> lines = Splitter.on("\n").splitToList(truncated);
    if(!lines.isEmpty()){
      return lines.get(1);
    }

    return "";
  }
}
