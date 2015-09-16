/**
 * 
 */
package jayhorn.soot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jayhorn.cfg.method.CfgBlock;
import jayhorn.soot.util.MethodInfo;
import jayhorn.soot.visitors.SootStmtSwitch;
import jayhorn.util.Log;
import soot.ArrayType;
import soot.BooleanType;
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.TrapManager;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.ThrowStmt;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.shimple.ShimpleFactory;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;

/**
 * This is the main class for the translation. It first invokes Soot to load all
 * classes and perform points-to analysis and then translates them into
 * Boogie/Horn.
 * 
 * @author schaef
 *
 */
public class SootToCfg {

	/**
	 * Run Soot and translate classes into Boogie/Horn
	 * 
	 * @param input
	 */
	public void run(String input) {

		// run soot to load all classes.
		SootRunner runner = new SootRunner();
		runner.run(input);

		createAssertionHelper();
		
		for (SootClass sc : Scene.v().getClasses()) {
			processSootClass(sc);
		}

	}

	public static final String assertionClassName = "JayHornAssertions";
	public static final String assertionProcedureName = "super_crazy_assertion";
	public static SootMethod internalAssertMethod = null;

	/**
	 * Helper procedure that creates a static method to represent Java
	 * assertions.
	 */
	private void createAssertionHelper() {
		if (this.internalAssertMethod==null) {
			SootClass sClass = new SootClass(assertionClassName, Modifier.PUBLIC);			
			internalAssertMethod = new SootMethod(assertionProcedureName,                 
				    Arrays.asList(new Type[] {ArrayType.v(BooleanType.v(), 1)}),
				    VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);			
			sClass.addMethod(internalAssertMethod);
			
			Scene.v().addClass(sClass);
		}
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
			Log.info("Class " + sc.getName() + "  " + sc.resolvingLevel());
			for (SootMethod sm : sc.getMethods()) {
				processSootMethod(sm);
			}
		}

	}

	private void processSootMethod(SootMethod sm) {
		if (sm.isConcrete()) {
			Log.info("\t" + sm.getName());
			processMethodBody(Shimple.v().newBody(sm.retrieveActiveBody()));
		}
	}

	private void processMethodBody(ShimpleBody body) {
		MethodInfo mi = new MethodInfo(body.getMethod());
		
		reconstructJavaAssertions(body);
		
		ShimpleFactory sf = soot.G.v().shimpleFactory;
		sf.setBody(body);
		BlockGraph bg = sf.getBlockGraph();
		Map<Block, SootStmtSwitch> subGraphs = new HashMap<Block, SootStmtSwitch>();
				
		for (Block b : bg.getBlocks()) {
			// first collect the traps and
			// exceptional successors for the block
			Set<Trap> traps = new HashSet<Trap>();
			Set<Block> exceptionalSuccessors = new HashSet<Block>();
			Iterator<Unit> iterator = b.iterator();
			//find traps
			
			while (iterator.hasNext()) {
				Unit u = iterator.next();
				traps.addAll(TrapManager.getTrapsAt(u, body));
			}
			//find successors that are traps
			for (Trap t : traps) {
				if (t.getHandlerUnit() == null) {
					throw new RuntimeException("No handler for trap");
				}
				for (Block s : b.getSuccs()) {
					if (s.getHead() == t.getHandlerUnit()) {
						exceptionalSuccessors.add(s);
					}
				}
			}

			// now do the actual translation
			SootStmtSwitch bss = new SootStmtSwitch(b, mi);
			subGraphs.put(b, bss);

			// connect the block to its non-exceptional successors
			if (bss.getExitBlock() != null) {
				for (Block s : b.getSuccs()) {
					if (!exceptionalSuccessors.contains(s)) {						
						bss.getExitBlock().addSuccessor(mi.lookupCfgBlock(s.getHead()));
					}
				}
			}

		}

		if (bg.getHeads().size() != 1) {
			throw new RuntimeException(
					"Never seen a procedure with two heads: "
							+ body.getMethod().getBytecodeSignature());
		} else {			
			CfgBlock root = subGraphs.get(bg.getHeads().get(0)).getEntryBlock();
			mi.setSource(root);
		}
//		debugPrint(mi);
	}

	/*
	 * Code to handle Java Assertions.
	 */
	
	private static final String javaAssertionType = "java.lang.AssertionError";
	private static final String javaAssertionFlag = "$assertionsDisabled";
	
	/**
	 * Look for parts of the body that have been created from Java
	 * assert statements. These are always of the form:
	 * 
	 *  $z0 = <Assert: boolean $assertionsDisabled>;
     *  if $z0 != 0 goto label1;
     *  [[some statements]]
     *  if [[some condition]] goto label1;
     *  $r1 = new java.lang.AssertionError;
     *  specialinvoke $r1.<java.lang.AssertionError: void <init>()>();
     *  throw $r1; 
     *  
     *  and replace those blocks by:
     *  [[some statements]]
     *  $assert_condition = [[some condition]];
     *  staticinvoke <JayHornAssertions: void super_crazy_assertion(boolean[])>($assert_condition);
     *  
	 * @param body
	 */
	private void reconstructJavaAssertions(ShimpleBody body) {
		Set<Unit> unitsToRemove = new HashSet<Unit>();
		Map<Unit, Value> assertionsToInsert = new HashMap<Unit,Value>();
		
//		body.getUnits().insertAfter(toInsert, point);
		Iterator<Unit> iterator = body.getUnits().iterator();
		while (iterator.hasNext()) {
			Unit u = iterator.next();
			if (isSootAssertionFlag(u)) {
				//u := $z0 = <Assert: boolean $assertionsDisabled>;
				unitsToRemove.add(u);
				//u := if $z0 != 0 goto label1;
				u = iterator.next();
				if (!(u instanceof IfStmt)) {
					throw new RuntimeException("");
				}
				unitsToRemove.add(u);
				
				//now search for the new java.lang.AssertionError
				Unit previousUnit = null;
				while (iterator.hasNext()) {
					u = iterator.next();
					if (isNewJavaAssertionError(u)) {
						//u := $r1 = new java.lang.AssertionError;
						unitsToRemove.add(u);
						//the statement before should be 
						//the expression from the java assertion
						// previousUnit := if $i0 != 5 goto label1;
						if (!(previousUnit instanceof IfStmt)) {
							throw new RuntimeException("");
						}						
						unitsToRemove.add(previousUnit);
						IfStmt ite = (IfStmt)previousUnit;						
						assertionsToInsert.put(u, ite.getCondition());
						break;
					}
					previousUnit = u;
				}
				//u :=  specialinvoke $r1.<java.lang.AssertionError: void <init>()>();
				u = iterator.next();			     
				if (!(u instanceof InvokeStmt)) {
					throw new RuntimeException(u.toString());
				}
				unitsToRemove.add(u);
				
				// u :=  throw $r1;
				u = iterator.next();			    
				if (!(u instanceof ThrowStmt)) {
					throw new RuntimeException(u.toString());
				}
				unitsToRemove.add(u);
				
				continue;
			} 			
		}
		Local assertionLocal = null;
		if (!assertionsToInsert.isEmpty()) {
			assertionLocal = Jimple.v().newLocal("$assert_condition", BooleanType.v());
			body.getLocals().add(assertionLocal);
		}
		//remove all boilerplate statements
		//generated from the assertions.				
		for (Entry<Unit, Value> entry : assertionsToInsert.entrySet()) {
			List<Unit> unitsToInsert = new LinkedList<Unit>();
			List<Value> args = new LinkedList<Value>();
			args.add(assertionLocal);
			unitsToInsert.add(Jimple.v().newAssignStmt(assertionLocal, entry.getValue()));
			unitsToInsert.add(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(internalAssertMethod.makeRef(), args)));			

			body.getUnits().insertBefore(unitsToInsert, entry.getKey());
			unitsToRemove.add(entry.getKey());
		}
		
		body.getUnits().removeAll(unitsToRemove);
		body.validate();
	}
		
	/**
	 * Checks if u has been created from a Java assert statement
	 * and is of the form:
	 * $r1 = new java.lang.AssertionError;
	 * @param u
	 * @return
	 */
	private boolean isNewJavaAssertionError(Unit u) {
		if (u instanceof AssignStmt) {
			AssignStmt ids = (AssignStmt)u;
			if (ids.getRightOp() instanceof NewExpr) {
				NewExpr ne = (NewExpr)ids.getRightOp();
				return ne.getType().toString().equals(javaAssertionType);
			}
		}				
		return false;
	}
	
	/**
	 * Checks if u has been created from a Java assert statement
	 * and is of the form:
	 * $z0 = <Assert: boolean $assertionsDisabled>;
	 * @param u
	 * @return
	 */
	private boolean isSootAssertionFlag(Unit u) {
		if (u instanceof AssignStmt) {
			AssignStmt ids = (AssignStmt)u;
			if (ids.getRightOp() instanceof StaticFieldRef) {
				StaticFieldRef sfr = (StaticFieldRef)ids.getRightOp();
				return sfr.getField().getName().equals(javaAssertionFlag);
			}
		}		
		return false;
	}
	
//	private void debugPrint(MethodInfo mi) {
//		List<CfgBlock> todo = new LinkedList<CfgBlock>();
//		todo.add(mi.getSource());
//		Set<CfgBlock> done = new HashSet<CfgBlock>();
//		while (!todo.isEmpty()) {
//			CfgBlock current = todo.remove(0);
//			done.add(current);
//			System.err.println(current);
//			for (CfgBlock succ : current.getSuccessors()) {
//				if (!todo.contains(succ) && !done.contains(succ)) {
//					todo.add(succ);
//				}
//			}
//		}
//	}

}
