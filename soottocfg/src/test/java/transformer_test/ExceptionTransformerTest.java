/**
 * 
 */
package transformer_test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.toolkits.graph.CompleteUnitGraph;
import soottocfg.soot.SootRunner;
import soottocfg.soot.transformers.ExceptionTransformer;
import soottocfg.test.Util;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class ExceptionTransformerTest {
		private static final String userDir = System.getProperty("user.dir") + "/";
		private static final String testRoot = userDir + "src/test/resources/";

		private File sourceFile;

		@Parameterized.Parameters(name = "{index}: check ({1})")
		public static Collection<Object[]> data() {
			List<Object[]> filenames = new LinkedList<Object[]>();
			final File source_dir = new File(testRoot + "transformation_tests/exceptions/");
			File[] directoryListing = source_dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if (child.isFile() && child.getName().endsWith(".java")) {
						filenames.add(new Object[] { child, child.getName() });
					} else {
						// Ignore
					}
				}
			} else {
				// Handle the case where dir is not really a directory.
				// Checking dir.isDirectory() above would not be sufficient
				// to avoid race conditions with another process that deletes
				// directories.
				System.err.println("Test data in " + userDir + " not found");
				throw new RuntimeException("Test data not found!");
			}
			return filenames;
		}

		public ExceptionTransformerTest(File source, String name) {
			this.sourceFile = source;
		}


		
		@Test
		public void test() {
			for (SootMethod sm : loadSootMethods()) {
				System.out.println("Transforming " + sm.getSignature());
				Body body = sm.retrieveActiveBody();
				ExceptionTransformer em = new ExceptionTransformer(
						new NullnessAnalysis(new CompleteUnitGraph(body)),
						false);
				em.transform(body);
				System.out.println(body);				
			}
		}
		
		private void loadScene() {
			File classDir = null;
			try {
				classDir = Util.compileJavaFile(this.sourceFile, System.getProperty("java.class.path"));
			} catch (IOException e) {
				e.printStackTrace();
				Assert.fail();
			}
			if (classDir == null) {
				Assert.fail();
			}
			
			soot.G.reset();			
			SootRunner runner = new SootRunner();
			runner.run(classDir.getAbsolutePath(), System.getProperty("java.class.path"));			
		}
		
		private List<SootMethod> loadSootMethods() {
			List<SootMethod> methods = new LinkedList<SootMethod>();
			loadScene();
			List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
			for (SootClass sc : classes) {
				if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {
					for (SootMethod sm : sc.getMethods()) {					
						if (sm.isConcrete()) {
							methods.add(sm);
						}
					}
				}
			}
			return methods;
		}
		
}
