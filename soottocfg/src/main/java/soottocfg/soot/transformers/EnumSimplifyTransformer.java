/**
 *
 */
package soottocfg.soot.transformers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import soot.Body;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;


/**
 * @author schaef
 */
public class EnumSimplifyTransformer extends AbstractSceneTransformer {

    final Map<SootField, Integer> enumReplacementMap = new HashMap<>();
    final Set<SootField> switchMapFields = new HashSet<>();

    public void applyTransformation() {
        for (JimpleBody body : this.getSceneBodies()) {
            transform(body);
        }
    }


    private void transform(Body body) {
        final SootMethod method = body.getMethod();
        if (!method.isStaticInitializer() ||
            !method.getDeclaringClass().hasSuperclass() ||
            !"java.lang.Enum".equals(method.getDeclaringClass().getSuperclass().getName())) {
            return;
        }

        SootClass enumClass = method.getDeclaringClass();

        body.getUnits().forEach(u -> {
            if (u instanceof Stmt) {
                final Stmt s = (Stmt) u;
                if (s.containsInvokeExpr()
                    && s.getInvokeExpr().getMethod().isConstructor()
                    && s.getInvokeExpr().getMethod().getDeclaringClass().equals(enumClass)) {
                    if (s.getInvokeExpr().getArgCount() > 2) {
                        System.err.println("Not removing fancy enums");
                        return;
                    }
                    final String enumName = ((StringConstant) s.getInvokeExpr().getArg(0)).value;
                    final SootField enumField = enumClass.getFieldByName(enumName);
                    final int enumPos = ((IntConstant) s.getInvokeExpr().getArg(1)).value;

                    enumReplacementMap.put(enumField, enumPos);
                }
            }
        });


        final SootMethod valuesMethod = enumClass.getMethod("values", ImmutableList.of());
        final Collection<SootMethod> usingMethods = isUsedInApplicationClasses(ImmutableList.of(valuesMethod));
        if (!usingMethods.stream().allMatch(SootMethod::isStaticInitializer)) {
            System.out.println("*** can't handle enum " + enumClass.getName());
        }
        removeValuesFieldAndMethod(valuesMethod, body);

        // todo, go through all static initializers and remove the switch map.
        usingMethods.forEach(s -> removeSwitchMapRelatedUnits(s, enumClass));
        removeSwitchMapRelatedUnits(method, enumClass);
        // find all uses of the switch map and create an ite stmt using the enumReplacementMap

        replaceAllValueSwitches(enumClass);
        unstable_removeSwitchMapClasses();
    }

