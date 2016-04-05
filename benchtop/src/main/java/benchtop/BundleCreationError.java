package benchtop;

import benchtop.utils.Exceptions;

import java.io.Serializable;
import java.util.ArrayList;
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
    this.errorMessages = new ArrayList<Throwable>(errorMessages);
    if(!errorMessages.isEmpty()){
      sortMessages();
    }
  }

  public String getMessage() {
    return Exceptions.createErrorMessage(title, errorMessages);
  }

  protected void sortMessages(){
    Collections.sort(this.errorMessages, new MessageComparator());
  }

  static class MessageComparator implements Comparator<Throwable>, Serializable {
    @Override public int compare(Throwable a, Throwable b) {
      return a.getMessage().compareTo(b.getMessage());
    }
  }
}
