/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.tagkit.Host;

/**
 * @author schaef
 *
 */
public abstract class AbstractSceneTransformer {

	public abstract void applyTransformation();

	protected Set<JimpleBody> getSceneBodies() {
		Set<JimpleBody> bodies = new LinkedHashSet<JimpleBody>();
		for (SootClass sc : new LinkedList<SootClass>(Scene.v().getClasses())) {
			if (sc.resolvingLevel() >= SootClass.BODIES) {
				for (SootMethod sm : sc.getMethods()) {
					if (sm.isConcrete()) {
						bodies.add((JimpleBody) sm.retrieveActiveBody());
					}
				}
			}
		}
		return bodies;
	}

	/**
	 * Creates a jimple AssingStmt left:=right and copies the tags
	 * from createdFrom.
	 * 
	 * @param left
	 * @param right
	 * @param createdFrom
	 * @return
	 */
	protected Unit assignStmtFor(Value left, Value right, Host createdFrom) {
		Unit stmt = Jimple.v().newAssignStmt(left, right);
		stmt.addAllTagsOf(createdFrom);
		return stmt;
	}

	/**
	 * Creates a new jimple IfStmt and adds all tags from createdFrom to this
	 * statement.
	 * 
	 * @param condition
	 * @param target
	 * @param createdFrom
	 * @return
	 */
	protected Unit ifStmtFor(Value condition, Unit target, Host createdFrom) {
		IfStmt stmt = Jimple.v().newIfStmt(condition, target);
		stmt.addAllTagsOf(createdFrom);
		return stmt;
	}

	/**
	 * Creates a new jimple GotoStmt and adds all tags from createdFrom to this
	 * statement.
	 * 
	 * @param target
	 * @param createdFrom
	 * @return
	 */
	protected Unit gotoStmtFor(Unit target, Host createdFrom) {
		GotoStmt stmt = Jimple.v().newGotoStmt(target);
		stmt.addAllTagsOf(createdFrom);
		return stmt;
	}

	protected Unit invokeStmtFor(Value ivk, Host createdFrom) {
		InvokeStmt stmt = Jimple.v().newInvokeStmt(ivk);
		stmt.addAllTagsOf(createdFrom);
		return stmt;
	}

	protected Value jimpleEqZero(Value v) {
		return Jimple.v().newEqExpr(v, IntConstant.v(0));
	}

	protected Value jimpleNeZero(Value v) {
		return Jimple.v().newNeExpr(v, IntConstant.v(0));
	}

	private long counter = 0;

	protected Local getFreshLocal(Body body, Type t) {
		Local local = Jimple.v().newLocal("$helper" + (counter++), t);
		body.getLocals().add(local);
		return local;
	}
}
