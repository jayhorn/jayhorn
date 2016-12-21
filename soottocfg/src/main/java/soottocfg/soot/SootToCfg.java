package soottocfg.soot;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Preconditions;

import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soottocfg.Options;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.CfgStubber;
import soottocfg.soot.memory_model.MemoryModel;
import soottocfg.soot.memory_model.NewMemoryModel;
import soottocfg.soot.memory_model.PushIdentifierAdder;
import soottocfg.soot.memory_model.PushPullSimplifier;
import soottocfg.soot.transformers.ArrayTransformer;
import soottocfg.soot.transformers.AssertionReconstruction;
import soottocfg.soot.transformers.CfgCallInliner;
import soottocfg.soot.transformers.ExceptionTransformer;
import soottocfg.soot.transformers.SpecClassTransformer;
import soottocfg.soot.transformers.StaticInitializerTransformer;
import soottocfg.soot.transformers.SwitchStatementRemover;
import soottocfg.soot.transformers.VirtualCallResolver;
import soottocfg.soot.util.DuplicatedCatchDetection;
import soottocfg.soot.util.FlowBasedPointsToAnalysis;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;
import soottocfg.soot.visitors.SootStmtSwitch;

/**
 * This is the main class for the translation. It first invokes Soot to load all
 * classes and perform points-to analysis and then translates them into
 * Boogie/Horn.
 * 
 * @author schaef
 * @author rodykers
 *
 */
public class SootToCfg {

	public enum MemModel {
		PullPush
	}

	private final List<String> resolvedClassNames;

	private final Set<SourceLocation> locations = new LinkedHashSet<SourceLocation>();

	// Create a new program
	private final Program program = new Program();

	private static FlowBasedPointsToAnalysis pta;

	public SootToCfg() {
		this(new ArrayList<String>());
		SootTranslationHelpers.initialize(program);
	}

	public SootToCfg(List<String> resolvedClassNames) {
		this.resolvedClassNames = resolvedClassNames;
		// first reset everything:
		soot.G.reset();
		SootTranslationHelpers.initialize(program);
	}

	/**
	 * Run Soot and translate classes into Boogie/Horn
	 * 
	 * @param input
	 *            class folder, jar file, or apk file
	 * @param classPath
	 *            class path, or platform jar folder for apk. see
	 *            https://github.com/Sable/android-platforms
	 */
	public void run(String input, String classPath) {
		// run soot to load all classes.
		SootRunner runner = new SootRunner();
		runner.run(input, classPath);

		/*
		 * Get a reference for the main method. We have to get the
		 * reference before applying the array transformation because
		 * this changes this signature of main.
		 */
		final SootMethod mainMethod = Scene.v().getMainMethod();		
		performBehaviorPreservingTransformations();
		performAbstractionTransformations();
//		Variable exceptionGlobal = this.program
//				.lookupGlobalVariable(SootTranslationHelpers.v().getExceptionGlobal().getName(), SootTranslationHelpers
//						.v().getMemoryModel().lookupType(SootTranslationHelpers.v().getExceptionGlobal().getType()));
//		program.setExceptionGlobal(exceptionGlobal);

		constructCfg();

		// now set the entry points.
		Method m = program.lookupMethod(mainMethod.getSignature());		
		program.setEntryPoint(m);
		m.isProgramEntryPoint(true);
		
		if (Options.v().outDir() != null) {
			writeFile(".cfg", program.toString());
		}

		// stub
		CfgStubber stubber = new CfgStubber();
		stubber.stubUnboundFieldsAndMethods(program);
		
		// inline method calls
		CfgCallInliner inliner = new CfgCallInliner(program);
		inliner.inlineFromMain(Options.v().getInlineMaxSize(), Options.v().getInlineCount());
		removeUnreachableMethods(program);
		
		if (program.getEntryPoint()==null) {
			System.err.println("WARNING: No entry point found in program!");
			SootTranslationHelpers.v().reset();
			return;
		}

		// alias analysis
		setPointsToAnalysis(new FlowBasedPointsToAnalysis());
		if (Options.v().memPrecision() >= Options.MEMPREC_PTA) {
			getPointsToAnalysis().run(program);
		}
		
		// simplify push-pull
		if (Options.v().memPrecision() >= Options.MEMPREC_SIMPLIFY) {
			PushPullSimplifier pps = new PushPullSimplifier();
			pps.simplify(program);
			if (Options.v().outDir() != null)
				writeFile(".simpl.cfg", program.toString());
		}
		
		// add push IDs
		PushIdentifierAdder pia = new PushIdentifierAdder();
		pia.addIDs(program);

		// print CFG
		if (Options.v().printCFG()) {
			System.out.println(program);
		}

		// reset all the soot stuff.
		SootTranslationHelpers.v().reset();
	}
	
