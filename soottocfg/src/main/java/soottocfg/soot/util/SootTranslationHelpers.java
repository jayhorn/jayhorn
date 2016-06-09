/**
 * 
 */
package soottocfg.soot.util;

import java.util.LinkedList;
import java.util.List;

import soot.ArrayType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
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

	public static SootTranslationHelpers v(Program program, MemModel kind){
		final SootTranslationHelpers instance = INSTANCE;
		instance.setMemoryModelKind(kind);
		instance.setProgram(program);
		return instance;
	}

	private static final String parameterPrefix = "$in_";
	public static final String typeFieldName = "$dynamicType";

	public static final String arrayElementTypeFieldName = "$elType";
	public static final String lengthFieldName = "$length";
//	public static final String indexFieldNamePrefix = "$idx_";

	private transient SootMethod currentMethod;
//	private transient SootClass currentClass;
	private transient String currentSourceFileName;

	private transient MemoryModel memoryModel;
	private MemModel memoryModelKind = MemModel.PullPush;

	private transient Program program;

	public void reset() {
		currentMethod = null;
//		currentClass = null;
		currentSourceFileName = null;
		memoryModel = null;
		program = null;
//		arrayTypes.clear();
	}

//	private transient Map<soot.ArrayType, SootClass> arrayTypes = new HashMap<soot.ArrayType, SootClass>();
//
//	public SootClass getFakeArrayClass(soot.ArrayType t) {
//		if (!arrayTypes.containsKey(t)) {
//			SootClass arrayClass = new SootClass("JayHornArr" + arrayTypes.size(), Modifier.PUBLIC);
//			arrayClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
//			SootField lengthField = new SootField(SootTranslationHelpers.lengthFieldName,
//					RefType.v(Scene.v().getSootClass("java.lang.Integer")));
//			arrayClass.addField(lengthField);
//			
//			SootField elemTypeField = new SootField(SootTranslationHelpers.arrayElementTypeFieldName,
//					RefType.v(Scene.v().getSootClass("java.lang.Class")));
//			arrayClass.addField(elemTypeField);
//			
//			SootField typeField = new SootField(SootTranslationHelpers.typeFieldName,
//					RefType.v(Scene.v().getSootClass("java.lang.Class")));
//			arrayClass.addField(typeField);
//			
//			// TODO create some fields of t.getElementType()
//			SootMethod getElement = new SootMethod("get",                 
//				    Arrays.asList(new Type[] {IntType.v()}),
//				    t.getArrayElementType(), Modifier.PUBLIC);
//			arrayClass.addMethod(getElement);
//			JimpleBody body = Jimple.v().newBody(getElement);
//			body.insertIdentityStmts();			
//			//TODO: add body
//			body.getUnits().add(Jimple.v().newReturnStmt(getDefaultValue(t.getArrayElementType())));
//
//			getElement.setActiveBody(body);
//			
//			SootMethod setElement = new SootMethod("set",                 
//				    Arrays.asList(new Type[] {t.getArrayElementType(), IntType.v()}),
//				    VoidType.v(), Modifier.PUBLIC);
//			arrayClass.addMethod(setElement);
//			body = Jimple.v().newBody(setElement);
//			body.insertIdentityStmts();			
//			//TODO: add body
//			body.getUnits().add(Jimple.v().newReturnVoidStmt());
//			setElement.setActiveBody(body);
//			
//			//Now create a constructor that takes the array size as input
//			SootMethod constructor = new SootMethod("<init>", Arrays.asList(new Type[] {IntType.v()}), VoidType.v(),
//	                Modifier.PUBLIC);
//			//add the constructor to the class.
//			arrayClass.addMethod(constructor);
//		
//			body = Jimple.v().newBody(constructor);			
//			//add a local for the first param
//			body.insertIdentityStmts();
//			Local thisLocal = body.getThisLocal();
//			body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(thisLocal, lengthField.makeRef()), body.getParameterLocal(0)));
//			//TODO: test the two lines below:
//			String elementTypeName = t.getArrayElementType().toString();
//			if (t.getArrayElementType() instanceof RefType) {
//				elementTypeName = ((RefType)t.getArrayElementType()).getSootClass().getJavaStyleName();
//			}
//			elementTypeName = elementTypeName.replace('.', '/');
//			body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(thisLocal, elemTypeField.makeRef()), ClassConstant.v(elementTypeName)));
//			//TODO
//			//			body.getUnits().add(Jimple.v().newAssignStmt(Jimple.v().newInstanceFieldRef(thisLocal, typeField.makeRef()), ClassConstant.v(t.toString())));
//			body.getUnits().add(Jimple.v().newReturnVoidStmt());
//			constructor.setActiveBody(body);
//			Verify.verify(constructor.isConstructor());		
//			
//			//done adding methods
//			Scene.v().addClass(arrayClass);
//			arrayClass.setApplicationClass();
//			
//			arrayTypes.put(t, arrayClass);
//		}
//		return arrayTypes.get(t);
//	}

	public Value getDefaultValue(soot.Type t) {
		Value rhs = null;
		if (t instanceof PrimType) {
			if (t instanceof soot.BooleanType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.ByteType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.CharType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.DoubleType) {
				rhs = DoubleConstant.v(0);
			} else if (t instanceof soot.FloatType) {
				rhs = FloatConstant.v(0);
			} else if (t instanceof soot.IntType) {
				rhs = IntConstant.v(0);
			} else if (t instanceof soot.LongType) {
				rhs = LongConstant.v(0);
			} else if (t instanceof soot.ShortType) {
				rhs = IntConstant.v(0);
			} else {
				throw new RuntimeException("Unknown type " + t);
			}
		} else {
			rhs = NullConstant.v();
		}
		return rhs;
	}

	
	public ClassConstant getClassConstant(Type t) {
		if (t instanceof RefType) {
			final String className = ((RefType) t).getClassName().replace(".", "/");
			return ClassConstant.v(className);
		} else if (t instanceof ArrayType) {
//			final String className = getFakeArrayClass((ArrayType)t).getName().replace(".", "/");
//			return ClassConstant.v(className);
			throw new RuntimeException("Remove Arrays first! "+t);
		} else if (t instanceof PrimType) {
			final String className = ((PrimType) t).toString();
			return ClassConstant.v(className);
		}
		throw new RuntimeException("Not implemented");
	}

	public Method lookupOrCreateMethod(SootMethod m) {
		if (this.program.loopupMethod(m.getSignature()) != null) {
			return this.program.loopupMethod(m.getSignature());
		}
		int parameterCount = 0;
		final List<Variable> parameterList = new LinkedList<Variable>();
		if (!m.isStatic()) {
			parameterList.add(new Variable(parameterPrefix + (parameterCount++),
					getMemoryModel().lookupType(m.getDeclaringClass().getType())));
		}
		for (int i = 0; i < m.getParameterCount(); i++) {
			parameterList.add(new Variable(parameterPrefix + (parameterCount++),
					getMemoryModel().lookupType(m.getParameterType(i))));
		}
		
		List<soottocfg.cfg.type.Type> optRetType = new LinkedList<soottocfg.cfg.type.Type>();
		if (!m.getReturnType().equals(VoidType.v())) {
			optRetType.add(memoryModel.lookupType(m.getReturnType()));
		} 		
		return Method.createMethodInProgram(program, m.getSignature(), parameterList, optRetType, SootTranslationHelpers.v().getSourceLocation(m));
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

	void setProgram(Program p) {
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

	void setMemoryModelKind(MemModel kind) {
		memoryModelKind = kind;
	}

	public MemoryModel getMemoryModel() {
		if (this.memoryModel == null) {
			// TODO:
			if (memoryModelKind == MemModel.PullPush) {
				this.memoryModel = new NewMemoryModel();
			} else if (memoryModelKind == MemModel.BurstallBornat) {
				this.memoryModel = new SimpleBurstallBornatModel();
			} else {
				throw new RuntimeException("Unknown memory model");
			}
		}
		return this.memoryModel;
	}

  public void setCurrentClass(SootClass currentClass) {
		String fn = findFileName(currentClass.getTags());
		if (fn != null) {
			this.currentSourceFileName = fn;
		}

//		this.currentClass = currentClass;
	}

	private String findFileName(List<Tag> tags) {
		String fileName = null;
		for (Tag tag : tags) {
			if (tag instanceof SourceFileTag) {
				SourceFileTag t = (SourceFileTag) tag;
				if (t.getAbsolutePath() != null) {
					fileName = t.getAbsolutePath();
				} else {
					if (t.getSourceFile() != null) {
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
		if (fn != null) {
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
