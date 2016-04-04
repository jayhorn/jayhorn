package benchtop.utils;

import java.util.Collection;

/**
 * @author Huascar Sanchez
 */
public class Exceptions {
  private Exceptions(){
    throw new Error("Utility class");
  }

  /**
   * Creates a error message from a title and a collection of errors thrown during a computation.
   *
   * @param title the title describing a group of errors.
   * @param errorMessages the group of errors.
   * @return the new error message.
   */
  public static String createErrorMessage(String title, Collection<Throwable> errorMessages) {
    final java.util.Formatter messageFormatter = new java.util.Formatter();
    messageFormatter.format(title + ":%n%n");
    int index = 1;

    for (Throwable errorMessage : errorMessages) {
      final String message    = errorMessage.getLocalizedMessage();
      final String lineDigit  = message.substring(message.lastIndexOf("line") + 5, message.lastIndexOf("line") + 6);
      final String line       = isInteger(lineDigit) ? ("line " + lineDigit) : "test";



      messageFormatter.format("%s) Error at %s:%n", index++, line).format(" %s%n%n", message);
      messageFormatter.format("%s", Strings.getStringFromStackTrace(errorMessage));
    }

    return messageFormatter.format("%s error[s]", errorMessages.size()).toString();
  }

  private static boolean isInteger(String input) {
    try {
      //noinspection ResultOfMethodCallIgnored
      Integer.parseInt(input);
      return true;
    } catch (Exception e) {
      return false;
    }
  }


}
