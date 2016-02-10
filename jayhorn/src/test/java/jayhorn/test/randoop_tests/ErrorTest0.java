package jayhorn.test.randoop_tests;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ErrorTest0 {

  public static boolean debug = false;

  @Test
  public void test1() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test1"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType3 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr4 = princessProver1.mkHornVariable("", (jayhorn.solver.ProverType)boolType3);
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.ProverExpr[] proverExpr_array13 = new jayhorn.solver.ProverExpr[] { proverExpr12 };
    jayhorn.solver.princess.PrincessProver princessProver14 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType16 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr17 = princessProver14.mkHornVariable("", (jayhorn.solver.ProverType)boolType16);
    jayhorn.solver.ProverExpr[] proverExpr_array18 = new jayhorn.solver.ProverExpr[] { proverExpr17 };
    jayhorn.solver.ProverExpr proverExpr19 = princessProver1.substitute(proverExpr8, proverExpr_array13, proverExpr_array18);
    jayhorn.solver.princess.PrincessProver princessProver20 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType22 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr23 = princessProver20.mkHornVariable("", (jayhorn.solver.ProverType)boolType22);
    jayhorn.solver.ProverExpr proverExpr24 = princessProver0.mkLeq(proverExpr8, proverExpr23);
    jayhorn.solver.princess.PrincessProver princessProver25 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType27 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr28 = princessProver25.mkHornVariable("", (jayhorn.solver.ProverType)boolType27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.ProverExpr[] proverExpr_array41 = new jayhorn.solver.ProverExpr[] { proverExpr40 };
    jayhorn.solver.princess.PrincessProver princessProver42 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType44 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr45 = princessProver42.mkHornVariable("", (jayhorn.solver.ProverType)boolType44);
    jayhorn.solver.ProverExpr[] proverExpr_array46 = new jayhorn.solver.ProverExpr[] { proverExpr45 };
    jayhorn.solver.ProverExpr proverExpr47 = princessProver29.substitute(proverExpr36, proverExpr_array41, proverExpr_array46);
    jayhorn.solver.princess.PrincessProver princessProver48 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType50 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr51 = princessProver48.mkHornVariable("", (jayhorn.solver.ProverType)boolType50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.ProverExpr[] proverExpr_array60 = new jayhorn.solver.ProverExpr[] { proverExpr59 };
    jayhorn.solver.princess.PrincessProver princessProver61 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType63 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr64 = princessProver61.mkHornVariable("", (jayhorn.solver.ProverType)boolType63);
    jayhorn.solver.ProverExpr[] proverExpr_array65 = new jayhorn.solver.ProverExpr[] { proverExpr64 };
    jayhorn.solver.ProverExpr proverExpr66 = princessProver48.substitute(proverExpr55, proverExpr_array60, proverExpr_array65);
    jayhorn.solver.ProverExpr proverExpr67 = princessProver0.mkStore(proverExpr28, proverExpr_array41, proverExpr55);
    jayhorn.solver.IntType intType69 = jayhorn.solver.IntType.INSTANCE;
    java.lang.String str70 = intType69.toString();
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr proverExpr71 = princessProver0.mkBoundVariable((-1), (jayhorn.solver.ProverType)intType69);

  }

  @Test
  public void test2() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test2"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType6 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr7 = princessProver4.mkHornVariable("", (jayhorn.solver.ProverType)boolType6);
    jayhorn.solver.princess.PrincessProver princessProver8 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType10 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr11 = princessProver8.mkHornVariable("", (jayhorn.solver.ProverType)boolType10);
    jayhorn.solver.ProverExpr[] proverExpr_array12 = new jayhorn.solver.ProverExpr[] { proverExpr11 };
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.ProverExpr proverExpr18 = princessProver0.substitute(proverExpr7, proverExpr_array12, proverExpr_array17);
    jayhorn.solver.princess.PrincessProver princessProver19 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver20 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType22 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr23 = princessProver20.mkHornVariable("", (jayhorn.solver.ProverType)boolType22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType26 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr27 = princessProver24.mkHornVariable("", (jayhorn.solver.ProverType)boolType26);
    jayhorn.solver.princess.PrincessProver princessProver28 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType30 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr31 = princessProver28.mkHornVariable("", (jayhorn.solver.ProverType)boolType30);
    jayhorn.solver.ProverExpr[] proverExpr_array32 = new jayhorn.solver.ProverExpr[] { proverExpr31 };
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.ProverExpr[] proverExpr_array37 = new jayhorn.solver.ProverExpr[] { proverExpr36 };
    jayhorn.solver.ProverExpr proverExpr38 = princessProver20.substitute(proverExpr27, proverExpr_array32, proverExpr_array37);
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.ProverExpr proverExpr43 = princessProver19.mkLeq(proverExpr27, proverExpr42);
    jayhorn.solver.princess.PrincessProver princessProver44 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.princess.PrincessProver princessProver49 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType51 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr52 = princessProver49.mkHornVariable("", (jayhorn.solver.ProverType)boolType51);
    jayhorn.solver.princess.PrincessProver princessProver53 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType55 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr56 = princessProver53.mkHornVariable("", (jayhorn.solver.ProverType)boolType55);
    jayhorn.solver.ProverExpr[] proverExpr_array57 = new jayhorn.solver.ProverExpr[] { proverExpr56 };
    jayhorn.solver.princess.PrincessProver princessProver58 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType60 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr61 = princessProver58.mkHornVariable("", (jayhorn.solver.ProverType)boolType60);
    jayhorn.solver.ProverExpr[] proverExpr_array62 = new jayhorn.solver.ProverExpr[] { proverExpr61 };
    jayhorn.solver.ProverExpr proverExpr63 = princessProver45.substitute(proverExpr52, proverExpr_array57, proverExpr_array62);
    jayhorn.solver.princess.PrincessProver princessProver64 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType66 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr67 = princessProver64.mkHornVariable("", (jayhorn.solver.ProverType)boolType66);
    jayhorn.solver.ProverExpr proverExpr68 = princessProver44.mkLeq(proverExpr52, proverExpr67);
    jayhorn.solver.ProverExpr proverExpr69 = princessProver0.mkPlus(proverExpr42, proverExpr52);
    jayhorn.solver.princess.PrincessProver princessProver71 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType73 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr74 = princessProver71.mkHornVariable("", (jayhorn.solver.ProverType)boolType73);
    jayhorn.solver.ProverExpr proverExpr75 = princessProver0.mkVariable("hi!", (jayhorn.solver.ProverType)boolType73);
    jayhorn.util.SimplCfgToProver simplCfgToProver76 = new jayhorn.util.SimplCfgToProver((jayhorn.solver.Prover)princessProver0);
    princessProver0.push();
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverResult proverResult79 = princessProver0.nextModel(false);

  }

}
