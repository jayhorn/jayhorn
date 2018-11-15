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
 * @author rodykers
 * 
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

	public final static String AElem = "$arElem";

	private final Map<Type, RefType> arrayTypeMap = new HashMap<Type, RefType>();
	
	// private static final int NumberOfModeledElements = 0;

	public static boolean isArrayClass(SootClass sc) {
		return sc.getName().startsWith(arrayTypeName);
	}

	public ArrayTransformer() {

	}

	private final Map<String, SootField> fieldSubstitutionMap = new HashMap<String, SootField>();
	private final Map<String, SootMethod> methodSubstitutionMap = new HashMap<String, SootMethod>();

	public void applyTransformation() {
		
		// make sure we have the Object[] replacement for this dimension
		ArrayType objAr = ArrayType.v(Scene.v().getSootClass("java.lang.Object").getType(), 1);
		RefType objArRepl = getArrayReplacementType(objAr);
		
		
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
						
						// bit of a hack...
						if (! refType.getSootClass().declaresMethodByName(arraySetName))
							refType = objArRepl;
						
						SootMethod am = refType.getSootClass().getMethodByName(arraySetName);
						Stmt ivk = Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr((Local) aref.getBase(),
								am.makeRef(),
								Arrays.asList(new Value[] { aref.getIndex(), ((DefinitionStmt) u).getRightOp() })));
						ivk.addAllTagsOf(u);
						// replace u by ivk
						body.getUnits().insertAfter(ivk, u);
						body.getUnits().remove(u);
					} else {
						
						// bit of a hack...
						if (! refType.getSootClass().declaresMethodByName(arrayGetName))
							refType = objArRepl;
						
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
					
					// bit of a hack...
					if (! arrayClass.declaresMethodByName(arraySetName))
						arrayClass = objArRepl.getSootClass();
					
					SootField lenField = arrayClass.getFieldByName(SootTranslationHelpers.lengthFieldName);
					Value fieldRef = Jimple.v().newInstanceFieldRef(le.getOp(),
							lenField.makeRef());
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
			} catch (soot.validation.ValidationException e) {
				throw e;				
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

		// set the superclass to object for now (there will be a pass to fix this later
		SootClass objCls = Scene.v().getSootClass("java.lang.Object");
		Type objType = objCls.getType();

		if (elementType instanceof RefType 
				&& !elementType.equals(objType) 
				&& !elementType.toString().contains("java_lang_Object")
				) {

			// super class is JayArray_java_lang_Object
			SootClass objReplCls = this.arrayTypeMap.get(objType).getSootClass();
			arrayClass.setSuperclass(objReplCls);

			// Build constructor

			List<Type> argTypes = new ArrayList<Type>(Collections.nCopies(numDimensions, IntType.v()));
			SootMethod constructor = new SootMethod(SootMethod.constructorName, argTypes, VoidType.v(),
					Modifier.PUBLIC);
			// add the constructor to the class.
			arrayClass.addMethod(constructor);

			JimpleBody body = Jimple.v().newBody(constructor);
			// add a local for the first param
			body.insertIdentityStmts();

			// Add call to superclass constructor
			ArrayType objAr = ArrayType.v(Scene.v().getSootClass("java.lang.Object").getType(), numDimensions);
			SootClass objArrayClass = getArrayReplacementType(objAr).getSootClass();
			SootMethod superConstructor = objArrayClass.getMethod(SootMethod.constructorName, argTypes);
			body.getUnits().add(Jimple.v().newInvokeStmt(
					Jimple.v().newSpecialInvokeExpr(body.getThisLocal(),
							superConstructor.makeRef(), 
							body.getParameterLocals())));

			Stmt retStmt = Jimple.v().newReturnVoidStmt();

			// set the element type
			RefType objArReplacement = getArrayReplacementType(objAr);
			SootField elemTypeField = objArReplacement.getSootClass().getFieldByName(
					SootTranslationHelpers.arrayElementTypeFieldName);
			String elementTypeName = ((RefType) elementType).getSootClass().getJavaStyleName();
			elementTypeName = elementTypeName.replace('.', '/');
			body.getUnits().add(Jimple.v().newAssignStmt(
					Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemTypeField.makeRef()),
					ClassConstant.v(elementTypeName)));

			body.getUnits().add(retStmt);
			body.validate();
			constructor.setActiveBody(body);

		} else {

			// super class is java.lang.Object
			arrayClass.setSuperclass(objCls);

			// add a field for array.length
			SootField lengthField = new SootField(SootTranslationHelpers.lengthFieldName, IntType.v(),
					Modifier.PUBLIC | Modifier.FINAL);
			arrayClass.addField(lengthField);

			// type of the array elements (e.g., float for float[])
			SootField elemTypeField = new SootField(SootTranslationHelpers.arrayElementTypeFieldName,
					RefType.v(Scene.v().getSootClass("java.lang.Class")), Modifier.PUBLIC | Modifier.FINAL);
			arrayClass.addField(elemTypeField);

			// number of exactly modeled elements
			int num_exact = soottocfg.Options.v().exactArrayElements();
			if (num_exact < 0)
				num_exact = 0;

			/**
			 * New array model stuff
			 */
			if (numDimensions <= 1 && elementType instanceof RefType)
				elementType = Scene.v().getSootClass("java.lang.Object").getType();
			SootField elemField = new SootField(AElem, elementType);
			if (soottocfg.Options.v().arrayInv()) {
				arrayClass.addField(elemField);
			}

			// create one field for the first N elements of the array which we
			// model precisely:
			SootField[] arrFields = new SootField[num_exact];
			for (int i = 0; i < num_exact; i++) {
				arrFields[i] = new SootField(ArrayTransformer.arrayElementPrefix + "_" + i, elementType, Modifier.PUBLIC);
				arrayClass.addField(arrFields[i]);
			}

			/**
			 * GET METHOD
			 */

			SootMethod getElement = new SootMethod(arrayGetName, Arrays.asList(new Type[] { IntType.v() }), elementType,
					Modifier.PUBLIC);
			arrayClass.addMethod(getElement);
			JimpleBody body = Jimple.v().newBody(getElement);
			body.insertIdentityStmts();
			Local retLocal = Jimple.v().newLocal("retVal", elementType);
			body.getLocals().add(retLocal);
			List<Unit> retStmts = new LinkedList<Unit>();

			for (int i = 0; i < num_exact; i++) {
				Unit ret = Jimple.v().newAssignStmt(retLocal,
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), arrFields[i].makeRef()));
				retStmts.add(ret);
				retStmts.add(Jimple.v().newReturnStmt(retLocal));
				Value cond = Jimple.v().newEqExpr(body.getParameterLocal(0), IntConstant.v(i));
				body.getUnits().add(Jimple.v().newIfStmt(cond, ret));
			}

			/**
			 * New array model stuff
			 */
			if (soottocfg.Options.v().arrayInv()) {
				// ret = element; return ret;
				Unit ret = Jimple.v().newAssignStmt(retLocal,
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemField.makeRef()));
				body.getUnits().add(ret);
				body.getUnits().add(Jimple.v().newReturnStmt(retLocal));
			} else {
				// if none of the modeled fields was requested, add return havoc as
				// fall
				// through case.
				// ret = havoc; return ret;
				//			body.getUnits().add(Jimple.v().newAssignStmt(retLocal,
				//					Jimple.v().newStaticInvokeExpr(SootTranslationHelpers.v().getHavocMethod(elementType).makeRef())));
				//			body.getUnits().add(Jimple.v().newReturnStmt(retLocal));
				throw new RuntimeException("Let's stick to the new Array model");
			}

			// now add all the return statements
			body.getUnits().addAll(retStmts);

			body.validate();
			getElement.setActiveBody(body);

			/**
			 * SET METHOD
			 */

			SootMethod setElement = new SootMethod(arraySetName, Arrays.asList(new Type[] { IntType.v(), elementType }),
					VoidType.v(), Modifier.PUBLIC);
			arrayClass.addMethod(setElement);
			body = Jimple.v().newBody(setElement);
			body.insertIdentityStmts();

			List<Unit> updates = new LinkedList<Unit>();
			for (int i = 0; i < num_exact; i++) {
				Unit asn = Jimple.v().newAssignStmt(
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), arrFields[i].makeRef()),
						body.getParameterLocal(1));
				updates.add(asn);
				updates.add(Jimple.v().newReturnVoidStmt());
				Value cond = Jimple.v().newEqExpr(body.getParameterLocal(0), IntConstant.v(i));
				body.getUnits().add(Jimple.v().newIfStmt(cond, asn));
			}

			/**
			 * New array model stuff
			 */
			if (soottocfg.Options.v().arrayInv()) {
				Unit asn = Jimple.v().newAssignStmt(
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemField.makeRef()),
						body.getParameterLocal(1));
				body.getUnits().add(asn);
			}

			body.getUnits().add(Jimple.v().newReturnVoidStmt());
			body.getUnits().addAll(updates);

			body.validate();
			setElement.setActiveBody(body);

			/**
			 * CONSTRUCTOR
			 */
			/*
			 * Assume you have an:
			 * int[][][] arr;
			 * you can initialize that with
			 * arr = new int[1][][];
			 * arr = new int[1][2][];
			 * or
			 * arr = new int[1][2][3];
			 * so you need to create one constructor for each dimension ...
			 * a bit annoying, but sound.
			 * 
			 */
			if (numDimensions > 1 && !elementType.toString().contains("_java_"))
			{
				System.err.println("[WARNING] Multi-dimensional arrays not supported. Result will be unsound.");
			}

			// Now create constructors that takes the array size as input
			// For int[][][] we have to create 3 constructors since one
			// could create new int[1][][], new int[1][2][], or new int[1][2][3]

			// first, add the signatures for all constructors to the class.
			// this is necessary, because the constructors call each other.
			SootMethod[] constructors = new SootMethod[numDimensions];
			for (int i = 0; i < numDimensions; i++) {
				List<Type> argTypes = new ArrayList<Type>(Collections.nCopies(i + 1, IntType.v()));
				SootMethod constructor = new SootMethod(SootMethod.constructorName, argTypes, VoidType.v(),
						Modifier.PUBLIC);
				// add the constructor to the class.
				arrayClass.addMethod(constructor);
				constructors[i] = constructor;
			}

			/*
			 * Now create the bodies for all constructors. Note that this
			 * loop starts from 1 not from 0.
			 * For simple arrays, we initialize all elements to default values
			 * (i.e., zero or null). For multi arrays, we may have to initialize
			 * the elements to new arrays. E.g.,
			 * for new int[1][2] we create a constructor call
			 * <init>(1, 2) which contains one element of type int[] and
			 * we have to initialize this to a new array int[2] instead of null.
			 */
			for (int i = 1; i <= numDimensions; i++) {
				SootMethod constructor = constructors[i - 1];
				body = Jimple.v().newBody(constructor);
				// add a local for the first param
				body.insertIdentityStmts();

				Local newElement = Jimple.v().newLocal("elem", elementType);
				body.getLocals().add(newElement);

				Stmt retStmt = Jimple.v().newReturnVoidStmt();

				// create the assignment for the length field, so we can jump back to it
				Stmt lengthSet = Jimple.v().newAssignStmt(
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), lengthField.makeRef()),
						body.getParameterLocal(0));

				// set the length field.
				body.getUnits().add(lengthSet);

				// set the element type
				String elementTypeName = elementType.toString();
				if (elementType instanceof RefType) {
					elementTypeName = ((RefType) elementType).getSootClass().getJavaStyleName();
				}
				elementTypeName = elementTypeName.replace('.', '/');
				body.getUnits().add(Jimple.v().newAssignStmt(
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemTypeField.makeRef()),
						ClassConstant.v(elementTypeName)));

				// set element to default value
				Unit def = Jimple.v().newAssignStmt(
						Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemField.makeRef()),
						SootTranslationHelpers.v().getDefaultValue(elemField.getType()));
				body.getUnits().add(def);

				// initialize inner arrays if multi-dimensional
				if (i > 1) {
					initializeMultiArrayVars(elementType, elemField, newElement,
							body, setElement, constructor, retStmt);
				}	

				// pull element if multi-dim or set to default value else
				if (i > 1) {
					// TODO exactly modeled fields

					// for new array model
					Unit asn1 = Jimple.v().newAssignStmt(
							newElement,
							Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemField.makeRef()));
					body.getUnits().add(asn1);
					//				Unit asn = Jimple.v().newAssignStmt(
					//						Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemField.makeRef()),
					//						newElement);
					//				body.getUnits().add(asn);
				} else {
					// for exactly modeled fields
					for (int j = 0; j < num_exact; j++) {
						Unit asn = Jimple.v().newAssignStmt(
								Jimple.v().newInstanceFieldRef(body.getThisLocal(), arrFields[j].makeRef()),
								SootTranslationHelpers.v().getDefaultValue(arrFields[j].getType()));
						body.getUnits().add(asn);
					}

					// for new array model
					if (soottocfg.Options.v().arrayInv()) {
						Unit asn = Jimple.v().newAssignStmt(
								Jimple.v().newInstanceFieldRef(body.getThisLocal(), elemField.makeRef()),
								SootTranslationHelpers.v().getDefaultValue(elemField.getType()));
						body.getUnits().add(asn);
					}
				}

				body.getUnits().add(retStmt);
				body.validate();
				constructor.setActiveBody(body);
			}
		}
		
		// add the new class to the scene
		Scene.v().addClass(arrayClass);
		arrayClass.setResolvingLevel(SootClass.BODIES);
		arrayClass.setApplicationClass();

		return arrayClass;
	}

	private void initializeMultiArrayVars(Type elementType, SootField elemField, Local newElement,
			JimpleBody body, SootMethod setElement,
			SootMethod constructor, Stmt returnStmt) {
		/*
		 * Create n objects of the next smaller dimension of
		 * appropriate size.
		 * 
		 * ctr = 0;
		 * LoopHead: if (ctr>=param) goto exit;
		 * 
		 * this.set(
		 */
		Local counter = Jimple.v().newLocal("ctr", IntType.v());
		body.getLocals().add(counter);
		body.getUnits().add(Jimple.v().newAssignStmt(counter, IntConstant.v(0)));
		
		Stmt loopHead = Jimple.v().newIfStmt(Jimple.v().newGeExpr(counter, body.getParameterLocal(0)),
				returnStmt);
		body.getUnits().add(loopHead);

		RefType elRefType = (RefType) elementType;
		
		//create a new object				
		body.getUnits().add(Jimple.v().newAssignStmt(newElement, Jimple.v().newNewExpr(elRefType)));
		//the elements have one dimension less than the current one.
		int elParamCount = constructor.getParameterCount()-1;
		//call the constructor
		List<Type> parameterTypes = new ArrayList<Type>(Collections.nCopies(elParamCount, IntType.v()));
		SootMethod elConstructor = elRefType.getSootClass().getMethod(SootMethod.constructorName, parameterTypes, VoidType.v());
		List<Value> elConstructorArgs = new LinkedList<Value>();
		for (int k=1; k<constructor.getParameterCount();k++) {
			elConstructorArgs.add(body.getParameterLocal(k));
		}
		body.getUnits().add(
				Jimple.v().newInvokeStmt(
						Jimple.v().newSpecialInvokeExpr(newElement, elConstructor.makeRef(), elConstructorArgs)
						));
		
		//update the current field to that new object.
		List<Value> args = new LinkedList<Value>();
		args.add(counter);
		args.add(newElement);
		
		body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(body.getThisLocal(), setElement.makeRef(), args)));
		body.getUnits().add(Jimple.v().newAssignStmt(counter, Jimple.v().newAddExpr(counter, IntConstant.v(1))));
		body.getUnits().add(Jimple.v().newGotoStmt(loopHead));
	}
	
