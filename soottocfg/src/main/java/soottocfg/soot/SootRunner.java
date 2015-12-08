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
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import soot.Scene;
import soot.SootClass;

/**
 * The Soot Runner
 * 
 * @author schaef
 * @author Dietsch
 */
public class SootRunner {

	private final soot.options.Options sootOpt;	

	public SootRunner() {
		sootOpt = soot.options.Options.v();
	}

	public void run(String input, String classPath) {
		if (null == input || input.isEmpty()) {
			return;
		}
		if (input.endsWith(".jar")) {
			// run with JAR file
			runWithJar(input, classPath);
		} else if (input.endsWith(".apk")) {
			runWithApk(input, classPath);
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
	 * @param cga
	 *            Which {@link CallgraphAlgorithm} should be used? May not be
	 *            null.
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
			runSootAndAnalysis(enumClasses(new File(jarFile)));

		} catch (Exception e) {
			throw e;
		}
	}
	
	private void runWithApk(String apkFile, String androidPlatformPath) {
		try {
			sootOpt.set_src_prec(soot.options.Options.src_prec_apk);
			//https://github.com/Sable/android-platforms
			if (androidPlatformPath==null) {
				throw new RuntimeException("You need to pass the android-platforms folder from https://github.com/Sable/android-platforms");
			}
			sootOpt.set_android_jars(androidPlatformPath);
			List<String> procdir = new LinkedList<String>();
			procdir.add(apkFile);
			sootOpt.set_process_dir(procdir);
			
			// finally, run soot
			runSootAndAnalysis(enumClasses(new File(apkFile)));

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
	 * @param cga
	 *            Which {@link CallgraphAlgorithm} should be used? May not be
	 *            null.
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
			sootOpt.set_src_prec(soot.options.Options.src_prec_class);

			List<String> processDirs = new LinkedList<String>();
			processDirs.add(path);
			sootOpt.set_process_dir(processDirs);
			
			// finally, run soot
			runSootAndAnalysis(new LinkedList<String>());

		} catch (Exception e) {
			throw e;
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
	protected void runSootAndAnalysis(List<String> classes) {
		sootOpt.set_keep_line_number(true);
		sootOpt.set_prepend_classpath(true); // -pp
		
		sootOpt.set_output_format(soot.options.Options.output_format_none);
			
		sootOpt.set_allow_phantom_refs(true);

		for (String s : classes) {
			Scene.v().addBasicClass(s, SootClass.BODIES);
		}
		
		//TODO: hack for the implicit entry points.
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
			Scene.v().loadNecessaryClasses();

			/*
			 * TODO: apply some preprocessing stuff like:
			 * soot.jimple.toolkits.base or maybe the optimize option from soot.
			 */
			for (SootClass sc : Scene.v().getClasses()) {
				if (sc.resolvingLevel() < SootClass.SIGNATURES) {
					sc.setResolvingLevel(SootClass.SIGNATURES);
				}

				if (classes.contains(sc.getName())) {
					sc.setApplicationClass();
				}
			}
			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.toString());
		} catch (RuntimeException e) {
			throw e;
		}
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

}
