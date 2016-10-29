/*
 * jimple2boogie - Translates Jimple (or Java) Programs to Boogie
 * Copyright (C) 2013 Martin Schaeaeaef and Stephan Arlt
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package soottocfg.soot;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.google.common.base.Verify;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import soot.ArrayType;
import soot.BooleanType;
import soot.Modifier;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soottocfg.Options;

/**
 * The Soot Runner
 * 
 * @author schaef
 * @author Dietsch
 */
public class SootRunner {

	private final soot.options.Options sootOpt;
	// private final List<String> resolvedClassNames;

	public SootRunner() {
		this(new ArrayList<String>());
	}

	public SootRunner(List<String> resolvedClassNames) {
		this.sootOpt = soot.options.Options.v();
	}

	public void run(String input, String classPath) {
		if (null == input || input.isEmpty()) {
			return;
		}
		if (input.endsWith(".jar")) {
			// run with JAR file
			runWithJar(input, classPath);
			throw new RuntimeException("currently not tested");
		} else if (input.endsWith(".apk")) {
			runWithApk(input, classPath);
			throw new RuntimeException("currently not tested");
		} else {
			File file = new File(input);
			if (file.isDirectory()) {
				runWithPath(input, classPath);
			} else {
				throw new RuntimeException("Don't know what to do with: " + input);
			}
		}
	}

	/**
	 * Runs Soot by using a JAR file
	 * 
	 * @param jarFile
	 *            JAR file
	 * @param classPath
	 *            Optional classpath (may be null)
	 * 
	 */
	private void runWithJar(String jarFile, String classPath) {
		try {
			// extract dependent JARs
			List<File> jarFiles = new ArrayList<File>();
			jarFiles.addAll(extractClassPath(new File(jarFile)));
			jarFiles.add(new File(jarFile));

			// additional classpath available?
			String cp = buildClassPath(jarFiles);
			if (classPath != null) {
				cp += File.pathSeparatorChar + classPath;
			}

			// set soot-class-path
			sootOpt.set_soot_classpath(cp);

			// finally, run soot
			loadClassesIntoScene(enumClasses(new File(jarFile)));

		} catch (Exception e) {
			throw e;
		}
	}

