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

import com.google.common.base.Verify;

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
import soot.VoidType;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceOfExpr;
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
 *         The ArrayTransformer iterates over all classes in the Scene
 *         and replaces Java arrays by generated JayArrays.
 *         For each array type A[] we generate a corresponding JayArray type:
 *         class JayArray1 {
 *         public final int size;
 *         public JayArray1(int size);
 *         public A get(int idx);
 *         public void set (int idx, A elem);
 *         }
 *         For multi arrays, the constructor takes one int per dimension.
 * 
 *         Then ArrayTransformer replaces all usages of arrays:
 *         Array reads x=a[i] become x = a.get(i).
 *         Array writes a[i]=x become a.set(i,x).
 *         Array length a.length becomes a.$length.
 *         New array a = new A[1] becomes
 *         a = new JayArrayA;
 *         specialinvoke a.<init>(1);
 *         New multi array a = new A[1][2] becomes
 *         a = new JayArrayA;
 *         specialinvoke a.<init>(1, 2);
 * 
 *         Currently, get and set are not implemented, so the program behavior
 *         is changed.
 * 
 *         Further, the ArrayTransformer changes the signature of main(String[]
 *         args), so
 *         the program cannot be run from main after this transformation.
 */
public class ArrayTransformer extends AbstractSceneTransformer {

	public static final String arraySetName = "set";
	public static final String arrayGetName = "get";
	public static final String arrayTypeName = "JayArray";
	public static final String arrayElementPrefix = "atIndex";

	private static final int NumberOfModeledElements = 5;

	public ArrayTransformer() {

	}

	private final Map<String, SootField> fieldSubstitutionMap = new HashMap<String, SootField>();
	private final Map<String, SootMethod> methodSubstitutionMap = new HashMap<String, SootMethod>();

