/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.LinkedList;
import java.util.List;

import soot.Unit;
import soot.Value;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.statement.ArrayReadStatement;
import soottocfg.cfg.statement.ArrayStoreStatement;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.Type;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class SimpleBurstallBornatModel extends BasicMemoryModel {

	private final Variable heapVariable;
	private final Type heapType;

	public SimpleBurstallBornatModel() {
		super();
		//Heap is a map from <Type, Type> to Type
		List<Type> ids = new LinkedList<Type>();
		ids.add(Type.instance());
		ids.add(Type.instance());
		heapType = new MapType(ids, Type.instance());
		this.heapVariable = this.program.loopupGlobalVariable("$heap", heapType);

	}
	
	@Override
	public void mkHeapWriteStatement(Unit u, FieldRef field, Value rhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Variable fieldVar = lookupField(field.getField());
		if (field instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) field;
			ifr.getBase().apply(valueSwitch);
			Expression base = valueSwitch.popExpression();
			rhs.apply(valueSwitch);
			Expression value = valueSwitch.popExpression();
			Expression target;
			Expression[] indices;
			indices = new Expression[] { base, new IdentifierExpression(fieldVar) };
			target = new IdentifierExpression(this.heapVariable);
			this.statementSwitch.push(new ArrayStoreStatement(loc, target, indices, value));
		} else if (field instanceof StaticFieldRef) {			
			Expression left = new IdentifierExpression(fieldVar);
			rhs.apply(valueSwitch);
			Expression right = valueSwitch.popExpression();
			this.statementSwitch.push(new AssignStatement(loc, left, right));			
		} else {
			throw new RuntimeException("not implemented");
		}
	}

	
	@Override
	public void mkHeapReadStatement(Unit u, FieldRef field, Value lhs) {
		SourceLocation loc = SootTranslationHelpers.v().getSourceLocation(u);
		Variable fieldVar = lookupField(field.getField());
		if (field instanceof InstanceFieldRef) {
			lhs.apply(valueSwitch);
			IdentifierExpression left = (IdentifierExpression)valueSwitch.popExpression();

			InstanceFieldRef ifr = (InstanceFieldRef) field;
			ifr.getBase().apply(valueSwitch);
			Expression base = valueSwitch.popExpression();			
			Expression target;
			Expression[] indices;			
			indices = new Expression[] { base, new IdentifierExpression(fieldVar) };
			target = new IdentifierExpression(this.heapVariable);
			this.statementSwitch.push(new ArrayReadStatement(loc, target, indices, left));
		} else if (field instanceof StaticFieldRef) {			
			lhs.apply(valueSwitch);
			Expression left = valueSwitch.popExpression();
			Expression right = new IdentifierExpression(fieldVar);
			this.statementSwitch.push(new AssignStatement(loc, left, right));			
		} else {
			throw new RuntimeException("not implemented");
		}
	}

}
