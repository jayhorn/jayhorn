
/**
 * <i>Jayhorn's Benchtop</i> is a project that automates the execution of Randoop to
 * validate that bytecode transformations applied to a Java project by Soot don't
 * introduce unsoundness.
 *
 * <p>The principal public APIs in this package are: </p>
 *
 * <dl>
 * <dt>{@link benchtop.Benchtop}
 * <dd>The facade object that coordinates how Randoop, Javac, and Java commands will be
 *     executed.
 *
 * <dt>{@link benchtop.Classpath}
 * <dd>The classpath required by Benchtop's commands to execute properly.
 *
 * <dt>{@link benchtop.ExecutionBundle}
 * <dd>The interface you will implement when you need to customize exactly how
 *     Benchtop creates and executes Randoop tests for a particular Java project.
 *
 * <dt>{@link benchtop.Command}
 * <dd>The object behind all commands supported by Benchtop. This command can be configured to create
 *     new Benchtop commands.
 * </dl>
 *
 *
 * <p>Customizing an Execution Bundle: </p>
 *
 *
 * <p>Let's assume we want generate/execute Randoop tests for a project whose class files are
 * located at the directory temp/foo/classes/</p>
 *
 * <p>The project's dependencies are cached using ivy. Benchtop will look into the .ivy2 folder
 * to try to auto-resolve these dependencies (See host.bundleClasspath() method). If you are
 * interested in adding more files to the classpath, then you can do that by simply adding an array
 * of Classpath objects to the host.bundleClasspath() method.</p>
 *
 * <p>We are also interested in only executing Randoop's Regression tests as these tests don't
 * represent errors that break the Java project.</>
 *
 * <p>Given all the above information, here is how we can generate/execute Randoop tests
 * for this Java project: </p>
 *
 * <pre>
 *   // creates bundle
 *   ExecutionBundle a = new ExecutionBundle(){
 *     &#64Override public void configure(Environment host){
 *        host.bundleTarget(new File("temp/foo/classes/"));
 *        host.bundleOutput(new File("temp/out/"));
 *        host.bundleClasspath();
 *        host.bundleFocus("Regression");
 *     }
 *   };
 *
 *   // pass it to Benchtop, sit down and relax, and then let Benchtop
 *   // produce-execute all the needed tests
 *   Benchtop.consumes(a);
 * </pre>
 *
 *
 */
package benchtop;