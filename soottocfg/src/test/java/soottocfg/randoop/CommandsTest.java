package soottocfg.randoop;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Huascar Sanchez
 */
public class CommandsTest {
  @Test public void testBasicCommandInit() throws Exception {

    final Command cmd = new Command(Arrays.asList("boo", "lala")){
      @Override public List<String> execute() {
        return new ArrayList<>();
      }
    };



    assertNotNull(cmd);
    assertThat(cmd.execute().isEmpty(), is(true));
    assertThat(cmd.isStarted(), is(false));

  }

  @Test public void testCommandInitWithBuilder() throws Exception {
    final Command command = new Command.Builder(new BasicExecutionLog(System.out))
      .arguments("boo")
      .console(System.err)
      .environment("cool", "no")
      .maxCommandLength(100)
      .permitNonZeroExitStatus().workingDirectory(new File("nothing.nothing")).build();

    assertNotNull(command);
    assertThat(command.isStarted(), is(false));

    assertEquals(command.toString(), "cool=no boo");

  }

  @Test public void testJavac() throws Exception {
    final Javac javac = new Javac(){
      @Override public List<String> compile(Collection<File> files) {
        return new ArrayList<>();
      }
    };

    final List<String> result = javac.debug()
      .classpath(new File("boo"))
      .destination(new File("doo"))
      .extraArgs(Collections.singletonList("yay"))
      .sourcepath(new File("src"))
      .compile(new File("java"));

    javac.log(result);

    assertNotNull(javac);

  }
}
