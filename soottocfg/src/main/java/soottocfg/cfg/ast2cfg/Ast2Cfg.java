/**
 * 
 */
package soottocfg.cfg.ast2cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.ast.Yylex;
import soottocfg.ast.parser;
import soottocfg.ast.Absyn.BasicType;
import soottocfg.ast.Absyn.BuiltIn;
import soottocfg.ast.Absyn.ClassType;
import soottocfg.ast.Absyn.Decl;
import soottocfg.ast.Absyn.DeclBody;
import soottocfg.ast.Absyn.Dvar;
import soottocfg.ast.Absyn.FieldDeclaration;
import soottocfg.ast.Absyn.JhPrg;
import soottocfg.ast.Absyn.ListFieldDeclaration;
import soottocfg.ast.Absyn.ListTupleEntry;
import soottocfg.ast.Absyn.ListVarDecl;
import soottocfg.ast.Absyn.NamedTpl;
import soottocfg.ast.Absyn.ProgramFile;
import soottocfg.ast.Absyn.TDecl;
import soottocfg.ast.Absyn.TDecl2;
import soottocfg.ast.Absyn.TDeclBody;
import soottocfg.ast.Absyn.TDeclBody2;
import soottocfg.ast.Absyn.TVoid;
import soottocfg.ast.Absyn.Tboolean;
import soottocfg.ast.Absyn.Tdouble;
import soottocfg.ast.Absyn.Tfloat;
import soottocfg.ast.Absyn.Tint;
import soottocfg.ast.Absyn.Tlong;
import soottocfg.ast.Absyn.TupleEntry;
import soottocfg.ast.Absyn.UDvar;
import soottocfg.ast.Absyn.UNamedTpl;
import soottocfg.ast.Absyn.VDecl;
import soottocfg.ast.Absyn.VarDecl;
import soottocfg.cfg.Program;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public class Ast2Cfg {

	protected final Program program;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Give me a list of files that you want to parse.");
		}
		for (String s : args) {
			Ast2Cfg a2c = new Ast2Cfg(new Program());
			if (a2c.loadFile(new File(s)) == null) {
				throw new RuntimeException(s + " could not be parsed.");
			}
		}
	}

	public Ast2Cfg(Program prog) {
		this.program = prog;
	}

	public ProgramFile loadFile(File f) {
		ProgramFile parse_tree = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));) {
			Yylex lexer = new Yylex(br);
			parser p = new parser(lexer);
			parse_tree = p.pProgramFile();
			iterateParseTree(parse_tree);
		} catch (IOException e) {
			// TODO lexer failed.
			e.printStackTrace();
		} catch (Exception e) {
			// TODO parser failed.
			e.printStackTrace();
		}
		return parse_tree;
	}

	protected void iterateParseTree(ProgramFile tree) {
		tree.<Boolean, Program>accept(new GlobalVisitor(), this.program);
	}

	public static class GlobalVisitor implements ProgramFile.Visitor<Boolean, Program> {

		@Override
		public Boolean visit(JhPrg p, Program arg) {
			Map<String, Decl> declMap = new LinkedHashMap<String, Decl>();
			// collect all declarations
			for (Decl x : p.listdecl_) {
				if (x instanceof TDecl || x instanceof TDecl2) {
					declMap.put(getDeclName(x), x);
				}
			}

			Map<String, ClassVariable> resolvedTypes = new LinkedHashMap<String, ClassVariable>();

			for (Entry<String, Decl> entry : declMap.entrySet()) {
				resolveType(entry.getKey(), arg, declMap, resolvedTypes, new HashSet<String>());
			}

			return true;
		}

		private ClassVariable resolveType(String tname, Program prog, Map<String, Decl> declMap,
				Map<String, ClassVariable> resolvedTypes, Set<String> processed) {
			if (resolvedTypes.containsKey(tname)) {
				return resolvedTypes.get(tname);
			}
			Verify.verify(!processed.contains(tname), "Recursive inheritance is illegal!");
			Decl d = declMap.get(tname);
			Collection<ClassVariable> parents = new ArrayList<ClassVariable>();
			DeclBody dbody;
			if (d instanceof TDecl) {
				dbody = ((TDecl) d).declbody_;
			} else if (d instanceof TDecl2) {
				dbody = ((TDecl2) d).declbody_;
				// recursively resolve the parent.
				Set<String> processed_ = new HashSet<String>(processed);
				processed_.add(tname);
				resolveType(((TDecl2) d).ident_2, prog, declMap, resolvedTypes, processed_);
				parents.add(resolvedTypes.get(((TDecl2) d).ident_2));
			} else {
				throw new RuntimeException(tname +" : " +d);
			}
			ClassVariable cv = new ClassVariable(tname, parents);
			prog.addClassVariable(cv);
			resolvedTypes.put(tname, cv);
			resolveDeclBody(dbody, cv, prog, declMap, resolvedTypes, processed);

			return resolvedTypes.get(tname);
		}

		private void resolveDeclBody(DeclBody db, ClassVariable cv, Program prog, Map<String, Decl> declMap,
				Map<String, ClassVariable> resolvedTypes, Set<String> processed) {
			ListFieldDeclaration fields;
			List<Variable> bodyVariables = new ArrayList<Variable>();
			if (db instanceof TDeclBody) {
				fields = ((TDeclBody) db).listfielddeclaration_;
			} else if (db instanceof TDeclBody2) {
				fields = ((TDeclBody2) db).listfielddeclaration_;
				for (TupleEntry tentry : (ListTupleEntry) ((TDeclBody2) db).listtupleentry_) {
					String name;
					soottocfg.ast.Absyn.Type tplType;
					boolean isUnique = false;
					if (tentry instanceof UNamedTpl) {
						UNamedTpl ntpl = (UNamedTpl) tentry;
						name = ntpl.ident_;
						tplType = ntpl.type_;
						isUnique = true;
					} else if (tentry instanceof NamedTpl) {
						NamedTpl ntpl = (NamedTpl) tentry;
						name = ntpl.ident_;
						tplType = ntpl.type_;
					} else {
						throw new RuntimeException();
					}
					soottocfg.cfg.type.Type cfgType = resolveAstTyple(tplType, prog, declMap, resolvedTypes, processed);
					bodyVariables.add(new Variable(name, cfgType, true, isUnique));
				}
			} else {
				throw new RuntimeException();
			}
			for (FieldDeclaration fd : fields) {
				boolean isUnique = false;
				ListVarDecl varnames;
				soottocfg.ast.Absyn.Type fType;

				if (fd instanceof Dvar) {
					isUnique = false;
					varnames = ((Dvar) fd).listvardecl_;
					fType = ((Dvar) fd).type_;
				} else if (fd instanceof UDvar) {
					isUnique = true;
					varnames = ((UDvar) fd).listvardecl_;
					fType = ((UDvar) fd).type_;
				} else {
					throw new RuntimeException();
				}
				for (VarDecl vd : varnames) {
					String name = ((VDecl) vd).ident_;
					soottocfg.cfg.type.Type cfgType = resolveAstTyple(fType, prog, declMap, resolvedTypes, processed);
					bodyVariables.add(new Variable(name, cfgType, true, isUnique));
				}
			}
			cv.addFields(bodyVariables);
		}

		private soottocfg.cfg.type.Type resolveAstTyple(soottocfg.ast.Absyn.Type astType, Program prog,
				Map<String, Decl> declMap, Map<String, ClassVariable> resolvedTypes, Set<String> processed) {
			if (astType instanceof BuiltIn) {
				BasicType bt = ((BuiltIn) astType).basictype_;
				if (bt instanceof Tboolean) {
					return BoolType.instance();
				} else if (bt instanceof Tdouble) {
				} else if (bt instanceof Tfloat) {
				} else if (bt instanceof Tint) {
					return IntType.instance();
				} else if (bt instanceof Tlong) {
				} else if (bt instanceof TVoid) {
				} else {
					throw new RuntimeException();
				}
			} else if (astType instanceof ClassType) {
				ClassVariable cv = resolveType(((ClassType) astType).ident_, prog, declMap, resolvedTypes, processed);
				return new ReferenceType(cv);
			}
			throw new RuntimeException("Cannot resolve type " + astType);
		}

		private String getDeclName(Decl d) {
			if (d instanceof TDecl) {
				return ((TDecl) d).ident_;
			} else if (d instanceof TDecl2) {
				return ((TDecl2) d).ident_1;
			} else {
				throw new RuntimeException("Not implemented");
			}
		}

	}

}
