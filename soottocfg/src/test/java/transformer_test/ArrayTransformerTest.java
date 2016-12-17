/**
 * 
 */
package transformer_test;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import soot.SootMethod;
import soottocfg.cfg.Program;
import soottocfg.soot.transformers.ArrayTransformer;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class ArrayTransformerTest extends AbstractTransformerTest {

		@Parameterized.Parameters(name = "{index}: check ({1})")
		public static Collection<Object[]> data() {
			List<Object[]> filenames = new LinkedList<Object[]>();
			final File source_dir = new File(testRoot + "transformation_tests/arrays/");
			File[] directoryListing = source_dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if (child.isFile() && child.getName().endsWith(".java")) {
						filenames.add(new Object[] { child, child.getName() });
					} 
				}
			} 
			return filenames;
		}

		public ArrayTransformerTest(File source, String name) {
			this.sourceFile = source;
		}
		
		@Test
		public void test() {
			Program p = new Program();
			SootTranslationHelpers.initialize(p);
			List<SootMethod> methods = loadSootMethods();
//			SootTranslationHelpers.createTypeFields();
			ArrayTransformer arr = new ArrayTransformer();
			arr.applyTransformation();
			for (SootMethod sm : methods) {
				if (sm.hasActiveBody()) {
					System.out.println(sm.getActiveBody());
				}
			}
		}
		

}
