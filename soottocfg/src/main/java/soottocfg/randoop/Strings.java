package soottocfg.randoop;

import java.util.Collection;
import java.util.Iterator;
import java.util.StringJoiner;


/**
 * @author Huascar Sanchez
 */
public class Strings {

  private Strings(){
    throw new Error("Utility class");
  }

  static <T> String joinCollection(String delimiter, Collection<T> entries){
    return java6LikeJoin(delimiter, entries);
  }

  public static String[] generateArrayOfStrings(Object[] objects) {
    String[] result = new String[objects.length];
    int i = 0;

    for (Object o : objects) {
      result[i++] = o.toString();
    }
    return result;
  }

  public static String[] generateArrayOfStrings(Collection<?> objects) {
    return generateArrayOfStrings(objects.toArray());
  }

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

}