	public void applyTransformation() {
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
		List<SootMethod> entryPoints = new LinkedList<SootMethod>(Scene.v().getEntryPoints());
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
					// we also have to update the refs in the EntryPoint list.
					boolean wasMain = sm.isEntryMethod();
					if (wasMain) {
						entryPoints.remove(sm);
					}

					if (sc.resolvingLevel() >= SootClass.BODIES && sm.isConcrete() && !sc.isLibraryClass()
							&& !sc.isJavaLibraryClass()) {
						// record all methods for which we found a body.
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

					if (wasMain) {
						entryPoints.add(sm);
					}

					methodSubstitutionMap.put(oldSignature, sm);
				}
			}
		}
		Scene.v().setEntryPoints(entryPoints);

		for (JimpleBody body : bodies) {
			for (Local local : body.getLocals()) {
				local.setType(arrayTypeToRefType(local.getType()));
			}

			// now replace ArrayRefs and NewArray, NewMulitArray
			// statements.

			for (Unit u : new LinkedList<Unit>(body.getUnits())) {
				/*
				 * Changing the types from ArrayType to RefType breaks the soot
				 * 'references', FieldRef, MethodRef,
				 * and ParameterRef since they do not get updated automatically.
				 * Hence, we need to re-build these
				 * Refs by hand:
				 */
				if (((Stmt) u).containsFieldRef() && ((Stmt) u).getFieldRef().getType() instanceof ArrayType) {
					FieldRef fr = ((Stmt) u).getFieldRef();
					String sig = fr.getField().getSignature();
					Verify.verify(fieldSubstitutionMap.containsKey(sig), "No entry found for " + fr + " in stmt " + u);
					fr.setFieldRef(fieldSubstitutionMap.get(sig).makeRef());
				} else if (((Stmt) u).containsInvokeExpr()) {
					InvokeExpr ive = ((Stmt) u).getInvokeExpr();
					final String oldSignature = ive.getMethodRef().toString();
					if (methodSubstitutionMap.containsKey(oldSignature)) {
						ive.setMethodRef(methodSubstitutionMap.get(oldSignature).makeRef());
					}
				} else if (((Stmt) u) instanceof IdentityStmt
						&& ((IdentityStmt) u).getRightOp() instanceof ParameterRef) {
					ParameterRef pr = (ParameterRef) ((IdentityStmt) u).getRightOp();
					((IdentityStmt) u).getRightOpBox().setValue(Jimple.v()
							.newParameterRef(body.getMethod().getParameterType(pr.getIndex()), pr.getIndex()));
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
							arrayClass.getFieldByName(SootTranslationHelpers.lengthFieldName).makeRef());
					((DefinitionStmt) u).getRightOpBox().setValue(fieldRef);
				} else if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof InstanceOfExpr) {
					InstanceOfExpr ioe = (InstanceOfExpr) ((DefinitionStmt) u).getRightOp();
					if (ioe.getCheckType() instanceof ArrayType) {
						ioe.setCheckType(getArrayReplacementType((ArrayType) ioe.getCheckType()));
					}
				} else if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof CastExpr) {
					CastExpr ce = (CastExpr) ((DefinitionStmt) u).getRightOp();
					if (ce.getCastType() instanceof ArrayType) {
						ce.setCastType(getArrayReplacementType((ArrayType) ce.getCastType()));
					}
				} else if (u instanceof DefinitionStmt && ((DefinitionStmt) u).getRightOp() instanceof ClassConstant) {
					ClassConstant cc = (ClassConstant) ((DefinitionStmt) u).getRightOp();
					if (cc.getValue().contains("[")) {
						// TODO, here we have to parse the cc name and
						// find the corresponding array class.
						throw new RuntimeException("Not implemented: " + cc);
					}
				}
			}

			/*
			 * Changing the types of array fields
			 */
			for (SootClass sc : classes) {
				if (sc.resolvingLevel() < SootClass.SIGNATURES) {
					continue;
				}
				for (SootField f : new LinkedList<SootField>(sc.getFields())) {
					if (f.getType() instanceof ArrayType) {
						sc.removeField(f);
					}
				}
			}
			try {
				body.validate();
			} catch (RuntimeException e) {
				throw e;
			}
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
		SootClass arrayClass = new SootClass(
				arrayTypeName + "_" + elementType.toString().replace(".", "_").replace("$", "D"),
				Modifier.PUBLIC | Modifier.FINAL);
		// set the superclass to object
		arrayClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
		// add the new class to the scene
		Scene.v().addClass(arrayClass);
		// add a field for array.length
		SootField lengthField = new SootField(SootTranslationHelpers.lengthFieldName,
				RefType.v(Scene.v().getSootClass("java.lang.Integer")), Modifier.PUBLIC | Modifier.FINAL);
		arrayClass.addField(lengthField);

		// type of the array elements (e.g., float for float[])
		SootField elemTypeField = new SootField(SootTranslationHelpers.arrayElementTypeFieldName,
				RefType.v(Scene.v().getSootClass("java.lang.Class")), Modifier.PUBLIC | Modifier.FINAL);
		arrayClass.addField(elemTypeField);

		// dynamic type of the array.
		SootField typeField = new SootField(SootTranslationHelpers.typeFieldName,
				RefType.v(Scene.v().getSootClass("java.lang.Class")), Modifier.PUBLIC | Modifier.FINAL);
		arrayClass.addField(typeField);

		// create one field for the first N elements of the array which we
		// model precisely:
		SootField[] arrFields = new SootField[ArrayTransformer.NumberOfModeledElements];
		for (int i = 0; i < ArrayTransformer.NumberOfModeledElements; i++) {
			arrFields[i] = new SootField(ArrayTransformer.arrayElementPrefix + "_" + i, elementType, Modifier.PUBLIC);
			arrayClass.addField(arrFields[i]);
		}

		SootMethod getElement = new SootMethod(arrayGetName, Arrays.asList(new Type[] { IntType.v() }), elementType,
				Modifier.PUBLIC);
		arrayClass.addMethod(getElement);
		JimpleBody body = Jimple.v().newBody(getElement);
		body.insertIdentityStmts();
		Local retLocal = Jimple.v().newLocal("retVal", elementType);
		body.getLocals().add(retLocal);
		List<Unit> retStmts = new LinkedList<Unit>();
		for (int i = 0; i < ArrayTransformer.NumberOfModeledElements; i++) {
			Unit ret = Jimple.v().newAssignStmt(retLocal,
					Jimple.v().newInstanceFieldRef(body.getThisLocal(), arrFields[i].makeRef()));
			retStmts.add(ret);
			retStmts.add(Jimple.v().newReturnStmt(retLocal));
			Value cond = Jimple.v().newEqExpr(body.getParameterLocal(0), IntConstant.v(i));
			body.getUnits().add(Jimple.v().newIfStmt(cond, ret));
		}
		// if none of the modeled fields was requested, add return havoc as fall
		// through case.
		// ret = havoc; return ret;
		body.getUnits().add(Jimple.v().newAssignStmt(retLocal,
				Jimple.v().newStaticInvokeExpr(SootTranslationHelpers.v().getHavocMethod(elementType).makeRef())));
		body.getUnits().add(Jimple.v().newReturnStmt(retLocal));
		// now add all the return statements
		body.getUnits().addAll(retStmts);

		body.getUnits().add(Jimple.v().newReturnStmt(SootTranslationHelpers.v().getDefaultValue(elementType)));
		getElement.setActiveBody(body);

		SootMethod setElement = new SootMethod(arraySetName, Arrays.asList(new Type[] { elementType, IntType.v() }),
				VoidType.v(), Modifier.PUBLIC);
		arrayClass.addMethod(setElement);
		body = Jimple.v().newBody(setElement);
		body.insertIdentityStmts();

		List<Unit> updates = new LinkedList<Unit>();
		for (int i = 0; i < ArrayTransformer.NumberOfModeledElements; i++) {
			Unit asn = Jimple.v().newAssignStmt(
					Jimple.v().newInstanceFieldRef(body.getThisLocal(), arrFields[i].makeRef()),
					body.getParameterLocal(0));
			updates.add(asn);
			updates.add(Jimple.v().newReturnVoidStmt());
			Value cond = Jimple.v().newEqExpr(body.getParameterLocal(1), IntConstant.v(i));
			body.getUnits().add(Jimple.v().newIfStmt(cond, asn));
		}
		body.getUnits().addAll(updates);

		body.getUnits().add(Jimple.v().newReturnVoidStmt());
		setElement.setActiveBody(body);

		// add a constructor
		// Now create constructors that takes the array size as input
		// For int[][][] we have to create 3 constructors since one
		// could create new int[1][][], new int[1][2][], or new int[1][2][3]
		for (int i = 1; i <= numDimensions; i++) {
			List<Type> argTypes = new ArrayList<Type>(Collections.nCopies(i, IntType.v()));
			SootMethod constructor = new SootMethod(SootMethod.constructorName, argTypes, VoidType.v(),
					Modifier.PUBLIC);
			// add the constructor to the class.
			arrayClass.addMethod(constructor);

			body = Jimple.v().newBody(constructor);
			// add a local for the first param
			body.insertIdentityStmts();
			// set the length field.
			body.getUnits()
					.add(Jimple.v().newAssignStmt(
							Jimple.v().newInstanceFieldRef(body.getThisLocal(), lengthField.makeRef()),
							body.getParameterLocal(0)));
			// set the element type
			String elementTypeName = elementType.toString();
			if (elementType instanceof RefType) {
				elementTypeName = ((RefType) elementType).getSootClass().getJavaStyleName();
			}
			elementTypeName = elementTypeName.replace('.', '/');
			body.getUnits()
					.add(Jimple.v().newAssignStmt(
							Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemTypeField.makeRef()),
							ClassConstant.v(elementTypeName)));
			body.getUnits().add(Jimple.v().newReturnVoidStmt());
			constructor.setActiveBody(body);
		}
		return arrayClass;
	}

}