	/**
	 * Like run, but only performs the behavior preserving transformations
	 * and does construct a CFG. This method is only needed to test the
	 * soundness of the transformation with randoop.
	 * 
	 * @param input
	 * @param classPath
	 */
	public void runPreservingTransformationOnly(String input, String classPath) {
		SootRunner runner = new SootRunner(this.resolvedClassNames);
		runner.run(input, classPath);
		performBehaviorPreservingTransformations();
		SootTranslationHelpers.v().reset();
	}

	public Program getProgram() {
		return program;
	}

	public Set<SourceLocation> getDuplicatedSourceLocations() {
		return locations;
	}

	private void constructCfg(SootClass sc) {
		SootTranslationHelpers.v().setCurrentClass(sc);
		for (SootMethod sm : sc.getMethods()) {
			if (sm.isConcrete()) {
				constructCfg(sm);
			}
		}
	}

	private void constructCfg(SootMethod sm) {
		if (sm.equals(SootTranslationHelpers.v().getAssertMethod())) {
			// Do not translate the assertion method.
			return;
		}
		SootTranslationHelpers.v().setCurrentMethod(sm);

		try {
			Body body = null;
			try {
				body = sm.retrieveActiveBody();
				performSootOptimizations(body);
			} catch (RuntimeException e) {
				// TODO: print warning that body couldn't be retrieved.
				return;
			}
			MethodInfo mi = new MethodInfo(body.getMethod(), SootTranslationHelpers.v().getCurrentSourceFileName());

			// pre-calculate when to pull/push
			MemoryModel mm = SootTranslationHelpers.v().getMemoryModel();
			if (mm instanceof NewMemoryModel) {
				((NewMemoryModel) mm).clearFieldToLocalMap();
			}

			// System.err.println(sm.getSignature()+"\n"+body);
			SootStmtSwitch ss = new SootStmtSwitch(body, mi);
			mi.setSource(ss.getEntryBlock());

			mi.finalizeAndAddToProgram();
		} catch (RuntimeException e) {
			System.err.println("Soot failed to parse " + sm.getSignature());
			e.printStackTrace(System.err);
			// return;
			throw e;
		}
	}

