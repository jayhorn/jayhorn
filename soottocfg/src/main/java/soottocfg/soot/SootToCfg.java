/**
 * 
 */
package soottocfg.soot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SourceLocator;
import soot.jimple.JasminClass;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.options.Options;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.util.JasminOutputStream;
import soottocfg.cfg.Program;
import soottocfg.cfg.method.Method;
import soottocfg.soot.transformers.AssertionReconstruction;
import soottocfg.soot.transformers.ExceptionTransformer;
import soottocfg.soot.transformers.SwitchStatementRemover;
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

	private boolean debug = true;

	private final boolean resolveVirtualCalls;
	private final boolean createAssertionsForUncaughtExceptions;

	// Create a new program
	private final Program program = new Program();

	public SootToCfg() {
		this(true, false);
	}

	public SootToCfg(boolean resolveVCalls, boolean excAsAssert) {
		resolveVirtualCalls = resolveVCalls;
		createAssertionsForUncaughtExceptions = excAsAssert;
		SootTranslationHelpers.v().setProgram(program);
	}

	private File classFileOutputDirectory = null;

	public void setOuputDirForTransformedClassFiles(File dir) {
		Preconditions.checkArgument(dir != null && dir.isDirectory());
		classFileOutputDirectory = dir;
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
		// init the helper classes for pre-processing
		AssertionReconstruction.v().initialize();

		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			processSootClass(sc);
		}

		// now set the entry points.
		for (SootMethod entryPoint : Scene.v().getEntryPoints()) {
			if (entryPoint.getDeclaringClass().isApplicationClass()) {
				// TODO: maybe we want to add all Main methods instead.
				program.addEntryPoint(program.loopupMethod(entryPoint.getSignature()));
			}
		}

		// reset all the soot stuff.
		SootTranslationHelpers.v().reset();
		// soot.G.reset();
	}

	public Program getProgram() {
		return program;
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
			if (classFileOutputDirectory!=null) {
				writeOutModifiedClass(sc);
			}
		}
	}

	/**
	 * Write out a class file into the folder given by classFileOutputDirectory.
	 * This is used to save the class files after we applied our transformations.
	 * Currently, this is only needed for unit testing.
	 * @param sClass
	 */
	private void writeOutModifiedClass(SootClass sClass) {
		sClass.validate();
		
		String currentName = SourceLocator.v().getFileNameFor(sClass, Options.output_format_class);
		
		StringBuilder sb = new StringBuilder();
		sb.append(classFileOutputDirectory.getAbsolutePath());
		sb.append(File.separator);		
		sb.append(sClass.getPackageName().replace(".", File.separator));
		sb.append(File.separator);
		sb.append(Files.getNameWithoutExtension(currentName));
		sb.append(".class");
		File modifiedClassFile = new File(sb.toString());
		if (!modifiedClassFile.getParentFile().mkdirs()) {
			throw new RuntimeException("Failed to create path to "+ sb.toString());
		}
		String fileName = modifiedClassFile.getAbsolutePath();
				
		// write the class to a file
		try (OutputStream streamOut = new JasminOutputStream(new FileOutputStream(fileName));
				PrintWriter writerOut = new PrintWriter(new OutputStreamWriter(streamOut, "UTF-8"));) {
			JasminClass jasminClass = new JasminClass(sClass);
			jasminClass.print(writerOut);
			writerOut.flush();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private void processSootMethod(SootMethod sm) {
		if (sm.isConcrete()) {
			// System.out.println("Processing method: " + sm);

//			if (!sm.getSignature().equals("<jayhorn.test.regression_tests.ProverTest: void test()>")) {
//				return;
//			}
			// System.err.println(sm.getSignature());

			SootTranslationHelpers.v().setCurrentMethod(sm);

			Body body = sm.retrieveActiveBody();
			processMethodBody(body);
		}
	}

	private void processMethodBody(Body body) {

//		System.err.println(body.toString());
		preProcessBody(body);
		// System.err.println(body.toString());

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
		// System.out.println(m.toString());
	}

	private void preProcessBody(Body body) {
		// pre-process the body
		
		// first reconstruct the assertions.
		AssertionReconstruction.v().removeAssertionRelatedNonsense(body);
		AssertionReconstruction.v().reconstructJavaAssertions(body);

		// make the exception handling explicit
		ExceptionTransformer em = new ExceptionTransformer(new NullnessAnalysis(new CompleteUnitGraph(body)),
				createAssertionsForUncaughtExceptions);
		em.transform(body);
		// replace all switches by sets of IfStmt
		SwitchStatementRemover so = new SwitchStatementRemover();
		so.transform(body);

		if (resolveVirtualCalls) {
//			VirtualCallResolver vc = new VirtualCallResolver();
//			vc.transform(body);
		}

	}
}
