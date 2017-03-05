/**
 * 
 */
package soottocfg.cfg.ast2cfg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Map.Entry;

import com.google.common.base.Verify;

import soottocfg.cfg.Program;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;

/**
 * @author schaef Prints a Program in AST format.
 */
public class Cfg2AstPrinter {

	public static void printProgramToFile(Program program, File outFile) {
		try (Writer out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(outFile), StandardCharsets.UTF_8));) {
			out.write(printProgramToString(program));
		} catch (IOException e) {
			throw new RuntimeException(e.toString());
		}
	}

	public static String printProgramToString(Program program) {
		StringBuilder sb = new StringBuilder();
		for (ClassVariable cv : program.getClassVariables()) {
			sb.append(printClassVariable(cv));
			sb.append("\n");
		}
		return sb.toString();
	}

	protected static String printClassVariable(ClassVariable cv) {
		StringBuilder sb = new StringBuilder();
		ReferenceType ref = new ReferenceType(cv);
		sb.append("class ");
		sb.append(cv.getName());
		if (!ref.getElementTypes().isEmpty()) {
			sb.append("<");
			String comma = "";
			for (Entry<String, Type> entry : ref.getElementTypes().entrySet()) {
				sb.append(comma);
				comma = ", ";
				sb.append(entry.getKey());
				sb.append(" : ");
				sb.append(entry.getValue().toString()); //TODO is toString right?
			}
			sb.append(">");
		} else {
			throw new RuntimeException("Unexpected!");
		}

		if (!cv.getParents().isEmpty()) {
			Verify.verify(cv.getParents().size()==1);
			sb.append(" extends ");
			sb.append(cv.getParents().iterator().next().getName());
			sb.append(" ");
		}
		sb.append("{\n");
		for (Variable v : cv.getAssociatedFields()) {
			sb.append("\t");
			sb.append(v.getType());
			sb.append(" ");
			sb.append(v.getName());
			sb.append(";\n");
		}
		sb.append("}\n");
		return sb.toString();
	}

}
