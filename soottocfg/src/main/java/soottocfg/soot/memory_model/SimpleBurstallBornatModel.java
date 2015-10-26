/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.HashMap;
import java.util.Map;

import soot.SootField;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.ArrayAccessExpression;
import soottocfg.cfg.expression.ArrayStoreExpression;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class SimpleBurstallBornatModel extends MemoryModel {

	private final Variable nullConstant, heapVariable;
	private Program program;
	private final Map<soot.Type, soottocfg.cfg.type.Type> types = new HashMap<soot.Type, soottocfg.cfg.type.Type>();
	private final Map<SootField, Variable> fieldGlobals = new HashMap<SootField, Variable>();

	private final Map<Constant, Variable> constantDictionary = new HashMap<Constant, Variable>();

	private final Type nullType, heapType;

	public SimpleBurstallBornatModel() {
		this.program = SootTranslationHelpers.v().getProgram();
		// TODO
		nullType = new ReferenceType(null);
		heapType = new MapType();
		this.nullConstant = this.program.loopupGlobalVariable("$null", nullType);
		this.heapVariable = this.program.loopupGlobalVariable("$heap", heapType);

		this.types.put(soot.IntType.v(), IntType.instance());
		this.types.put(soot.BooleanType.v(), BoolType.instance());

		// this should be refined
		this.types.put(soot.LongType.v(), IntType.instance());
		this.types.put(soot.ByteType.v(), IntType.instance());
		this.types.put(soot.CharType.v(), IntType.instance());
		this.types.put(soot.ShortType.v(), IntType.instance());
	}

	@Override
	public void mkHeapAssignment(Unit u, Value lhs, Value rhs) {
		rhs.apply(valueSwitch);
		Expression value = valueSwitch.popExpression();
		Expression target;
		Expression[] indices;
		if (lhs instanceof InstanceFieldRef) {
			InstanceFieldRef ifr = (InstanceFieldRef) lhs;
			ifr.getBase().apply(valueSwitch);
			Expression base = valueSwitch.popExpression();
			// TODO: assert that base!=null
			Variable fieldVar = lookupField(ifr.getField());
			indices = new Expression[] { base, new IdentifierExpression(fieldVar) };
			target = new IdentifierExpression(this.heapVariable);
		} else if (lhs instanceof ArrayRef) {
			ArrayRef ar = (ArrayRef) lhs;
			ar.getBase().apply(valueSwitch);
			Expression base = valueSwitch.popExpression();
			// TODO: assert that idx is in bounds.
			ar.getIndex().apply(valueSwitch);
			Expression arrayIdx = valueSwitch.popExpression();
			indices = new Expression[] { base, arrayIdx };
			target = new IdentifierExpression(this.heapVariable);
			// TODO: only a hack.
		} else {
			throw new RuntimeException();
		}
		this.statementSwitch.push(new AssignStatement(SootTranslationHelpers.v().getSourceLocation(u),
				new IdentifierExpression(heapVariable), new ArrayStoreExpression(target, indices, value)));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkNewExpr(soot.jimple.NewExpr)
	 */
	@Override
	public Expression mkNewExpr(NewExpr arg0) {
		Type newType = this.lookupType(arg0.getBaseType());
		MethodInfo mi = this.statementSwitch.getMethodInto();
		Variable newLocal = mi.createFreshLocal("$new", newType);
		// add: assume newLocal!=null
		this.statementSwitch.push(new AssumeStatement(
				SootTranslationHelpers.v().getSourceLocation(this.statementSwitch.getCurrentStmt()),
				new BinaryExpression(BinaryOperator.Ne, new IdentifierExpression(newLocal), this.mkNullConstant())));
		// add: assume newLocal instanceof newType

		Expression instof = new InstanceOfExpression(new IdentifierExpression(newLocal),
				SootTranslationHelpers.v().lookupTypeVariable(arg0.getBaseType()));
		this.statementSwitch.push(
				new AssumeStatement(SootTranslationHelpers.v().getSourceLocation(this.statementSwitch.getCurrentStmt()),
						new BinaryExpression(BinaryOperator.Ne, instof, IntegerLiteral.zero())));

		return new IdentifierExpression(newLocal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkNewArrayExpr(soot.jimple.
	 * NewArrayExpr )
	 */
	@Override
	public Expression mkNewArrayExpr(NewArrayExpr arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(
				SootTranslationHelpers.v().getProgram().loopupGlobalVariable("TODO", IntType.instance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.soot.memory_model.MemoryModel#mkNewMultiArrayExpr(soot.jimple
	 * .NewMultiArrayExpr)
	 */
	@Override
	public Expression mkNewMultiArrayExpr(NewMultiArrayExpr arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(
				SootTranslationHelpers.v().getProgram().loopupGlobalVariable("TODO", IntType.instance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.soot.memory_model.MemoryModel#mkArrayRefExpr(soot.jimple.ArrayRef
	 * )
	 */
	@Override
	public Expression mkArrayRefExpr(ArrayRef arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(
				SootTranslationHelpers.v().getProgram().loopupGlobalVariable("TODO", IntType.instance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkArrayLengthExpr(soot.Value)
	 */
	@Override
	public Expression mkArrayLengthExpr(Value arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(
				SootTranslationHelpers.v().getProgram().loopupGlobalVariable("TODO", IntType.instance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkStringLengthExpr(soot.Value)
	 */
	@Override
	public Expression mkStringLengthExpr(Value arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(
				SootTranslationHelpers.v().getProgram().loopupGlobalVariable("TODO", IntType.instance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.soot.memory_model.MemoryModel#mkInstanceFieldRefExpr(soot.jimple
	 * .InstanceFieldRef)
	 */
	@Override
	public Expression mkInstanceFieldRefExpr(InstanceFieldRef arg0) {
		arg0.getBase().apply(valueSwitch);
		Expression base = valueSwitch.popExpression();
		Variable fieldVar = lookupField(arg0.getField());
		// TODO call the error model.
		return new ArrayAccessExpression(new IdentifierExpression(this.heapVariable),
				new Expression[] { base, new IdentifierExpression(fieldVar) });
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.soot.memory_model.MemoryModel#mkStaticFieldRefExpr(soot.jimple
	 * .StaticFieldRef)
	 */
	@Override
	public Expression mkStaticFieldRefExpr(StaticFieldRef arg0) {
		return new IdentifierExpression(lookupField(arg0.getField()));
	}

	private Variable lookupField(SootField field) {
		if (!this.fieldGlobals.containsKey(field)) {
			final String fieldName = field.getDeclaringClass().getName() + "." + field.getName();
			Variable fieldVar = this.program.loopupGlobalVariable(fieldName, this.lookupType(field.getType()));
			this.fieldGlobals.put(field, fieldVar);
		}
		return this.fieldGlobals.get(field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkNullConstant()
	 */
	@Override
	public Expression mkNullConstant() {
		return new IdentifierExpression(nullConstant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkStringConstant(soot.jimple.
	 * StringConstant)
	 */
	@Override
	public Expression mkStringConstant(StringConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram()
					.loopupGlobalVariable("$string" + constantDictionary.size(), IntType.instance()));
		}
		return new IdentifierExpression(constantDictionary.get(arg0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkDoubleConstant(soot.jimple.
	 * DoubleConstant)
	 */
	@Override
	public Expression mkDoubleConstant(DoubleConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram()
					.loopupGlobalVariable("$double" + constantDictionary.size(), IntType.instance()));
		}
		return new IdentifierExpression(constantDictionary.get(arg0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkFloatConstant(soot.jimple.
	 * FloatConstant)
	 */
	@Override
	public Expression mkFloatConstant(FloatConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram()
					.loopupGlobalVariable("$float" + constantDictionary.size(), IntType.instance()));
		}
		return new IdentifierExpression(constantDictionary.get(arg0));
	}

	@Override
	public Type lookupType(soot.Type t) {
		if (!types.containsKey(t)) {
			// throw new IllegalArgumentException("type " + t + " is unknown");
			System.err.println("Warning: type " + t + " is unknown, assuming int");
			return IntType.instance();
		}
		return types.get(t);
	}

	@Override
	public Type getNullType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Type getHeapType() {
		// TODO Auto-generated method stub
		return null;
	}

}
