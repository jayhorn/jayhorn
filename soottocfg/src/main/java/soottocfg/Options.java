package soottocfg;

import java.nio.file.Path;

import soottocfg.soot.SootToCfg.MemModel;

/**
 * @author schaef
 * @author rodykers
 *
 */
public class Options {

	public final static int MEMPREC_LOW = 0;
	public final static int MEMPREC_SIMPLIFY = 1;
	public final static int MEMPREC_LASTPUSH = 2;
	public final static int MEMPREC_PTA = 3;
	
	public boolean optimizeMethods = true;
	
	public boolean useAllocationSiteTupleElement = true;
	
	/*
	 * Built in specs.
	 */	
	private boolean useBuiltInSpecs = false;

	public boolean useBuiltInSpecs() {
		return useBuiltInSpecs;
	}

	public void setBuiltInSpecs(boolean val) {
		useBuiltInSpecs = val;
	}
	
	/*
	 * Memory model
	 */
	
	private MemModel mm = MemModel.PullPush;
	
	public MemModel memModel() {
		return mm;
	}
	
	public void setMemModel(MemModel mm) {
		this.mm = mm;
	}
	
	/*
	 * Use array invariants.
	 */
	
	private boolean arrayInv = true;
	
	public boolean arrayInv() { 
		return arrayInv;
	}
	
	public void setArrayInv(boolean b) {		
		this.arrayInv = b;
	}
	
	/*
	 * Number of array elements modeled exactly
	 */
	
	private int exactArrayElements = 0;
	
	public int exactArrayElements() { 
		return exactArrayElements;
	}
	
	public void setExactArrayElements(int ar) {		
		this.exactArrayElements = ar;
	}

	/*
	 * Precision of push-pull memory model.
	 */
	
	private int memPrecision = Options.MEMPREC_PTA;
	
	public int memPrecision() { 
		return memPrecision;
	}
	
	public void setMemPrecision(int prec) {		
		this.memPrecision = prec;
	}
	
	/*
	 * Resolve virtual calls
	 */
	
	private boolean resolveVirtualCalls = true;
	
	public boolean resolveVirtualCalls() { 
		return this.resolveVirtualCalls;
	}
	
	public void setResolveVirtualCalls(boolean r) {
		this.resolveVirtualCalls = r;
	}
	

	/* 
	 * Create Assertions For Uncaught Exceptions
	 */
	private boolean excAsAssert = false;
	
	public boolean excAsAssert() {
		return this.excAsAssert;
	}
	
	public void setExcAsAssert(boolean b) {
		this.excAsAssert = b;
	}

        /**
         * When encountering any Java feature that is not fully
         * supported yet, throw an exception rather than risking
         * a wrong answer.
         */
	
        private boolean strictlySound = false;

        public boolean strictlySound() {
            return strictlySound;
        }

        public void setStrictlySound(boolean b) {
            this.strictlySound = b;
        }

	/* 
	 * Print CFG
	 */
	private boolean printCFG = false;
	
	public boolean printCFG() {
		return this.printCFG;
	}
	
	public void setPrintCFG(boolean b) {
		this.printCFG = b;
	}
	
	/*
	 * Output files
	 */
	
	Path outDir = null;
	String outBaseName = "noname";
	
	public Path outDir() {
		return outDir;
	}
	
	public void setOutDir(Path dir) {
		this.outDir = dir;
	}
	
	public String outBaseName() {
		return this.outBaseName;
	}
	
	public void setOutBaseName(String s) {
		this.outBaseName = s;
	}
	
	private boolean checkMixedJavaClassFiles = true;
	
	
	public boolean checkMixedJavaClassFiles() {
		return checkMixedJavaClassFiles;
	}
	
	public void checkMixedJavaClassFiles(boolean val) {
		checkMixedJavaClassFiles = val;
	}
	
	private int inlineMaxSize = -1;

	/**
	 * @return the inlineMinSize
	 */
	public int getInlineMaxSize() {
		return inlineMaxSize;
	}

	/**
	 * @param inlineMinSize the inlineMinSize to set
	 */
	public void setInlineMaxSize(int inlineMaxSize) {
		this.inlineMaxSize = inlineMaxSize;
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
	}

	private int inlineCount = -1;

        public boolean printJimple = false;
    
	/*
	 * Singleton
	 */

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
