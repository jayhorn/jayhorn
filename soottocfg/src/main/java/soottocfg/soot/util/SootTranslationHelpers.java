/**
 * 
 */
package soottocfg.soot.util;

import java.util.LinkedList;
import java.util.List;

import soot.PrimType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.tagkit.AbstractHost;
import soot.tagkit.Host;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.method.Method;
import soottocfg.soot.SootRunner;
import soottocfg.soot.SootToCfg.MemModel;
import soottocfg.soot.memory_model.MemoryModel;
import soottocfg.soot.memory_model.NewMemoryModel;
import soottocfg.soot.memory_model.SimpleBurstallBornatModel;

/**
 * @author schaef
 *
 */
public enum SootTranslationHelpers {
	INSTANCE;	
	
	public static SootTranslationHelpers v() {
		return INSTANCE;
	}

	private static final String parameterPrefix = "$in_";	

	private transient SootMethod currentMethod;
	private transient SootClass currentClass;
	private transient String currentSourceFileName;

	private transient MemoryModel memoryModel;
	private MemModel memoryModelKind = MemModel.PackUnpack;
	
	private transient Program program;

	public void reset() {		
		currentMethod = null;
		currentClass = null;
		currentSourceFileName = null;
		memoryModel = null;
		program = null;
	}

	
	public Method lookupOrCreateMethod(SootMethod m) {
		if (this.program.loopupMethod(m.getSignature())!=null) {
			return this.program.loopupMethod(m.getSignature());
		}
		int parameterCount = 0;
		final List<Variable> parameterList = new LinkedList<Variable>();
		if (!m.isStatic()) {
			parameterList.add(new Variable(parameterPrefix + (parameterCount++),
					getMemoryModel().lookupType(m.getDeclaringClass().getType())));
		}
		for (int i=0; i < m.getParameterCount(); i++) {
			parameterList.add(new Variable(parameterPrefix + (parameterCount++),
					getMemoryModel().lookupType(m.getParameterType(i))));
		}
		return Method.createMethodInProgram(program, m.getSignature(), parameterList);
	}
	
	public Stmt getDefaultReturnStatement(Type returnType, Host createdFrom) {
		Stmt stmt;
		if (returnType instanceof VoidType) {
			stmt = Jimple.v().newReturnVoidStmt();
		} else {
			Value retVal = NullConstant.v();
			if (returnType instanceof PrimType) {
				retVal = IntConstant.v(0);
			}
			stmt = Jimple.v().newReturnStmt(retVal);
		}
		stmt.addAllTagsOf(createdFrom);
		return stmt;
	}
	
	public void setProgram(Program p) {
		this.program = p;
	}

	public Program getProgram() {
		return this.program;
	}

	public SootClass getAssertionClass() {
		return Scene.v().getSootClass(SootRunner.assertionClassName);
	}

	public SootMethod getAssertMethod() {
		SootClass assertionClass = Scene.v().getSootClass(SootRunner.assertionClassName);
		return assertionClass.getMethodByName(SootRunner.assertionProcedureName);
	}

	public SootField getExceptionGlobal() {
		SootClass assertionClass = Scene.v().getSootClass(SootRunner.assertionClassName);
		return assertionClass.getFieldByName(SootRunner.exceptionGlobalName);
	}

	public Value getExceptionGlobalRef() {
		return Jimple.v().newStaticFieldRef(getExceptionGlobal().makeRef());
	}

	public Unit makeAssertion(Value cond, Host createdFrom) {
		List<Value> args = new LinkedList<Value>();
		args.add(cond);
		InvokeStmt stmt = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(getAssertMethod().makeRef(), args));
		stmt.addAllTagsOf(createdFrom);
		return stmt;
	}

	public SourceLocation getSourceLocation(Unit u) {
		int lineNumber = u.getJavaSourceStartLineNumber();

		if (lineNumber < 0) {
			lineNumber = SootTranslationHelpers.v().getJavaSourceLine(SootTranslationHelpers.v().getCurrentMethod());
		}
		return new SourceLocation(this.currentSourceFileName, lineNumber);
	}
	
	public SourceLocation getSourceLocation(SootMethod sm) {
		int lineNumber = sm.getJavaSourceStartLineNumber();

		if (lineNumber < 0) {
			lineNumber = SootTranslationHelpers.v().getJavaSourceLine(SootTranslationHelpers.v().getCurrentMethod());
		}
		
		return new SourceLocation(this.currentSourceFileName, lineNumber);
	}

	public void setMemoryModelKind(MemModel kind) {
		memoryModelKind = kind;
	}
	
	public MemoryModel getMemoryModel() {
		if (this.memoryModel == null) {
			// TODO:
			if (memoryModelKind==MemModel.PackUnpack) {
				this.memoryModel = new NewMemoryModel();
			} else if (memoryModelKind==MemModel.BurstallBornat) {
				this.memoryModel = new SimpleBurstallBornatModel();
			} else {
				throw new RuntimeException("Unknown memory model");
			}
		}
		return this.memoryModel;
	}	
	

	public SootClass getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(SootClass currentClass) {
		String fn = findFileName(currentClass.getTags());
		if (fn !=null) {
			this.currentSourceFileName = fn;
		}

		this.currentClass = currentClass;
	}
	
	private String findFileName(List<Tag> tags) {
		String fileName = null;
		for (Tag tag : tags) {
			if (tag instanceof SourceFileTag) {
				SourceFileTag t = (SourceFileTag) tag;
				if (t.getAbsolutePath()!=null) {
					fileName = t.getAbsolutePath();
				} else {
					if (t.getSourceFile()!=null) {
						fileName = t.getSourceFile();
					}
				}
			} else {
				// System.err.println("Unprocessed tag " + tag.getClass() + " -
				// " + tag);
			}
		}
		return fileName;
	}

	public SootMethod getCurrentMethod() {		
		return currentMethod;
	}

	public void setCurrentMethod(SootMethod currentMethod) {
		String fn = findFileName(currentMethod.getTags());
		if (fn !=null) {
			this.currentSourceFileName = fn;
		}
		this.currentMethod = currentMethod;
	}

	public String getCurrentSourceFileName() {
		return this.currentSourceFileName;
	}

	public int getJavaSourceLine(AbstractHost ah) {
		return ah.getJavaSourceStartLineNumber();
	}
}
