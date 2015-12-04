/*
 * jimple2boogie - Translates Jimple (or Java) Programs to Boogie
 * Copyright (C) 2013 Martin Schaeaeaeaeaeaeaeaeaef and Stephan Arlt
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

package jayhorn;

import org.kohsuke.args4j.Option;

import soottocfg.soot.SootRunner.CallgraphAlgorithm;

/**
 * Options
 * 
 * @author schaef, schaef
 */
public class Options {

	
	@Option(name = "-android-jars", usage = "Path to the jars that stub the android platform.")
	private String androidStubPath=null;
	
	public String getAndroidStubPath() {
		return androidStubPath;
	}

	public void setAndroidStubPath(String path) {
		this.androidStubPath = path;
	}
	
	/**
	 * JAR file
	 */
	@Option(name = "-checker", usage = "Select a checker [inconsistency, or safety]", required = false)
	private String checker = "safety";
	public String getChecker() {
		return checker;
	}
	
	
	/**
	 * JAR file
	 */
	@Option(name = "-j", usage = "JAR file, class folder, or apk", required = false)
	private String javaInput;
	
	public String getJavaInput() {
		return this.javaInput;
	}

	@Option(name = "-cg", usage = "Set the callgraph algorithm: CHA,RTA,VTA,SPARK, or None (default).", required = false)
	private String callGraphAlgorithm = "None";
	public CallgraphAlgorithm getCallGraphAlgorithm() {
		if (callGraphAlgorithm==null || callGraphAlgorithm.toLowerCase().equals("none")) {
			return CallgraphAlgorithm.None;
		} else if (callGraphAlgorithm.toLowerCase().equals("cha")) {
			return CallgraphAlgorithm.CHA;
		} else if (callGraphAlgorithm.toLowerCase().equals("rta")) {
			return CallgraphAlgorithm.RTA;
		} else if (callGraphAlgorithm.toLowerCase().equals("vta")) {
			return CallgraphAlgorithm.VTA;
		} else if (callGraphAlgorithm.toLowerCase().equals("spark")) {
			return CallgraphAlgorithm.SPARK;			
		} else {
			Log.error("Unknown callgraph algorithm "+callGraphAlgorithm+". Using none instead");
		}
		return CallgraphAlgorithm.None;
	}
	
	public void setCallGraphAlgorithm(String s) {
		this.callGraphAlgorithm = s;
	}
	
	

	/**
	 * Scope
	 */
	@Option(name = "--scope", usage = "Only analyze classes/methods that contain the scope string")
	private String scope = null;
	public String getScope() {
		return scope;
	}
	public boolean hasScope() {
		return scope == null || scope.isEmpty();
	}

	/**
	 * Classpath
	 */
	@Option(name = "-cp", usage = "Classpath")
	private String classpath;

	@Option(name = "-t", usage = "Timeout per procedure in seconds. Use 0 for no timeout. (Default is 0)")
	private int timeout=0;
	
	public int getTimeout() {
		return this.timeout;
	}


	/**
	 * Determines, whether Joogie has an additional classpath
	 * 
	 * @return true = Joogie has an additional classpath
	 */
	public boolean hasClasspath() {
		return (null != classpath);
	}

	/**
	 * Returns the additional classpath
	 * 
	 * @return Additional classpath
	 */
	public String getClasspath() {
		return classpath;
	}

	/**
	 * Assigns the additional classpath
	 * 
	 * @param classpath
	 *            Additional classpath
	 */
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	

	
	private static Options options;

	public static void resetInstance() {
		options = null;	
	}
		
	public static Options v() {
		if (null == options) {
			options = new Options();
		}
		return options;
	}

	private Options() {
	}

}