//	private void fixSubtypingRelation() {
//		for (Map.Entry<Type, RefType> t : this.arrayTypeMap.entrySet()) {
//			for (int numDimensions : this.arrayDimensionMap.get(t.getValue())) {
//			
//				Type elementType = t.getKey();
//				SootClass arrayClass = t.getValue().getSootClass();
//
//				// if elements are of ref type T extends S, set super class to S[]
//				if (elementType instanceof RefType) {
//					SootClass superCls = getRefArraySuperClass((RefType) elementType, numDimensions);
////					System.out.println("Superclass of " + arrayClass.getName() + " set to " + superCls.getName());
//					arrayClass.setSuperclass(superCls);
//				}
//			}
//		}
//	}
//	
//	private SootClass getRefArraySuperClass(RefType elType, int numDimensions) {
//		SootClass obj = Scene.v().getSootClass("java.lang.Object");
//		RefType objType = obj.getType();
//		
//		// try to find array superclass
//		// e.g. see if there is a jayhorn equivalent of Number[] for Integer[]
//		SootClass cur = elType.getSootClass();
//		while (!cur.equals(obj) && cur.resolvingLevel() >= SootClass.HIERARCHY) {
//			ArrayType at = ArrayType.v(cur.getType(), numDimensions);
//			if (this.arrayTypeMap.containsKey(at)) {
//				return this.arrayTypeMap.get(at).getSootClass();
//			}
//			cur = cur.getSuperclass();
//		}
//		
//		// else return Object[] jayarray if it exists
//		if (!elType.equals(objType) && this.arrayTypeMap.containsKey(objType)) {
//			return this.arrayTypeMap.get(objType).getSootClass();
//		}
//		
//		// else superclass is java.lang.Object
//		return obj;
//	}

}
