/**
 * 
 */
package jayhorn.soot;

import java.util.HashMap;
import java.util.Map;

import jayhorn.cfg.Program;
import jayhorn.cfg.Variable;
import jayhorn.cfg.method.Method;
import jayhorn.soot.memory_model.MemoryModel;
import jayhorn.soot.memory_model.SimpleBurstallBornatModel;
import jayhorn.util.Log;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.ClassConstant;
import soot.tagkit.AbstractHost;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;

/**
 * @author schaef
 *
 */
public enum SootTranslationHelpers {
	INSTANCE;
	
	public static SootTranslationHelpers v() {
		return INSTANCE;
	}
	
	private final Map<SootMethod, Method> methods = new HashMap<SootMethod, Method>();
	private final Map<soot.Type, Variable> typeVariables = new HashMap<soot.Type, Variable>();	
	
	private SootMethod currentMethod;
	private SootClass currentClass;
	private String currentSourceFileName;
	
	private MemoryModel memoryModel;
	private Program program;

	private long uniqueNumber = 0L;
	
	public long getUniqueNumber() {
		return this.uniqueNumber++;
	}

	public void setProgram(Program p) {
		this.program = p;
	}
	
	public Program getProgram() {
		return this.program;
	}
	
	public MemoryModel getMemoryModel() {
		if (this.memoryModel==null) {
			//TODO:
			this.memoryModel = new SimpleBurstallBornatModel();
		}
		return this.memoryModel;
	}
	
	public Method loopupMethod(SootMethod m) {
		if (!methods.containsKey(m)) {
			Method method = new Method(m);
			methods.put(m, method);
		}
		return methods.get(m);
	}
		
	public Variable lookupClassConstant(ClassConstant cc) {
		throw new RuntimeException("Not implemented");
	}
	
	public Variable lookupTypeVariable(soot.Type t) {
		if (!typeVariables.containsKey(t)) {			
			Variable var = new Variable(createTypeName(t), this.memoryModel.lookupType(t));
			typeVariables.put(t, var);	
		}
		return typeVariables.get(t);
	}
	
	private String createTypeName(soot.Type t) {
		if (t instanceof RefType) {
			RefType rt = (RefType)t;
			return rt.getClassName();
		} else {
			throw new RuntimeException("Did not expect that!");
		}
	}
	
	public SootClass getCurrentClass() {
		return currentClass;
	}

	public void setCurrentClass(SootClass currentClass) {
		this.currentClass = currentClass;
		for (Tag tag : this.currentClass.getTags()) {
			if (tag instanceof SourceFileTag) {
				SourceFileTag t = (SourceFileTag) tag;
				currentSourceFileName = t.getAbsolutePath();
			} else {
				Log.error("Unprocessed tag " + tag.getClass() + " - " + tag);
			}
		}
	}

	public SootMethod getCurrentMethod() {
		return currentMethod;
	}

	public void setCurrentMethod(SootMethod currentMethod) {
		this.currentMethod = currentMethod;
	}
	
	public String getCurrentSourceFileName() {
		return this.currentSourceFileName;
	}
	
	public int getJavaSourceLine(AbstractHost ah) {
		return ah.getJavaSourceStartLineNumber();
	}
}
