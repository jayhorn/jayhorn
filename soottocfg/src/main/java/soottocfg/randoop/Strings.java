package soottocfg.randoop;

import java.util.Collection;
import java.util.StringJoiner;


/**
 * @author Huascar Sanchez
 */
public class Strings {

  private Strings(){
    throw new Error("Utility class");
  }

  static <T> String joinCollection(String delimiter, Collection<T> entries){
    StringJoiner joiner = new StringJoiner(delimiter);
    for (T cs: entries) {
      joiner.add(cs.toString());
    }

    return joiner.toString();
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

}
