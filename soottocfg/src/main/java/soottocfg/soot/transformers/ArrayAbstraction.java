/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.google.common.base.Verify;

import soot.ArrayType;
import soot.Body;
import soot.IntType;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class ArrayAbstraction extends AbstractTransformer {
	
	private static final String getterMethodName = "get";
	private static final String setterMethodName = "set";
	private final Map<SootField, SootField> globalArrayFields;
	private final Map<Local, Local>	localArrayFields;
	
	public ArrayAbstraction(Map<SootField, SootField> globalArrayFields) {
		this.globalArrayFields = globalArrayFields; 
		localArrayFields = new HashMap<Local, Local>();		
	}



	@Override
	protected void internalTransform(Body b, String arg1, Map<String, String> arg2) {
		/*
		 * First, replace all ArrayRefs and NewArrayExpr in the body by equivalent
		 * statements that use the fake array classes instead of arrays.
		 */
		for (Unit u : new LinkedList<Unit>(b.getUnits())) {
			if (!(u instanceof Stmt)) {
				continue;
			}
			Stmt s = (Stmt)u;
			if (s.containsArrayRef()) {
				ArrayRef ar = s.getArrayRef();
				Verify.verify(ar.getBase() instanceof Local && s instanceof DefinitionStmt);
				DefinitionStmt ds = (DefinitionStmt)s;
				Local base = (Local)ar.getBase();
				SootClass arrayClass = SootTranslationHelpers.v().getFakeArrayClass((ArrayType)ar.getBase().getType());
				if (ds.getLeftOp() instanceof ArrayRef) {
					//replace array reference assignments by calls to the setter
					//if the fake array class
					SootMethodRef method = arrayClass.getMethodByName(setterMethodName).makeRef();
					InvokeExpr ivk = Jimple.v().newVirtualInvokeExpr(base, method, Arrays.asList(new Value[] {ds.getRightOp(), ar.getIndex()}));
					Unit replacement = this.invokeStmtFor(ivk, ds);
					b.getUnits().insertAfter(replacement, u);
					b.getUnits().remove(u);
				} else if (ds.getRightOp() instanceof ArrayRef) {
					SootMethodRef method = arrayClass.getMethodByName(getterMethodName).makeRef();
					InvokeExpr ivk = Jimple.v().newVirtualInvokeExpr(base, method, Arrays.asList(new Value[] {ar.getIndex()}));
					Unit replacement = this.assignStmtFor(ds.getLeftOp(), ivk, ds);
					b.getUnits().insertAfter(replacement, u);
					b.getUnits().remove(u);
				} else {
					throw new RuntimeException("Array abstraction not implemented for " + s.toString());	
				}				
			} else if (s instanceof DefinitionStmt && ((DefinitionStmt)s).getRightOp() instanceof NewArrayExpr) {
				DefinitionStmt ds = (DefinitionStmt)s;
				NewArrayExpr nae = (NewArrayExpr)((DefinitionStmt)s).getRightOp();
				Verify.verify(ds.getLeftOp() instanceof Local);
				SootClass arrayClass = SootTranslationHelpers.v().getFakeArrayClass((ArrayType)ds.getLeftOp().getType());
				
				Unit replacement = this.assignStmtFor(ds.getLeftOp(), Jimple.v().newNewExpr(RefType.v(arrayClass)), ds);
				b.getUnits().insertAfter(replacement, u);
				replacement.addAllTagsOf(u);
				
				SootMethod constructor = arrayClass.getMethod("<init>", Arrays.asList(new Type[] {IntType.v()}));	
				Verify.verify(constructor.isConstructor() && constructor.hasActiveBody());
				InvokeExpr ivk = Jimple.v().newSpecialInvokeExpr((Local)ds.getLeftOp(), constructor.makeRef(), nae.getSize());
				Unit constructorCall = Jimple.v().newInvokeStmt(ivk);
				constructorCall.addAllTagsOf(u);
				b.getUnits().insertAfter(constructorCall, replacement);
				b.getUnits().remove(u);
			} else if (s instanceof DefinitionStmt && ((DefinitionStmt)s).getRightOp() instanceof LengthExpr) {
				DefinitionStmt ds = (DefinitionStmt)s;
				LengthExpr le = (LengthExpr) ds.getRightOp();
				SootClass arrayClass = SootTranslationHelpers.v().getFakeArrayClass((ArrayType)le.getOp().getType());
				SootField lengthField = arrayClass.getFieldByName(SootTranslationHelpers.lengthFieldName);
				
				Unit replacement = this.assignStmtFor(ds.getLeftOp(), Jimple.v().newInstanceFieldRef(le.getOp(), lengthField.makeRef()), ds);
				b.getUnits().insertAfter(replacement, u);
				replacement.addAllTagsOf(u);
			}
		}
		/*
		 * Now, change the type of the locals and params.
		 */
		//TODO
	}
	
	
}