	private void constructCfg() {
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {
				if ((!sc.isJavaLibraryClass() && !sc.isLibraryClass())) {
					constructCfg(sc);
				}
			}
		}
	}

	private void performAbstractionTransformations() {		
		StaticInitializerTransformer sit = new StaticInitializerTransformer();
		sit.applyTransformation();

		ArrayTransformer atrans = new ArrayTransformer();
		atrans.applyTransformation();
		if (Options.v().useBuiltInSpecs()) {
			SpecClassTransformer spctrans = new SpecClassTransformer();
			spctrans.applyTransformation();
		}
	}

	/**
	 * Perform a sequence of behavior preserving transformations to the body
	 * of each method:
	 * - reconstruct Java asserts.
	 * - transform exceptional flow into regular flow.
	 * - transform switch statements into if-then-else statements.
	 * - de-virtualization.
	 */
	private void performBehaviorPreservingTransformations() {
		// add a field for the dynamic type of an object to each class.
//		SootTranslationHelpers.createTypeFields();
		
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			if (sc == SootTranslationHelpers.v().getAssertionClass()) {
				continue; // no need to process this guy.
			}

			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {
				SootTranslationHelpers.v().setCurrentClass(sc);
				for (SootMethod sm : sc.getMethods()) {
					if (sm.isConcrete()) {
						addDefaultInitializers(sm, sc);

						SootTranslationHelpers.v().setCurrentMethod(sm);

						Body body = sm.retrieveActiveBody();
						try {
							body.validate();
						} catch (soot.validation.ValidationException e) {
							System.out.println("Unable to validate method body. Possible NullPointerException?");
							e.printStackTrace();
						}

						try {
							// System.out.println(body);
							UnreachableCodeEliminator.v().transform(body);
							// detect duplicated finally blocks
							DuplicatedCatchDetection duplicatedUnits = new DuplicatedCatchDetection();
							Map<Unit, Set<Unit>> duplicatedFinallyUnits = duplicatedUnits
									.identifiedDuplicatedUnitsFromFinallyBlocks(body);
							for (Entry<Unit, Set<Unit>> entry : duplicatedFinallyUnits.entrySet()) {
								locations.add(SootTranslationHelpers.v().getSourceLocation(entry.getKey()));
								for (Unit u : entry.getValue()) {
									locations.add(SootTranslationHelpers.v().getSourceLocation(u));
								}
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
							throw new RuntimeException("Behavior preserving transformation failed " + sm.getSignature()
									+ " " + e.toString());
						}
					}
				}
			}
		}
		AssertionReconstruction ar = new AssertionReconstruction();
		ar.applyTransformation();
		ExceptionTransformer em = new ExceptionTransformer(Options.v().excAsAssert());
		em.applyTransformation();
		SwitchStatementRemover so = new SwitchStatementRemover();
		so.applyTransformation();
		if (Options.v().resolveVirtualCalls()) {
			VirtualCallResolver vc = new VirtualCallResolver();
			vc.applyTransformation();
		}		
	}

	 // apply some standard Soot optimizations
	private void performSootOptimizations(Body body) {
		 soot.jimple.toolkits.scalar.CopyPropagator.v().transform(body);
//		 soot.jimple.toolkits.scalar.UnreachableCodeEliminator.v().transform(body);
		 soot.jimple.toolkits.scalar.ConstantCastEliminator.v().transform(body);
		 soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder.v().transform(body);
		 soot.jimple.toolkits.scalar.DeadAssignmentEliminator.v().transform(body);
		 soot.jimple.toolkits.scalar.EmptySwitchEliminator.v().transform(body);
	}

	private void addDefaultInitializers(SootMethod constructor, SootClass containingClass) {
		if (constructor.isConstructor()) {
			Preconditions.checkArgument(constructor.getDeclaringClass().equals(containingClass));
			JimpleBody jbody = (JimpleBody)constructor.retrieveActiveBody();
			
			//TODO: use this guy in instead.
//			jbody.insertIdentityStmts();
			
			Set<SootField> instanceFields = new LinkedHashSet<SootField>();
			for (SootField f : containingClass.getFields()) {
				if (!f.isStatic()) {
					instanceFields.add(f);
				}
			}
			for (ValueBox vb : jbody.getDefBoxes()) {
				if (vb.getValue() instanceof InstanceFieldRef) {
					Value base = ((InstanceFieldRef) vb.getValue()).getBase();
					soot.Type baseType = base.getType();
					if (baseType instanceof RefType && ((RefType) baseType).getSootClass().equals(containingClass)) {
						// remove the fields that are initialized anyways from
						// our staticFields set.
						instanceFields.remove(((InstanceFieldRef) vb.getValue()).getField());
					}
				}
			}

			Unit insertPos = null;
			
			for (Unit u : jbody.getUnits()) {
				if (u instanceof IdentityStmt) {
					insertPos = u;
				} else {
					break; // insert after the last IdentityStmt
				}
			}
			for (SootField f : instanceFields) {
				Unit init;
//				if (SootTranslationHelpers.isDynamicTypeVar(f)) {
//					init = Jimple.v().newAssignStmt(
//							Jimple.v().newInstanceFieldRef(jbody.getThisLocal(), f.makeRef()),
//							SootTranslationHelpers.v().getClassConstant(RefType.v(containingClass)));
//				} else {
					init = Jimple.v().newAssignStmt(
							Jimple.v().newInstanceFieldRef(jbody.getThisLocal(), f.makeRef()),
							SootTranslationHelpers.v().getDefaultValue(f.getType()));
//				}
				if (insertPos==null) {
					jbody.getUnits().addFirst(init);	
				} else {
					jbody.getUnits().insertAfter(init, insertPos);
				}
			}

		}
	}

	private void writeFile(String extension, String text) {
		if (Options.v().outDir() == null)
			return;

		Path file = Paths.get(Options.v().outDir().toString() + Options.v().outBaseName() + extension);
		LinkedList<String> it = new LinkedList<String>();
		it.add(text);
		try {
			Files.createDirectories(Options.v().outDir());
			Files.write(file, it, Charset.forName("UTF-8"));
		} catch (Exception e) {
			System.err.println("Error writing file " + file);
		}
	}

	public static FlowBasedPointsToAnalysis getPointsToAnalysis() {
		return pta;
	}

	private static void setPointsToAnalysis(FlowBasedPointsToAnalysis pointsto) {
		pta = pointsto;
	}
	
	private void removeUnreachableMethods(Program program) {
		Set<Method> reachable = reachableMethod(program.getEntryPoint());
		Set<Method> toRemove = new HashSet<Method>();
		for (Method m : program.getMethods()) {
			if (!reachable.contains(m)) {
				toRemove.add(m);
			} 
		}
		program.removeMethods(toRemove);
	}
	
	private Set<Method> reachableMethod(Method main) {
		Set<Method> reachable = new HashSet<Method>();
		List<Method> todo = new LinkedList<Method>();
		todo.add(main);
		while (!todo.isEmpty()) {
			Method m = todo.remove(0);
			reachable.add(m);
			for (Method n : calledMethods(m)) {
				if (!reachable.contains(n) && !todo.contains(n)) {
					todo.add(n);
				}
			}
		}
		return reachable;
	}
	
	private List<Method> calledMethods(Method m) {
		List<Method> res = new LinkedList<Method>();
		for (CfgBlock b : m.vertexSet()) {
			for (Statement s : b.getStatements()) {
				if (s instanceof CallStatement) {
					CallStatement cs = (CallStatement) s;
					res.add(cs.getCallTarget());
				}
			}
		}
		return res;
	}
}
