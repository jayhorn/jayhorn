/**
 * 
 */
package soottocfg.cfg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import soottocfg.ast.Yylex;
import soottocfg.ast.parser;
import soottocfg.ast.Absyn.ProgramFile;

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
		if (args.length==0) {
			System.out.println("Give me a list of files that you want to parse.");
		}
		for (String s : args) {
			Ast2Cfg a2c = new Ast2Cfg(new Program());
			if (a2c.loadFile(new File(s))==null) {
				throw new RuntimeException(s + " could not be parsed.");
			}
		}
	}

	public Ast2Cfg(Program prog) {
		this.program = prog;
	}

	public ProgramFile loadFile(File f) {
		ProgramFile parse_tree=null;
		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(f), "UTF-8"));) {
			Yylex lexer = new Yylex(br);
			parser p = new parser(lexer);
			parse_tree =p.pProgramFile();
		} catch (IOException e) {
			// TODO lexer failed.
			e.printStackTrace();
		} catch (Exception e) {
			// TODO parser failed.
			e.printStackTrace();			
		}
		return parse_tree;
	}
}
