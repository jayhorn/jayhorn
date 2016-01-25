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
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class ArrayAbstraction extends AbstractTransformer {

	private final Map<soot.ArrayType, SootClass> arrayTypes = new HashMap<soot.ArrayType, SootClass>();
	
	private static final String getterMethodName = "get";
	private static final String setterMethodName = "set";
	
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
				SootClass arrayClass = getFakeArrayClass((ArrayType)ar.getBase().getType());
				if (ds.getLeftOp() instanceof ArrayRef) {
					//replace array reference assignments by calls to the setter
					//if the fake array class
					SootMethodRef method = arrayClass.getMethodByName(setterMethodName).makeRef();
					InvokeExpr ivk = Jimple.v().newVirtualInvokeExpr(base, method, Arrays.asList(new Value[] {ds.getRightOp(), ar.getIndex()}));
					Unit replacement = this.invokeStmtFor(ivk, ds);
					b.getUnits().insertAfter(replacement, u);
					b.getUnits().remove(u);
				} else {
					SootMethodRef method = arrayClass.getMethodByName(getterMethodName).makeRef();
					InvokeExpr ivk = Jimple.v().newVirtualInvokeExpr(base, method, Arrays.asList(new Value[] {ar.getIndex()}));
					Unit replacement = this.assignStmtFor(ds.getLeftOp(), ivk, ds);
					b.getUnits().insertAfter(replacement, u);
					b.getUnits().remove(u);
				}
			} else if (s instanceof DefinitionStmt && ((DefinitionStmt)s).getRightOp() instanceof NewArrayExpr) {
				DefinitionStmt ds = (DefinitionStmt)s;
				Verify.verify(ds.getLeftOp() instanceof Local);
				SootClass arrayClass = getFakeArrayClass((ArrayType)ds.getLeftOp().getType());
				
				Unit replacement = this.assignStmtFor(ds.getLeftOp(), Jimple.v().newNewExpr(RefType.v(arrayClass)), ds);
				b.getUnits().insertAfter(replacement, u);
				b.getUnits().remove(u);
			}
		}
		/*
		 * Now, change the type of the locals and params.
		 */
		//TODO
	}
	
	private SootClass getFakeArrayClass(soot.ArrayType t) {
		if (!arrayTypes.containsKey(t)) {
			SootClass arrayClass = new SootClass("JayHornArr" + arrayTypes.size(), Modifier.PUBLIC);
			arrayClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
			arrayClass.addField(new SootField(SootTranslationHelpers.lengthFieldName,
					RefType.v(Scene.v().getSootClass("java.lang.Integer"))));
			arrayClass.addField(new SootField(SootTranslationHelpers.arrayElementTypeFieldName,
					RefType.v(Scene.v().getSootClass("java.lang.Class"))));
			arrayClass.addField(new SootField(SootTranslationHelpers.typeFieldName,
					RefType.v(Scene.v().getSootClass("java.lang.Class"))));
			// TODO create some fields of t.getElementType()
			SootMethod getElement = new SootMethod(getterMethodName,                 
				    Arrays.asList(new Type[] {IntType.v()}),
				    t.getArrayElementType(), Modifier.PUBLIC);
			arrayClass.addMethod(getElement);
			//TODO: add body
			SootMethod setElement = new SootMethod(setterMethodName,                 
				    Arrays.asList(new Type[] {t.getArrayElementType(), IntType.v()}),
				    VoidType.v(), Modifier.PUBLIC);
			arrayClass.addMethod(setElement);
			//TODO: add body
			Scene.v().addClass(arrayClass);
			arrayTypes.put(t, arrayClass);
		}
		return arrayTypes.get(t);
	}
	
}
