/**
 * 
 */
package jayhorn.soot.memory_model;

import jayhorn.cfg.expression.Expression;
import jayhorn.soot.visitors.SootStmtSwitch;
import jayhorn.soot.visitors.SootValueSwitch;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;

/**
 * @author schaef
 *
 */
public class MemoryModel {

//	private SootStmtSwitch statementSwitch = null;
//	private SootValueSwitch valueSwitch = null;
		
	public MemoryModel(SootStmtSwitch ss, SootValueSwitch vs) {
		
	}
	
	public Expression mkNewExpr(NewExpr arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkNewArrayExpr(NewArrayExpr arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkNewMultiArrayExpr(NewMultiArrayExpr arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkArrayRefExpr(ArrayRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkArrayLengthExpr(Value arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Expression mkInstanceFieldRefExpr(InstanceFieldRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkStaticFieldRefExpr(StaticFieldRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkThisRefExpr(ThisRef arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkNullConstant() {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkStringConstant(StringConstant arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkClassConstant(ClassConstant arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkDoubleConstant(DoubleConstant arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkFloatConstant(FloatConstant arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression mkInstanceOfExpr(InstanceOfExpr arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
