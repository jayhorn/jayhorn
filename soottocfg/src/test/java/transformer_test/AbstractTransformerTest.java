/**
 * 
 */
package transformer_test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soottocfg.soot.SootRunner;
import soottocfg.test.Util;

/**
 * @author schaef
 *
 */
public abstract class AbstractTransformerTest {
	
	protected static final String userDir = System.getProperty("user.dir") + "/";
	protected static final String testRoot = userDir + "src/test/resources/";

	protected File sourceFile;
	
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
	
	protected List<SootMethod> loadSootMethods() {
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
