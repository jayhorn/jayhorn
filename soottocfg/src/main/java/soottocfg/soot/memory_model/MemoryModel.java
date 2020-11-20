/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.List;

import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FieldRef;
import soot.jimple.FloatConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StringConstant;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;
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

	public abstract void mkArrayWriteStatement(Unit u, ArrayRef arrayRef, Value rhs);

	public abstract void mkArrayReadStatement(Unit u, ArrayRef arrayRef, Value lhs);
	
	public void mkCopy(Local lhs, Local rhs) { }
	
	public abstract Expression mkNewArrayExpr(NewArrayExpr arg0);

	public abstract Expression mkNewMultiArrayExpr(NewMultiArrayExpr arg0);

	public abstract Expression mkStringLengthExpr(Value arg0);

	public abstract Expression mkNullConstant();

	public abstract Expression mkStringConstant(StringConstant arg0);

	public abstract Expression mkDoubleConstant(DoubleConstant arg0);

	public abstract Expression mkFloatConstant(FloatConstant arg0);
	
	public abstract void mkConstructorCall(Unit u, SootMethod constructor, List<Expression> args);

	public abstract ClassVariable lookupClassVariable(ClassConstant t);

	public abstract void putExpression(Variable variable, Expression expr);

	public abstract Expression lookupExpression(Variable variable);

	public abstract Type lookupType(soot.Type t);

	public boolean isNullReference(Expression e) {
		return false;
	}

}
