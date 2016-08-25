package jayhorn.hornify;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;

/**
 * @author teme
 *
 */

public class Hornify {

	private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();
	private final Prover prover;
	// private Map<Method, List<ProverHornClause>> methodClauses = new
	// HashMap<Method, List<ProverHornClause>>();

	private MethodEncoder mEncoder;

	public Hornify(ProverFactory fac, Program program) {
		prover = fac.spawn();
		prover.setHornLogic(true);
		Log.info("Building type hierarchy ... ");

		ClassType cType = new ClassType();

		for (ClassVariable var : program.getTypeGraph().vertexSet())
			cType.addClassVar(var, typeIds.size());

		mEncoder = new MethodEncoder(prover, program, cType);

		Log.info("Generating Method Contract ... ");
		mEncoder.mkMethodContract();

		Log.info("Compile Methods as Horn Clauses ... ");
		for (Method method : program.getMethods()) {
			stubHavocAndUnboundParams(program, method);
			// @TODO This is ugly each encoding of a method should return a list
			// of clauses
			mEncoder.encode(method);
			
		}
		
		// print Horn clauses
		if (jayhorn.Options.v().getPrintHorn()) {
			for (ProverHornClause clause : mEncoder.clauses)
				Log.info("\t\t" + clause);
		}

                hornToSMTLIBFile(mEncoder.clauses, 0, prover);
                hornToFile(mEncoder.clauses, 0);
	}

	/**
	 * Get the MethodEncoder
	 * 
	 * @return
	 */
	public MethodEncoder getMethodEncoder() {
		return mEncoder;
	}

	public Prover getProver() {
		return prover;
	}

	/**
	 * This is a hacky method to handle unbound globals before they are being translated
	 * to Horn. The problem is that e.g., the object 'args' in
	 * static void main(String[] args) ...
	 * is never initialized in the actual code, so pulling args would generate an
	 * assume false.
	 * The same is true for library classes that are not stubbed, like the classes we
	 * introduce for havoc.
	 * This is not the best place to do it but for now it works.
	 * @param program
	 * @param method
	 */
	private void stubHavocAndUnboundParams(Program program, Method method) {
		// Stub all methods for which we don't have a body
		// TODO: hack - this has to be done as a program transformation.
		if (method.getSource() == null) {
			CfgBlock block = new CfgBlock(method);
			SourceLocation loc = method.getLocation();

			AssignStatement asn = new AssignStatement(loc, new IdentifierExpression(loc, program.getExceptionGlobal()),
					new IdentifierExpression(loc,
							program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
			block.addStatement(asn);

			ClassVariable c = ((ReferenceType) program.getExceptionGlobal().getType()).getClassVariable();
			List<Expression> rhs = new LinkedList<Expression>();
			rhs.add(new IdentifierExpression(loc,
					program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
			PushStatement pack = new PushStatement(loc, c, new IdentifierExpression(loc, program.getExceptionGlobal()),
					rhs);
			block.addStatement(pack);

			Verify.verifyNotNull(method.getSource());
			// throw new RuntimeException("The checker currently expects that
			// all methods have a body. Go and create a stub during translation
			// for "+method.getMethodName());
		} else if (method.isProgramEntryPoint()) {
			if (method.getInParams().size() == 1 && method.getMethodName().contains("main")) {
				Variable argsParam = method.getInParams().get(0);
				ReferenceType argsType = (ReferenceType) argsParam.getType();
				CfgBlock entry = method.getSource();
				SourceLocation loc = method.getLocation();
				Variable sizeLocal = new Variable("undef_size", IntType.instance());
				AssumeStatement asm = new AssumeStatement(loc, new BinaryExpression(loc, BinaryOperator.Ge,
						new IdentifierExpression(loc, sizeLocal), IntegerLiteral.zero()));
				entry.addStatement(0, asm);

				// pack(JayHornArr12, r0, [JayHornArr12.$length,
				// JayHornArr12.$elType, JayHornArr12.$dynamicType])
				List<Expression> rhs = new LinkedList<Expression>();
				rhs.add(new IdentifierExpression(loc, sizeLocal));
				rhs.add(new IdentifierExpression(loc, argsType.getClassVariable()));
				ClassVariable c = ((ReferenceType) argsParam.getType()).getClassVariable();
				rhs.add(new IdentifierExpression(loc, c));
				// this is an array, so initialize the remaining fields with sth
				// as well
				// TODO:
				int i = 0;
				while (rhs.size() < c.getAssociatedFields().length) {
					Variable undefLocal = new Variable("undef_field" + (i++), IntType.instance());
					rhs.add(new IdentifierExpression(loc, undefLocal));
				}
				PushStatement pack = new PushStatement(loc, c, new IdentifierExpression(loc, argsParam), rhs);
				entry.addStatement(1, pack);
			}
		}
	}


	/**
	 * 
	 */

	/**
	 * Write clauses
	 * 
	 * @return
	 */
	public String writeHorn() {
		StringBuilder st = new StringBuilder();
		for (ProverHornClause clause : mEncoder.clauses)
			st.append("\t\t" + clause + "\n");
		st.append("\t\t-------------\n");
		return st.toString();
	}

	/**
	 * Write Horn clauses to file
	 */
	public static void hornToFile(List<ProverHornClause> clauses,
                                      int num) {
            // write Horn clauses to file
            String out = jayhorn.Options.v().getOutDir();
            if (out != null) {
                String basename = jayhorn.Options.v().getOutBasename();
                Path file = Paths.get(out + basename + "_" + num + ".horn");
                
                LinkedList<String> it = new LinkedList<String>();
                for (ProverHornClause clause : clauses)
                    it.add("\t\t" + clause);

                writeToFile(file, it);
            }
	}

	/**
	 * Write Horn clauses to an SMT-LIB file
	 */
	public static void hornToSMTLIBFile(List<ProverHornClause> clauses,
                                            int num,
                                            Prover prover) {
            String out = jayhorn.Options.v().getOutDir();
            if (out != null) {
                String basename = jayhorn.Options.v().getOutBasename();
                Path file = Paths.get(out + basename + "_" + num + ".smt2");

		Log.info("Writing Horn clauses to " + file);

                LinkedList<String> it = new LinkedList<String>();

                it.add("(set-info :origin \"Horn clauses generated by JayHorn\")");
                it.add("(set-logic HORN)");

                it.add("");

                Set<ProverFun> predicates = new LinkedHashSet<ProverFun>();
                for (ProverHornClause clause : clauses) {
                    // null indicates that the head of the clause is "false"
                    if (clause.getHeadFun() != null)
                        predicates.add(clause.getHeadFun());
                    for (int i = 0; i < clause.getArity(); ++i)
                        predicates.add(clause.getBodyFun(i));
                }
                
                for (ProverFun fun : predicates)
                    it.add(prover.toSMTLIBDeclaration(fun));

                it.add("");

                for (ProverHornClause clause : clauses)
                    it.add("(assert " + prover.toSMTLIBFormula(clause) + ")");

                it.add("");
                it.add("(check-sat)");
                
                writeToFile(file, it);
            }
	}

        private static void writeToFile(Path file, List<String> it) {
            try {					
                Path parent = file.getParent();
                if (parent != null)
                    Files.createDirectories(parent);
                Files.write(file, it, Charset.forName("UTF-8"));
            } catch (Exception e) {
                System.err.println("Error writing file " + file);
            }
        }
                            

}