    // HACK!
    private void unstable_removeSwitchMapClasses() {
        switchMapFields.forEach(sf -> {
            if (Scene.v().getClasses().contains(sf.getDeclaringClass())) {
                System.err.println("Deleting switchmap class " + sf.getDeclaringClass());
                Scene.v().removeClass(sf.getDeclaringClass());
            }
        });
        final Set<SootClass> switchMapClasses = switchMapFields.stream().map(SootField::getDeclaringClass).collect(Collectors.toSet());
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod sm : sc.getMethods()) {
                if (sm.hasActiveBody()) {
                    Set<Unit> unitsToRemove = new HashSet<>();
                    for (Unit u : sm.retrieveActiveBody().getUnits()) {
                        if (u instanceof Stmt) {
                            final Stmt stmt = (Stmt) u;
                            if (stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof StaticInvokeExpr) {
                                final SootClass s_ = stmt.getInvokeExpr().getMethod().getDeclaringClass();
                                if (switchMapClasses.contains(s_)) {
                                    unitsToRemove.add(u);
                                    System.err.println("Removing call " + u + " in " + sm.getSignature());
                                }
                            }
                        }
                    }
                    sm.getActiveBody().getUnits().removeAll(unitsToRemove);
                }
            }
        }
    }

    public static class SwitchStmtMetaData {
        public Local enumLocal;
        public Local switchFieldLocal;
        public Unit preSwitchStmt;
    }

    /**
     * Detect and remove blocks of code generated for value switches over enums. E.g.:
     * $r2 = <MinePumpSystem.Environment$1: int[] $SwitchMap$MinePumpSystem$Environment$WaterLevelEnum>;
     * $r1 = r0.<MinePumpSystem.Environment: MinePumpSystem.Environment$WaterLevelEnum waterLevel>;
     * $i0 = virtualinvoke $r1.<MinePumpSystem.Environment$WaterLevelEnum: int ordinal()>();
     * $i1 = $r2[$i0];
     *
     * @param enumClass
     */
    private void replaceAllValueSwitches(final SootClass enumClass) {
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            if (sc.equals(enumClass)) continue;
            for (SootMethod sm : sc.getMethods()) {
                List<SwitchStmtMetaData> switchVars = new LinkedList<>();
                if (sm.hasActiveBody() && !sm.isStaticInitializer()) {
                    final Body body = sm.retrieveActiveBody();

                    SootField switchMap = null;
                    Local ordinal = null;
                    Local enumLocal = null;

                    Set<Unit> unitsToRemove = new HashSet<>();
                    Set<Local> localsToRemove = new HashSet<>();
                    final String switchMapName = String.format("$SwitchMap$%s",
                                                               enumClass.getName().replace('.', '$'));
                    for (Unit u : body.getUnits()) {
                        if (u instanceof Stmt) {
                            final Stmt stmt = (Stmt) u;
                            if (stmt.containsFieldRef() &&
                                stmt.getFieldRef().getField().getName().equals(switchMapName)) {
                                // we know its a definition stmt to assign the field to a local

                                final Local switchMapLocal = (Local) ((DefinitionStmt) stmt).getLeftOp();
                                localsToRemove.add(switchMapLocal);
                                unitsToRemove.add(u);
                                switchMap = stmt.getFieldRef().getField();

                                switchMapFields.add(switchMap);
                            } else if (stmt.containsInvokeExpr()
                                       && stmt.getInvokeExpr().getMethod().getName().contains("ordinal")) {
                                final InvokeExpr ordinalCall = stmt.getInvokeExpr();
                                if (ordinalCall instanceof InstanceInvokeExpr &&
                                    ((InstanceInvokeExpr) ordinalCall).getBase()
                                                                      .getType()
                                                                      .equals(RefType.v(enumClass))) {
                                    unitsToRemove.add(u);
                                    // we know its a definition stmt to assign the field to a local
                                    ordinal = (Local) ((DefinitionStmt) stmt).getLeftOp();
                                    localsToRemove.add(ordinal);
                                    enumLocal = (Local) ((InstanceInvokeExpr) stmt.getInvokeExpr()).getBase();
                                }
                            } else if (stmt.containsArrayRef()
//                                       && stmt.getArrayRef().getBase().equals(switchMap)
                                       && stmt.getArrayRef().getIndex().equals(ordinal)) {
                                // we know its a definition stmt to assign the field to a local
                                unitsToRemove.add(u);

                                SwitchStmtMetaData md = new SwitchStmtMetaData();
                                md.enumLocal = enumLocal;
                                md.switchFieldLocal = (Local) ((DefinitionStmt) stmt).getLeftOp();
                                md.preSwitchStmt = u;
                                switchVars.add(md);
                            }
                        }
                    }


                    if (switchVars.isEmpty()) {
                        continue;
                    }

                    body.getUnits().forEach(u -> {
                        for (ValueBox vb : u.getUseAndDefBoxes()) {
                            if (localsToRemove.contains(vb.getValue())) {
                                unitsToRemove.add(u);
                                break;
                            }
                        }
                    });

                    final Map<SootField, Local> enumLocals = new HashMap<>();
                    int i = 0;
                    for (SootField sf : enumReplacementMap.keySet()) {
                        final Unit firstNonIdentityStmt = ((JimpleBody) body).getFirstNonIdentityStmt();
                        final Local l = Jimple.v().newLocal("enumLocal" + (++i) + "_" + enumClass.getName(),
                                                            RefType.v(enumClass));
                        enumLocals.put(sf, l);
                        body.getLocals().add(l);

                        final Stmt stmt = Jimple.v().newAssignStmt(l, Jimple.v().newStaticFieldRef(sf.makeRef()));
                        body.getUnits().insertAfter(stmt, firstNonIdentityStmt);
                    }

                    switchVars.forEach(md -> {
                        assignSwitchVar(md, enumLocals, body);
                    });

                    body.getUnits().removeAll(unitsToRemove);
                    localsToRemove.forEach(body.getLocals()::remove);
                }
            }
        }
    }

    private void assignSwitchVar(final SwitchStmtMetaData switchStmtMetaData,
                                 final Map<SootField, Local> enumLocals,
                                 final Body body) {

        Unit target = body.getUnits().getSuccOf(switchStmtMetaData.preSwitchStmt);

        for (Map.Entry<SootField, Local> el : enumLocals.entrySet()) {
            final Value cond = Jimple.v().newNeExpr(el.getValue(), switchStmtMetaData.enumLocal);
            final Stmt ifStmt = Jimple.v().newIfStmt(cond, target);
            final Stmt assign = Jimple.v().newAssignStmt(switchStmtMetaData.switchFieldLocal,
                                                         IntConstant.v(enumReplacementMap.get(el.getKey())));
            body.getUnits().insertAfter(ImmutableList.of(ifStmt, assign), switchStmtMetaData.preSwitchStmt);
            target = ifStmt;
        }
    }


    private void removeSwitchMapRelatedUnits(final SootMethod caller, final SootClass enumClass) {
        final JimpleBody body = (JimpleBody) caller.getActiveBody();

        Set<Unit> unitsToRemove = new HashSet<>();
        Set<Local> localsToRemove = new HashSet<>();
        int currentSize;
        do {
            currentSize = unitsToRemove.size();
            for (Unit u : body.getUnits()) {
                if (u instanceof Stmt) {
                    final Stmt s = (Stmt) u;
                    boolean callsEnum = false;
                    if (s.containsInvokeExpr()
                        && (s.getInvokeExpr().getMethod().getName().equals("ordinal")
                            || s.getInvokeExpr().getMethod().getName().equals("values"))) {
                        callsEnum = true;
                    }

                    Set<Local> usedLocals = u.getUseAndDefBoxes()
                                             .stream()
                                             .map(ValueBox::getValue)
                                             .filter(v -> v instanceof Local)
                                             .map(v -> (Local) v)
                                             .collect(Collectors.toSet());
                    final boolean referencesEnum = u.getUseAndDefBoxes()
                                                    .stream()
                                                    .map(ValueBox::getValue)
                                                    .filter(v -> v instanceof SootField)
                                                    .filter(v -> ((SootField) v).getDeclaringClass().equals(enumClass))
                                                    .findAny().isPresent();

                    if (s instanceof DefinitionStmt && ((DefinitionStmt) s).getRightOp() instanceof FieldRef) {
                        boolean isLeftoverLocal = ((StaticFieldRef) ((DefinitionStmt) s).getRightOp())
                            .getField().getType().equals(RefType.v(enumClass));
                        if (isLeftoverLocal) {
                            localsToRemove.add((Local) ((DefinitionStmt) s).getLeftOp());
                            unitsToRemove.add(u);
                        }
                    }


                    final boolean containsAny = usedLocals.stream().anyMatch(localsToRemove::contains);
                    if (callsEnum || referencesEnum || containsAny) {
                        unitsToRemove.add(u);
                        localsToRemove.addAll(localsToRemove);
                    }
                }
            }
        } while (currentSize != unitsToRemove.size());

        body.getUnits().removeAll(unitsToRemove);
        localsToRemove.forEach(body.getLocals()::remove);
    }

    /**
     * Cleans up a static initializer of a class that uses a switch over an enum.
     * In the bytecode, an array called $SwtichMap$Enum name is created an populated.
     * Instead, we add a bunch of ITE-statements to methods where the switch is used.
     *
     * @param valuesMethod
     * @param staticInit
     */
    private void removeValuesFieldAndMethod(final SootMethod valuesMethod, final Body staticInit) {
        final SootClass enumClass = valuesMethod.getDeclaringClass();
        final SootField valueField = enumClass.getFieldByName("$VALUES");
        final Type valueFieldType = valueField.getType();

        enumClass.removeField(valueField);
        enumClass.removeMethod(valuesMethod);

        // now remove any reverence from the static initializer.
        // we know that this local exists by default.
        Local valuesLocal = staticInit.getLocals()
                                      .stream()
                                      .filter(local -> local.getType().equals(valueFieldType))
                                      .findFirst()
                                      .orElse(null);

        Preconditions.checkArgument(valuesLocal != null);

        List<Unit> unitsToRemove = new ArrayList<>();
        staticInit.getUnits().forEach(u -> {
            if (u instanceof Stmt) {
                final Stmt stmt = (Stmt) u;
                if (u.getUseAndDefBoxes().stream().anyMatch(vb -> vb.getValue().equals(valuesLocal))) {
//                if (stmt.containsArrayRef() && stmt.getArrayRef().getBase().equals(valuesLocal)) {
                    unitsToRemove.add(u);
                }
            }
        });
        staticInit.getUnits().removeAll(unitsToRemove);
        staticInit.getLocals().remove(valuesLocal);
    }

    private Collection<SootMethod> isUsedInApplicationClasses(Collection<SootMethod> methods) {
        final Collection<SootMethod> usedInMethods = new HashSet<>();
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            for (SootMethod sm : sc.getMethods()) {
                if (sm.hasActiveBody()) {
                    for (Unit u : sm.retrieveActiveBody().getUnits()) {
                        if (u instanceof Stmt) {
                            final Stmt st = (Stmt) u;
                            if (st.containsInvokeExpr() && methods.contains(st.getInvokeExpr().getMethod())) {
                                usedInMethods.add(sm);
                            }
                        }
                    }
                }
            }
        }
        return usedInMethods;
    }

