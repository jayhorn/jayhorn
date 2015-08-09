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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import jayhorn.Options;
import jayhorn.soot.visitors.SootBodyTransformer;
import jayhorn.util.Log;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;

/**
 * The Soot Runner
 * 
 * @author schaef
 */
public class SootRunner {

	/**
	 * Runs Soot by using a JAR file
	 * 
	 * @param jarFile
	 *            JAR file
	 * @param smtFile
	 *            Boogie file
	 */
	public void runWithJar(String jarFile, String smtFile) {
		try {
			// command-line arguments for Soot
			List<String> args = new ArrayList<String>();
			fillSootArgs(args);

			// extract dependent JARs
			List<File> jarFiles = new ArrayList<File>();
			extractClassPath(new File(jarFile), jarFiles);
			jarFiles.add(new File(jarFile));
			fillClassPath(jarFiles);

			// additional classpath available?
			String cp = buildClassPath(jarFiles);
			if (Options.v().hasClasspath()) {
				cp += File.pathSeparatorChar + Options.v().getClasspath();
			}

			// set soot-class-path
			args.add("-cp");
			args.add(cp);

			// set classes
			enumClasses(new File(jarFile), args);

			// finally, run soot
			run(args, smtFile);

		} catch (Exception e) {
			Log.error(e.toString());
		}
	}


	public void runWithApk(String apkFile, String smtFile) {
		try {
			// command-line arguments for Soot
			
			List<String> args = new ArrayList<String>();
			fillSootArgs(args);

			String cp = apkFile;
			
			//enforce android
			args.add("-src-prec");
			args.add("apk");	
			//add path to android framework stubs
			args.add("-android-jars");
			String android_path = Options.v().getAndroidStubPath();
			if (android_path==null) {
				throw new RuntimeException("Need to specify -android-jars when analyzing apk's.");
			}
			args.add(android_path);
			// add soot-class-path
			args.add("-cp");
			args.add(cp);
			
			//add process-dir
			args.add("-process-dir");
			args.add(apkFile);
						
			// finally, run soot
			run(args, smtFile);

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
	public void runWithPath(String path, String smtFile) {
		try {
			// dependent JAR files
			List<File> jarFiles = new ArrayList<File>();
			fillClassPath(jarFiles);

			// additional classpath available?
			String cp = buildClassPath(jarFiles);
			if (Options.v().hasClasspath()) {
				cp += File.pathSeparatorChar + Options.v().getClasspath();
			}

			// command-line arguments for Soot
			List<String> args = new ArrayList<String>();
			fillSootArgs(args);

			// add soot-class-path
			args.add("-cp");
			args.add(cp);
			
			Log.info("Using classpath: "+cp);
			
			args.add("-src-prec");
			args.add("class");
			
			// add path to be processed
			args.add("-process-path");
			args.add(path);

			
			
			// finally, run soot
			run(args, smtFile);

		} catch (Exception e) {
			Log.error(e.toString());
		}
	}

	/**
	 * Runs Soot
	 * 
	 * @param args
	 *            Command-line arguments
	 * @param smtFile
	 *            Boogie file
	 */
	protected void run(List<String> args, String smtFile) {
		try {

			// reset & init Soot
			soot.G.reset();
			
			/*
			case CHA:
				Options.v().setPhaseOption("cg.cha", "on");
				break;
			case RTA:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg.spark", "rta:true");
				Options.v().setPhaseOption("cg.spark", "string-constants:true");
				break;
			case VTA:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg.spark", "vta:true");
				Options.v().setPhaseOption("cg.spark", "string-constants:true");
				break;
			case SPARK:
				Options.v().setPhaseOption("cg.spark", "on");
				Options.v().setPhaseOption("cg.spark", "string-constants:true");

			 */
			
			//check if we need fullprogram analysis
//			if (Options.v().useSoundThreads()) {
//				args.add("-w");
//
//				
//				args.add("-p");
//				args.add("cg.spark");
//				args.add("enabled:true");
//
//				//enable field RW analysis		
//				args.add("-p");
//				//args.add("jap.fieldrw");
//				args.add("jap.sea");
//				args.add("enabled:true");
//				
//				//enable MayHappenInParallel analysis
//				args.add("-p");
//				args.add("wjtp.mhp");
//				args.add("enabled:true");
//				
//			} else {
				// Iterator Hack
				Scene.v().addBasicClass("org.eclipse.jdt.core.compiler.CategorizedProblem",SootClass.HIERARCHY);
				Scene.v().addBasicClass("java.lang.Iterable",SootClass.SIGNATURES);			
				Scene.v().addBasicClass("java.util.Iterator",SootClass.SIGNATURES);
				Scene.v().addBasicClass("java.lang.reflect.Array",SootClass.SIGNATURES);
				
//			}
			
			Pack pack = PackManager.v().getPack("jtp");
			
			//pack.add(new Transform("jtp.NullCheckEliminator",new NullCheckEliminator()));
			
			pack.add(new Transform("jtp.JHornTransform",
					new SootBodyTransformer()));
			
			StringBuilder sb = new StringBuilder();
			for (String s : args) {
				sb.append(" "+s);
			}
			Log.info("Running soot with "+sb.toString());

			// Finally, run Soot		    
			soot.Main.main(args.toArray(new String[args.size()]));

			Log.info("Done.");
		} catch (Exception e) {
			Log.error(e);
		} 
	}

		
	/**
	 * Fills a list with the standard command-line arguments needed by Soot
	 * 
	 * @param args
	 *            Command-line arguments
	 */
	protected void fillSootArgs(List<String> args) {
		args.add("-keep-line-number");
		args.add("-pp");		
		args.add("-output-format");
		args.add("none");
		args.add("-allow-phantom-refs");	
	}

	/**
	 * Fills a list with the standard JAR files needed by Soot
	 * 
	 * @param files
	 *            Standard JAR files needed by Soot
	 */
	protected void fillClassPath(List<File> files) {
		// add Runtime Library
		files.add(new File(new File(System.getProperty("java.home"), "lib"),
				"rt.jar"));

		// add Java Cryptography Extension Library
		files.add(new File(new File(System.getProperty("java.home"), "lib"),
				"jce.jar"));
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
	 * @param jarFiles
	 *            List of dependent JARs
	 */
	protected void extractClassPath(File file, List<File> jarFiles) {
		try {
			// open JAR file
			JarFile jarFile = new JarFile(file);

			// get manifest and their main attributes
			Manifest manifest = jarFile.getManifest();
			if (manifest==null) {
				jarFile.close();
				return;
			}
			Attributes mainAttributes = manifest.getMainAttributes();			
			if (mainAttributes==null) {
				jarFile.close();
				return;
			}
			String classPath = mainAttributes
					.getValue(Attributes.Name.CLASS_PATH);

			// close JAR file
			jarFile.close();

			// empty class path?
			if (null == classPath)
				return;

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
	}

	/**
	 * Enumerates all classes in a JAR file
	 * 
	 * @param file
	 *            JAR file object
	 * @param classes
	 *            List of classes
	 */
	protected void enumClasses(File file, List<String> classes) {
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
	}

}