	private void runWithApk(String apkFile, String androidPlatformPath) {
		try {
			sootOpt.set_src_prec(soot.options.Options.src_prec_apk);
			// https://github.com/Sable/android-platforms
			if (androidPlatformPath == null) {
				throw new RuntimeException(
						"You need to pass the android-platforms folder from https://github.com/Sable/android-platforms");
			}
			sootOpt.set_android_jars(androidPlatformPath);
			List<String> procdir = new LinkedList<String>();
			procdir.add(apkFile);
			sootOpt.set_process_dir(procdir);

			// finally, run soot
			loadClassesIntoScene(enumClasses(new File(apkFile)));

		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Runs Soot by using a path (e.g., from Joogie)
	 * 
	 * @param path
	 *            Path * @param classPath Optional classpath (may be null)
	 * @param classPath
	 *            Optional classpath (may be null)
	 */
	private void runWithPath(String path, String classPath) {
		try {
			// dependent JAR files
			List<File> jarFiles = new ArrayList<File>();

			// additional classpath available?
			String cp = buildClassPath(jarFiles);
			if (classPath != null) {
				cp += File.pathSeparatorChar + classPath;
			}

			// set soot-class-path
			sootOpt.set_soot_classpath(cp);
			sootOpt.set_src_prec(soot.options.Options.src_prec_only_class);

			List<String> processDirs = new LinkedList<String>();
			processDirs.add(path);

			if (Options.v().useBuiltInSpecs()) {
				File specDir = new File("spec_stuff/");
				writeSpecPackageToDisc(specDir);
				processDirs.add(specDir.getAbsolutePath());
			}
			if (Options.v().checkMixedJavaClassFiles()) {
				enforceNoSrcPolicy(processDirs);
			}
			sootOpt.set_process_dir(processDirs);

			// finally, run soot
			loadClassesIntoScene(new LinkedList<String>());

			// now set the main class
			inferMainMethod();

		} catch (Exception e) {
			throw e;
		}
	}

	private void inferMainMethod() {
		SootMethod mainMethod = null;
		SootClass mainClass = null;
		boolean toManyMains = false;
		StringBuilder sb = new StringBuilder();
		for (SootClass c : Scene.v().getApplicationClasses()) {
			if (c.declaresMethod("main", Arrays.asList((Type)ArrayType.v(RefType.v("java.lang.String"), 1)), VoidType.v())) {
				if (mainMethod != null) {
					toManyMains = true;
				}
				mainMethod = c.getMethod("main", Arrays.asList((Type)ArrayType.v(RefType.v("java.lang.String"), 1)),
						VoidType.v());
				
				mainClass = c;
//				System.err.println(mainMethod.getSignature());
				sb.append(mainMethod.getSignature());
				sb.append("\n");
			}
		}
		Verify.verify(mainClass!=null && mainMethod!=null, "No main method found. Terminating.");
		Scene.v().setMainClass(mainClass);
		if (toManyMains) {
			System.err.println("More than one main found:");
			System.err.println(sb.toString());
			System.err.println("Picking the last one.");
		}
	}

	/**
	 * Soot only runs properly if there are only class files in the processed
	 * directory.
	 * If there are source files mixed with class files, stange errors happen
	 * because
	 * soot mixes them in the scene.
	 * To avoid these random error, we fail early.
	 * 
	 * @param dirs
	 */
	private void enforceNoSrcPolicy(List<String> dirs) {
		for (String dir : dirs) {
			for (File f : Files.fileTreeTraverser().preOrderTraversal(new File(dir))) {
				if (f != null && f.isFile() && f.getName().endsWith(".java")) {
					StringBuilder sb = new StringBuilder();
					sb.append("Found mix of source and class files in folder ");
					sb.append(f.getParent());
					sb.append("\nSoot expects a directory tree that only contains class files.\n");
					sb.append("Please create a directory, compile your code with javac -d [dir]\n");
					sb.append("and pass us this dir. Sorry for the inconvenience.");
					System.err.println(sb.toString());
					throw new UnsupportedOperationException("Bad value for -j argument.");
				}
			}
		}
	}

	/**
	 * Run Soot and creates an inter-procedural callgraph that could be loaded
	 * by Soot.
	 * 
	 * @param classes
	 *            additional classes that need to be loaded (e.g., when
	 *            analyzing jars)
	 */
	protected void loadClassesIntoScene(List<String> classes) {
		sootOpt.set_keep_line_number(true);
		sootOpt.set_prepend_classpath(true); // -pp

		sootOpt.set_output_format(soot.options.Options.output_format_none);
		// prevent strange assertion optimization.
		sootOpt.setPhaseOption("jop.cpf", "enabled:false");
		sootOpt.set_allow_phantom_refs(true);

		for (String s : classes) {
			Scene.v().addBasicClass(s, SootClass.BODIES);
		}

		// TODO: hack for the implicit entry points.

		Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.Thread", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.ThreadGroup", SootClass.SIGNATURES);

		Scene.v().addBasicClass("java.lang.ClassLoader", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.security.PrivilegedActionException", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.ref.Finalizer", SootClass.SIGNATURES);

		try {
			// redirect soot output into a stream.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			soot.G.v().out = new PrintStream(baos, true, "utf-8");
			// Now load the soot classes.

			Scene.v().loadBasicClasses();
			// if (resolvedClassNames.isEmpty()) {
			Scene.v().loadNecessaryClasses();
			// } else {
			// //TODO: Is this reachable?
			// loadNecessaryClasses();
			// }
			PackManager.v().runPacks();
			createAssertionClass();
			
			/*
			 * TODO: apply some preprocessing stuff like:
			 * soot.jimple.toolkits.base or maybe the optimize option from soot.
			 * TODO: NOT SURE IF THE CODE BELOW IS NECESSARY!
			 */
			for (SootClass sc : Scene.v().getClasses()) {
				if (sc.resolvingLevel() < SootClass.SIGNATURES) {
					sc.setResolvingLevel(SootClass.SIGNATURES);
				}

				// if (classes.contains(sc.getName())) {
				// sc.setApplicationClass();
				// }
			}

		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.toString());
		} catch (RuntimeException e) {
			throw e;
		}
	}

	// private void loadNecessaryClasses() {
	// for (String eachClassname : resolvedClassNames) {
	// final SootClass theClass = Scene.v().loadClassAndSupport(eachClassname);
	// theClass.setApplicationClass();
	// }
	// }

	public static final String assertionClassName = "JayHornAssertions";
	public static final String assertionProcedureName = "super_crazy_assertion";
	public static final String exceptionGlobalName = "lastExceptionThrown";

