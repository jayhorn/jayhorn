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

package jayhorn.soot;

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

import jayhorn.Options;
import jayhorn.util.Log;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;

/**
 * The Soot Runner
 * 
 * @author schaef
 */
public class SootRunner {

	protected final soot.options.Options sootOpt = soot.options.Options.v();
	
	public void run(String input) {
		if (null == input || input.isEmpty()) {
			return;
		}

		if (input.endsWith(".jar")) {
			// run with JAR file
			runWithJar(input);
		} else {
			File file = new File(input);
			if (file.isDirectory()) {
				runWithPath(input);
			} else {
				throw new RuntimeException("Don't know what to do with: "
						+ input);
			}
		}
	}
	
	
	/**
	 * Runs Soot by using a JAR file
	 * 
	 * @param jarFile
	 *            JAR file
	 * @param smtFile
	 *            Boogie file
	 */
	public void runWithJar(String jarFile) {
		try {
			// extract dependent JARs
			List<File> jarFiles = new ArrayList<File>();
			jarFiles.addAll(extractClassPath(new File(jarFile)));
			jarFiles.add(new File(jarFile));

			// additional classpath available?
			String cp = buildClassPath(jarFiles);
			if (Options.v().hasClasspath()) {
				cp += File.pathSeparatorChar + Options.v().getClasspath();
			}
			
			// set soot-class-path
			sootOpt.set_soot_classpath(cp);			
			
			// finally, run soot
			runSootAndAnalysis(enumClasses(new File(jarFile)));

		} catch (Exception e) {
			Log.error(e.toString());
		}
	}