//    private void replaceStaticEnumFieldRefsByIntegers(final Body body) {
//
//        Map<Unit, Unit> replaceMap = new HashMap<>();
//
//        for (Unit u : body.getUnits()) {
//            if (u instanceof Stmt) {
//                final Stmt s = (Stmt) u;
//                if (s.containsFieldRef() && enumReplacementMap.containsKey(s.getFieldRef().getField())) {
//                    final SootField staticEnumField = s.getFieldRef().getField();
//                    final IntConstant enumValue = IntConstant.v(enumReplacementMap.get(staticEnumField));
//                    if (s instanceof DefinitionStmt && ((((DefinitionStmt) s).getRightOp())) instanceof StaticFieldRef) {
//                        final Local lhs = (Local) ((DefinitionStmt) s).getLeftOp();
//                        lhs.setType(IntType.v());
//                        if (s instanceof IdentityStmt) {
//                            replaceMap.put(u, Jimple.v().newIdentityStmt(lhs, enumValue));
//                        } else {
//                            replaceMap.put(u, Jimple.v().newAssignStmt(lhs, enumValue));
//                        }
//                    } else if (s instanceof IfStmt && ((IfStmt) s).getCondition() instanceof BinopExpr) {
//                        final BinopExpr binopExpr = (BinopExpr) ((IfStmt) s).getCondition();
//                        if ((binopExpr.getOp1() instanceof StaticFieldRef) &&
//                            ((StaticFieldRef) binopExpr.getOp1()).getField().equals(staticEnumField)) {
//                            binopExpr.getOp1Box().setValue(enumValue);
//                        } else if ((binopExpr.getOp2() instanceof StaticFieldRef) &&
//                                   ((StaticFieldRef) binopExpr.getOp2()).getField().equals(staticEnumField)) {
//                            binopExpr.getOp1Box().setValue(enumValue);
//                        }
//
//                    }
//
//                }
//            }
//        }
//    }

}
