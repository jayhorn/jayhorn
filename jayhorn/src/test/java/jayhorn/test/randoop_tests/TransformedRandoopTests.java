package jayhorn.test.randoop_tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.google.common.io.Files;

import jayhorn.test.Util;
import soot.Scene;
import soot.SootClass;
import soot.SourceLocator;
import soot.jimple.JasminClass;
import soot.options.Options;
import soot.tagkit.Tag;
import soot.util.JasminOutputStream;
import soottocfg.randoop.Randoop;
import soottocfg.soot.SootToCfg;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author Huascar Sanchez, Martin Schaef
 */
@RunWith(Parameterized.class)
public class TransformedRandoopTests {

	private File sourceFile;
	private String sourceFilePath;

	public TransformedRandoopTests(File sourceFile, String path) {
		this.sourceFile = sourceFile;
		this.sourceFilePath = path;
	}

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		final File testDirectory = Util.currentTestDirectory("randoop_tests");
		return Util.getData(testDirectory);
	}

	@Test
	public void testTransformations() throws Throwable {
		System.out.println("Testing " + sourceFilePath);

		final String classPath = Randoop.jayHornPath();

		Options.v().set_verbose(false);
		Options.v().set_soot_classpath(classPath);

		final File tempFolder = Files.createTempDir();

		File classTempDirectory = null;
		try {
			classTempDirectory = Util.compileJavaFile(sourceFile, tempFolder, classPath.split(":"));
			Assert.assertTrue(classTempDirectory.isDirectory());
		} catch (IOException ioe) {
			ioe.printStackTrace(System.err);
			Assert.fail();
		}

		final File transformedTempFolder = Files.createTempDir();
		final SootToCfg soot2cfg = new SootToCfg();
		soot2cfg.runPreservingTransformationOnly(classTempDirectory.getAbsolutePath(), null);
		
		List<SootClass> suiteClasses = new LinkedList<SootClass>();
		
		// write out the transformed classes
		for (SootClass sc : Scene.v().getApplicationClasses()) {
			transformClass(sc, transformedTempFolder);
			for (Tag t : sc.getTags()) {
				if (t.toString().contains("Suite$SuiteClasses")) {
					assertNotLoaded(sc);
					suiteClasses.add(sc);
				}
			}
		}

		for (SootClass sc : suiteClasses) {
			Computer computer = new Computer();
			JUnitCore jUnitCore = new JUnitCore();
			System.err.println("Runnning: " + sc.getName());
			jUnitCore.run(computer, loadSootClass(sc));
		}
		
		
		final Class<?> originalClass = Util.loadClass(tempFolder);
				
		final Class<?> transformedClass = loadClass(originalClass, transformedTempFolder);
				
		Assert.assertFalse(originalClass==transformedClass);
		
		Assert.assertTrue(compareClassFiles(originalClass, transformedClass));

		tempFolder.deleteOnExit();
		transformedTempFolder.deleteOnExit();
	}

	
	
	/*
	 * TODO remove
	 */
	private Class<?> loadSootClass(SootClass sc) {
		try {
			return this.getClass().getClassLoader().loadClass(sc.getName());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("cannot happen");
		}
	}
	
	private void assertNotLoaded(SootClass sc) {
		String className = sc.getName();
		System.err.println(className);
		boolean classAlreadyLoaded = true;
		try {
			Class.forName(className, false, this.getClass().getClassLoader());			
		} catch (ClassNotFoundException e) {
			classAlreadyLoaded = false;
		}
		if (classAlreadyLoaded) {
			Assert.fail(className + " has already been loaded. You cannot apply this project on itself!");
		}
	}
	
	
	private boolean compareClassFiles(Class<?> originalClass, Class<?> transformedClass) {
		for (Method eachMethod : originalClass.getDeclaredMethods()) {
			if ("$jacocoInit".equals(eachMethod.getName()))
				continue;

			try {

				final Method eachMethod2 = transformedClass.getDeclaredMethod(eachMethod.getName(),
						eachMethod.getParameterTypes());
				// check return type
				// if (eachMethod.getReturnType().equals(Void.TYPE)) {
				// System.out.println("Ignoring void return");
				// continue;
				// }

				if (eachMethod.getParameterTypes().length != 0) {
					System.out.println("Ignoring methods with args ");
					continue;
				}

				if (!compareMethodsWithoutParameters(originalClass, eachMethod, transformedClass, eachMethod2)) {
					return false;
				}

			} catch (NoSuchMethodException | SecurityException e) {
				return false;
			}
		}

		return true;
	}

	private boolean compareMethodsWithoutParameters(Class<?> originalClass, Method eachMethod,
			Class<?> transformedClass, Method eachMethod2) {

		Object output1 = null;

		try {
			if (eachMethod.getReturnType() != Void.TYPE) {
				output1 = eachMethod.invoke(originalClass.newInstance());
			} else {
				eachMethod.invoke(originalClass.newInstance());
			}
		} catch (InvocationTargetException e) {
			output1 = e.getTargetException().toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return true;
		}

		Object output2 = null;
		try {
			if (eachMethod2.getReturnType() != Void.TYPE) {
				output2 = eachMethod2.invoke(transformedClass.newInstance());
			} else {
				eachMethod2.invoke(transformedClass.newInstance());
			}
		} catch (InvocationTargetException e) {
			output2 = e.getTargetException().toString();
		} catch (Throwable e) {
			e.printStackTrace();
			return true;
		}

		final StringBuilder nullOutput = new StringBuilder();
		if (output1 == null) {
			if (output2 == null) {
				System.err.println("both null");
				return true;
			}

			nullOutput.append("Output: ");
			nullOutput.append("null is not equals to ");
			nullOutput.append(output2);
			System.out.println(nullOutput.toString());
			return false;
		} else {
			if (!output1.equals(output2)) {
				final StringBuilder differentOutput = reuseForBetterPerformance(nullOutput);
				differentOutput.append("For ").append(eachMethod.getName());
				differentOutput.append("\nOutput different: \n");
				differentOutput.append("\"");
				differentOutput.append(output1);
				differentOutput.append("\"\n");
				differentOutput.append(" is different from \n");
				differentOutput.append("\"");
				differentOutput.append(output2);
				differentOutput.append("\"\n");
				System.out.println(differentOutput.toString());
				return false;
			}

			final StringBuilder equalOutput = reuseForBetterPerformance(nullOutput);
			equalOutput.append("Output: ");
			equalOutput.append(output1);
			equalOutput.append(" is equals to ");
			equalOutput.append(output2);
			System.out.println(equalOutput.toString());

			return true;
		}
	}

	public static StringBuilder reuseForBetterPerformance(final StringBuilder sb) {
		sb.delete(0, sb.length());
		return sb;
	}

	private void transformClass(SootClass sootClass, File transformedTempFolder) {
		sootClass.validate();

		final String currentClassname = SourceLocator.v().getFileNameFor(sootClass, Options.output_format_class);

		final StringBuilder content = new StringBuilder(1000);
		content.append(transformedTempFolder.getAbsolutePath());
		content.append(File.separator);

		if (!sootClass.getPackageName().isEmpty()) {
			content.append(sootClass.getPackageName().replace(".", File.separator));
			content.append(File.separator);
		}

		content.append(Files.getNameWithoutExtension(currentClassname));
		content.append(".class");

		final File modifiedClassFile = new File(content.toString());

		if (!modifiedClassFile.getParentFile().mkdirs()) {
			System.out.println("no folders needed");
		}

		final String fileName = modifiedClassFile.getAbsolutePath();
		try (OutputStream streamOut = new JasminOutputStream(new FileOutputStream(fileName));
				PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut, "UTF-8"));) {
			final JasminClass jasminClass = new JasminClass(sootClass);
			jasminClass.print(writerOut);
			writerOut.flush();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

	}

	private static Class<?> loadClass(Class<?> cOrig, File classDir) throws Throwable {
		try (URLClassLoader classLoader = new URLClassLoader(new URL[] { classDir.toURI().toURL() })) {
			classLoader.loadClass(SootTranslationHelpers.v().getAssertionClass().getName());
			return classLoader.loadClass(cOrig.getName());
		} catch (Throwable e) {
			e.printStackTrace(System.out);
			throw e;
		}
	}

}
