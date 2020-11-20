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

import java.io.File;
import java.util.Arrays;
import java.util.List;
import jayhorn.hornify.encoder.StringEncoder;

import org.kohsuke.args4j.Option;

/**
 * Options
 * 
 * @author schaef
 */
public class Options {

	// @Option(name = "-android-jars", usage = "Path to the jars that stub the
	// android platform.")
	// private String androidStubPath=null;
	//
	// public String getAndroidStubPath() {
	// return androidStubPath;
	// }
	//
	// public void setAndroidStubPath(String path) {
	// this.androidStubPath = path;
	// }
	//
	// /**
	// * JAR file
	// */
	@Option(name = "-version", usage = "Print version", required = false)
	public boolean version = false;

	@Option(name = "-checker", usage = "Select a checker [inconsistency, or safety]", required = false)
	// //@Option(name = "-checker", usage = "Select a checker [safety]",
	// required = false)
	private String checker = "safety";

	public String getChecker() {
		return checker;
	}

	@Option(name = "-solver", usage = "Select a solver [eldarica or spacer]", required = false)
	private String solver = "eldarica";

	public String getSolver() {
		return solver;
	}
	

	@Option(name = "-solver-options", usage = "Options for the solver [eldarica: abstract, abstractPO, debug]", required = false)
	private String solverOptions = "";

	public List<String> getSolverOptions() {
		return Arrays.asList(solverOptions.split(","));
	}
	
	public void setSolverOptions(String so) {
		solverOptions = so;
	}

	@Option(name = "-string-encoding", usage = "Sets the string encoding method")
	private StringEncoder.StringEncoding stringEncoding = StringEncoder.StringEncoding.recursive;

	public StringEncoder.StringEncoding getStringEncoding() {
		return stringEncoding;
	}

	public void setStringEncoding(StringEncoder.StringEncoding stringEncoding) {
		this.stringEncoding = stringEncoding;
	}


	@Option(name = "-string-direction", usage = "Sets the string ADT direction")
	private StringEncoder.StringDirection stringDirection = StringEncoder.StringDirection.ltr;

	public StringEncoder.StringDirection getStringDirection() {
		return stringDirection;
	}

	public void setStringDirection(StringEncoder.StringDirection stringDirection) {
		this.stringDirection = stringDirection;
	}

	/**
	 * JAR file
	 */
	@Option(name = "-j", usage = "JAR file, class folder, or apk", required = true)
	private String javaInput;

	public String getJavaInput() {
		return this.javaInput;
	}
	
	public void setJavaInput(String input) {
		this.javaInput = input;
	}

	// /**
	// * Print Horn clauses
	// */
    @Option(name = "-print-horn", usage = "Print horn clauses", required = false, hidden=true)
	private boolean printHorn = false;

	public boolean getPrintHorn() {
		return this.printHorn;
	}

	public void setPrintHorn(boolean b) {
		this.printHorn = b;
	}
	
	@Option(name = "-verbose", usage = "Bla bla bla", required = false)
	public boolean verbose = false;
	
	
	@Option(name = "-cfg", usage = "Print CFG", required = false)
	public boolean printCFG = false;

	
	@Option(name = "-specs", usage = "Use built-in specs", required = false)
	public boolean useSpecs = false;
	
	
	@Option(name = "-stats", usage = "Generate Stats", required = false)
	public boolean stats = false;
	
	@Option(name = "-solution", usage = "Output full solution or counter-example", required = false)
	public boolean solution = false;

	@Option(name = "-dot-cex", usage = "Output counter-examples in GraphViz format", required = false)
	public boolean dotCEX = false;
	
    @Option(name = "-full-cex", usage = "Output full internal counter-examples in textual format", required = false, depends = {"-solution"})
	public boolean fullCEX = false;
	
	@Option(name = "-trace", usage = "Output counterexample traces", required = false, depends = {"-solution"})
	public boolean trace = false;
	
	@Option(name = "-cid", usage = "Insert call IDs variables to track calling context into pull and push statements", required = false, hidden=true)
	public boolean useCallIDs = false;

    @Option(name = "-jimple", usage = "Prints the jimple of the bytecode", required = false, hidden=true)
        public boolean printJimple = false;

	
	/*
	 * Memory precision
	 */
	@Option(name = "-mem-prec", usage = "Precision of memory model", required = false)
	private int memPrecision = soottocfg.Options.MEMPREC_PTA;
	
	/*
	 * Disable array invariants
	 */
	@Option(name = "-disable-array-inv", usage = "Disable array invariants", required = false, hidden=true)
	private boolean disableArrayInv = false;

	
	/*
	 * Exact array elements
	 */
	@Option(name = "-array-exact", usage = "Number of exactly modeled array elements", required = false, hidden=true)
	private int exactArrayElements = 0;

	// /**
	// * Output intermediate representations
	// */
	@Option(name = "-out", usage = "Output directory for Horn clauses", required = false)
	private String out = null;

	public String getOut() {
		return this.out;
	}

