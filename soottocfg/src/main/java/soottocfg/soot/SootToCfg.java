package soottocfg.soot;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Preconditions;

import soot.Body;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StaticFieldRef;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soottocfg.Options;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.util.CfgStubber;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.memory_model.MemoryModel;
import soottocfg.soot.memory_model.NewMemoryModel;
import soottocfg.soot.memory_model.PushIdentifierAdder;
import soottocfg.soot.memory_model.PushPullSimplifier;
import soottocfg.soot.transformers.ArrayTransformer;
import soottocfg.soot.transformers.AssertionReconstruction;
import soottocfg.soot.transformers.ExceptionTransformer;
import soottocfg.soot.transformers.MethodStubber;
import soottocfg.soot.transformers.SpecClassTransformer;
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
	private boolean debug = false;
	
	private final Set<SourceLocation> locations = new HashSet<SourceLocation>();
	
	private Set<SootClass> stubbedLibClasses;

	// Create a new program
	private final Program program = new Program();

	public SootToCfg() {
		this(new ArrayList<String>());
	}

	public SootToCfg(List<String> resolvedClassNames) {
		this.resolvedClassNames = resolvedClassNames;
		// first reset everything:
		soot.G.reset();
		SootTranslationHelpers.v().reset();
		SootTranslationHelpers.v(program);
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
		performBehaviorPreservingTransformations();
		performAbstractionTransformations();
		Variable exceptionGlobal = this.program
				.lookupGlobalVariable(SootTranslationHelpers.v().getExceptionGlobal().getName(), SootTranslationHelpers
						.v().getMemoryModel().lookupType(SootTranslationHelpers.v().getExceptionGlobal().getType()));
		program.setExceptionGlobal(exceptionGlobal);

		// add havoc method for ints for lastpull
//		SootMethod havocSoot =
//				SootTranslationHelpers.v().getHavocMethod(soot.IntType.v());
//		SootTranslationHelpers.v().setCurrentMethod(havocSoot);
//		Method havoc =
//				SootTranslationHelpers.v().lookupOrCreateMethod(havocSoot);
		
		constructCfg();
		if (Options.v().outDir() != null) {
			writeFile(".cfg", program.toString());
		}
		
		CfgStubber stubber = new CfgStubber();
		stubber.stubUnboundFieldsAndMethods(program);
		
		if (program.getEntryPoints()==null || program.getEntryPoints().length==0) {
			System.err.println("WARNING: No entry point found in program!");
			SootTranslationHelpers.v().reset();
			return;
		}
		
		// alias analysis
		if (Options.v().memPrecision() >= 3) {
			FlowBasedPointsToAnalysis pta = new FlowBasedPointsToAnalysis();
			pta.run(program);
		}

		// simplify push-pull
		if (Options.v().memPrecision() >= 1) {
			PushPullSimplifier pps = new PushPullSimplifier();
			pps.simplify(program);
			if (Options.v().outDir() != null)
				writeFile(".simpl.cfg", program.toString());
		}

		// add push IDs
		if (Options.v().memPrecision() >= 2) {
			PushIdentifierAdder pia = new PushIdentifierAdder();
			pia.addIDs(program);
			if (Options.v().outDir() != null)
				writeFile("precise.cfg", program.toString());
		}
		
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
				// CopyPropagator.v().transform(body);
			} catch (RuntimeException e) {
				// TODO: print warning that body couldn't be retrieved.
				return;
			}
			MethodInfo mi = new MethodInfo(body.getMethod(),
					SootTranslationHelpers.v().getCurrentSourceFileName());

			// pre-calculate when to pull/push
			MemoryModel mm = SootTranslationHelpers.v().getMemoryModel();
			if (mm instanceof NewMemoryModel) {
				((NewMemoryModel) mm).clearFieldToLocalMap();
			}

			// System.err.println(sm.getSignature()+"\n"+body);
			SootStmtSwitch ss = new SootStmtSwitch(body, mi);
			mi.setSource(ss.getEntryBlock());

			mi.finalizeAndAddToProgram();
			Method m = mi.getMethod();

			if (debug) {
				// System.out.println("adding method: " +
				// m.getMethodName());
				getProgram().addEntryPoint(m);
			}
		} catch (RuntimeException e) {
			System.err.println("Soot failed to parse " + sm.getSignature());
			e.printStackTrace(System.err);
			// return;
			throw e;
		}
	}
	
	
	private void constructCfg() {
		System.out.println("Stubbed classes: " + this.stubbedLibClasses);
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {
				if ((!sc.isJavaLibraryClass() && !sc.isLibraryClass()) 
					|| (this.stubbedLibClasses.contains(sc) 
							// HACK this next condition should not be here, but otherwise &^@#&@$%$
							&& sc.getName().contains("java.lang.Integer")
							)
					) {
					constructCfg(sc);	
				}
			}
		}
		// now set the entry points.
		for (SootMethod entryPoint : Scene.v().getEntryPoints()) {
			if (entryPoint.getDeclaringClass().isApplicationClass()) {
				if (entryPoint.isStaticInitializer()) {
					// TODO hack? do not use static initializers as entry
					// points.
					continue;
				}
				Method m = program.loopupMethod(entryPoint.getSignature());
				if (m != null) {
					// System.out.println("Adding entry point " +
					// m.getMethodName());
					// TODO
					program.addEntryPoint(m);
				}
			}
		}
	}

	private void performAbstractionTransformations() {

		if (Options.v().memModel()==MemModel.PullPush) {
			MethodStubber mstubber = new MethodStubber();
			mstubber.applyTransformation();
			this.stubbedLibClasses = mstubber.getModifiedClasses();
		}

		ArrayTransformer atrans = new ArrayTransformer();
		atrans.applyTransformation();

		if (Options.v().useBuiltInSpecs()) {
			SpecClassTransformer spctrans = new SpecClassTransformer();
			spctrans.applyTransformation();
		}
	}
	
	private Set<SootMethod> staticInitializers = new HashSet<SootMethod>();

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
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			sc.addField(new SootField(SootTranslationHelpers.typeFieldName,
					RefType.v(Scene.v().getSootClass("java.lang.Class")), Modifier.PUBLIC | Modifier.FINAL));
		}

		for (SootClass sc : classes) {
			if (sc == SootTranslationHelpers.v().getAssertionClass()) {
				initializeStaticFields(sc);
				continue; // no need to process this guy.
			}

			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {

				initializeStaticFields(sc);
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
		addStaticInitializerCallsToMain();
	}

	private void addStaticInitializerCallsToMain() {
		SootMethod entry = null;
		for (SootMethod entryPoint : Scene.v().getEntryPoints()) {
			if (entryPoint.getDeclaringClass().isApplicationClass()) {
				if (entryPoint.isStaticInitializer()) {
					continue;
				}
				if (entry != null) {
					// System.err.println("Found more than one main. Not adding
					// static initializers to main :(");
					return;
				}
				entry = entryPoint;
			}
		}
		if (entry != null) {
			// System.out.println("Adding " + staticInitializers.size() + "
			// static init calls to " + entry.getSignature());
			for (SootMethod initializer : staticInitializers) {
				Unit initCall = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(initializer.makeRef()));
				JimpleBody jb = (JimpleBody) entry.getActiveBody();
				jb.getUnits().insertBefore(initCall, jb.getFirstNonIdentityStmt());
			}
		}
	}

	private void initializeStaticFields(SootClass containingClass) {
		// find all static fields of the class.
		Set<SootField> staticFields = new HashSet<SootField>();
		for (SootField f : containingClass.getFields()) {
			if (f.isStatic()) {
				staticFields.add(f);
			}
		}
		if (staticFields.isEmpty()) {
			return; // nothing to do.
		}

		SootMethod staticInit = null;
		for (SootMethod m : containingClass.getMethods()) {
			if (m.isStaticInitializer()) {
				staticInit = m;
				break;
			}
		}

		if (staticInit == null) {
			// TODO: super hacky!
			staticInit = new SootMethod(SootMethod.staticInitializerName, new LinkedList<soot.Type>(), VoidType.v(),
					Modifier.STATIC | Modifier.PUBLIC);
			JimpleBody body = Jimple.v().newBody(staticInit);
			body.getUnits().add(Jimple.v().newReturnVoidStmt());
			staticInit.setActiveBody(body);
			containingClass.addMethod(staticInit);
		}
		staticInitializers.add(staticInit);
		for (ValueBox vb : staticInit.retrieveActiveBody().getDefBoxes()) {
			if (vb.getValue() instanceof StaticFieldRef) {
				staticFields.remove(((StaticFieldRef) vb.getValue()).getField());
			}
		}

		for (SootField f : staticFields) {
			Unit init = Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(f.makeRef()),
					SootTranslationHelpers.v().getDefaultValue(f.getType()));
			staticInit.getActiveBody().getUnits().addFirst(init);
		}

	}

	private void addDefaultInitializers(SootMethod constructor, SootClass containingClass) {
		if (constructor.isConstructor()) {
			Preconditions.checkArgument(constructor.getDeclaringClass().equals(containingClass));
			Set<SootField> instanceFields = new HashSet<SootField>();
			for (SootField f : containingClass.getFields()) {
				if (!f.isStatic()) {
					instanceFields.add(f);
				}
			}
			for (ValueBox vb : constructor.retrieveActiveBody().getDefBoxes()) {
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
			for (Unit u : constructor.getActiveBody().getUnits()) {
				if (u instanceof IdentityStmt) {
					insertPos = u;
				} else {
					break; // insert after the last IdentityStmt
				}
			}
			for (SootField f : instanceFields) {
				Unit init;
				if (f.getName().contains(SootTranslationHelpers.typeFieldName)) {
					init = Jimple.v().newAssignStmt(
							Jimple.v().newInstanceFieldRef(constructor.getActiveBody().getThisLocal(), f.makeRef()),
							SootTranslationHelpers.v().getClassConstant(RefType.v(containingClass)));
				} else {
					init = Jimple.v().newAssignStmt(
							Jimple.v().newInstanceFieldRef(constructor.getActiveBody().getThisLocal(), f.makeRef()),
							SootTranslationHelpers.v().getDefaultValue(f.getType()));
				}
				constructor.getActiveBody().getUnits().insertAfter(init, insertPos);
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

}
