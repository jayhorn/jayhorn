package benchtop;

/**
 *
 * Command's configuration unit.
 *
 * @author Huascar Sanchez
 */
public interface Configuration {
  /**
   * Configures a command builder.
   *
   * @param builder command builder
   */
  void configure(Command.Builder builder);
}
