/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import soot.ArrayType;
import soot.Body;
import soot.IntType;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.NewArrayExpr;
import soot.jimple.Stmt;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class ArrayAbstraction extends AbstractTransformer {

	private final Map<soot.ArrayType, SootClass> arrayTypes = new HashMap<soot.ArrayType, SootClass>();
	
	@Override
	protected void internalTransform(Body b, String arg1, Map<String, String> arg2) {
		//TODO
		
		for (Unit u : b.getUnits()) {
			if (!(u instanceof Stmt)) {
				continue;
			}
			Stmt s = (Stmt)u;
			if (s.containsArrayRef()) {
				//TODO:
				ArrayRef ar = s.getArrayRef();
				getFakeArrayClass((ArrayType)ar.getBase().getType());				
			} else if (s instanceof DefinitionStmt && ((DefinitionStmt)s).getRightOp() instanceof NewArrayExpr) {
				DefinitionStmt ds = (DefinitionStmt)s;
				ds.getRightOp();
				//TODO
			}
			//TODO change call params etc.
		}
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
			SootMethod getElement = new SootMethod("get",                 
				    Arrays.asList(new Type[] {IntType.v()}),
				    t.getArrayElementType(), Modifier.PUBLIC);
			arrayClass.addMethod(getElement);
			//TODO: add body
			SootMethod setElement = new SootMethod("set",                 
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
