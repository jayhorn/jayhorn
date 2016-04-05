/**
 * 
 */
package soottocfg.soot;

import com.google.common.base.Preconditions;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.annotation.nullcheck.NullnessAnalysis;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.toolkits.graph.CompleteUnitGraph;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.method.Method;
import soottocfg.soot.transformers.ArrayAbstraction;
import soottocfg.soot.transformers.AssertionReconstruction;
import soottocfg.soot.transformers.ExceptionTransformer;
import soottocfg.soot.transformers.SwitchStatementRemover;
import soottocfg.soot.transformers.VirtualCallResolver;
import soottocfg.soot.util.DuplicatedCatchDetection;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;
import soottocfg.soot.visitors.SootStmtSwitch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

	private final List<String> resolvedClassNames;
	private boolean debug = false;

	private final boolean resolveVirtualCalls;
	private final boolean createAssertionsForUncaughtExceptions;

	private final Set<SourceLocation> locations = new HashSet<SourceLocation>();

	// Create a new program
	private final Program program = new Program();

	public SootToCfg() {
		this(true, false);
	}

	public SootToCfg(List<String> resolvedClassNames) {
		this(true, false, MemModel.PackUnpack, resolvedClassNames);
	}

	public SootToCfg(boolean resolveVCalls, boolean excAsAssert) {
		this(resolveVCalls, excAsAssert, MemModel.PackUnpack);
	}

	public SootToCfg(boolean resolveVCalls, boolean excAsAssert, MemModel memModel) {
		this(resolveVCalls, excAsAssert, memModel, new ArrayList<String>());
	}

	public SootToCfg(boolean resolveVCalls, boolean excAsAssert, MemModel memModel, List<String> resolvedClassNames) {
		this.resolvedClassNames = resolvedClassNames;
		// first reset everything:
		soot.G.reset();
		SootTranslationHelpers.v().reset();
		resolveVirtualCalls = resolveVCalls;
		createAssertionsForUncaughtExceptions = excAsAssert;

		SootTranslationHelpers.v(program, memModel);
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

		constructCfg();
		// reset all the soot stuff.
		SootTranslationHelpers.v().reset();
	}

	/**
	 * Run the transformation on an existing Scene and only generate the CFG
	 * for a single class sc.
	 * 
	 * @param sc
	 */
	public void runForSingleClass(SootClass sc) {
		SootRunner.createAssertionClass();
		performBehaviorPreservingTransformations();
		performAbstractionTransformations();
		constructCfg(sc);
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
				SootTranslationHelpers.v().setCurrentMethod(sm);
				try {
					Body body = sm.retrieveActiveBody();
					System.out.println(body);
					MethodInfo mi = new MethodInfo(body.getMethod(),
							SootTranslationHelpers.v().getCurrentSourceFileName());
					SootStmtSwitch ss = new SootStmtSwitch(body, mi);
					mi.setSource(ss.getEntryBlock());

					mi.finalizeAndAddToProgram();
					Method m = mi.getMethod();
					System.err.println(m);
					if (debug) {
						// System.out.println("adding method: " +
						// m.getMethodName());
						getProgram().addEntryPoint(m);
					}
				} catch (RuntimeException e) {
					System.err.println("Soot failed to parse " + sm.getSignature());
					e.printStackTrace();
					return;
				}
			}
		}

	}

	private void constructCfg() {
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			if (sc == SootTranslationHelpers.v().getAssertionClass()) {
				continue; // no need to process this guy.
			}
			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {
				constructCfg(sc);
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
					System.out.println("Adding entry point " + m.getMethodName());
					// TODO
					program.addEntryPoint(m);
				}
			}
		}
	}

	private void performAbstractionTransformations() {
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			if (sc == SootTranslationHelpers.v().getAssertionClass()) {
				continue; // no need to process this guy.
			}
			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {
				SootTranslationHelpers.v().setCurrentClass(sc);
				for (SootMethod sm : sc.getMethods()) {
					if (sm.isConcrete()) {
						SootTranslationHelpers.v().setCurrentMethod(sm);
						try {
							Body body = sm.retrieveActiveBody();
							ArrayAbstraction abstraction = new ArrayAbstraction();
							abstraction.transform(body);
						} catch (RuntimeException e) {
							System.err.println("Soot failed to parse " + sm.getSignature());
							return;
						}
					}
				}
			}
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
		Variable exceptionGlobal = this.program
				.lookupGlobalVariable(SootTranslationHelpers.v().getExceptionGlobal().getName(), SootTranslationHelpers
						.v().getMemoryModel().lookupType(SootTranslationHelpers.v().getExceptionGlobal().getType()));
		program.setExceptionGlobal(exceptionGlobal);
		// add a field for the dynamic type of an object to each class.
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		for (SootClass sc : classes) {
			sc.addField(new SootField(SootTranslationHelpers.typeFieldName,
					RefType.v(Scene.v().getSootClass("java.lang.Class"))));
		}

		for (SootClass sc : classes) {
			if (sc == SootTranslationHelpers.v().getAssertionClass()) {
				continue; // no need to process this guy.
			}

			if (sc.resolvingLevel() >= SootClass.SIGNATURES && sc.isApplicationClass()) {

				initializeStaticFields(sc);

				SootTranslationHelpers.v().setCurrentClass(sc);
				for (SootMethod sm : sc.getMethods()) {
					if (sm.isConcrete()) {
						addDefaultInitializers(sm, sc);

						SootTranslationHelpers.v().setCurrentMethod(sm);
						try {
							Body body = sm.retrieveActiveBody();
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
							
							// first reconstruct the assertions.
							AssertionReconstruction ar = new AssertionReconstruction();
							ar.transform(body);

							// make the exception handling explicit
							ExceptionTransformer em = new ExceptionTransformer(
									new NullnessAnalysis(new CompleteUnitGraph(body)),
									createAssertionsForUncaughtExceptions);
							em.transform(body);

							// replace all switches by sets of IfStmt
							SwitchStatementRemover so = new SwitchStatementRemover();
							so.transform(body);

							if (resolveVirtualCalls) {
								VirtualCallResolver vc = new VirtualCallResolver();
								vc.transform(body);
							}
						} catch (RuntimeException e) {
							e.printStackTrace();
							throw new RuntimeException(
									"Soot failed to parse " + sm.getSignature() + " " + e.toString());
						}
					}
				}
			}
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
					System.err.println("Found more than one main. Not adding static initializers to main :(");
					return;
				}
				entry = entryPoint;
			}
		}
		if (entry != null) {
			System.out.println("Adding " + staticInitializers.size() + " static init calls to " + entry.getSignature());
			for (SootMethod initializer : staticInitializers) {
				Unit initCall = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(initializer.makeRef()));
				entry.getActiveBody().getUnits().addFirst(initCall);
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
					getDefaultValue(f.getType()));
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
							getDefaultValue(f.getType()));
				}
				constructor.getActiveBody().getUnits().insertAfter(init, insertPos);
			}

		}
	}

	private Value getDefaultValue(soot.Type t) {
		Value rhs = null;
		if (t instanceof PrimType) {
			if (t instanceof soot.BooleanType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.ByteType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.CharType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.DoubleType) {
				rhs = DoubleConstant.v(0);
			} else if (t instanceof soot.FloatType) {
				rhs = FloatConstant.v(0);
			} else if (t instanceof soot.IntType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.LongType) {
				rhs = LongConstant.v(0);
			} else if (t instanceof soot.ShortType) {
				rhs = IntConstant.v(0);
			} else {
				throw new RuntimeException("Unknown type " + t);
			}
		} else {
			rhs = NullConstant.v();
		}
		return rhs;
	}
}