	/**
	 * Runs Soot by using a path (e.g., from Joogie)
	 * 
	 * @param path
	 *            Path
	 * @param smtFile
	 *            Boogie file
	 */
	public void runWithPath(String path) {
		try {
			// dependent JAR files
			List<File> jarFiles = new ArrayList<File>();

			// additional classpath available?
			String cp = buildClassPath(jarFiles);
			if (Options.v().hasClasspath()) {
				cp += File.pathSeparatorChar + Options.v().getClasspath();
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
			Log.error(e.toString());
		}		
	}

	/**
	 * Enumeration containing the callgraph algorithms supported for the use
	 * with the data flow tracker
	 * 
	 * @see https 
	 *      ://github.com/secure-software-engineering/soot-infoflow/blob/develop
	 *      /src/soot/jimple/infoflow/Infoflow.java
	 */
	public enum CallgraphAlgorithm {
		None, CHA, VTA, RTA, SPARK
	}

	/**
	 * Enumeration containing the aliasing algorithms supported by FlowDroid
	 * 
	 * @see https 
	 *      ://github.com/secure-software-engineering/soot-infoflow/blob/develop
	 *      /src/soot/jimple/infoflow/Infoflow.java
	 */
	public enum AliasingAlgorithm {
		/**
		 * A fully flow-sensitive algorithm based on Andromeda
		 */
		FlowSensitive,
		/**
		 * A flow-insensitive algorithm based on Soot's point-to-sets
		 */
		PtsBased
	}



	/**
	 * Run Soot and creates an inter-procedural callgraph
	 * that could be loaded by Soot.
	 * @param classes additional classes that need to be loaded (e.g., when analyzing jars)
	 */
	protected void runSootAndAnalysis(List<String> classes) {
		sootOpt.set_keep_line_number(true);
		sootOpt.set_prepend_classpath(true); //-pp
		sootOpt.set_output_format(soot.options.Options.output_format_none);
		sootOpt.set_allow_phantom_refs(true);
				
		for (String s : classes) {
			Scene.v().addBasicClass(s, SootClass.BODIES);
		}
		
		CallgraphAlgorithm cga = Options.v().getCallGraphAlgorithm();
		if (cga!=CallgraphAlgorithm.None) {
			sootOpt.set_whole_program(true);
			// Configure the callgraph algorithm
			switch (cga) {
			case CHA:
				sootOpt.setPhaseOption("cg.cha", "on");
				break;
			case RTA:
				sootOpt.setPhaseOption("cg.spark", "on");
				sootOpt.setPhaseOption("cg.spark", "rta:true");
				sootOpt.setPhaseOption("cg.spark", "string-constants:true");
				
				break;
			case VTA:
				sootOpt.setPhaseOption("cg.spark", "on");
				sootOpt.setPhaseOption("cg.spark", "vta:true");
				sootOpt.setPhaseOption("cg.spark", "string-constants:true");
				break;
			case SPARK:
				sootOpt.setPhaseOption("cg.spark", "on");
				sootOpt.setPhaseOption("cg.spark", "string-constants:true");
				break;
			default:
				throw new RuntimeException("Invalid callgraph algorithm");
			}
		}

		// Iterator Hack
		Scene.v().addBasicClass(
				"org.eclipse.jdt.core.compiler.CategorizedProblem",
				SootClass.HIERARCHY);
		Scene.v().addBasicClass("java.lang.Iterable", SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.util.Iterator", SootClass.SIGNATURES);
		Scene.v()
				.addBasicClass("java.lang.reflect.Array", SootClass.SIGNATURES);

		try {
			// redirect soot output into a stream.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			soot.G.v().out = new PrintStream(baos, true, "utf-8");				
			//Now load the soot classes.
			Scene.v().loadNecessaryClasses();
			Scene.v().loadBasicClasses();

			
			// We explicitly select the packs we want to run for performance
	        // reasons. Do not re-run the callgraph algorithm if the host
	        // application already provides us with a CG.
			if (cga != CallgraphAlgorithm.None
					&& !Scene.v().hasCallGraph()) {
		        PackManager.v().getPack("wjpp").apply();
		        PackManager.v().getPack("cg").apply();
			}			
			
//			Log.info("Building ICFG.");
//			//now create the icfg
//			if (Options.v().getCallGraphAlgorithm() != CallgraphAlgorithm.None) {							 
//				SootKitchenSink.v().iCfg = new JimpleBasedInterproceduralCFG();
//			} else {
//				OnTheFlyJimpleBasedICFG.loadAllClassesOnClassPathToSignatures();
//				Scene.v().getOrMakeFastHierarchy();
////				SootKitchenSink.v().iCfg = new OnTheFlyJimpleBasedICFG(Scene.v().getEntryPoints());
//			}
//
//			SootKitchenSink.v().pointsToAnalysis = Scene.v().getPointsToAnalysis();
			
			Log.info("Done.");
		} catch (UnsupportedEncodingException e) {
			Log.error(e.toString());
		} catch (RuntimeException e) {
			Log.error("Soot could not process the input. STOPPING");
			e.printStackTrace();
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
	 * @returns jarFiles
	 *            List of dependent JARs
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
			String classPath = mainAttributes
					.getValue(Attributes.Name.CLASS_PATH);

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
					Log.debug("Adding " + classPathItem
							+ " to Soot's class path");
					jarFiles.add(new File(file.getParent(), classPathItem));
				}
			}

		} catch (IOException e) {
			Log.error(e.toString());
		}
		return jarFiles;
	}


	/**
	 * Enumerates all classes in a JAR file
	 * @param file a Jar file
	 * @returns list of classes in the Jar file.
	 */
	protected List<String> enumClasses(File file) {
		List<String> classes = new LinkedList<String>();
		try {
			// open JAR file
			Log.debug("Opening jar " + file.getPath());
			JarFile jarFile = new JarFile(file);
			Enumeration<JarEntry> entries = jarFile.entries();

			// iterate JAR entries
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				String entryName = entry.getName();

				if (entryName.endsWith(".class")) {
					// get class
					String className = entryName.substring(0,
							entryName.length() - ".class".length());
					className = className.replace('/', '.');

					// add class
					Log.debug("Adding class " + className);
					classes.add(className);
				}
			}

			// close JAR file
			jarFile.close();

		} catch (IOException e) {
			Log.error(e.toString());
		}
		return classes;
	}

}
