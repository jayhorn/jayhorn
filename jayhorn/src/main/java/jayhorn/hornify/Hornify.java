package jayhorn.hornify;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.Options;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
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
		private Prover prover;
		private List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		//private Map<Method, List<ProverHornClause>> methodClauses = new HashMap<Method, List<ProverHornClause>>();
		
		private MethodEncoder mEncoder;
		
		public Hornify(ProverFactory fac) {
			this.prover = fac.spawn();			
		}
		
		/**
		 * Get the MethodEncoder
		 * @return
		 */
		public MethodEncoder getMethodEncoder(){
			return mEncoder;
		}
		
		/**
		 * Main method to encode into Horn
		 * @param program
		 */
		public void toHorn(Program program){
			
			Log.info("Building type hierarchy ... ");

			ClassType cType = new ClassType();
			
			for (ClassVariable var : program.getTypeGraph().vertexSet())
				cType.addClassVar(var, typeIds.size());
			
			MethodEncoder mEncoder = new MethodEncoder(this.prover, program, cType);
			this.mEncoder = mEncoder;
			
			Log.info("Generating Method Contract ... ");
			mEncoder.mkMethodContract();

			Log.info("Compile Methods as Horn Clauses ... ");

			/**
			 * @TODO this looks like a hack change it
			 */
			for (Method method : program.getMethods()) {
				// hack
				if (method.getSource() == null) {
					CfgBlock block = new CfgBlock(method);
					SourceLocation loc = method.getLocation();
					
					AssignStatement asn = new AssignStatement(loc,
							new IdentifierExpression(loc, program.getExceptionGlobal()), new IdentifierExpression(loc,
									program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
					block.addStatement(asn);

					ClassVariable c = ((ReferenceType) program.getExceptionGlobal().getType()).getClassVariable();
					List<Expression> rhs = new LinkedList<Expression>();
					rhs.add(new IdentifierExpression(loc,
							program.createFreshGlobal("havoc", program.getExceptionGlobal().getType())));
					PushStatement pack = new PushStatement(loc, c,
							new IdentifierExpression(loc, program.getExceptionGlobal()), rhs);
					block.addStatement(pack);

					Verify.verifyNotNull(method.getSource());
//					throw new RuntimeException("The checker currently expects that all methods have a body. Go and create a stub during translation for "+method.getMethodName());
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
						//this is an array, so initialize the remaining fields with sth as well
						//TODO:
						int i=0;
						while (rhs.size()<c.getAssociatedFields().length) {
							Variable undefLocal = new Variable("undef_field"+(i++), IntType.instance());
							rhs.add(new IdentifierExpression(loc, undefLocal));
						}
						PushStatement pack = new PushStatement(loc, c, new IdentifierExpression(loc, argsParam), rhs);
						entry.addStatement(1, pack);
					}
				}

				// @TODO This is ugly each encoding of a method should return a list of clauses
				mEncoder.encode(method);
//				List<ProverHornClause> methodHorn = mEncoder.encode(method);
//				clauses.addAll(methodHorn);
//				methodClauses.put(method, methodHorn);
			}	
			
			clauses.addAll(mEncoder.clauses);
		}
		
		/**
		 * Return the list of Horn Clauses
		 */
		public List<ProverHornClause> getClauses(){
			return clauses;
		}
		
		/**
		 * 
		 */
		
		/**
		 * Write clauses
		 * @return
		 */
		public String writeHorn(){
			StringBuilder st = new StringBuilder();
			for (ProverHornClause clause : clauses)
				st.append("\t\t" + clause + "\n");
				st.append("\t\t-------------\n");
			return st.toString();
		}

		/**
		 * Write Horn clauses to file
		 */
		public void hornToFile(){
			// write Horn clauses to file
			String out = jayhorn.Options.v().getOut();
			if(out != null) {
				if (!out.endsWith("/"))
					out += "/";
				String in = Options.v().getJavaInput();
				String outName = in.substring(in.lastIndexOf('/'), in.length()).replace(".java", "").replace(".class", "");
				Path file = Paths.get(out+outName+".horn");
				LinkedList<String> it = new LinkedList<String>();
				for (ProverHornClause clause : clauses)
					it.add("\t\t" + clause);
				try {
					Files.createDirectories(file.getParent());
					Files.write(file, it, Charset.forName("UTF-8"));
				} catch (Exception e) {
					System.err.println("Error writing file " + file);
				}
			}
		}

	}