	public String getOutDir() {
		if (this.out != null && !this.out.endsWith("/"))
			return this.out + "/";
		return this.out;
	}

	public String getOutBasename() {
		String outName = "";
		String in = getJavaInput();
		if (in != null) {
			if (in.endsWith(File.separator))
				in = in.substring(0, in.length() - 1);
			outName = in.substring(in.lastIndexOf(File.separator) + 1, in.length()).replace(".java", "").replace(".class", "");
		}
		if (outName.equals(""))
			outName = "noname";
		return outName;
	}

	public void setOut(String s) {
		this.out = s;
	}

	/*
	 * Memory precision
	 */
	@Option(name = "-inline-size", usage = "Inline everything with less than N stmts", required = false)
	private int inlineMaxSize = -1;

	public int getInlineMaxSize() {
		return inlineMaxSize;
	}

	public void setInlineMaxSize(int inlineMaxSize) {
		this.inlineMaxSize = inlineMaxSize;
		soottocfg.Options.v().setInlineMaxSize(inlineMaxSize);
	}

        /**
         * When encountering any Java feature that is not fully
         * supported yet, throw an exception rather than risking
         * a wrong answer.
         */
	
        @Option(name = "-conservative", usage = "Exit when encountering a Java feature that is not fully supported", required = false)
        private boolean strictlySound = false;

        public boolean strictlySound() {
            return strictlySound;
        }

        public void setStrictlySound(boolean b) {
            this.strictlySound = b;
            soottocfg.Options.v().setStrictlySound(b);
        }

	/**
	 * @return the inlineCount
	 */
	public int getInlineCount() {
		return inlineCount;
	}

	/**
	 * @param inlineCount the inlineCount to set
	 */
	public void setInlineCount(int inlineCount) {
		this.inlineCount = inlineCount;
		soottocfg.Options.v().setInlineCount(inlineCount);
	}

	@Option(name = "-inline-count", usage = "Inline everything that's called less than N times", required = false)
	private int inlineCount = -1;

	
	/**
	 * Classpath
	 */
	@Option(name = "-cp", usage = "Classpath")
	private String classpath;

	@Option(name = "-t", usage = "Timeout per procedure in seconds. Use 0 for no timeout. (Default is 0)")
	private int timeout = 0;

	public int getTimeout() {
		return this.timeout;
	}

	public void setTimeout(int seconds) {
		this.timeout = seconds;
	}

	
	@Option(name = "-rta", usage = "Automatically inserts runtime assertions for Null deref, array bounds, and illegal casts.")
	private boolean insertRuntimeAssertions = false;

//	@Option(name = "-callid", usage = "Pass id of caller statement as argument to method")
//	private boolean passCallerID = soottocfg.Options.v().passCallerIdIntoMethods();
	
	
	/*
	 * Check Heap Limit 
	 */
	@Option(name = "-heap-limit", usage = "Max Heap allocation (for Spacer)", required = false)
	private int heapLimit = -1;
	
	public int getHeapLimit(){
		return heapLimit;
	}

    public static enum HeapMode {
        // only verify with unbounded heap encoding
        unbounded,
        // only verify with bounded heap encoding
        bounded,
        // use a combination of unbounded and bounded heap encoding
        auto
    }

    @Option(name = "-heap-mode", usage = "Choose the heap encoding", required = false)
    private HeapMode heapMode = HeapMode.auto;
	public HeapMode getHeapMode() {
	    return heapMode;
    }

    public void setHeapMode(HeapMode m) {
        heapMode = m;
    }

    @Option(name = "-bounded-heap-size", usage = "Verify up to a bounded heap size", required = false)
    private int boundedHeapSize = 10;
	public int getBoundedHeapSize() {
	    return boundedHeapSize;
    }

    public void setBoundedHeapSize(int s) {
        boundedHeapSize = s;
    }

    @Option(name = "-initial-heap-size", usage = "Start value for -bounded-heap-size", required = false)
    private int initialHeapSize = 1;
    public int getInitialHeapSize() {
        return initialHeapSize;
    }

    public void setInitialHeapSize(int s) {
        initialHeapSize = s;
    }

    @Option(name = "-step-heap-size", usage = "Step size for -bounded-heap-size", required = false)
    private int stepHeapSize = 1;
    public int getStepHeapSize() {
        return stepHeapSize;
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

	public void updateSootToCfgOptions() {
//		soottocfg.Options.v().passCallerIdIntoMethods(passCallerID);
		soottocfg.Options.v().setExcAsAssert(insertRuntimeAssertions);
		soottocfg.Options.v().setMemPrecision(memPrecision);
//		soottocfg.Options.v().setPrintCFG(printCFG);
		soottocfg.Options.v().setInlineMaxSize(inlineMaxSize);
		soottocfg.Options.v().setInlineCount(inlineCount);
		soottocfg.Options.v().setStrictlySound(strictlySound);
		soottocfg.Options.v().setArrayInv(!disableArrayInv);
		soottocfg.Options.v().setExactArrayElements(exactArrayElements);
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
