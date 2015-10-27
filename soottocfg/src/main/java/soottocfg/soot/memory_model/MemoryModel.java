/**
 * 
 */
package soottocfg.soot.memory_model;

import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StringConstant;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.type.ClassConstant;
import soottocfg.cfg.type.Type;
import soottocfg.soot.visitors.SootStmtSwitch;
import soottocfg.soot.visitors.SootValueSwitch;

/**
 * @author schaef
 *
 */
public abstract class MemoryModel {

	protected SootStmtSwitch statementSwitch;
	protected SootValueSwitch valueSwitch;
		
	public MemoryModel() {
	}
	
	public void setStmtSwitch(SootStmtSwitch ss) {
		this.statementSwitch = ss;
	}
	
	public void setValueSwitch(SootValueSwitch vs) {
		this.valueSwitch = vs;
	}
	
	public abstract void mkHeapWriteStatement(Unit u, FieldRef field, Value rhs);
	
	public abstract void mkHeapReadStatement(Unit u, FieldRef field, Value lhs);
	
	public abstract Expression mkNewExpr(NewExpr arg0);

	public abstract Expression mkNewArrayExpr(NewArrayExpr arg0);

	public abstract Expression mkNewMultiArrayExpr(NewMultiArrayExpr arg0);

	public abstract Expression mkArrayRefExpr(ArrayRef arg0);

	public abstract Expression mkArrayLengthExpr(Value arg0);
	
	public abstract Expression mkStringLengthExpr(Value arg0);
	
	public abstract Expression mkNullConstant();

	public abstract Expression mkStringConstant(StringConstant arg0);

	public abstract Expression mkDoubleConstant(DoubleConstant arg0);

	public abstract Expression mkFloatConstant(FloatConstant arg0);

	public abstract Type lookupType(soot.Type t);
	
	public abstract ClassConstant lookupClassConstant(SootClass c);
		
}
