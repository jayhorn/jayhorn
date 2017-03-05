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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Verify;

import soottocfg.ast.Yylex;
import soottocfg.ast.parser;
import soottocfg.ast.Absyn.Decl;
import soottocfg.ast.Absyn.DeclBody;
import soottocfg.ast.Absyn.Dvar;
import soottocfg.ast.Absyn.FieldDeclaration;
import soottocfg.ast.Absyn.JhPrg;
import soottocfg.ast.Absyn.ListFieldDeclaration;
import soottocfg.ast.Absyn.ListTupleEntry;
import soottocfg.ast.Absyn.NamedTpl;
import soottocfg.ast.Absyn.ProgramFile;
import soottocfg.ast.Absyn.TDecl;
import soottocfg.ast.Absyn.TDecl2;
import soottocfg.ast.Absyn.TDeclBody;
import soottocfg.ast.Absyn.TDeclBody2;
import soottocfg.ast.Absyn.TupleEntry;
import soottocfg.cfg.Program;
import soottocfg.cfg.variable.ClassVariable;

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
			//collect all declarations
			for (Decl x : p.listdecl_) {
				declMap.put(getDeclName(x), x);
			}
			
			Map<String, ClassVariable> resolvedTypes = new LinkedHashMap<String, ClassVariable>();
			
			for (Entry<String, Decl> entry : declMap.entrySet()) {
				resolveType(entry.getKey(), arg, declMap, resolvedTypes, new HashSet<String>());
			}
			
			return true;
		}
		
		private void resolveType(String tname, Program prog, Map<String, Decl> declMap, Map<String, ClassVariable> resolvedTypes, Set<String> processed) {
			if (resolvedTypes.containsKey(tname)) {
				return;
			}
			Verify.verify(!processed.contains(tname), "Recursive inheritance is illegal!");
			Decl d = declMap.get(tname);
			Collection<ClassVariable> parents = new ArrayList<ClassVariable>();
			DeclBody dbody;
			if (d instanceof TDecl) {
				dbody = ((TDecl)d).declbody_;
			} else if (d instanceof TDecl2) {
				dbody = ((TDecl2)d).declbody_;
				//recursively resolve the parent.
				Set<String> processed_ = new HashSet<String>(processed);
				processed_.add(tname);
				resolveType(((TDecl2)d).ident_2, prog, declMap, resolvedTypes,processed_);
				parents.add(resolvedTypes.get(((TDecl2)d).ident_2));
			} else {
				throw new RuntimeException();
			}
			ClassVariable cv = new ClassVariable(tname, parents);
			prog.addClassVariable(cv);
			resolvedTypes.put(tname, cv);
			resolveDeclBody(dbody, cv);
			
		}
		
		private void resolveDeclBody(DeclBody db, ClassVariable cv) {
			ListFieldDeclaration fields;
			if (db instanceof TDeclBody) {
				fields = ((TDeclBody)db).listfielddeclaration_;
			} else if (db instanceof TDeclBody2) {
				fields = ((TDeclBody2)db).listfielddeclaration_;
				for (TupleEntry tentry : (ListTupleEntry)((TDeclBody2)db).listtupleentry_) {
					if (tentry instanceof NamedTpl) {
						//TODO
					} else {
						throw new RuntimeException();
					}
				}				
			} else {
				throw new RuntimeException();
			}
			for (FieldDeclaration fd : fields) {
				if (fd instanceof Dvar) {
					//TODO
				} else {
					throw new RuntimeException();
				}
			}			
		}
		
		private String getDeclName(Decl d) {
			if (d instanceof TDecl) {
				return ((TDecl)d).ident_;
			} else if (d instanceof TDecl2) {
				return ((TDecl2)d).ident_1;
			} else {
				throw new RuntimeException("Not implemented");
			}
		}

	
	}

}
