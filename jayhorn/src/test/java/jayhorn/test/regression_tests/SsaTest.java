/**
 * 
 */
package jayhorn.test.regression_tests;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.CycleDetector;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import jayhorn.test.Util;
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
import soottocfg.soot.SootToCfg;
import soottocfg.soot.SootToCfg.MemModel;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class SsaTest {

	private static final String userDir = System.getProperty("user.dir") + "/";
	private static final String testRoot = userDir + "src/test/resources/";

	private File sourceFile;

	@Parameterized.Parameters(name = "{index}: check ({1})")
	public static Collection<Object[]> data() {
		List<Object[]> filenames = new LinkedList<Object[]>();
		final File source_dir = new File(testRoot + "ssa/");
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

	public SsaTest(File source, String name) {
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

				CycleDetector<CfgBlock, CfgEdge> cycles = new CycleDetector<CfgBlock, CfgEdge>(m);
				boolean loopfree = cycles.findCycles().isEmpty();

				
				if (m.vertexSet().isEmpty()) {
					continue;
				}
				SsaTransformer ssatrans = new SsaTransformer(prog, m);	
				if (loopfree) {
					ssatrans.eliminatePhiStatements();
				}
				System.out.println("Checking method: "+m.getMethodName());				
				SsaPrinter printer = new SsaPrinter();
				StringBuilder sb = new StringBuilder();
				printer.printMethod(sb, m);
				System.out.println(sb);
				
				Assert.assertTrue("Variable is written more than once.", validateSsa(m));
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
						if (!checkSsaInAssignment(left, ((AssignStatement)s).getRight().getIdentifierExpressions())) {
							StringBuilder sb = new StringBuilder();
							sb.append("SSA didn't work\n");
							SsaPrinter printer = new SsaPrinter();
							printer.printStatement(sb, s);
							System.err.println(sb.toString());
							return false;
						}
					}
				} else if (s instanceof CallStatement) {
					if ( ((CallStatement)s).getReceiver().size()==1) {
						Expression lexp = ((CallStatement)s).getReceiver().iterator().next();
						if (lexp instanceof IdentifierExpression) {
							IdentifierExpression left = (IdentifierExpression)lexp;
							if (hasAlreadyBeenAssigned(assignedSsaVars, left)) {
								System.err.println(left.toString()+ " is written more than once.");
								return false;
							}
							if (!checkSsaInAssignment(left, ((CallStatement)s).getIdentifierExpressions())) {
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
