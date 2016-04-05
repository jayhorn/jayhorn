package benchtop;

import benchtop.utils.Exceptions;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Huascar Sanchez
 */
final class Result implements Iterable <String> {

  private final Map<String, List<String>> classNameToOutput;

  /**
   * Constructs a result monitor
   */
  public Result(){
    this.classNameToOutput = Maps.newHashMap();
  }

  /**
   * Adds the output of a Junit command executing the tests of a JUnit test class.
   *
   * @param className the name of the JUnit test class
   * @param output the output of the JUnit command
   */
  public void add(String className, String output){
    Preconditions.checkArgument(!Strings.isNullOrEmpty(className));
    Preconditions.checkArgument(!Strings.isNullOrEmpty(output));

    if(classNameToOutput.containsKey(className)){
      classNameToOutput.get(className).add(output);
    } else {
      final List<String> value = Lists.newArrayList(output);
      classNameToOutput.put(className, value);
    }
  }

  public Result combines(Result other){

    final Result monitor = new Result();

    for(String each : classNameToOutput.keySet()){
      for(String eachVal : getOutputs(each)){
        monitor.add(each, eachVal);
      }
    }

    for(String each : other.classNameToOutput.keySet()){
      for(String eachVal : other.getOutputs(each)){
        monitor.add(each, eachVal);
      }
    }

    return monitor;
  }

  /**
   * Returns the output of a JUnit command that executed a JUnit test class.
   *
   * @param className the name of the JUnit test class.
   * @return the output
   */
  public List<String> getOutputs(String className){
    Preconditions.checkArgument(!Strings.isNullOrEmpty(className));

    if(!classNameToOutput.containsKey(className)) return ImmutableList.of();

    return classNameToOutput.get(className);

  }

  @Override public Iterator<String> iterator() {
    return classNameToOutput.keySet().iterator();
  }

  /**
   * Removes the output of a JUnit command that executed the tests of a JUnit test class.
   * @param className the name of the JUnit test class.
   */
  public void remove(String className){
    Preconditions.checkArgument(!Strings.isNullOrEmpty(className));
    if(classNameToOutput.containsKey(className)){
      classNameToOutput.remove(className);
    }
  }

  /**
   * Analysis both of the outputs of a JUnit test class (before it was transformed and after it
   * was transformed).
   *
   * @param monitor the current result monitor
   * @throws RuntimeException if outputs are inconsistent.
   */
  public static void passOrThrow(Result monitor) throws RuntimeException {
    final List<Throwable> soundnessErrors = Lists.newArrayList();

    for(String className : monitor){
      final List<String> pairOfOutputs = monitor.getOutputs(className);
      if(pairOfOutputs.isEmpty()) continue;

      // each entry is either a ., I, E, or F. A . means a passed test, an I is an incomplete test,
      // and E is an error test, and an F is a failure test.
      final char[] batchA = pairOfOutputs.get(0).trim().toCharArray();
      final char[] batchB = pairOfOutputs.get(1).trim().toCharArray();

      for(int idx = 0; idx < batchA.length; idx++){
        if(!(batchA[idx] == batchB[idx] && batchA[idx] == '.')){

          soundnessErrors.add(new RuntimeException((className + "#test" + idx + "()")));
        }
      }

    }


    if(!soundnessErrors.isEmpty()){
      throw new SoundnessError(
        Exceptions.createErrorMessage("Soundness error", soundnessErrors)
      );
    }
  }

  static class SoundnessError extends RuntimeException {
    public SoundnessError(String message) {
      super(message);
    }
  }

}
