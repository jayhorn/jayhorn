/**
 * 
 */
package soottocfg.soot.util;

import java.util.HashSet;
import java.util.Set;

import soot.Body;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ParameterRef;

/**
 * @author schaef
 *
 */
public class WriteOnceFieldCollector {

	public static Set<SootField> getWriteOnceInstanceFields() {
		Set<SootField> writtenFields = new HashSet<SootField>();
		Set<SootField> writtenTwiceFields = new HashSet<SootField>();
		
		for (SootClass sc : Scene.v().getClasses()) {
			if (sc.resolvingLevel()<SootClass.BODIES) {
				continue;
			}
			for (SootMethod sm : sc.getMethods()) {
				try {
					Body body =  sm.retrieveActiveBody();
					for (Unit u : body.getUnits()) {
						if (u instanceof DefinitionStmt ) {
							Value lOp = ((DefinitionStmt)u).getLeftOp();
							Value rOp = ((DefinitionStmt)u).getRightOp();
							if (lOp instanceof InstanceFieldRef) {
								SootField f = ((InstanceFieldRef)lOp).getField();
								
								boolean nonVariableAssign = f.isFinal() || (sm.isConstructor() && (rOp instanceof ParameterRef)) || (rOp instanceof Constant); 								
								if (writtenFields.contains(f) || !nonVariableAssign ) {
									writtenTwiceFields.add(f);
								}
								writtenFields.add(f);
							}
						}
					}					
				} catch (Exception e) {
					//ignore
					continue;
				}
			}
		}
		writtenFields.removeAll(writtenTwiceFields);
		removeRecursiveFields(writtenFields);
		return writtenFields;
 	}

	/**
	 * Removes all fields that are of a type that is defined recursively.
	 * @param fields
	 */
	private static void removeRecursiveFields(Set<SootField> fields) {
		//TODO:
		for (SootField sf : new HashSet<SootField>(fields)) {			
			if (sf.getType() instanceof RefType) {
				SootClass sc = ((RefType)sf.getType()).getSootClass();
				if (sc.equals(sf.getDeclaringClass())) {
					fields.remove(sf);
					continue;
				}
				//TODO: we also have ot check if nested fields
				//can be recursive.
			}
		}		
	}
	
}
