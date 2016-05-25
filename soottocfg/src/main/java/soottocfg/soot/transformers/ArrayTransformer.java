/**
 * 
 */
package soottocfg.soot.transformers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LengthExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public class ArrayTransformer {

	public static final String arraySetName = "set";
	public static final String arrayGetName = "get";
	public static final String arrayTypeName = "JayArray";
	public static final String lengthFieldName = "$length";

	public ArrayTransformer() {

	}

	private final Map<String, SootField> fieldSubstitutionMap = new HashMap<String, SootField>();
	private final Map<String, SootMethod> methodSubstitutionMap = new HashMap<String, SootMethod>();

	public void substituteAllArrayTypes() {
		/*
		 * We have to do two passes. In the first pass, we update all fields and
		 * method signatures
		 * but not the method bodies. This will break all MethodsRefs and
		 * FieldRefs in the bodies.
		 * In the second pass, we update the body and replace newarray,
		 * newmultiarray, fieldrefs and
		 * lengthexpr by the appropriate expressions.
		 * For the broken FieldRefs and MethodRefs, the toString will not
		 * change, so we can do a
		 * lookup to find the original field/method that we created in the first
		 * pass and create a fresh
		 * refs.
		 */
		List<SootClass> classes = new LinkedList<SootClass>(Scene.v().getClasses());
		List<JimpleBody> bodies = new LinkedList<JimpleBody>();
		for (SootClass sc : classes) {
			if (sc.resolvingLevel() >= SootClass.SIGNATURES) {
				// change the type of all array fields.
				for (SootField f : sc.getFields()) {
					if (f.getType() instanceof ArrayType) {
						final String oldSignature = f.getSignature();
						f.setType(arrayTypeToRefType(f.getType()));
						fieldSubstitutionMap.put(oldSignature, f);
					}
				}
				for (SootMethod sm : sc.getMethods()) {
					final String oldSignature = sm.getSignature();
					
					if (sc.resolvingLevel() >= SootClass.BODIES && sm.isConcrete()) {
						//record all methods for which we found a body.						
						bodies.add((JimpleBody) sm.retrieveActiveBody());
					}
					// update return type
					sm.setReturnType(arrayTypeToRefType(sm.getReturnType()));
					// update parameter types
					List<Type> newParamTypes = new LinkedList<Type>();
					for (Type t : sm.getParameterTypes()) {
						newParamTypes.add(arrayTypeToRefType(t));
					}
					sm.setParameterTypes(newParamTypes);
					methodSubstitutionMap.put(oldSignature, sm);
				}
			}
		}

		for (JimpleBody body : bodies) {			
			for (Local local : body.getLocals()) {
				local.setType(arrayTypeToRefType(local.getType()));
			}
			
			// now replace ArrayRefs and NewArray, NewMulitArray
			// statements.

			for (Unit u : new LinkedList<Unit>(body.getUnits())) {

				/*
				 * Changing the types from ArrayType to RefType breaks the soot 'references', FieldRef, MethodRef, 
				 * and ParameterRef since they do not get updated automatically. Hence, we need to re-build these
				 * Refs by hand: 
				 */
				if (((Stmt) u).containsFieldRef() && ((Stmt) u).getFieldRef().getType() instanceof ArrayType) {
					FieldRef fr = ((Stmt) u).getFieldRef();
					fr.setFieldRef(fieldSubstitutionMap.get(fr.toString()).makeRef());
				} else if (((Stmt) u).containsInvokeExpr()) {
					InvokeExpr ive = ((Stmt) u).getInvokeExpr();
					final String oldSignature = ive.getMethodRef().toString();
					if (methodSubstitutionMap.containsKey(oldSignature)) {
						ive.setMethodRef(methodSubstitutionMap.get(oldSignature).makeRef());
					} 
				} else if (((Stmt)u) instanceof IdentityStmt && ((IdentityStmt)u).getRightOp() instanceof ParameterRef) {
					ParameterRef pr = (ParameterRef)((IdentityStmt)u).getRightOp();					
					((IdentityStmt)u).getRightOpBox().setValue(Jimple.v().newParameterRef(body.getMethod().getParameterType(pr.getIndex()), pr.getIndex()));
				}
				
					
					
				if (((Stmt) u).containsArrayRef()) {
					ArrayRef aref = ((Stmt) u).getArrayRef();
					// Note that the baseType has already been replaced, so it
					// is
					// a RefType not an ArrayType!
					RefType refType = (RefType) aref.getBase().getType();
					// check if its an array write or read.
					if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getLeftOp() instanceof ArrayRef) {
						// replace the a[i]=x by a.set(i,x);
						SootMethod am = refType.getSootClass().getMethodByName(arraySetName);
						Stmt ivk = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr((Local) aref.getBase(),
								am.makeRef(),
								Arrays.asList(new Value[] { aref.getIndex(), ((DefinitionStmt) u).getRightOp() })));
						ivk.addAllTagsOf(u);
						// replace u by ivk
						body.getUnits().insertAfter(ivk, u);
						body.getUnits().remove(u);
					} else {
						SootMethod am = refType.getSootClass().getMethodByName(arrayGetName);
						// replace the x = a[i] by x = a.get(i)
						Value ivk = Jimple.v().newVirtualInvokeExpr((Local) aref.getBase(), am.makeRef(),
								aref.getIndex());
						((Stmt) u).getArrayRefBox().setValue(ivk);
					}
				}

				if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof NewArrayExpr) {
					NewArrayExpr na = (NewArrayExpr) ((DefinitionStmt) u).getRightOp();
					// replace the NewArrayExpr by a NewExpr of
					// appropriate type.
					RefType refType = getArrayReplacementType((ArrayType) na.getType());
					((DefinitionStmt) u).getRightOpBox().setValue(Jimple.v().newNewExpr(refType));
					// now add a constructor call where we pass the size of the
					// array.
					SootClass arrClass = refType.getSootClass();
					SootMethod constructor = arrClass.getMethod(SootMethod.constructorName,
							Arrays.asList(new Type[] { IntType.v() }));
					Local lhs = (Local) ((DefinitionStmt) u).getLeftOp();
					Stmt ccall = Jimple.v()
							.newInvokeStmt(Jimple.v().newSpecialInvokeExpr(lhs, constructor.makeRef(), na.getSize()));
					ccall.addAllTagsOf(u);
					body.getUnits().insertAfter(ccall, u);
				} else if (u instanceof DefinitionStmt
						&& ((DefinitionStmt) u).getRightOp() instanceof NewMultiArrayExpr) {
					NewMultiArrayExpr na = (NewMultiArrayExpr) ((DefinitionStmt) u).getRightOp();
					RefType refType = getArrayReplacementType((ArrayType) na.getType());
					((DefinitionStmt) u).getRightOpBox().setValue(Jimple.v().newNewExpr(refType));
					SootClass arrClass = refType.getSootClass();
					List<Type> paramTypes = new ArrayList<Type>(
							Collections.nCopies(((ArrayType) na.getType()).numDimensions, IntType.v()));
					SootMethod constructor = arrClass.getMethod(SootMethod.constructorName, paramTypes);
					List<Value> args = new LinkedList<Value>(na.getSizes());
					while (args.size() < paramTypes.size()) {
						args.add(IntConstant.v(0));
					}
					Local lhs = (Local) ((DefinitionStmt) u).getLeftOp();
					Stmt ccall = Jimple.v()
							.newInvokeStmt(Jimple.v().newSpecialInvokeExpr(lhs, constructor.makeRef(), args));
					ccall.addAllTagsOf(u);
					body.getUnits().insertAfter(ccall, u);
				} else if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof LengthExpr) {
					LengthExpr le = (LengthExpr) ((DefinitionStmt) u).getRightOp();
					SootClass arrayClass = ((RefType) le.getOp().getType()).getSootClass();
					Value fieldRef = Jimple.v().newInstanceFieldRef(le.getOp(),
							arrayClass.getFieldByName(lengthFieldName).makeRef());
					((DefinitionStmt) u).getRightOpBox().setValue(fieldRef);
				}
			}
			body.validate();
		}
	}

	protected Type arrayTypeToRefType(Type t) {
		if (t instanceof ArrayType) {
			return getArrayReplacementType((ArrayType) t);
		}
		return t;
	}

	private final Map<Type, RefType> arrayTypeMap = new HashMap<Type, RefType>();

	protected RefType getArrayReplacementType(ArrayType t) {
		Type base = arrayTypeToRefType(t.getElementType());
		if (!this.arrayTypeMap.containsKey(base)) {
			SootClass arrClass = createArrayClass(base, t.numDimensions);
			this.arrayTypeMap.put(base, RefType.v(arrClass));
		}
		return this.arrayTypeMap.get(base);
	}

	protected SootClass createArrayClass(Type elementType, int numDimensions) {
		SootClass arrayClass = new SootClass(arrayTypeName + this.arrayTypeMap.size(),
				Modifier.PUBLIC | Modifier.FINAL);
		// set the superclass to object
		arrayClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
		// add the new class to the scene
		Scene.v().addClass(arrayClass);
		// add a field for array.length
		SootField lengthField = new SootField(lengthFieldName, RefType.v(Scene.v().getSootClass("java.lang.Integer")),
				Modifier.PUBLIC | Modifier.FINAL);
		arrayClass.addField(lengthField);

		// TODO create some fields of t.getElementType()
		SootMethod getElement = new SootMethod(arrayGetName, Arrays.asList(new Type[] { IntType.v() }), elementType,
				Modifier.PUBLIC);
		arrayClass.addMethod(getElement);
		JimpleBody body = Jimple.v().newBody(getElement);
		body.insertIdentityStmts();
		// TODO: add body
		body.getUnits().add(Jimple.v().newReturnStmt(SootTranslationHelpers.v().getDefaultValue(elementType)));
		getElement.setActiveBody(body);

		SootMethod setElement = new SootMethod(arraySetName, Arrays.asList(new Type[] { elementType, IntType.v() }),
				VoidType.v(), Modifier.PUBLIC);
		arrayClass.addMethod(setElement);
		body = Jimple.v().newBody(setElement);
		body.insertIdentityStmts();
		// TODO: add body
		body.getUnits().add(Jimple.v().newReturnVoidStmt());
		setElement.setActiveBody(body);

		// add a constructor
		// Now create a constructor that takes the array size as input
		List<Type> argTypes = new ArrayList<Type>(Collections.nCopies(numDimensions, IntType.v()));
		SootMethod constructor = new SootMethod(SootMethod.constructorName, argTypes, VoidType.v(), Modifier.PUBLIC);
		// add the constructor to the class.
		arrayClass.addMethod(constructor);

		body = Jimple.v().newBody(constructor);
		// add a local for the first param
		body.insertIdentityStmts();
		// set the length field.
		body.getUnits().add(Jimple.v().newAssignStmt(
				Jimple.v().newInstanceFieldRef(body.getThisLocal(), lengthField.makeRef()), body.getParameterLocal(0)));

		return arrayClass;
	}

}
