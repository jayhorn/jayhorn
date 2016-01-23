package soottocfg.randoop;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * @author Huascar Sanchez
 */
public class Javac {

  private final Command.Builder builder;
  private boolean debugMode;
  private final ExecutionLog javacLog;

  /**
   * Constructs the Javac object. Compilation of
   * Java objects made simple.
   */
  public Javac(){
    this(new BasicExecutionLog(System.out));
  }

  Javac(ExecutionLog log, String javac){
    this.builder = Command.of(log);
    builder.arguments(javac);
    this.debugMode = false;
    this.javacLog  = log;
  }

  Javac(ExecutionLog log){
    this(log, "javac");
  }

  public Javac bootClasspath(Classpath classpath) {
    builder.arguments("-bootclasspath", classpath.toString());
    return this;
  }

  public Javac classpath(File... path) {
    return classpath(Classpath.of(path));
  }

  public Javac classpath(Classpath classpath) {
    builder.arguments("-classpath", classpath.toString());
    return this;
  }

  public List<String> version(){
    return builder.arguments("-version").execute();
  }

  public Javac sourcepath(File... path) {
    builder.arguments("-sourcepath", Classpath.of(path).toString());
    return this;
  }

  public void log(String message, Throwable throwable){
    this.javacLog.error(message, throwable);
  }

  public void log(List<String> output){
    for(String eachString : output){
      javacLog.info(eachString);
    }
  }

  public Javac sourcepath(Collection<File> path) {
    builder.arguments("-sourcepath", Classpath.of(path).toString());
    return this;
  }

  public Javac destination(File directory) {
    builder.arguments("-d", directory.toString());
    return this;
  }

  public Javac debug() {
    builder.arguments("-g");
    debugMode = true;
    return this;
  }

  public boolean inDebugMode(){
    return debugMode;
  }

  public Javac extraArgs(List<String> extra) {
    builder.arguments(extra);
    return this;
  }

  public List<String> compile(Collection<File> files) {
    return builder.arguments((Object[]) Strings.generateArrayOfStrings(files)).execute();
  }

  public List<String> compile(File... files) {
    return compile(Arrays.asList(files));
  }
}
