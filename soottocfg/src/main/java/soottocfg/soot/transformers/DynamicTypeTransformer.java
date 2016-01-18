/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import soot.ArrayType;
import soot.Body;
import soot.RefType;
import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.AnyNewExpr;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.Jimple;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 * TODO test this thing!
 * Makes the dynamic type of each object explicit by in a field.
 *  
 */
public class DynamicTypeTransformer  extends AbstractTransformer {

	private Map<Unit, List<Unit>> insertBefore = new HashMap<Unit, List<Unit>>(); 
	
	@Override
	protected void internalTransform(Body b, String arg1, Map<String, String> arg2) {		
		for (Unit u : b.getUnits()) {
			if (u instanceof DefinitionStmt) {
				DefinitionStmt ds = (DefinitionStmt)u;
				Value rhs = ds.getRightOp();
				if (rhs instanceof InstanceOfExpr) {
					InstanceOfExpr ex = (InstanceOfExpr)rhs;
					handleInstanceOf(ds, ex);
				} else if (rhs instanceof AnyNewExpr) {
					AnyNewExpr ex = (AnyNewExpr)rhs;
				} else if (rhs instanceof CastExpr) {
					handleCaseExpr(ds, (CastExpr)rhs);
				}					
			}				
		}	
		//now insert the generated statements.
		for (Entry<Unit, List<Unit>> entry : insertBefore.entrySet()) {
			b.getUnits().insertBefore(entry.getValue(), entry.getKey());
		}
	}
	
	private void handleInstanceOf(DefinitionStmt ds, InstanceOfExpr rhs) {
//		SootField typeField = ((RefType)t).getSootClass().getFieldByName(SootTranslationHelpers.typeFieldName);
//		final String localName = "$tmp"+this.statementSwitch.getMethod().getActiveBody().getLocals().size();
//		Local freshLocal = Jimple.v().newLocal(localName, typeField.getType());
//		this.statementSwitch.getMethod().getActiveBody().getLocals().add(freshLocal);
//		FieldRef fieldRef = Jimple.v().newInstanceFieldRef(left, typeField.makeRef());
	}
	
	private void handleCaseExpr(DefinitionStmt ds, CastExpr ex) {
		if (ex.getCastType() instanceof RefType) {
			RefType rt = (RefType)ex.getOp().getType();
			SootField typeField = rt.getSootClass().getFieldByName(SootTranslationHelpers.typeFieldName);
			FieldRef fieldRef = Jimple.v().newInstanceFieldRef(ex.getOp(), typeField.makeRef());
			final String className = ((RefType)ex.getCastType()).getClassName().replace(".", "/");
			Unit asgn = Jimple.v().newAssignStmt(fieldRef, ClassConstant.v(className) );
			
			insertBefore.put(ds, new LinkedList<Unit>());
			insertBefore.get(ds).add(asgn);			
		} else if (ex.getCastType() instanceof ArrayType) {
			throw new RuntimeException("not implemented.");
		} //else do nothing.	
	}
	
}
