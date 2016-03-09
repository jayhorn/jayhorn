package benchtop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class BundleCreationError extends RuntimeException {
  private final List<Throwable> errorMessages;
  private final String title;

  protected BundleCreationError(String title, List<Throwable> errorMessages){
    super();
    this.title         = title;
    // Sort the messages by source.
    this.errorMessages = new ArrayList<>(errorMessages);
    if(!errorMessages.isEmpty()){
      sortMessages();
    }
  }

  public void cache(Throwable throwable){
    errorMessages.add(throwable);
  }

  public String getMessage() {
    return createErrorMessage(title, errorMessages);
  }

  protected void sortMessages(){
    Collections.sort(this.errorMessages, new MessageComparator());
  }

  private static String createErrorMessage(String title, Collection<Throwable> errorMessages) {
    final java.util.Formatter messageFormatter = new java.util.Formatter();
    messageFormatter.format(title + ":%n%n");
    int index = 1;

    for (Throwable errorMessage : errorMessages) {
      final String    message = errorMessage.getLocalizedMessage();
      final String    line    = "line " + message.substring(message.lastIndexOf("line") + 5, message.lastIndexOf("line") + 6);
      messageFormatter.format("%s) Error at %s:%n", index++, line)
        .format(" %s%n%n", message
        );
    }

    return messageFormatter.format("%s error[s]", errorMessages.size()).toString();
  }


  static class MessageComparator implements Comparator<Throwable> {
    @Override public int compare(Throwable a, Throwable b) {
      return a.getMessage().compareTo(b.getMessage());
    }
  }
}
