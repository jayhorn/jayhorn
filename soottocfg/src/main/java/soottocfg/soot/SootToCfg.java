/**
 * 
 */
package soottocfg.soot;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.toolkits.graph.CompleteUnitGraph;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.method.Method;
import soottocfg.soot.transformers.AssertionReconstruction;
import soottocfg.soot.transformers.ExceptionTransformer;
import soottocfg.soot.transformers.SwitchStatementRemover;
import soottocfg.soot.transformers.VirtualCallResolver;
import soottocfg.soot.util.DuplicatedCatchDetection;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;
import soottocfg.soot.visitors.SootStmtSwitch;

/**
 * This is the main class for the translation. It first invokes Soot to load all
 * classes and perform points-to analysis and then translates them into
 * Boogie/Horn.
 * 
 * @author schaef
 *
 */
public class SootToCfg {

	public enum MemModel {
		BurstallBornat, PackUnpack
	}

	private boolean debug = true;

	private final boolean resolveVirtualCalls;
	private final boolean createAssertionsForUncaughtExceptions;

	private final Set<SourceLocation> locations = new HashSet<SourceLocation>();

	// Create a new program
	private final Program program = new Program();

	public SootToCfg() {
		this(true, false);
	}

	public SootToCfg(boolean resolveVCalls, boolean excAsAssert) {
		this(resolveVCalls, excAsAssert, MemModel.PackUnpack);
	}

	public SootToCfg(boolean resolveVCalls, boolean excAsAssert, MemModel memModel) {
		// first reset everything:
		soot.G.reset();
		SootTranslationHelpers.v().reset();
		SootTranslationHelpers.v().setMemoryModelKind(memModel);

		resolveVirtualCalls = resolveVCalls;
		createAssertionsForUncaughtExceptions = excAsAssert;

		SootTranslationHelpers.v().setProgram(program);
	}

	/**
	 * Run Soot and translate classes into Boogie/Horn
	 * 
	 * @param input
	 *            class folder, jar file, or apk file
	 * @param classPath
	 *            class path, or platform jar folder for apk. see
	 *            https://github.com/Sable/android-platforms
	 * @param cfg
	 */
	public void run(String input, String classPath) {
		// run soot to load all classes.
		SootRunner runner = new SootRunner();
		runner.run(input, classPath);

		SootTranslationHelpers.v().getExceptionGlobalRef();

		// TODO, hacky way to get the exceptionGlobal into the program.
		Variable exceptionGlobal = this.program
				.lookupGlobalVariable(SootTranslationHelpers.v().getExceptionGlobal().getName(), SootTranslationHelpers
						.v().getMemoryModel().lookupType(SootTranslationHelpers.v().getExceptionGlobal().getType()));
		program.setExceptionGlobal(exceptionGlobal);
		//TODO move this to a better location		
		for (SootClass sc : new LinkedList<SootClass>(Scene.v().getClasses())) {			
			sc.addField(new SootField(SootTranslationHelpers.typeFieldName, RefType.v(Scene.v().getSootClass("java.lang.Class")) ));			
		}
		
		
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			if (sc == SootTranslationHelpers.v().getAssertionClass()) {
				// no need to process this guy.
				continue;
			}
			processSootClass(sc);
		}

		// now set the entry points.
		for (SootMethod entryPoint : Scene.v().getEntryPoints()) {
			if (entryPoint.getDeclaringClass().isApplicationClass()) {
				Method m = program.loopupMethod(entryPoint.getSignature());
				if (m != null) {
					program.addEntryPoint(m);
				}
			}
		}

		// reset all the soot stuff.
		SootTranslationHelpers.v().reset();
		// soot.G.reset();
	}

	public Program getProgram() {
		return program;
	}

	public Set<SourceLocation> getDuplicatedSourceLocations() {
		return locations;
	}

	/**
	 * Analyze a single SootClass and transform all its Methods
	 * 
	 * @param sc
	 */
	private void processSootClass(SootClass sc) {
		if (sc.resolvingLevel() < SootClass.SIGNATURES) {
			return;
		}

		if (sc.isApplicationClass()) {
			// Log.info("Class " + sc.getName() + " " + sc.resolvingLevel());

			SootTranslationHelpers.v().setCurrentClass(sc);

			for (SootMethod sm : sc.getMethods()) {
				processSootMethod(sm);
			}
		}
	}

	private void processSootMethod(SootMethod sm) {
		if (sm.isConcrete()) {
			// System.out.println("Processing method: " + sm);

//			 if
//			 (!sm.getSignature().equals("<org.apache.tools.ant.taskdefs.optional.pvcs.Pvcs: void execute()>")) {
//			 return;
//			 }
			// System.err.println(sm.getSignature());

			SootTranslationHelpers.v().setCurrentMethod(sm);

			Body body = null;
			try {
				body = sm.retrieveActiveBody();
			} catch (RuntimeException e) {
				System.err.println("Soot failed to parse " + sm.getSignature());
				return;
			}
			processMethodBody(body);
		}
	}

	private void processMethodBody(Body body) {
		System.err.println(body);
		// StringBuilder sb = new StringBuilder();
		// for (Unit u : body.getUnits()) {
		// sb.append(u.getJavaSourceStartLineNumber());
		// sb.append(": ");
		// sb.append(u);
		// sb.append("\n");
		// }
		// System.err.println(sb.toString());

		preProcessBody(body);

		// generate the CFG structures on the processed body.
		MethodInfo mi = new MethodInfo(body.getMethod(), SootTranslationHelpers.v().getCurrentSourceFileName());
		SootStmtSwitch ss = new SootStmtSwitch(body, mi);
		mi.setSource(ss.getEntryBlock());

		mi.finalizeAndAddToProgram();
		Method m = mi.getMethod();

		if (debug) {
			// System.out.println("adding method: " + m.getMethodName());
			getProgram().addEntryPoint(m);
		}
		 System.out.println(m.toString());
	}

	private void preProcessBody(Body body) {
		// pre-process the body

		UnreachableCodeEliminator.v().transform(body);

		// detect duplicated finally blocks
		DuplicatedCatchDetection duplicatedUnits = new DuplicatedCatchDetection();
		Map<Unit, Set<Unit>> duplicatedFinallyUnits = duplicatedUnits.identifiedDuplicatedUnitsFromFinallyBlocks(body);
		for (Entry<Unit, Set<Unit>> entry : duplicatedFinallyUnits.entrySet()) {
			locations.add(SootTranslationHelpers.v().getSourceLocation(entry.getKey()));
			for (Unit u : entry.getValue()) {
				locations.add(SootTranslationHelpers.v().getSourceLocation(u));
			}
		}

		// first reconstruct the assertions.
		AssertionReconstruction ar = new AssertionReconstruction();
		ar.transform(body);

		// make the exception handling explicit
		ExceptionTransformer em = new ExceptionTransformer(new NullnessAnalysis(new CompleteUnitGraph(body)),
				createAssertionsForUncaughtExceptions);
		em.transform(body);
		// replace all switches by sets of IfStmt
		SwitchStatementRemover so = new SwitchStatementRemover();
		so.transform(body);

		if (resolveVirtualCalls) {
			VirtualCallResolver vc = new VirtualCallResolver();
			vc.transform(body);
		}
		
	}
}
