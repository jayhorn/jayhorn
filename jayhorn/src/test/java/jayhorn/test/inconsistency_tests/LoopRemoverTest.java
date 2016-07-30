/**
 * 
 */
package jayhorn.test.inconsistency_tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jayhorn.test.Util;
import jayhorn.util.EdgeLabelToAssume;
import jayhorn.util.LoopRemoval;
import jayhorn.util.SsaPrinter;
import jayhorn.util.SsaTransformer;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.CfgEdge;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.CallStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.GraphUtil;
import soottocfg.cfg.util.UnreachableNodeRemover;
import soottocfg.soot.SootToCfg;
import soottocfg.soot.SootToCfg.MemModel;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class LoopRemoverTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "loopremoval/");
		File[] directoryListing = source_dir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if (child.isFile() && child.getName().endsWith(".java")) {
					filenames.add(new Object[] { child, child.getName() });
				} else {
					// Ignore
				}
			}
		} else {
			// Handle the case where dir is not really a directory.
			// Checking dir.isDirectory() above would not be sufficient
			// to avoid race conditions with another process that deletes
			// directories.
			System.err.println("Test data in " + userDir + " not found");
			throw new RuntimeException("Test data not found!");
		}
		return filenames;
	}

	public LoopRemoverTest(File source, String name) {
		this.sourceFile = source;
	}

	
	@Test
	public void testSsa() {		
		System.out.println("\nRunning test " + this.sourceFile.getName()+"\n");
		File classDir = null;
		try {
			classDir = Util.compileJavaFile(this.sourceFile);
			System.out.println("Class files at "+ classDir.getAbsolutePath());
			
			SootToCfg soot2cfg = new SootToCfg(false, true, MemModel.BurstallBornat);
			soot2cfg.run(classDir.getAbsolutePath(), null);
			Program prog = soot2cfg.getProgram();
			for (Method m : prog.getMethods()) {
				if (m.vertexSet().isEmpty()) continue;
				System.out.println("For method "+ m.getMethodName());
				System.out.println(m);		
				
				//now remove the loops and do ssa again.
				UnreachableNodeRemover<CfgBlock, CfgEdge> unr = new UnreachableNodeRemover<CfgBlock, CfgEdge>(m, m.getSource(), m.getSink());
				if (unr.pruneUnreachableNodes()) {
					System.err.println("removed unreachable nodes for "+m.getMethodName());
				}				
				EdgeLabelToAssume etoa = new EdgeLabelToAssume(m);
				etoa.turnLabeledEdgesIntoAssumes();
				
				if (!GraphUtil.isReducibleGraph(m, m.getSource())) {
					System.err.println("Skipping irreducible cfg");
					//TODO: dont skip, be awesome instead!
					continue;
				}
				
				LoopRemoval lr = new LoopRemoval(m);
				lr.removeLoops();				
				lr.verifyLoopFree();
				
				SsaTransformer ssa = new SsaTransformer(prog, m);
				Assert.assertTrue("Variable is written more than once.", validateSsa(m));
				ssa.eliminatePhiStatements();
				//TODO: assert that after removing the phi statement, each variable is only
				//written once per path. Note that we cannot use validateSsa because it
				//checks if every variable is only written once per procedure. This is
				//violated when eliminating the phi statements.
								
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		} finally {
			if (classDir!=null) {
				classDir.deleteOnExit();
			}
		}	
	}
		
	private boolean validateSsa(Method m) {
		Map<Variable, Set<Integer>> assignedSsaVars = new HashMap<Variable, Set<Integer>>(); 
		for (CfgBlock b : m.vertexSet()) {
			System.out.println("Checking block: "+b.getLabel());
			for (Statement s : b.getStatements()) {
				if (s instanceof AssignStatement) {
					if (((AssignStatement)s).getLeft() instanceof IdentifierExpression) {
						IdentifierExpression left = (IdentifierExpression)((AssignStatement)s).getLeft();
						if (hasAlreadyBeenAssigned(assignedSsaVars, left)) {
							System.err.println(left.toString()+ " is written more than once.");
							return false;
						}
						if (!checkSsaInAssignment(left, ((AssignStatement)s).getRight().getUseIdentifierExpressions())) {
							StringBuilder sb = new StringBuilder();
							sb.append("SSA didn't work\n");
							SsaPrinter printer = new SsaPrinter();
							printer.printStatement(sb, s);
							printer.printMethod(sb, m);
							System.err.println(sb.toString());
							return false;
						}
					}
				} else if (s instanceof CallStatement) {
					if ( !((CallStatement)s).getReceiver().isEmpty()) {
						Expression lexp = ((CallStatement)s).getReceiver().get(0);
						if (lexp instanceof IdentifierExpression) {
							IdentifierExpression left = (IdentifierExpression)lexp;
							if (hasAlreadyBeenAssigned(assignedSsaVars, left)) {
								System.err.println(left.toString()+ " is written more than once.");
								return false;
							}
							if (!checkSsaInAssignment(left, ((CallStatement)s).getUseIdentifierExpressions())) {
								StringBuilder sb = new StringBuilder();
								sb.append("SSA didn't work ");
								SsaPrinter printer = new SsaPrinter();
								printer.printStatement(sb, s);
								System.err.println(sb.toString());
								return false;
							}
						}
					}
				}
			}
		}		
		return true;
	}
	
	private boolean checkSsaInAssignment(IdentifierExpression left, Set<IdentifierExpression> rights) {		
		for (IdentifierExpression ie : rights) {
			if (ie.getVariable()==left.getVariable()) {
				if (ie.getIncarnation()>=left.getIncarnation()) {
					return false;
				}
			}
		}		
		return true;
	}
	
	private boolean hasAlreadyBeenAssigned(Map<Variable, Set<Integer>> assignedVars, IdentifierExpression exp) {
		if (!assignedVars.containsKey(exp.getVariable())) {
			assignedVars.put(exp.getVariable(), new HashSet<Integer>());
			assignedVars.get(exp.getVariable()).add(exp.getIncarnation());
			return false;
		}
		if (assignedVars.get(exp.getVariable()).contains(exp.getIncarnation())) {
			return true;
		}
		assignedVars.get(exp.getVariable()).add(exp.getIncarnation());
		return false;
	}
}
