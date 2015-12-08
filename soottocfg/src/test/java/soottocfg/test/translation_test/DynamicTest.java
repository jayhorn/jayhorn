/**
 * 
 */
package soottocfg.test.translation_test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.io.Files;

import soottocfg.soot.SootToCfg;
import soottocfg.test.Util;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class DynamicTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "dynamic_tests/");
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

	public DynamicTest(File source, String name) {
		this.sourceFile = source;
	}

	@Test
	public void test() throws Throwable {
		soot.G.reset();
		File classDir = null;
		try {
			classDir = Util.compileJavaFile(this.sourceFile);
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
		if (classDir == null) {
			Assert.fail();
		}
		Assert.assertTrue(classDir.isDirectory());
		// Now we have the classDir of the original program

		File transformedClassDir = Util.getTempDir();
		SootToCfg soot2cfg = new SootToCfg();
		soot2cfg.setOuputDirForTransformedClassFiles(transformedClassDir);
		soot2cfg.run(classDir.getAbsolutePath(), null);

		Class<?> cOrig = loadClass(classDir);
		Class<?> cNew = loadClass(transformedClassDir);
		Assert.assertTrue(compareClassFiles(cOrig, cNew));
	}

	private boolean compareClassFiles(Class<?> c1, Class<?> c2) {
		for (Method m1 : c1.getDeclaredMethods()) {
			if ("$jacocoInit".equals(m1.getName())) {
				//This is generated by our CI, ignore it.
				continue;
			}
			try {
				Method m2 = c2.getDeclaredMethod(m1.getName(), m1.getParameterTypes());
				System.out.print("Testing "+ m1.getName()+": ");
				if (m1.getReturnType().equals(Void.TYPE)) {
					System.out.println(" returns void. Skipping");
					continue;
				}
				if (!compareMethodsWithoutParameters(c1,m1, c2, m2)) {
					System.out.println(" output different :(");
					return false;
				}
				System.out.println(" all good :) ");
			} catch (NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	private boolean compareMethodsWithoutParameters(Class<?> c1, Method m1, Class<?> c2, Method m2) {	
		// only run methods that do not have parameters
		if (m1.getParameterTypes().length != 0) {
			return true;
		}
		Object out1 = null; 
		try {
			out1 = m1.invoke(c1.newInstance());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			return false;
		} catch (Throwable e) {
			//if the invoked procedure threw an exception,
			//store that as the return value.
			out1 = e;
		}
		
		Object out2 = null; 
		try {
			out2 = m2.invoke(c2.newInstance());
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| InstantiationException e) {
			return false;
		} catch (Throwable e) {
			//if the invoked procedure threw an exception,
			//store that as the return value.
			out2 = e;
		}

		System.err.println("out1: "+ out1);
		System.err.println("out2: "+ out2);
		if (out1==null) {
			return out2==null;
		} else {
			return out1.equals(out2);
		}
	}

	private Class<?> loadClass(File classDir) throws Throwable {
		File classFile = null;		
		StringBuilder sb = new StringBuilder();
		File dir = classDir;
		while (dir.isDirectory()) {
			if (dir.listFiles() != null) {
				dir = dir.listFiles()[0];
				sb.append(Files.getNameWithoutExtension(dir.getAbsolutePath()));
				if (dir.isFile() && "class".equals(Files.getFileExtension(dir.getAbsolutePath()))) {
					classFile = dir;
					break;
				} else {
					sb.append(".");
				}
			}
		}
		String className = sb.toString();
		System.err.println(className);
		System.err.println(classFile);

		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { classDir.toURI().toURL() });) {
			return classLoader.loadClass(className);
		} catch (Throwable e) {
			throw e;
		}		
	}

}
