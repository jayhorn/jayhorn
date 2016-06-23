package jayhorn.hornify;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Verify;

import jayhorn.Log;
import jayhorn.Options;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverResult;
import jayhorn.solver.ProverType;
import soottocfg.cfg.ClassVariable;
import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;


/**
 * @author teme
 *
 */

public class Hornify {

		private Map<ClassVariable, Integer> typeIds = new LinkedHashMap<ClassVariable, Integer>();
		private Prover prover;
		private List<ProverHornClause> clauses = new LinkedList<ProverHornClause>();
		//private Map<Method, List<ProverHornClause>> mclause = new HashMap<Method, List<ProverHornClause>>();
		
		public Hornify(ProverFactory fac) {
			this.prover = fac.spawn();
			
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


						List<Expression> rhs = new LinkedList<Expression>();
						rhs.add(new IdentifierExpression(loc, sizeLocal));
						rhs.add(new IdentifierExpression(loc, argsType.getClassVariable()));
						ClassVariable c = ((ReferenceType) argsParam.getType()).getClassVariable();
						rhs.add(new IdentifierExpression(loc, c));
						PushStatement pack = new PushStatement(loc, c, new IdentifierExpression(loc, argsParam), rhs);
						entry.addStatement(1, pack);
					}
				}
				mEncoder.encode(method);
				System.out.println(method.getMethodName());
				clauses.addAll(mEncoder.clauses);
				//mclause.put(method, clauses);
			}		
		}
		
		
//		public String writeHorn(){
//			StringBuilder st = new StringBuilder();
//			for (Map.Entry<Method, List<ProverHornClause>> mc : mclause.entrySet()) {
//				st.append("\t\t-------------\n");
//				st.append("Method Name: " + mc.getKey().getMethodName() + "\n");
//				for (ProverHornClause clause : mc.getValue())
//					st.append("\t\t" + clause + "\n");
//				st.append("\t\t-------------\n");
//			}
//			return st.toString();
//		}

	}

