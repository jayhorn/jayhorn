/**
 * 
 */
package soottocfg.cfg.ast2cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import soottocfg.ast.Yylex;
import soottocfg.ast.parser;
import soottocfg.ast.Absyn.Decl;
import soottocfg.ast.Absyn.FieldDeclaration;
import soottocfg.ast.Absyn.JhPrg;
import soottocfg.ast.Absyn.ListFieldDeclaration;
import soottocfg.ast.Absyn.MDecl;
import soottocfg.ast.Absyn.ProgramFile;
import soottocfg.ast.Absyn.TupleEntry;
import soottocfg.cfg.Program;

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
			for (Decl x : p.listdecl_) {
				x.accept(new DeclVisitor(), arg);
			}
			return true;
		}

		public static class DeclVisitor implements Decl.Visitor<Boolean, Program> {
			@Override
			public Boolean visit(soottocfg.ast.Absyn.TDecl p,
					Program arg) { /* Code For TDecl Goes Here */
				createClassType(p.ident_, p.listfielddeclaration_, arg);
				return true;
			}

			@Override
			public Boolean visit(soottocfg.ast.Absyn.TDecl2 p,
					Program arg) { /* Code For TDecl2 Goes Here */
				for (TupleEntry x : p.listtupleentry_) {
					/* ... */
					System.err.println(x);
				}
				createClassType(p.ident_, p.listfielddeclaration_, arg);
				return true;
			}

			protected void createClassType(String name, ListFieldDeclaration fields, Program program) {
				for (FieldDeclaration x : fields) {
					System.err.println(x);
					/* ... */ }
			}

			@Override
			public Boolean visit(MDecl p, Program arg) {
				// TODO Auto-generated method stub
				return false;
			}
		}
	}
}