	/**
	 * TODO
	 */
	public static void createAssertionClass() {
		if (Scene.v().containsClass(assertionClassName)) {
			throw new RuntimeException("Don't try to call me twice!");
		}
		SootClass sClass = new SootClass(assertionClassName, Modifier.PUBLIC);
		sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));

		// add a static field to keep track of thrown exceptions.
		SootClass javaThrowableClass = Scene.v().getSootClass("java.lang.Throwable");
		SootField exceptionGlobal = new SootField(exceptionGlobalName, javaThrowableClass.getType(),
				Modifier.PUBLIC | Modifier.STATIC);
		sClass.addField(exceptionGlobal);

		// add a method to model assertions.
		SootMethod internalAssertMethod = new SootMethod(assertionProcedureName,
				Arrays.asList(new Type[] { BooleanType.v() }), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
		sClass.addMethod(internalAssertMethod);

		JimpleBody body = Jimple.v().newBody(internalAssertMethod);
		internalAssertMethod.setActiveBody(body);
		body.insertIdentityStmts();
		body.getUnits().add(Jimple.v().newReturnVoidStmt());

		SootMethod staticInitializer = new SootMethod(SootMethod.staticInitializerName, Arrays.asList(new Type[] {}),
				VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
		body = Jimple.v().newBody(staticInitializer);
		staticInitializer.setActiveBody(body);
		body.insertIdentityStmts();
		body.getUnits().add(Jimple.v().newReturnVoidStmt());
		sClass.addMethod(staticInitializer);

		Scene.v().addClass(sClass);
		sClass.setApplicationClass();
	}

	/**
	 * Returns the class path argument for Soot
	 * 
	 * @param files
	 *            Files in the class path
	 * @return Class path argument for Soot
	 */
	protected String buildClassPath(List<File> files) {
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			sb.append(file.getPath() + File.pathSeparatorChar);
		}
		return sb.toString();
	}

	/**
	 * Extracts dependent JARs from the JAR's manifest
	 * 
	 * @param file
	 *            JAR file object
	 * @returns jarFiles List of dependent JARs
	 */
	protected List<File> extractClassPath(File file) {
		List<File> jarFiles = new LinkedList<File>();
		try {
			// open JAR file
			JarFile jarFile = new JarFile(file);

			// get manifest and their main attributes
			Manifest manifest = jarFile.getManifest();
			if (manifest == null) {
				jarFile.close();
				return jarFiles;
			}
			Attributes mainAttributes = manifest.getMainAttributes();
			if (mainAttributes == null) {
				jarFile.close();
				return jarFiles;
			}
			String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);

			// close JAR file
			jarFile.close();

			// empty class path?
			if (null == classPath)
				return jarFiles;

			// look for dependent JARs
			String[] classPathItems = classPath.split(" ");
			for (String classPathItem : classPathItems) {
				if (classPathItem.endsWith(".jar")) {
					// add jar
					jarFiles.add(new File(file.getParent(), classPathItem));
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
		return jarFiles;
	}

	/**
	 * Enumerates all classes in a JAR file
	 * 
	 * @param file
	 *            a Jar file
	 * @returns list of classes in the Jar file.
	 */
	protected List<String> enumClasses(File file) {
		List<String> classes = new LinkedList<String>();
		try {
			// open JAR file
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();

			// iterate JAR entries
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();

				if (entryName.endsWith(".class")) {
					// get class
					String className = entryName.substring(0, entryName.length() - ".class".length());
					className = className.replace('/', '.');

					// add class
					classes.add(className);
				}
			}

			// close JAR file
			jarFile.close();

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
		return classes;
	}

	/*
	 * ======= below deals with writing out the spec classes
	 */

	/**
	 * Writes all classes from the soottocfg.spec to targetDir
	 * so that we can use them later when re-writing the bytecode.
	 * 
	 * @param targetDir
	 */
	protected void writeSpecPackageToDisc(File targetDir) {
		if (!targetDir.isDirectory()) {
			if (!targetDir.mkdirs()) {
				throw new RuntimeException("Can't write to disk");
			}
		}
		try {
			ClassLoader cl = this.getClass().getClassLoader();
			ClassPath cp = ClassPath.from(cl);
			for (ClassInfo ci : cp.getTopLevelClassesRecursive("soottocfg.spec")) {

				StringBuilder sb = new StringBuilder();
				sb.append(targetDir.getAbsolutePath());
				sb.append(File.separator);
				sb.append(ci.getPackageName().replace(".", File.separator));
				File outFileDir = new File(sb.toString());
				if (!outFileDir.exists() && !outFileDir.mkdirs()) {
					throw new RuntimeException("Couldn't generate dirs for " + sb.toString());
				}
				sb.append(File.separator);
				sb.append(ci.getSimpleName());
				sb.append(".class");
				File outFile = new File(sb.toString());

				try (InputStream inputStream = cl.getResourceAsStream(ci.getResourceName());
						OutputStream outputStream = new FileOutputStream(outFile);) {
					ByteStreams.copy(inputStream, outputStream);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
