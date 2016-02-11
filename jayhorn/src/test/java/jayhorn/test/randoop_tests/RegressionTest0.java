package jayhorn.test.randoop_tests;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest0 {

  public static boolean debug = false;

  @Test
  public void test01() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test01"); }


    jayhorn.util.SourceLocationUtil sourceLocationUtil0 = new jayhorn.util.SourceLocationUtil();

  }

  @Test
  public void test02() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test02"); }


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
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.ProverExpr[] proverExpr_array43 = new jayhorn.solver.ProverExpr[] { proverExpr42 };
    jayhorn.solver.ProverExpr proverExpr44 = princessProver26.substitute(proverExpr33, proverExpr_array38, proverExpr_array43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.ProverExpr proverExpr49 = princessProver25.mkLeq(proverExpr33, proverExpr48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = princessProver0.freeVariables(proverExpr49);
    jayhorn.solver.princess.PrincessProver princessProver51 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType53 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr54 = princessProver51.mkHornVariable("", (jayhorn.solver.ProverType)boolType53);
    jayhorn.solver.princess.PrincessProver princessProver55 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType57 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr58 = princessProver55.mkHornVariable("", (jayhorn.solver.ProverType)boolType57);
    jayhorn.solver.princess.PrincessProver princessProver59 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType61 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr62 = princessProver59.mkHornVariable("", (jayhorn.solver.ProverType)boolType61);
    jayhorn.solver.ProverExpr[] proverExpr_array63 = new jayhorn.solver.ProverExpr[] { proverExpr62 };
    jayhorn.solver.princess.PrincessProver princessProver64 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType66 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr67 = princessProver64.mkHornVariable("", (jayhorn.solver.ProverType)boolType66);
    jayhorn.solver.ProverExpr[] proverExpr_array68 = new jayhorn.solver.ProverExpr[] { proverExpr67 };
    jayhorn.solver.ProverExpr proverExpr69 = princessProver51.substitute(proverExpr58, proverExpr_array63, proverExpr_array68);
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver71 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType73 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr74 = princessProver71.mkHornVariable("", (jayhorn.solver.ProverType)boolType73);
    jayhorn.solver.princess.PrincessProver princessProver75 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType77 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr78 = princessProver75.mkHornVariable("", (jayhorn.solver.ProverType)boolType77);
    jayhorn.solver.princess.PrincessProver princessProver79 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType81 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr82 = princessProver79.mkHornVariable("", (jayhorn.solver.ProverType)boolType81);
    jayhorn.solver.ProverExpr[] proverExpr_array83 = new jayhorn.solver.ProverExpr[] { proverExpr82 };
    jayhorn.solver.princess.PrincessProver princessProver84 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType86 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr87 = princessProver84.mkHornVariable("", (jayhorn.solver.ProverType)boolType86);
    jayhorn.solver.ProverExpr[] proverExpr_array88 = new jayhorn.solver.ProverExpr[] { proverExpr87 };
    jayhorn.solver.ProverExpr proverExpr89 = princessProver71.substitute(proverExpr78, proverExpr_array83, proverExpr_array88);
    jayhorn.solver.princess.PrincessProver princessProver90 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType92 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr93 = princessProver90.mkHornVariable("", (jayhorn.solver.ProverType)boolType92);
    jayhorn.solver.ProverExpr proverExpr94 = princessProver70.mkLeq(proverExpr78, proverExpr93);
    // The following exception was thrown during execution in test generation
    try {
      jayhorn.solver.ProverExpr proverExpr95 = princessProver0.mkGeq(proverExpr58, proverExpr94);
      org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException");
    } catch (java.lang.ClassCastException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.ClassCastException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array83);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType86);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType92);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr93);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr94);

  }

  @Test
  public void test03() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test03"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.io.File[] file_array1 = new java.io.File[] {  };
    // The following exception was thrown during execution in test generation
    try {
      java.util.List list2 = javac0.compile(file_array1);
      org.junit.Assert.fail("Expected exception of type soottocfg.randoop.Command.CommandFailedException");
    } catch (soottocfg.randoop.Command.CommandFailedException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("soottocfg.randoop.Command.CommandFailedException")) {
        org.junit.Assert.fail("Expected exception of type soottocfg.randoop.Command.CommandFailedException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array1);

  }

  @Test
  public void test04() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test04"); }


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
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver71 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType73 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr74 = princessProver71.mkHornVariable("", (jayhorn.solver.ProverType)boolType73);
    jayhorn.solver.princess.PrincessProver princessProver75 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType77 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr78 = princessProver75.mkHornVariable("", (jayhorn.solver.ProverType)boolType77);
    jayhorn.solver.princess.PrincessProver princessProver79 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType81 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr82 = princessProver79.mkHornVariable("", (jayhorn.solver.ProverType)boolType81);
    jayhorn.solver.ProverExpr[] proverExpr_array83 = new jayhorn.solver.ProverExpr[] { proverExpr82 };
    jayhorn.solver.princess.PrincessProver princessProver84 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType86 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr87 = princessProver84.mkHornVariable("", (jayhorn.solver.ProverType)boolType86);
    jayhorn.solver.ProverExpr[] proverExpr_array88 = new jayhorn.solver.ProverExpr[] { proverExpr87 };
    jayhorn.solver.ProverExpr proverExpr89 = princessProver71.substitute(proverExpr78, proverExpr_array83, proverExpr_array88);
    jayhorn.solver.princess.PrincessProver princessProver90 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType92 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr93 = princessProver90.mkHornVariable("", (jayhorn.solver.ProverType)boolType92);
    jayhorn.solver.ProverExpr proverExpr94 = princessProver70.mkLeq(proverExpr78, proverExpr93);
    java.lang.String str95 = princessProver0.proverExprToSMT(proverExpr94);
    princessProver0.setConstructProofs(true);
    jayhorn.solver.ProverExpr proverExpr99 = princessProver0.mkLiteral(1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array83);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType86);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType92);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr93);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr94);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str95 + "' != '" + "(<= 0 (+ || (* (- 1) || ) ) ) "+ "'", str95.equals("(<= 0 (+ || (* (- 1) || ) ) ) "));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr99);

  }

  @Test
  public void test05() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test05"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver5.substitute(proverExpr12, proverExpr_array17, proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType26 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr27 = princessProver24.mkHornVariable("", (jayhorn.solver.ProverType)boolType26);
    jayhorn.solver.ProverExpr proverExpr28 = princessProver4.mkLeq(proverExpr12, proverExpr27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.ProverExpr[] proverExpr_array45 = new jayhorn.solver.ProverExpr[] { proverExpr44 };
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver33.substitute(proverExpr40, proverExpr_array45, proverExpr_array50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverExpr[] proverExpr_array64 = new jayhorn.solver.ProverExpr[] { proverExpr63 };
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.ProverExpr proverExpr70 = princessProver52.substitute(proverExpr59, proverExpr_array64, proverExpr_array69);
    jayhorn.solver.ProverExpr proverExpr71 = princessProver4.mkStore(proverExpr32, proverExpr_array45, proverExpr59);
    jayhorn.solver.ProverExpr[] proverExpr_array72 = new jayhorn.solver.ProverExpr[] { proverExpr59 };
    jayhorn.solver.ProverExpr proverExpr73 = princessProver0.mkPlus(proverExpr_array72);
    jayhorn.solver.BoolType boolType75 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str76 = boolType75.toString();
    jayhorn.solver.ProverType[] proverType_array77 = new jayhorn.solver.ProverType[] { boolType75 };
    jayhorn.solver.BoolType boolType78 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverFun proverFun79 = princessProver0.mkUnintFunction("hi!", proverType_array77, (jayhorn.solver.ProverType)boolType78);
    java.lang.String str80 = boolType78.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str76 + "' != '" + "Bool"+ "'", str76.equals("Bool"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType_array77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverFun79);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str80 + "' != '" + "Bool"+ "'", str80.equals("Bool"));

  }

  @Test
  public void test06() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test06"); }


    jayhorn.solver.princess.PrincessProverFactory princessProverFactory0 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover1 = princessProverFactory0.spawn();
    jayhorn.util.SimplCfgToProver simplCfgToProver2 = new jayhorn.util.SimplCfgToProver(prover1);
    soottocfg.randoop.Javac javac3 = new soottocfg.randoop.Javac();
    java.util.List list4 = javac3.version();
    java.util.List list5 = javac3.version();
    // The following exception was thrown during execution in test generation
    try {
      jayhorn.solver.ProverExpr proverExpr6 = simplCfgToProver2.statementListToTransitionRelation(list5);
      org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException");
    } catch (java.lang.ClassCastException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.ClassCastException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list5);

  }

  @Test
  public void test07() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test07"); }


    jayhorn.util.SsaPrinter ssaPrinter0 = new jayhorn.util.SsaPrinter();

  }

  @Test
  public void test08() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test08"); }


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
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.ProverExpr[] proverExpr_array43 = new jayhorn.solver.ProverExpr[] { proverExpr42 };
    jayhorn.solver.ProverExpr proverExpr44 = princessProver26.substitute(proverExpr33, proverExpr_array38, proverExpr_array43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.ProverExpr proverExpr49 = princessProver25.mkLeq(proverExpr33, proverExpr48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = princessProver0.freeVariables(proverExpr49);
    jayhorn.solver.ProverResult proverResult52 = princessProver0.checkSat(false);
    // The following exception was thrown during execution in test generation
    try {
      princessProver0.pop();
      org.junit.Assert.fail("Expected exception of type java.lang.RuntimeException");
    } catch (java.lang.RuntimeException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.RuntimeException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.RuntimeException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult52);

  }

  @Test
  public void test09() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test09"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    soottocfg.randoop.Classpath classpath2 = soottocfg.randoop.Classpath.of((java.util.Collection)list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath2);

  }

  @Test
  public void test10() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test10"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverType proverType1 = princessProver0.getIntType();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.princess.PrincessProver princessProver25 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType27 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr28 = princessProver25.mkHornVariable("", (jayhorn.solver.ProverType)boolType27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.ProverExpr[] proverExpr_array33 = new jayhorn.solver.ProverExpr[] { proverExpr32 };
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.ProverExpr proverExpr39 = princessProver21.substitute(proverExpr28, proverExpr_array33, proverExpr_array38);
    jayhorn.solver.princess.PrincessProver princessProver40 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.princess.PrincessProver princessProver49 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType51 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr52 = princessProver49.mkHornVariable("", (jayhorn.solver.ProverType)boolType51);
    jayhorn.solver.ProverExpr[] proverExpr_array53 = new jayhorn.solver.ProverExpr[] { proverExpr52 };
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.ProverExpr[] proverExpr_array58 = new jayhorn.solver.ProverExpr[] { proverExpr57 };
    jayhorn.solver.ProverExpr proverExpr59 = princessProver41.substitute(proverExpr48, proverExpr_array53, proverExpr_array58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverExpr proverExpr64 = princessProver40.mkLeq(proverExpr48, proverExpr63);
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver66 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType68 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr69 = princessProver66.mkHornVariable("", (jayhorn.solver.ProverType)boolType68);
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType72 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr73 = princessProver70.mkHornVariable("", (jayhorn.solver.ProverType)boolType72);
    jayhorn.solver.princess.PrincessProver princessProver74 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType76 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr77 = princessProver74.mkHornVariable("", (jayhorn.solver.ProverType)boolType76);
    jayhorn.solver.ProverExpr[] proverExpr_array78 = new jayhorn.solver.ProverExpr[] { proverExpr77 };
    jayhorn.solver.princess.PrincessProver princessProver79 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType81 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr82 = princessProver79.mkHornVariable("", (jayhorn.solver.ProverType)boolType81);
    jayhorn.solver.ProverExpr[] proverExpr_array83 = new jayhorn.solver.ProverExpr[] { proverExpr82 };
    jayhorn.solver.ProverExpr proverExpr84 = princessProver66.substitute(proverExpr73, proverExpr_array78, proverExpr_array83);
    jayhorn.solver.princess.PrincessProver princessProver85 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType87 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr88 = princessProver85.mkHornVariable("", (jayhorn.solver.ProverType)boolType87);
    jayhorn.solver.ProverExpr proverExpr89 = princessProver65.mkLeq(proverExpr73, proverExpr88);
    jayhorn.solver.ProverExpr proverExpr90 = princessProver21.mkPlus(proverExpr63, proverExpr73);
    jayhorn.solver.ProverExpr proverExpr91 = princessProver0.mkGeq(proverExpr20, proverExpr63);
    princessProver0.setConstructProofs(false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array83);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr84);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr90);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr91);

  }

  @Test
  public void test11() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test11"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.ProverExpr proverExpr25 = princessProver1.mkLeq(proverExpr9, proverExpr24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.princess.PrincessProver princessProver38 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType40 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr41 = princessProver38.mkHornVariable("", (jayhorn.solver.ProverType)boolType40);
    jayhorn.solver.ProverExpr[] proverExpr_array42 = new jayhorn.solver.ProverExpr[] { proverExpr41 };
    jayhorn.solver.princess.PrincessProver princessProver43 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType45 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr46 = princessProver43.mkHornVariable("", (jayhorn.solver.ProverType)boolType45);
    jayhorn.solver.ProverExpr[] proverExpr_array47 = new jayhorn.solver.ProverExpr[] { proverExpr46 };
    jayhorn.solver.ProverExpr proverExpr48 = princessProver30.substitute(proverExpr37, proverExpr_array42, proverExpr_array47);
    jayhorn.solver.princess.PrincessProver princessProver49 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType51 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr52 = princessProver49.mkHornVariable("", (jayhorn.solver.ProverType)boolType51);
    jayhorn.solver.princess.PrincessProver princessProver53 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType55 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr56 = princessProver53.mkHornVariable("", (jayhorn.solver.ProverType)boolType55);
    jayhorn.solver.princess.PrincessProver princessProver57 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType59 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr60 = princessProver57.mkHornVariable("", (jayhorn.solver.ProverType)boolType59);
    jayhorn.solver.ProverExpr[] proverExpr_array61 = new jayhorn.solver.ProverExpr[] { proverExpr60 };
    jayhorn.solver.princess.PrincessProver princessProver62 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType64 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr65 = princessProver62.mkHornVariable("", (jayhorn.solver.ProverType)boolType64);
    jayhorn.solver.ProverExpr[] proverExpr_array66 = new jayhorn.solver.ProverExpr[] { proverExpr65 };
    jayhorn.solver.ProverExpr proverExpr67 = princessProver49.substitute(proverExpr56, proverExpr_array61, proverExpr_array66);
    jayhorn.solver.ProverExpr proverExpr68 = princessProver1.mkStore(proverExpr29, proverExpr_array42, proverExpr56);
    jayhorn.solver.ProverExpr proverExpr69 = princessProver0.mkPlus(proverExpr_array42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr65);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);

  }

  @Test
  public void test12() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test12"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverType proverType1 = princessProver0.getIntType();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver7 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType9 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr10 = princessProver7.mkHornVariable("", (jayhorn.solver.ProverType)boolType9);
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType13 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr14 = princessProver11.mkHornVariable("", (jayhorn.solver.ProverType)boolType13);
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.princess.PrincessProver princessProver20 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType22 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr23 = princessProver20.mkHornVariable("", (jayhorn.solver.ProverType)boolType22);
    jayhorn.solver.ProverExpr[] proverExpr_array24 = new jayhorn.solver.ProverExpr[] { proverExpr23 };
    jayhorn.solver.ProverExpr proverExpr25 = princessProver7.substitute(proverExpr14, proverExpr_array19, proverExpr_array24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.ProverExpr proverExpr30 = princessProver6.mkLeq(proverExpr14, proverExpr29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType33 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr34 = princessProver31.mkHornVariable("", (jayhorn.solver.ProverType)boolType33);
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.princess.PrincessProver princessProver43 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType45 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr46 = princessProver43.mkHornVariable("", (jayhorn.solver.ProverType)boolType45);
    jayhorn.solver.ProverExpr[] proverExpr_array47 = new jayhorn.solver.ProverExpr[] { proverExpr46 };
    jayhorn.solver.princess.PrincessProver princessProver48 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType50 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr51 = princessProver48.mkHornVariable("", (jayhorn.solver.ProverType)boolType50);
    jayhorn.solver.ProverExpr[] proverExpr_array52 = new jayhorn.solver.ProverExpr[] { proverExpr51 };
    jayhorn.solver.ProverExpr proverExpr53 = princessProver35.substitute(proverExpr42, proverExpr_array47, proverExpr_array52);
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.princess.PrincessProver princessProver58 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType60 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr61 = princessProver58.mkHornVariable("", (jayhorn.solver.ProverType)boolType60);
    jayhorn.solver.princess.PrincessProver princessProver62 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType64 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr65 = princessProver62.mkHornVariable("", (jayhorn.solver.ProverType)boolType64);
    jayhorn.solver.ProverExpr[] proverExpr_array66 = new jayhorn.solver.ProverExpr[] { proverExpr65 };
    jayhorn.solver.princess.PrincessProver princessProver67 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType69 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr70 = princessProver67.mkHornVariable("", (jayhorn.solver.ProverType)boolType69);
    jayhorn.solver.ProverExpr[] proverExpr_array71 = new jayhorn.solver.ProverExpr[] { proverExpr70 };
    jayhorn.solver.ProverExpr proverExpr72 = princessProver54.substitute(proverExpr61, proverExpr_array66, proverExpr_array71);
    jayhorn.solver.ProverExpr proverExpr73 = princessProver6.mkStore(proverExpr34, proverExpr_array47, proverExpr61);
    jayhorn.solver.ProverExpr[] proverExpr_array74 = new jayhorn.solver.ProverExpr[] { proverExpr61 };
    jayhorn.solver.ProverExpr proverExpr75 = princessProver2.mkPlus(proverExpr_array74);
    jayhorn.solver.princess.PrincessProver princessProver77 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType79 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr80 = princessProver77.mkHornVariable("", (jayhorn.solver.ProverType)boolType79);
    jayhorn.solver.ProverType[] proverType_array81 = new jayhorn.solver.ProverType[] { boolType79 };
    jayhorn.solver.BoolType boolType82 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str83 = boolType82.toString();
    jayhorn.solver.ProverFun proverFun84 = princessProver2.mkUnintFunction("hi!", proverType_array81, (jayhorn.solver.ProverType)boolType82);
    jayhorn.solver.ArrayType arrayType86 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.ProverType proverType87 = princessProver0.getArrayType(proverType_array81, (jayhorn.solver.ProverType)arrayType86);
    jayhorn.solver.ProverExpr proverExpr89 = princessProver0.mkLiteral(10);
    jayhorn.solver.ProverResult proverResult91 = princessProver0.getResult(0L);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr65);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType79);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr80);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType_array81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str83 + "' != '" + "Bool"+ "'", str83.equals("Bool"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverFun84);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult91);

  }

  @Test
  public void test13() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test13"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver5.substitute(proverExpr12, proverExpr_array17, proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType26 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr27 = princessProver24.mkHornVariable("", (jayhorn.solver.ProverType)boolType26);
    jayhorn.solver.ProverExpr proverExpr28 = princessProver4.mkLeq(proverExpr12, proverExpr27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.ProverExpr[] proverExpr_array45 = new jayhorn.solver.ProverExpr[] { proverExpr44 };
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver33.substitute(proverExpr40, proverExpr_array45, proverExpr_array50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverExpr[] proverExpr_array64 = new jayhorn.solver.ProverExpr[] { proverExpr63 };
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.ProverExpr proverExpr70 = princessProver52.substitute(proverExpr59, proverExpr_array64, proverExpr_array69);
    jayhorn.solver.ProverExpr proverExpr71 = princessProver4.mkStore(proverExpr32, proverExpr_array45, proverExpr59);
    jayhorn.solver.ProverExpr[] proverExpr_array72 = new jayhorn.solver.ProverExpr[] { proverExpr59 };
    jayhorn.solver.ProverExpr proverExpr73 = princessProver0.mkPlus(proverExpr_array72);
    jayhorn.solver.ProverResult proverResult75 = princessProver0.getResult((-1L));
    princessProver0.reset();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult75);

  }

  @Test
  public void test14() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test14"); }


    jayhorn.solver.Main main0 = new jayhorn.solver.Main();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.ProverExpr proverExpr25 = princessProver1.mkLeq(proverExpr9, proverExpr24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver27 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType29 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr30 = princessProver27.mkHornVariable("", (jayhorn.solver.ProverType)boolType29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType33 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr34 = princessProver31.mkHornVariable("", (jayhorn.solver.ProverType)boolType33);
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.ProverExpr[] proverExpr_array39 = new jayhorn.solver.ProverExpr[] { proverExpr38 };
    jayhorn.solver.princess.PrincessProver princessProver40 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType42 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr43 = princessProver40.mkHornVariable("", (jayhorn.solver.ProverType)boolType42);
    jayhorn.solver.ProverExpr[] proverExpr_array44 = new jayhorn.solver.ProverExpr[] { proverExpr43 };
    jayhorn.solver.ProverExpr proverExpr45 = princessProver27.substitute(proverExpr34, proverExpr_array39, proverExpr_array44);
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr proverExpr50 = princessProver26.mkLeq(proverExpr34, proverExpr49);
    jayhorn.solver.ProverExpr[] proverExpr_array51 = princessProver1.freeVariables(proverExpr50);
    main0.test03((jayhorn.solver.Prover)princessProver1);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory53 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover54 = princessProverFactory53.spawn();
    main0.test06(prover54);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory56 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover57 = princessProverFactory56.spawn();
    jayhorn.solver.Prover prover58 = princessProverFactory56.spawn();
    main0.test01(prover58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverResult proverResult65 = princessProver60.getResult((-1L));
    main0.test04((jayhorn.solver.Prover)princessProver60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult65);

  }

  @Test
  public void test15() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test15"); }


    soottocfg.cfg.util.GraphUtil graphUtil0 = new soottocfg.cfg.util.GraphUtil();

  }

  @Test
  public void test16() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test16"); }


    jayhorn.solver.IntType intType0 = jayhorn.solver.IntType.INSTANCE;
    java.lang.String str1 = intType0.toString();
    java.lang.String str2 = intType0.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(intType0);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str1 + "' != '" + "Int"+ "'", str1.equals("Int"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str2 + "' != '" + "Int"+ "'", str2.equals("Int"));

  }

  @Test
  public void test17() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test17"); }


    jayhorn.util.ConvertToDiamondShape convertToDiamondShape0 = new jayhorn.util.ConvertToDiamondShape();

  }

  @Test
  public void test18() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test18"); }


    jayhorn.solver.ArrayType arrayType1 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    boolean b21 = arrayType1.equals((java.lang.Object)proverExpr_array14);
    java.lang.String str22 = arrayType1.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b21 == false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str22 + "' != '" + "Array(10)"+ "'", str22.equals("Array(10)"));

  }

  @Test
  public void test19() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test19"); }


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
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType72 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr73 = princessProver70.mkHornVariable("", (jayhorn.solver.ProverType)boolType72);
    jayhorn.solver.princess.PrincessProver princessProver74 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType76 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr77 = princessProver74.mkHornVariable("", (jayhorn.solver.ProverType)boolType76);
    jayhorn.solver.princess.PrincessProver princessProver78 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType80 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr81 = princessProver78.mkHornVariable("", (jayhorn.solver.ProverType)boolType80);
    jayhorn.solver.ProverExpr[] proverExpr_array82 = new jayhorn.solver.ProverExpr[] { proverExpr81 };
    jayhorn.solver.princess.PrincessProver princessProver83 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType85 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr86 = princessProver83.mkHornVariable("", (jayhorn.solver.ProverType)boolType85);
    jayhorn.solver.ProverExpr[] proverExpr_array87 = new jayhorn.solver.ProverExpr[] { proverExpr86 };
    jayhorn.solver.ProverExpr proverExpr88 = princessProver70.substitute(proverExpr77, proverExpr_array82, proverExpr_array87);
    jayhorn.solver.BoolType boolType89 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr90 = princessProver0.mkEx(proverExpr77, (jayhorn.solver.ProverType)boolType89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType80);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType85);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr86);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr90);

  }

  @Test
  public void test20() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test20"); }


    soottocfg.cfg.SourceLocation sourceLocation2 = new soottocfg.cfg.SourceLocation("Bool", 100);
    java.lang.String str3 = sourceLocation2.getSourceFileName();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str3 + "' != '" + "Bool"+ "'", str3.equals("Bool"));

  }

  @Test
  public void test21() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test21"); }


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
    jayhorn.solver.ProverExpr proverExpr71 = princessProver0.mkLiteral(false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);

  }

  @Test
  public void test22() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test22"); }


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
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.ProverExpr[] proverExpr_array43 = new jayhorn.solver.ProverExpr[] { proverExpr42 };
    jayhorn.solver.ProverExpr proverExpr44 = princessProver26.substitute(proverExpr33, proverExpr_array38, proverExpr_array43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.ProverExpr proverExpr49 = princessProver25.mkLeq(proverExpr33, proverExpr48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = princessProver0.freeVariables(proverExpr49);
    jayhorn.solver.ProverResult proverResult52 = princessProver0.checkSat(false);
    jayhorn.solver.ArrayType arrayType55 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.princess.PrincessProver princessProver64 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType66 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr67 = princessProver64.mkHornVariable("", (jayhorn.solver.ProverType)boolType66);
    jayhorn.solver.ProverExpr[] proverExpr_array68 = new jayhorn.solver.ProverExpr[] { proverExpr67 };
    jayhorn.solver.princess.PrincessProver princessProver69 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType71 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr72 = princessProver69.mkHornVariable("", (jayhorn.solver.ProverType)boolType71);
    jayhorn.solver.ProverExpr[] proverExpr_array73 = new jayhorn.solver.ProverExpr[] { proverExpr72 };
    jayhorn.solver.ProverExpr proverExpr74 = princessProver56.substitute(proverExpr63, proverExpr_array68, proverExpr_array73);
    boolean b75 = arrayType55.equals((java.lang.Object)proverExpr_array68);
    jayhorn.solver.ProverExpr proverExpr76 = princessProver0.mkVariable("hi!", (jayhorn.solver.ProverType)arrayType55);
    princessProver0.setPartitionNumber((-1));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b75 == false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr76);

  }

  @Test
  public void test23() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test23"); }


    jayhorn.solver.z3.Z3ProverFactory z3ProverFactory0 = new jayhorn.solver.z3.Z3ProverFactory();
    jayhorn.solver.Prover prover2 = z3ProverFactory0.spawnWithLog("(<= 0 (+ || (* (- 1) || ) ) ) ");
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(prover2);

  }

  @Test
  public void test24() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test24"); }


    // The following exception was thrown during execution in test generation
    try {
      jayhorn.solver.z3.Z3Prover z3Prover0 = new jayhorn.solver.z3.Z3Prover();
      org.junit.Assert.fail("Expected exception of type java.lang.NoClassDefFoundError");
    } catch (java.lang.NoClassDefFoundError e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.NoClassDefFoundError")) {
        org.junit.Assert.fail("Expected exception of type java.lang.NoClassDefFoundError, got " + e.getClass().getCanonicalName());
      }
    }

  }

  @Test
  public void test25() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test25"); }


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
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver71 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType73 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr74 = princessProver71.mkHornVariable("", (jayhorn.solver.ProverType)boolType73);
    jayhorn.solver.princess.PrincessProver princessProver75 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType77 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr78 = princessProver75.mkHornVariable("", (jayhorn.solver.ProverType)boolType77);
    jayhorn.solver.princess.PrincessProver princessProver79 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType81 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr82 = princessProver79.mkHornVariable("", (jayhorn.solver.ProverType)boolType81);
    jayhorn.solver.ProverExpr[] proverExpr_array83 = new jayhorn.solver.ProverExpr[] { proverExpr82 };
    jayhorn.solver.princess.PrincessProver princessProver84 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType86 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr87 = princessProver84.mkHornVariable("", (jayhorn.solver.ProverType)boolType86);
    jayhorn.solver.ProverExpr[] proverExpr_array88 = new jayhorn.solver.ProverExpr[] { proverExpr87 };
    jayhorn.solver.ProverExpr proverExpr89 = princessProver71.substitute(proverExpr78, proverExpr_array83, proverExpr_array88);
    jayhorn.solver.princess.PrincessProver princessProver90 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType92 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr93 = princessProver90.mkHornVariable("", (jayhorn.solver.ProverType)boolType92);
    jayhorn.solver.ProverExpr proverExpr94 = princessProver70.mkLeq(proverExpr78, proverExpr93);
    java.lang.String str95 = princessProver0.proverExprToSMT(proverExpr94);
    jayhorn.solver.ProverResult proverResult97 = princessProver0.getResult(false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array83);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType86);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType92);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr93);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr94);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str95 + "' != '" + "(<= 0 (+ || (* (- 1) || ) ) ) "+ "'", str95.equals("(<= 0 (+ || (* (- 1) || ) ) ) "));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult97);

  }

  @Test
  public void test26() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test26"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.bootClasspath(classpath3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);

  }

  @Test
  public void test27() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test27"); }


    jayhorn.solver.princess.PrincessProverFactory princessProverFactory0 = new jayhorn.solver.princess.PrincessProverFactory();
    // The following exception was thrown during execution in test generation
    try {
      jayhorn.solver.Prover prover2 = princessProverFactory0.spawnWithLog("");
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }

  }

  @Test
  public void test28() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test28"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver5.substitute(proverExpr12, proverExpr_array17, proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType26 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr27 = princessProver24.mkHornVariable("", (jayhorn.solver.ProverType)boolType26);
    jayhorn.solver.ProverExpr proverExpr28 = princessProver4.mkLeq(proverExpr12, proverExpr27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.ProverExpr[] proverExpr_array45 = new jayhorn.solver.ProverExpr[] { proverExpr44 };
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver33.substitute(proverExpr40, proverExpr_array45, proverExpr_array50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverExpr[] proverExpr_array64 = new jayhorn.solver.ProverExpr[] { proverExpr63 };
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.ProverExpr proverExpr70 = princessProver52.substitute(proverExpr59, proverExpr_array64, proverExpr_array69);
    jayhorn.solver.ProverExpr proverExpr71 = princessProver4.mkStore(proverExpr32, proverExpr_array45, proverExpr59);
    jayhorn.solver.ProverExpr[] proverExpr_array72 = new jayhorn.solver.ProverExpr[] { proverExpr59 };
    jayhorn.solver.ProverExpr proverExpr73 = princessProver0.mkPlus(proverExpr_array72);
    jayhorn.solver.ProverResult proverResult75 = princessProver0.getResult((-1L));
    jayhorn.util.SimplCfgToProver simplCfgToProver76 = new jayhorn.util.SimplCfgToProver((jayhorn.solver.Prover)princessProver0);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult75);

  }

  @Test
  public void test29() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test29"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.Variable[] variable_array2 = program0.getGlobalVariables();
    soottocfg.cfg.method.Method method4 = program0.loopupMethod("Bool");
    // The following exception was thrown during execution in test generation
    try {
      soottocfg.cfg.Variable variable5 = program0.getExceptionGlobal();
      org.junit.Assert.fail("Expected exception of type com.google.common.base.VerifyException");
    } catch (com.google.common.base.VerifyException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("com.google.common.base.VerifyException")) {
        org.junit.Assert.fail("Expected exception of type com.google.common.base.VerifyException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(variable_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(method4);

  }

  @Test
  public void test30() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test30"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Javac javac7 = new soottocfg.randoop.Javac();
    java.util.List list8 = javac7.version();
    java.util.List list9 = javac7.version();
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    soottocfg.randoop.Javac javac13 = javac7.classpath(file_array11);
    soottocfg.randoop.Classpath classpath14 = new soottocfg.randoop.Classpath();
    java.lang.String str15 = classpath14.toString();
    soottocfg.randoop.Javac javac16 = javac13.bootClasspath(classpath14);
    soottocfg.randoop.Classpath classpath17 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array18 = new java.io.File[] {  };
    classpath17.addAll(file_array18);
    java.util.List list20 = javac16.compile(file_array18);
    soottocfg.randoop.Classpath classpath21 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array22 = new java.io.File[] {  };
    classpath21.addAll(file_array22);
    soottocfg.randoop.Javac javac24 = javac16.classpath(file_array22);
    soottocfg.randoop.Classpath classpath25 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array26 = new java.io.File[] {  };
    classpath25.addAll(file_array26);
    soottocfg.randoop.Classpath classpath28 = soottocfg.randoop.Classpath.empty();
    classpath25.addAll(classpath28);
    java.util.Collection collection30 = classpath28.getElements();
    java.util.Collection collection31 = classpath28.getElements();
    soottocfg.randoop.Javac javac32 = javac24.bootClasspath(classpath28);
    soottocfg.randoop.Javac javac33 = javac0.classpath(classpath28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str15 + "' != '" + ""+ "'", str15.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac33);

  }

  @Test
  public void test31() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test31"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    java.util.Map map1 = program0.getModifiedGlobals();
    soottocfg.cfg.Program program2 = new soottocfg.cfg.Program();
    java.util.Map map3 = program2.getModifiedGlobals();
    soottocfg.cfg.LiveVars liveVars4 = new soottocfg.cfg.LiveVars(map1, map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);

  }

  @Test
  public void test32() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test32"); }


    soottocfg.randoop.Classpath classpath0 = new soottocfg.randoop.Classpath();
    boolean b1 = classpath0.isEmpty();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b1 == true);

  }

  @Test
  public void test33() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test33"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverType proverType1 = princessProver0.getIntType();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver7 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType9 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr10 = princessProver7.mkHornVariable("", (jayhorn.solver.ProverType)boolType9);
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType13 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr14 = princessProver11.mkHornVariable("", (jayhorn.solver.ProverType)boolType13);
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.princess.PrincessProver princessProver20 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType22 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr23 = princessProver20.mkHornVariable("", (jayhorn.solver.ProverType)boolType22);
    jayhorn.solver.ProverExpr[] proverExpr_array24 = new jayhorn.solver.ProverExpr[] { proverExpr23 };
    jayhorn.solver.ProverExpr proverExpr25 = princessProver7.substitute(proverExpr14, proverExpr_array19, proverExpr_array24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.ProverExpr proverExpr30 = princessProver6.mkLeq(proverExpr14, proverExpr29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType33 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr34 = princessProver31.mkHornVariable("", (jayhorn.solver.ProverType)boolType33);
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.princess.PrincessProver princessProver43 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType45 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr46 = princessProver43.mkHornVariable("", (jayhorn.solver.ProverType)boolType45);
    jayhorn.solver.ProverExpr[] proverExpr_array47 = new jayhorn.solver.ProverExpr[] { proverExpr46 };
    jayhorn.solver.princess.PrincessProver princessProver48 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType50 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr51 = princessProver48.mkHornVariable("", (jayhorn.solver.ProverType)boolType50);
    jayhorn.solver.ProverExpr[] proverExpr_array52 = new jayhorn.solver.ProverExpr[] { proverExpr51 };
    jayhorn.solver.ProverExpr proverExpr53 = princessProver35.substitute(proverExpr42, proverExpr_array47, proverExpr_array52);
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.princess.PrincessProver princessProver58 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType60 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr61 = princessProver58.mkHornVariable("", (jayhorn.solver.ProverType)boolType60);
    jayhorn.solver.princess.PrincessProver princessProver62 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType64 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr65 = princessProver62.mkHornVariable("", (jayhorn.solver.ProverType)boolType64);
    jayhorn.solver.ProverExpr[] proverExpr_array66 = new jayhorn.solver.ProverExpr[] { proverExpr65 };
    jayhorn.solver.princess.PrincessProver princessProver67 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType69 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr70 = princessProver67.mkHornVariable("", (jayhorn.solver.ProverType)boolType69);
    jayhorn.solver.ProverExpr[] proverExpr_array71 = new jayhorn.solver.ProverExpr[] { proverExpr70 };
    jayhorn.solver.ProverExpr proverExpr72 = princessProver54.substitute(proverExpr61, proverExpr_array66, proverExpr_array71);
    jayhorn.solver.ProverExpr proverExpr73 = princessProver6.mkStore(proverExpr34, proverExpr_array47, proverExpr61);
    jayhorn.solver.ProverExpr[] proverExpr_array74 = new jayhorn.solver.ProverExpr[] { proverExpr61 };
    jayhorn.solver.ProverExpr proverExpr75 = princessProver2.mkPlus(proverExpr_array74);
    jayhorn.solver.princess.PrincessProver princessProver77 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType79 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr80 = princessProver77.mkHornVariable("", (jayhorn.solver.ProverType)boolType79);
    jayhorn.solver.ProverType[] proverType_array81 = new jayhorn.solver.ProverType[] { boolType79 };
    jayhorn.solver.BoolType boolType82 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str83 = boolType82.toString();
    jayhorn.solver.ProverFun proverFun84 = princessProver2.mkUnintFunction("hi!", proverType_array81, (jayhorn.solver.ProverType)boolType82);
    jayhorn.solver.ArrayType arrayType86 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.ProverType proverType87 = princessProver0.getArrayType(proverType_array81, (jayhorn.solver.ProverType)arrayType86);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory88 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover89 = princessProverFactory88.spawn();
    jayhorn.solver.Prover prover90 = princessProverFactory88.spawn();
    boolean b91 = arrayType86.equals((java.lang.Object)princessProverFactory88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr65);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType79);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr80);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType_array81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str83 + "' != '" + "Bool"+ "'", str83.equals("Bool"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverFun84);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover90);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b91 == false);

  }

  @Test
  public void test34() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test34"); }


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
    princessProver0.reset();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);

  }

  @Test
  public void test35() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test35"); }


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
    jayhorn.solver.ProverExpr proverExpr26 = princessProver0.mkLiteral(true);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr26);

  }

  @Test
  public void test36() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test36"); }


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
    soottocfg.randoop.Javac javac77 = new soottocfg.randoop.Javac();
    java.util.List list78 = javac77.version();
    java.lang.String[] str_array79 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list78);
    // The following exception was thrown during execution in test generation
    try {
      jayhorn.solver.ProverExpr proverExpr80 = simplCfgToProver76.statementListToTransitionRelation(list78);
      org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException");
    } catch (java.lang.ClassCastException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.ClassCastException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array79);

  }

  @Test
  public void test37() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test37"); }


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
    princessProver0.reset();
    princessProver0.push();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);

  }

  @Test
  public void test38() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test38"); }


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
    princessProver0.shutdown();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);

  }

  @Test
  public void test39() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test39"); }


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
    jayhorn.solver.ProverType proverType78 = princessProver0.getIntType();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType78);

  }

  @Test
  public void test40() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test40"); }


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
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.ProverExpr[] proverExpr_array43 = new jayhorn.solver.ProverExpr[] { proverExpr42 };
    jayhorn.solver.ProverExpr proverExpr44 = princessProver26.substitute(proverExpr33, proverExpr_array38, proverExpr_array43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.ProverExpr proverExpr49 = princessProver25.mkLeq(proverExpr33, proverExpr48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = princessProver0.freeVariables(proverExpr49);
    jayhorn.solver.ProverResult proverResult52 = princessProver0.checkSat(false);
    jayhorn.solver.ArrayType arrayType55 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.princess.PrincessProver princessProver64 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType66 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr67 = princessProver64.mkHornVariable("", (jayhorn.solver.ProverType)boolType66);
    jayhorn.solver.ProverExpr[] proverExpr_array68 = new jayhorn.solver.ProverExpr[] { proverExpr67 };
    jayhorn.solver.princess.PrincessProver princessProver69 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType71 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr72 = princessProver69.mkHornVariable("", (jayhorn.solver.ProverType)boolType71);
    jayhorn.solver.ProverExpr[] proverExpr_array73 = new jayhorn.solver.ProverExpr[] { proverExpr72 };
    jayhorn.solver.ProverExpr proverExpr74 = princessProver56.substitute(proverExpr63, proverExpr_array68, proverExpr_array73);
    boolean b75 = arrayType55.equals((java.lang.Object)proverExpr_array68);
    jayhorn.solver.ProverExpr proverExpr76 = princessProver0.mkVariable("hi!", (jayhorn.solver.ProverType)arrayType55);
    int i77 = arrayType55.arity;
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b75 == false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(i77 == 10);

  }

  @Test
  public void test41() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test41"); }


    jayhorn.solver.z3.Z3ProverFactory z3ProverFactory0 = new jayhorn.solver.z3.Z3ProverFactory();
    // The following exception was thrown during execution in test generation
    try {
      jayhorn.solver.Prover prover1 = z3ProverFactory0.spawn();
      org.junit.Assert.fail("Expected exception of type java.lang.NoClassDefFoundError");
    } catch (java.lang.NoClassDefFoundError e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.NoClassDefFoundError")) {
        org.junit.Assert.fail("Expected exception of type java.lang.NoClassDefFoundError, got " + e.getClass().getCanonicalName());
      }
    }

  }

  @Test
  public void test42() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test42"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Classpath classpath7 = new soottocfg.randoop.Classpath();
    java.lang.String str8 = classpath7.toString();
    soottocfg.randoop.Javac javac9 = javac6.bootClasspath(classpath7);
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    java.util.List list13 = javac9.compile(file_array11);
    soottocfg.randoop.Classpath classpath14 = soottocfg.randoop.Classpath.of(file_array11);
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.princess.PrincessProver princessProver19 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType21 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr22 = princessProver19.mkHornVariable("", (jayhorn.solver.ProverType)boolType21);
    jayhorn.solver.princess.PrincessProver princessProver23 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType25 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr26 = princessProver23.mkHornVariable("", (jayhorn.solver.ProverType)boolType25);
    jayhorn.solver.ProverExpr[] proverExpr_array27 = new jayhorn.solver.ProverExpr[] { proverExpr26 };
    jayhorn.solver.princess.PrincessProver princessProver28 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType30 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr31 = princessProver28.mkHornVariable("", (jayhorn.solver.ProverType)boolType30);
    jayhorn.solver.ProverExpr[] proverExpr_array32 = new jayhorn.solver.ProverExpr[] { proverExpr31 };
    jayhorn.solver.ProverExpr proverExpr33 = princessProver15.substitute(proverExpr22, proverExpr_array27, proverExpr_array32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.princess.PrincessProver princessProver43 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType45 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr46 = princessProver43.mkHornVariable("", (jayhorn.solver.ProverType)boolType45);
    jayhorn.solver.ProverExpr[] proverExpr_array47 = new jayhorn.solver.ProverExpr[] { proverExpr46 };
    jayhorn.solver.princess.PrincessProver princessProver48 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType50 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr51 = princessProver48.mkHornVariable("", (jayhorn.solver.ProverType)boolType50);
    jayhorn.solver.ProverExpr[] proverExpr_array52 = new jayhorn.solver.ProverExpr[] { proverExpr51 };
    jayhorn.solver.ProverExpr proverExpr53 = princessProver35.substitute(proverExpr42, proverExpr_array47, proverExpr_array52);
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.ProverExpr proverExpr58 = princessProver34.mkLeq(proverExpr42, proverExpr57);
    jayhorn.solver.princess.PrincessProver princessProver59 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.princess.PrincessProver princessProver64 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType66 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr67 = princessProver64.mkHornVariable("", (jayhorn.solver.ProverType)boolType66);
    jayhorn.solver.princess.PrincessProver princessProver68 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType70 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr71 = princessProver68.mkHornVariable("", (jayhorn.solver.ProverType)boolType70);
    jayhorn.solver.ProverExpr[] proverExpr_array72 = new jayhorn.solver.ProverExpr[] { proverExpr71 };
    jayhorn.solver.princess.PrincessProver princessProver73 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType75 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr76 = princessProver73.mkHornVariable("", (jayhorn.solver.ProverType)boolType75);
    jayhorn.solver.ProverExpr[] proverExpr_array77 = new jayhorn.solver.ProverExpr[] { proverExpr76 };
    jayhorn.solver.ProverExpr proverExpr78 = princessProver60.substitute(proverExpr67, proverExpr_array72, proverExpr_array77);
    jayhorn.solver.princess.PrincessProver princessProver79 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType81 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr82 = princessProver79.mkHornVariable("", (jayhorn.solver.ProverType)boolType81);
    jayhorn.solver.ProverExpr proverExpr83 = princessProver59.mkLeq(proverExpr67, proverExpr82);
    jayhorn.solver.ProverExpr proverExpr84 = princessProver15.mkPlus(proverExpr57, proverExpr67);
    jayhorn.solver.princess.PrincessProver princessProver86 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType88 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr89 = princessProver86.mkHornVariable("", (jayhorn.solver.ProverType)boolType88);
    jayhorn.solver.ProverExpr proverExpr90 = princessProver15.mkVariable("hi!", (jayhorn.solver.ProverType)boolType88);
    jayhorn.util.SimplCfgToProver simplCfgToProver91 = new jayhorn.util.SimplCfgToProver((jayhorn.solver.Prover)princessProver15);
    java.util.List list92 = simplCfgToProver91.generatedAxioms();
    classpath14.addAll((java.util.Collection)list92);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + ""+ "'", str8.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr83);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr84);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType88);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr89);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr90);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list92);

  }

  @Test
  public void test43() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test43"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    jayhorn.solver.BoolType boolType5 = jayhorn.solver.BoolType.INSTANCE;
    // The following exception was thrown during execution in test generation
    try {
      java.util.Set set6 = soottocfg.cfg.util.GraphUtil.getForwardReachableVertices(directedGraph4, (java.lang.Object)boolType5);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType5);

  }

  @Test
  public void test44() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test44"); }


    soottocfg.cfg.SourceLocation sourceLocation2 = new soottocfg.cfg.SourceLocation("Int", 1);

  }

  @Test
  public void test45() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test45"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.princess.PrincessProver princessProver14 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType16 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr17 = princessProver14.mkHornVariable("", (jayhorn.solver.ProverType)boolType16);
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.princess.PrincessProver princessProver23 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType25 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr26 = princessProver23.mkHornVariable("", (jayhorn.solver.ProverType)boolType25);
    jayhorn.solver.ProverExpr[] proverExpr_array27 = new jayhorn.solver.ProverExpr[] { proverExpr26 };
    jayhorn.solver.ProverExpr proverExpr28 = princessProver10.substitute(proverExpr17, proverExpr_array22, proverExpr_array27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.ProverExpr proverExpr33 = princessProver9.mkLeq(proverExpr17, proverExpr32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.princess.PrincessProver princessProver38 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType40 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr41 = princessProver38.mkHornVariable("", (jayhorn.solver.ProverType)boolType40);
    jayhorn.solver.princess.PrincessProver princessProver42 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType44 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr45 = princessProver42.mkHornVariable("", (jayhorn.solver.ProverType)boolType44);
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.princess.PrincessProver princessProver51 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType53 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr54 = princessProver51.mkHornVariable("", (jayhorn.solver.ProverType)boolType53);
    jayhorn.solver.ProverExpr[] proverExpr_array55 = new jayhorn.solver.ProverExpr[] { proverExpr54 };
    jayhorn.solver.ProverExpr proverExpr56 = princessProver38.substitute(proverExpr45, proverExpr_array50, proverExpr_array55);
    jayhorn.solver.princess.PrincessProver princessProver57 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType59 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr60 = princessProver57.mkHornVariable("", (jayhorn.solver.ProverType)boolType59);
    jayhorn.solver.princess.PrincessProver princessProver61 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType63 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr64 = princessProver61.mkHornVariable("", (jayhorn.solver.ProverType)boolType63);
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType72 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr73 = princessProver70.mkHornVariable("", (jayhorn.solver.ProverType)boolType72);
    jayhorn.solver.ProverExpr[] proverExpr_array74 = new jayhorn.solver.ProverExpr[] { proverExpr73 };
    jayhorn.solver.ProverExpr proverExpr75 = princessProver57.substitute(proverExpr64, proverExpr_array69, proverExpr_array74);
    jayhorn.solver.ProverExpr proverExpr76 = princessProver9.mkStore(proverExpr37, proverExpr_array50, proverExpr64);
    jayhorn.solver.ProverExpr[] proverExpr_array77 = new jayhorn.solver.ProverExpr[] { proverExpr64 };
    jayhorn.solver.ProverExpr proverExpr78 = princessProver5.mkPlus(proverExpr_array77);
    // The following exception was thrown during execution in test generation
    try {
      java.util.Set set79 = soottocfg.cfg.util.GraphUtil.getForwardReachableVertices(directedGraph4, (java.lang.Object)proverExpr78);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);

  }

  @Test
  public void test46() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test46"); }


    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver("Bool");

  }

  @Test
  public void test47() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test47"); }


    jayhorn.solver.princess.PrincessProverFactory princessProverFactory0 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover2 = princessProverFactory0.spawnWithLog("Bool");
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover2);

  }

  @Test
  public void test48() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test48"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Classpath classpath7 = new soottocfg.randoop.Classpath();
    java.lang.String str8 = classpath7.toString();
    soottocfg.randoop.Javac javac9 = javac6.bootClasspath(classpath7);
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    java.util.List list13 = javac9.compile(file_array11);
    java.lang.String[] str_array14 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list13);
    soottocfg.randoop.Command command15 = new soottocfg.randoop.Command(list13);
    // The following exception was thrown during execution in test generation
    try {
      java.util.List list16 = command15.execute();
      org.junit.Assert.fail("Expected exception of type java.lang.RuntimeException");
    } catch (java.lang.RuntimeException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.RuntimeException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.RuntimeException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + ""+ "'", str8.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array14);

  }

  @Test
  public void test49() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test49"); }


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
    jayhorn.solver.IntType intType26 = jayhorn.solver.IntType.INSTANCE;
    java.lang.String str27 = intType26.toString();
    jayhorn.solver.ProverExpr proverExpr28 = princessProver0.mkBoundVariable(100, (jayhorn.solver.ProverType)intType26);
    java.lang.String str29 = intType26.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(intType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + "Int"+ "'", str27.equals("Int"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str29 + "' != '" + "Int"+ "'", str29.equals("Int"));

  }

  @Test
  public void test50() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test50"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Javac javac7 = new soottocfg.randoop.Javac();
    java.util.List list8 = javac7.version();
    java.util.List list9 = javac7.version();
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    soottocfg.randoop.Javac javac13 = javac7.classpath(file_array11);
    soottocfg.randoop.Javac javac14 = javac0.sourcepath(file_array11);
    java.io.File[] file_array15 = new java.io.File[] {  };
    java.util.List list16 = javac0.compile(file_array15);
    java.lang.String[] str_array17 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array17);

  }

  @Test
  public void test51() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test51"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    java.lang.Object obj5 = new java.lang.Object();
    // The following exception was thrown during execution in test generation
    try {
      soottocfg.cfg.util.BfsIterator bfsIterator6 = new soottocfg.cfg.util.BfsIterator(directedGraph4, obj5);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);

  }

  @Test
  public void test52() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test52"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver5.substitute(proverExpr12, proverExpr_array17, proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType26 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr27 = princessProver24.mkHornVariable("", (jayhorn.solver.ProverType)boolType26);
    jayhorn.solver.ProverExpr proverExpr28 = princessProver4.mkLeq(proverExpr12, proverExpr27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.ProverExpr[] proverExpr_array45 = new jayhorn.solver.ProverExpr[] { proverExpr44 };
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver33.substitute(proverExpr40, proverExpr_array45, proverExpr_array50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverExpr[] proverExpr_array64 = new jayhorn.solver.ProverExpr[] { proverExpr63 };
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.ProverExpr proverExpr70 = princessProver52.substitute(proverExpr59, proverExpr_array64, proverExpr_array69);
    jayhorn.solver.ProverExpr proverExpr71 = princessProver4.mkStore(proverExpr32, proverExpr_array45, proverExpr59);
    jayhorn.solver.ProverExpr[] proverExpr_array72 = new jayhorn.solver.ProverExpr[] { proverExpr59 };
    jayhorn.solver.ProverExpr proverExpr73 = princessProver0.mkPlus(proverExpr_array72);
    jayhorn.solver.ProverResult proverResult75 = princessProver0.getResult((-1L));
    jayhorn.solver.princess.PrincessProver princessProver76 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType78 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr79 = princessProver76.mkHornVariable("", (jayhorn.solver.ProverType)boolType78);
    // The following exception was thrown during execution in test generation
    try {
      java.lang.String str80 = princessProver0.proverExprToSMT(proverExpr79);
      org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException");
    } catch (java.lang.ClassCastException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.ClassCastException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.ClassCastException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr79);

  }

  @Test
  public void test53() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test53"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    java.lang.String str9 = boolType7.toString();
    jayhorn.solver.ProverExpr proverExpr10 = princessProver0.mkBoundVariable(1, (jayhorn.solver.ProverType)boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + "Bool"+ "'", str9.equals("Bool"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr10);

  }

  @Test
  public void test54() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test54"); }


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
    princessProver0.setPartitionNumber(0);
    jayhorn.util.SimplCfgToProver simplCfgToProver72 = new jayhorn.util.SimplCfgToProver((jayhorn.solver.Prover)princessProver0);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);

  }

  @Test
  public void test55() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test55"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.princess.PrincessProver princessProver14 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType16 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr17 = princessProver14.mkHornVariable("", (jayhorn.solver.ProverType)boolType16);
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.princess.PrincessProver princessProver23 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType25 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr26 = princessProver23.mkHornVariable("", (jayhorn.solver.ProverType)boolType25);
    jayhorn.solver.ProverExpr[] proverExpr_array27 = new jayhorn.solver.ProverExpr[] { proverExpr26 };
    jayhorn.solver.ProverExpr proverExpr28 = princessProver10.substitute(proverExpr17, proverExpr_array22, proverExpr_array27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.ProverExpr proverExpr33 = princessProver9.mkLeq(proverExpr17, proverExpr32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.princess.PrincessProver princessProver38 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType40 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr41 = princessProver38.mkHornVariable("", (jayhorn.solver.ProverType)boolType40);
    jayhorn.solver.princess.PrincessProver princessProver42 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType44 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr45 = princessProver42.mkHornVariable("", (jayhorn.solver.ProverType)boolType44);
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.princess.PrincessProver princessProver51 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType53 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr54 = princessProver51.mkHornVariable("", (jayhorn.solver.ProverType)boolType53);
    jayhorn.solver.ProverExpr[] proverExpr_array55 = new jayhorn.solver.ProverExpr[] { proverExpr54 };
    jayhorn.solver.ProverExpr proverExpr56 = princessProver38.substitute(proverExpr45, proverExpr_array50, proverExpr_array55);
    jayhorn.solver.princess.PrincessProver princessProver57 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType59 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr60 = princessProver57.mkHornVariable("", (jayhorn.solver.ProverType)boolType59);
    jayhorn.solver.princess.PrincessProver princessProver61 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType63 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr64 = princessProver61.mkHornVariable("", (jayhorn.solver.ProverType)boolType63);
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.princess.PrincessProver princessProver70 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType72 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr73 = princessProver70.mkHornVariable("", (jayhorn.solver.ProverType)boolType72);
    jayhorn.solver.ProverExpr[] proverExpr_array74 = new jayhorn.solver.ProverExpr[] { proverExpr73 };
    jayhorn.solver.ProverExpr proverExpr75 = princessProver57.substitute(proverExpr64, proverExpr_array69, proverExpr_array74);
    jayhorn.solver.ProverExpr proverExpr76 = princessProver9.mkStore(proverExpr37, proverExpr_array50, proverExpr64);
    jayhorn.solver.ProverExpr[] proverExpr_array77 = new jayhorn.solver.ProverExpr[] { proverExpr64 };
    jayhorn.solver.ProverExpr proverExpr78 = princessProver5.mkPlus(proverExpr_array77);
    jayhorn.solver.ProverResult proverResult80 = princessProver5.getResult((-1L));
    jayhorn.solver.ArrayType arrayType83 = new jayhorn.solver.ArrayType(0);
    jayhorn.solver.ProverExpr proverExpr84 = princessProver5.mkBoundVariable(0, (jayhorn.solver.ProverType)arrayType83);
    jayhorn.solver.ProverExpr proverExpr85 = princessProver0.mkHornVariable("(<= 0 (+ || (* (- 1) || ) ) ) ", (jayhorn.solver.ProverType)arrayType83);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult80);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr84);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr85);

  }

  @Test
  public void test56() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test56"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method method3 = program0.loopupMethod("(<= 0 (+ || (* (- 1) || ) ) ) ");
    soottocfg.cfg.method.Method method5 = program0.loopupMethod("Array(10)");
    soottocfg.cfg.method.Method method7 = program0.loopupMethod("");
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(method3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(method5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(method7);

  }

  @Test
  public void test57() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test57"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Classpath classpath7 = new soottocfg.randoop.Classpath();
    java.lang.String str8 = classpath7.toString();
    soottocfg.randoop.Javac javac9 = javac6.bootClasspath(classpath7);
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    java.util.List list13 = javac9.compile(file_array11);
    soottocfg.randoop.Classpath classpath14 = new soottocfg.randoop.Classpath();
    java.util.Collection collection15 = classpath14.getElements();
    soottocfg.randoop.Javac javac16 = new soottocfg.randoop.Javac();
    java.util.List list17 = javac16.version();
    java.util.List list18 = javac16.version();
    soottocfg.randoop.Classpath classpath19 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array20 = new java.io.File[] {  };
    classpath19.addAll(file_array20);
    soottocfg.randoop.Javac javac22 = javac16.classpath(file_array20);
    soottocfg.randoop.Classpath classpath23 = new soottocfg.randoop.Classpath();
    java.lang.String str24 = classpath23.toString();
    soottocfg.randoop.Javac javac25 = javac22.bootClasspath(classpath23);
    soottocfg.randoop.Classpath classpath26 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array27 = new java.io.File[] {  };
    classpath26.addAll(file_array27);
    java.util.List list29 = javac25.compile(file_array27);
    soottocfg.randoop.Classpath classpath30 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array31 = new java.io.File[] {  };
    classpath30.addAll(file_array31);
    soottocfg.randoop.Javac javac33 = javac25.classpath(file_array31);
    classpath14.addAll(file_array31);
    soottocfg.randoop.Javac javac35 = javac9.bootClasspath(classpath14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + ""+ "'", str8.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str24 + "' != '" + ""+ "'", str24.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac35);

  }

  @Test
  public void test58() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test58"); }


    soottocfg.randoop.Classpath classpath1 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array2 = new java.io.File[] {  };
    classpath1.addAll(file_array2);
    soottocfg.randoop.Classpath classpath4 = soottocfg.randoop.Classpath.empty();
    classpath1.addAll(classpath4);
    java.util.Collection collection6 = classpath4.getElements();
    java.util.Collection collection7 = classpath4.getElements();
    soottocfg.cfg.ClassVariable classVariable8 = new soottocfg.cfg.ClassVariable("(<= 0 (+ || (* (- 1) || ) ) ) ", collection7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection7);

  }

  @Test
  public void test59() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test59"); }


    soottocfg.randoop.Classpath classpath0 = new soottocfg.randoop.Classpath();
    java.util.Collection collection1 = classpath0.getElements();
    soottocfg.randoop.Javac javac2 = new soottocfg.randoop.Javac();
    java.util.List list3 = javac2.version();
    java.util.List list4 = javac2.version();
    soottocfg.randoop.Classpath classpath5 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array6 = new java.io.File[] {  };
    classpath5.addAll(file_array6);
    soottocfg.randoop.Javac javac8 = javac2.classpath(file_array6);
    soottocfg.randoop.Classpath classpath9 = new soottocfg.randoop.Classpath();
    java.lang.String str10 = classpath9.toString();
    soottocfg.randoop.Javac javac11 = javac8.bootClasspath(classpath9);
    soottocfg.randoop.Classpath classpath12 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array13 = new java.io.File[] {  };
    classpath12.addAll(file_array13);
    java.util.List list15 = javac11.compile(file_array13);
    soottocfg.randoop.Classpath classpath16 = soottocfg.randoop.Classpath.of(file_array13);
    classpath0.addAll(file_array13);
    java.lang.String str18 = classpath0.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str10 + "' != '" + ""+ "'", str10.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str18 + "' != '" + ""+ "'", str18.equals(""));

  }

  @Test
  public void test60() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test60"); }


    jayhorn.solver.Main main0 = new jayhorn.solver.Main();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.ProverExpr proverExpr25 = princessProver1.mkLeq(proverExpr9, proverExpr24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver27 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType29 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr30 = princessProver27.mkHornVariable("", (jayhorn.solver.ProverType)boolType29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType33 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr34 = princessProver31.mkHornVariable("", (jayhorn.solver.ProverType)boolType33);
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.ProverExpr[] proverExpr_array39 = new jayhorn.solver.ProverExpr[] { proverExpr38 };
    jayhorn.solver.princess.PrincessProver princessProver40 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType42 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr43 = princessProver40.mkHornVariable("", (jayhorn.solver.ProverType)boolType42);
    jayhorn.solver.ProverExpr[] proverExpr_array44 = new jayhorn.solver.ProverExpr[] { proverExpr43 };
    jayhorn.solver.ProverExpr proverExpr45 = princessProver27.substitute(proverExpr34, proverExpr_array39, proverExpr_array44);
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr proverExpr50 = princessProver26.mkLeq(proverExpr34, proverExpr49);
    jayhorn.solver.ProverExpr[] proverExpr_array51 = princessProver1.freeVariables(proverExpr50);
    main0.test03((jayhorn.solver.Prover)princessProver1);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory53 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover54 = princessProverFactory53.spawn();
    main0.test06(prover54);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory56 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover57 = princessProverFactory56.spawn();
    jayhorn.solver.Prover prover58 = princessProverFactory56.spawn();
    main0.test01(prover58);
    jayhorn.util.SimplCfgToProver simplCfgToProver60 = new jayhorn.util.SimplCfgToProver(prover58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover58);

  }

  @Test
  public void test61() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test61"); }


    soottocfg.randoop.Classpath classpath0 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array1 = new java.io.File[] {  };
    classpath0.addAll(file_array1);
    soottocfg.randoop.Classpath classpath3 = soottocfg.randoop.Classpath.empty();
    classpath0.addAll(classpath3);
    java.util.Collection collection5 = classpath3.getElements();
    java.util.Collection collection6 = classpath3.getElements();
    java.lang.String[] str_array7 = soottocfg.randoop.Strings.generateArrayOfStrings(collection6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array7);

  }

  @Test
  public void test62() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test62"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    soottocfg.cfg.method.Method[] method_array3 = program0.getEntryPoints();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array3);

  }

  @Test
  public void test63() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test63"); }


    jayhorn.solver.princess.PrincessProverFactory princessProverFactory0 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover1 = princessProverFactory0.spawn();
    jayhorn.util.SimplCfgToProver simplCfgToProver2 = new jayhorn.util.SimplCfgToProver(prover1);
    java.util.List list3 = simplCfgToProver2.generatedAxioms();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list3);

  }

  @Test
  public void test64() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test64"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Javac javac7 = new soottocfg.randoop.Javac();
    java.util.List list8 = javac7.version();
    java.util.List list9 = javac7.version();
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    soottocfg.randoop.Javac javac13 = javac7.classpath(file_array11);
    soottocfg.randoop.Javac javac14 = javac0.sourcepath(file_array11);
    soottocfg.randoop.Javac javac15 = new soottocfg.randoop.Javac();
    java.util.List list16 = javac15.version();
    java.util.List list17 = javac15.version();
    soottocfg.randoop.Classpath classpath18 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array19 = new java.io.File[] {  };
    classpath18.addAll(file_array19);
    soottocfg.randoop.Javac javac21 = javac15.classpath(file_array19);
    soottocfg.randoop.Javac javac22 = javac14.classpath(file_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac22);

  }

  @Test
  public void test65() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test65"); }


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
    princessProver0.setPartitionNumber(0);
    jayhorn.solver.ProverResult proverResult73 = princessProver0.getResult(true);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult73);

  }

  @Test
  public void test66() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test66"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver5.substitute(proverExpr12, proverExpr_array17, proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType26 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr27 = princessProver24.mkHornVariable("", (jayhorn.solver.ProverType)boolType26);
    jayhorn.solver.ProverExpr proverExpr28 = princessProver4.mkLeq(proverExpr12, proverExpr27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.ProverExpr[] proverExpr_array45 = new jayhorn.solver.ProverExpr[] { proverExpr44 };
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver33.substitute(proverExpr40, proverExpr_array45, proverExpr_array50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.ProverExpr[] proverExpr_array64 = new jayhorn.solver.ProverExpr[] { proverExpr63 };
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr[] proverExpr_array69 = new jayhorn.solver.ProverExpr[] { proverExpr68 };
    jayhorn.solver.ProverExpr proverExpr70 = princessProver52.substitute(proverExpr59, proverExpr_array64, proverExpr_array69);
    jayhorn.solver.ProverExpr proverExpr71 = princessProver4.mkStore(proverExpr32, proverExpr_array45, proverExpr59);
    jayhorn.solver.ProverExpr[] proverExpr_array72 = new jayhorn.solver.ProverExpr[] { proverExpr59 };
    jayhorn.solver.ProverExpr proverExpr73 = princessProver0.mkPlus(proverExpr_array72);
    jayhorn.solver.ProverResult proverResult75 = princessProver0.getResult((-1L));
    princessProver0.setHornLogic(true);
    jayhorn.solver.ProverResult proverResult78 = princessProver0.stop();
    princessProver0.setHornLogic(false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult78);

  }

  @Test
  public void test67() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test67"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Classpath classpath7 = new soottocfg.randoop.Classpath();
    java.lang.String str8 = classpath7.toString();
    soottocfg.randoop.Javac javac9 = javac6.bootClasspath(classpath7);
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    java.util.List list13 = javac9.compile(file_array11);
    java.lang.String[] str_array14 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list13);
    soottocfg.randoop.Command command15 = new soottocfg.randoop.Command(list13);
    boolean b16 = command15.isStarted();
    // The following exception was thrown during execution in test generation
    try {
      command15.start();
      org.junit.Assert.fail("Expected exception of type java.io.IOException");
    } catch (java.io.IOException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.io.IOException")) {
        org.junit.Assert.fail("Expected exception of type java.io.IOException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + ""+ "'", str8.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b16 == false);

  }

  @Test
  public void test68() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test68"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Javac javac7 = new soottocfg.randoop.Javac();
    java.util.List list8 = javac7.version();
    java.util.List list9 = javac7.version();
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    soottocfg.randoop.Javac javac13 = javac7.classpath(file_array11);
    soottocfg.randoop.Javac javac14 = javac0.sourcepath(file_array11);
    soottocfg.randoop.Classpath classpath15 = new soottocfg.randoop.Classpath();
    java.lang.String str16 = classpath15.toString();
    soottocfg.randoop.Javac javac17 = javac14.classpath(classpath15);
    java.util.Collection collection18 = classpath15.getElements();
    java.lang.String str19 = classpath15.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + ""+ "'", str16.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str19 + "' != '" + ""+ "'", str19.equals(""));

  }

  @Test
  public void test69() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test69"); }


    jayhorn.solver.Main main0 = new jayhorn.solver.Main();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType3 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr4 = princessProver1.mkHornVariable("", (jayhorn.solver.ProverType)boolType3);
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.princess.PrincessProver princessProver14 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType16 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr17 = princessProver14.mkHornVariable("", (jayhorn.solver.ProverType)boolType16);
    jayhorn.solver.ProverExpr[] proverExpr_array18 = new jayhorn.solver.ProverExpr[] { proverExpr17 };
    jayhorn.solver.princess.PrincessProver princessProver19 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType21 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr22 = princessProver19.mkHornVariable("", (jayhorn.solver.ProverType)boolType21);
    jayhorn.solver.ProverExpr[] proverExpr_array23 = new jayhorn.solver.ProverExpr[] { proverExpr22 };
    jayhorn.solver.ProverExpr proverExpr24 = princessProver6.substitute(proverExpr13, proverExpr_array18, proverExpr_array23);
    jayhorn.solver.princess.PrincessProver princessProver25 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType27 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr28 = princessProver25.mkHornVariable("", (jayhorn.solver.ProverType)boolType27);
    jayhorn.solver.ProverExpr proverExpr29 = princessProver5.mkLeq(proverExpr13, proverExpr28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.princess.PrincessProver princessProver38 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType40 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr41 = princessProver38.mkHornVariable("", (jayhorn.solver.ProverType)boolType40);
    jayhorn.solver.princess.PrincessProver princessProver42 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType44 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr45 = princessProver42.mkHornVariable("", (jayhorn.solver.ProverType)boolType44);
    jayhorn.solver.ProverExpr[] proverExpr_array46 = new jayhorn.solver.ProverExpr[] { proverExpr45 };
    jayhorn.solver.princess.PrincessProver princessProver47 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType49 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr50 = princessProver47.mkHornVariable("", (jayhorn.solver.ProverType)boolType49);
    jayhorn.solver.ProverExpr[] proverExpr_array51 = new jayhorn.solver.ProverExpr[] { proverExpr50 };
    jayhorn.solver.ProverExpr proverExpr52 = princessProver34.substitute(proverExpr41, proverExpr_array46, proverExpr_array51);
    jayhorn.solver.princess.PrincessProver princessProver53 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType55 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr56 = princessProver53.mkHornVariable("", (jayhorn.solver.ProverType)boolType55);
    jayhorn.solver.princess.PrincessProver princessProver57 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType59 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr60 = princessProver57.mkHornVariable("", (jayhorn.solver.ProverType)boolType59);
    jayhorn.solver.princess.PrincessProver princessProver61 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType63 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr64 = princessProver61.mkHornVariable("", (jayhorn.solver.ProverType)boolType63);
    jayhorn.solver.ProverExpr[] proverExpr_array65 = new jayhorn.solver.ProverExpr[] { proverExpr64 };
    jayhorn.solver.princess.PrincessProver princessProver66 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType68 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr69 = princessProver66.mkHornVariable("", (jayhorn.solver.ProverType)boolType68);
    jayhorn.solver.ProverExpr[] proverExpr_array70 = new jayhorn.solver.ProverExpr[] { proverExpr69 };
    jayhorn.solver.ProverExpr proverExpr71 = princessProver53.substitute(proverExpr60, proverExpr_array65, proverExpr_array70);
    jayhorn.solver.ProverExpr proverExpr72 = princessProver5.mkStore(proverExpr33, proverExpr_array46, proverExpr60);
    jayhorn.solver.ProverExpr[] proverExpr_array73 = new jayhorn.solver.ProverExpr[] { proverExpr60 };
    jayhorn.solver.ProverExpr proverExpr74 = princessProver1.mkPlus(proverExpr_array73);
    jayhorn.solver.ProverResult proverResult76 = princessProver1.getResult((-1L));
    main0.test03((jayhorn.solver.Prover)princessProver1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array65);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult76);

  }

  @Test
  public void test70() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test70"); }


    jayhorn.solver.Main main0 = new jayhorn.solver.Main();
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
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.princess.PrincessProver princessProver25 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType27 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr28 = princessProver25.mkHornVariable("", (jayhorn.solver.ProverType)boolType27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.ProverExpr[] proverExpr_array33 = new jayhorn.solver.ProverExpr[] { proverExpr32 };
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.ProverExpr proverExpr39 = princessProver21.substitute(proverExpr28, proverExpr_array33, proverExpr_array38);
    jayhorn.solver.princess.PrincessProver princessProver40 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType42 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr43 = princessProver40.mkHornVariable("", (jayhorn.solver.ProverType)boolType42);
    jayhorn.solver.ProverExpr proverExpr44 = princessProver20.mkLeq(proverExpr28, proverExpr43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.princess.PrincessProver princessProver50 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType52 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr53 = princessProver50.mkHornVariable("", (jayhorn.solver.ProverType)boolType52);
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.ProverExpr[] proverExpr_array58 = new jayhorn.solver.ProverExpr[] { proverExpr57 };
    jayhorn.solver.princess.PrincessProver princessProver59 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType61 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr62 = princessProver59.mkHornVariable("", (jayhorn.solver.ProverType)boolType61);
    jayhorn.solver.ProverExpr[] proverExpr_array63 = new jayhorn.solver.ProverExpr[] { proverExpr62 };
    jayhorn.solver.ProverExpr proverExpr64 = princessProver46.substitute(proverExpr53, proverExpr_array58, proverExpr_array63);
    jayhorn.solver.princess.PrincessProver princessProver65 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType67 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr68 = princessProver65.mkHornVariable("", (jayhorn.solver.ProverType)boolType67);
    jayhorn.solver.ProverExpr proverExpr69 = princessProver45.mkLeq(proverExpr53, proverExpr68);
    jayhorn.solver.ProverExpr proverExpr70 = princessProver1.mkPlus(proverExpr43, proverExpr53);
    main0.test06((jayhorn.solver.Prover)princessProver1);
    jayhorn.solver.ProverResult proverResult73 = princessProver1.getResult(false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult73);

  }

  @Test
  public void test71() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test71"); }


    soottocfg.randoop.Classpath classpath0 = new soottocfg.randoop.Classpath();
    soottocfg.randoop.Javac javac1 = new soottocfg.randoop.Javac();
    soottocfg.randoop.Classpath classpath2 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array3 = new java.io.File[] {  };
    classpath2.addAll(file_array3);
    soottocfg.randoop.Classpath classpath5 = soottocfg.randoop.Classpath.empty();
    classpath2.addAll(classpath5);
    soottocfg.randoop.Javac javac7 = javac1.bootClasspath(classpath5);
    classpath0.addAll(classpath5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac7);

  }

  @Test
  public void test72() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test72"); }


    jayhorn.solver.Main main0 = new jayhorn.solver.Main();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType3 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr4 = princessProver1.mkHornVariable("", (jayhorn.solver.ProverType)boolType3);
    main0.test06((jayhorn.solver.Prover)princessProver1);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver7 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType9 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr10 = princessProver7.mkHornVariable("", (jayhorn.solver.ProverType)boolType9);
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType13 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr14 = princessProver11.mkHornVariable("", (jayhorn.solver.ProverType)boolType13);
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.princess.PrincessProver princessProver20 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType22 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr23 = princessProver20.mkHornVariable("", (jayhorn.solver.ProverType)boolType22);
    jayhorn.solver.ProverExpr[] proverExpr_array24 = new jayhorn.solver.ProverExpr[] { proverExpr23 };
    jayhorn.solver.ProverExpr proverExpr25 = princessProver7.substitute(proverExpr14, proverExpr_array19, proverExpr_array24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.ProverExpr proverExpr30 = princessProver6.mkLeq(proverExpr14, proverExpr29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver32 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType34 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr35 = princessProver32.mkHornVariable("", (jayhorn.solver.ProverType)boolType34);
    jayhorn.solver.princess.PrincessProver princessProver36 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType38 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr39 = princessProver36.mkHornVariable("", (jayhorn.solver.ProverType)boolType38);
    jayhorn.solver.princess.PrincessProver princessProver40 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType42 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr43 = princessProver40.mkHornVariable("", (jayhorn.solver.ProverType)boolType42);
    jayhorn.solver.ProverExpr[] proverExpr_array44 = new jayhorn.solver.ProverExpr[] { proverExpr43 };
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.ProverExpr[] proverExpr_array49 = new jayhorn.solver.ProverExpr[] { proverExpr48 };
    jayhorn.solver.ProverExpr proverExpr50 = princessProver32.substitute(proverExpr39, proverExpr_array44, proverExpr_array49);
    jayhorn.solver.princess.PrincessProver princessProver51 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType53 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr54 = princessProver51.mkHornVariable("", (jayhorn.solver.ProverType)boolType53);
    jayhorn.solver.ProverExpr proverExpr55 = princessProver31.mkLeq(proverExpr39, proverExpr54);
    jayhorn.solver.ProverExpr[] proverExpr_array56 = princessProver6.freeVariables(proverExpr55);
    princessProver6.setPartitionNumber(100);
    main0.test02((jayhorn.solver.Prover)princessProver6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array56);

  }

  @Test
  public void test73() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test73"); }


    jayhorn.solver.ArrayType arrayType1 = new jayhorn.solver.ArrayType(0);
    int i2 = arrayType1.arity;
    int i3 = arrayType1.arity;
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(i2 == 0);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(i3 == 0);

  }

  @Test
  public void test74() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test74"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.ProverResult proverResult5 = princessProver0.getResult((-1L));
    jayhorn.solver.ProverType proverType6 = princessProver0.getIntType();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType6);

  }

  @Test
  public void test75() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test75"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Classpath classpath7 = new soottocfg.randoop.Classpath();
    java.lang.String str8 = classpath7.toString();
    soottocfg.randoop.Javac javac9 = javac6.bootClasspath(classpath7);
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    java.util.List list13 = javac9.compile(file_array11);
    soottocfg.randoop.Classpath classpath14 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array15 = new java.io.File[] {  };
    classpath14.addAll(file_array15);
    soottocfg.randoop.Javac javac17 = javac9.classpath(file_array15);
    boolean b18 = javac17.inDebugMode();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str8 + "' != '" + ""+ "'", str8.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b18 == false);

  }

  @Test
  public void test76() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test76"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    soottocfg.randoop.Javac javac5 = new soottocfg.randoop.Javac();
    java.util.List list6 = javac5.version();
    java.util.List list7 = javac5.version();
    soottocfg.randoop.Javac javac8 = new soottocfg.randoop.Javac();
    java.util.List list9 = javac8.version();
    java.util.List list10 = javac8.version();
    soottocfg.randoop.Classpath classpath11 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array12 = new java.io.File[] {  };
    classpath11.addAll(file_array12);
    soottocfg.randoop.Javac javac14 = javac8.classpath(file_array12);
    soottocfg.randoop.Classpath classpath15 = new soottocfg.randoop.Classpath();
    java.lang.String str16 = classpath15.toString();
    soottocfg.randoop.Javac javac17 = javac14.bootClasspath(classpath15);
    soottocfg.randoop.Classpath classpath18 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array19 = new java.io.File[] {  };
    classpath18.addAll(file_array19);
    java.util.List list21 = javac17.compile(file_array19);
    soottocfg.randoop.Javac javac22 = javac17.debug();
    soottocfg.randoop.Classpath classpath23 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array24 = new java.io.File[] {  };
    classpath23.addAll(file_array24);
    java.util.Collection collection26 = classpath23.getElements();
    java.util.List list27 = javac17.compile(collection26);
    soottocfg.randoop.Javac javac28 = javac5.sourcepath(collection26);
    // The following exception was thrown during execution in test generation
    try {
      java.util.Set set29 = soottocfg.cfg.util.GraphUtil.getBackwardReachableVertices(directedGraph4, (java.lang.Object)javac28);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + ""+ "'", str16.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac28);

  }

  @Test
  public void test77() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test77"); }


    jayhorn.solver.ArrayType arrayType1 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    boolean b21 = arrayType1.equals((java.lang.Object)proverExpr_array14);
    java.lang.String[] str_array22 = soottocfg.randoop.Strings.generateArrayOfStrings((java.lang.Object[])proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b21 == false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array22);

  }

  @Test
  public void test78() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test78"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ArrayType arrayType3 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.princess.PrincessProver princessProver4 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType6 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr7 = princessProver4.mkHornVariable("", (jayhorn.solver.ProverType)boolType6);
    jayhorn.solver.princess.PrincessProver princessProver8 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType10 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr11 = princessProver8.mkHornVariable("", (jayhorn.solver.ProverType)boolType10);
    jayhorn.solver.princess.PrincessProver princessProver12 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType14 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr15 = princessProver12.mkHornVariable("", (jayhorn.solver.ProverType)boolType14);
    jayhorn.solver.ProverExpr[] proverExpr_array16 = new jayhorn.solver.ProverExpr[] { proverExpr15 };
    jayhorn.solver.princess.PrincessProver princessProver17 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType19 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr20 = princessProver17.mkHornVariable("", (jayhorn.solver.ProverType)boolType19);
    jayhorn.solver.ProverExpr[] proverExpr_array21 = new jayhorn.solver.ProverExpr[] { proverExpr20 };
    jayhorn.solver.ProverExpr proverExpr22 = princessProver4.substitute(proverExpr11, proverExpr_array16, proverExpr_array21);
    boolean b23 = arrayType3.equals((java.lang.Object)proverExpr_array16);
    jayhorn.solver.ProverExpr proverExpr24 = princessProver0.mkVariable("Array(10)", (jayhorn.solver.ProverType)arrayType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b23 == false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);

  }

  @Test
  public void test79() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test79"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    java.util.Map map1 = program0.getModifiedGlobals();
    java.util.Map map2 = program0.getModifiedGlobals();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map2);

  }

  @Test
  public void test80() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test80"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    java.lang.Object obj5 = soottocfg.cfg.util.GraphUtil.getSink(directedGraph4);
    soottocfg.randoop.Javac javac6 = new soottocfg.randoop.Javac();
    java.util.List list7 = javac6.version();
    java.util.List list8 = javac6.version();
    soottocfg.randoop.Javac javac9 = new soottocfg.randoop.Javac();
    java.util.List list10 = javac9.version();
    java.util.List list11 = javac9.version();
    soottocfg.randoop.Classpath classpath12 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array13 = new java.io.File[] {  };
    classpath12.addAll(file_array13);
    soottocfg.randoop.Javac javac15 = javac9.classpath(file_array13);
    soottocfg.randoop.Classpath classpath16 = new soottocfg.randoop.Classpath();
    java.lang.String str17 = classpath16.toString();
    soottocfg.randoop.Javac javac18 = javac15.bootClasspath(classpath16);
    soottocfg.randoop.Classpath classpath19 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array20 = new java.io.File[] {  };
    classpath19.addAll(file_array20);
    java.util.List list22 = javac18.compile(file_array20);
    soottocfg.randoop.Javac javac23 = javac18.debug();
    soottocfg.randoop.Classpath classpath24 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array25 = new java.io.File[] {  };
    classpath24.addAll(file_array25);
    java.util.Collection collection27 = classpath24.getElements();
    java.util.List list28 = javac18.compile(collection27);
    soottocfg.randoop.Javac javac29 = javac6.sourcepath(collection27);
    // The following exception was thrown during execution in test generation
    try {
      soottocfg.cfg.util.Dominators dominators30 = new soottocfg.cfg.util.Dominators(directedGraph4, (java.lang.Object)collection27);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(obj5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str17 + "' != '" + ""+ "'", str17.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(collection27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac29);

  }

  @Test
  public void test81() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test81"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Javac javac7 = new soottocfg.randoop.Javac();
    java.util.List list8 = javac7.version();
    java.util.List list9 = javac7.version();
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    soottocfg.randoop.Javac javac13 = javac7.classpath(file_array11);
    soottocfg.randoop.Javac javac14 = javac0.sourcepath(file_array11);
    soottocfg.randoop.Classpath classpath15 = new soottocfg.randoop.Classpath();
    java.lang.String str16 = classpath15.toString();
    soottocfg.randoop.Javac javac17 = javac14.classpath(classpath15);
    soottocfg.randoop.Javac javac18 = new soottocfg.randoop.Javac();
    java.util.List list19 = javac18.version();
    java.util.List list20 = javac18.version();
    soottocfg.randoop.Classpath classpath21 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array22 = new java.io.File[] {  };
    classpath21.addAll(file_array22);
    soottocfg.randoop.Javac javac24 = javac18.classpath(file_array22);
    soottocfg.randoop.Javac javac25 = new soottocfg.randoop.Javac();
    java.util.List list26 = javac25.version();
    java.util.List list27 = javac25.version();
    soottocfg.randoop.Classpath classpath28 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array29 = new java.io.File[] {  };
    classpath28.addAll(file_array29);
    soottocfg.randoop.Javac javac31 = javac25.classpath(file_array29);
    soottocfg.randoop.Javac javac32 = javac18.sourcepath(file_array29);
    soottocfg.randoop.Classpath classpath33 = new soottocfg.randoop.Classpath();
    java.lang.String str34 = classpath33.toString();
    soottocfg.randoop.Javac javac35 = javac32.classpath(classpath33);
    soottocfg.randoop.Javac javac36 = javac14.classpath(classpath33);
    soottocfg.randoop.Javac javac37 = new soottocfg.randoop.Javac();
    java.util.List list38 = javac37.version();
    java.util.List list39 = javac37.version();
    soottocfg.randoop.Classpath classpath40 = soottocfg.randoop.Classpath.of((java.util.Collection)list39);
    soottocfg.randoop.Command command41 = new soottocfg.randoop.Command(list39);
    soottocfg.randoop.Javac javac42 = javac14.extraArgs(list39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str16 + "' != '" + ""+ "'", str16.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str34 + "' != '" + ""+ "'", str34.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac42);

  }

  @Test
  public void test82() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test82"); }


    soottocfg.randoop.Javac javac1 = new soottocfg.randoop.Javac();
    java.util.List list2 = javac1.version();
    java.util.List list3 = javac1.version();
    soottocfg.randoop.Classpath classpath4 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array5 = new java.io.File[] {  };
    classpath4.addAll(file_array5);
    soottocfg.randoop.Javac javac7 = javac1.classpath(file_array5);
    soottocfg.randoop.Classpath classpath8 = new soottocfg.randoop.Classpath();
    java.lang.String str9 = classpath8.toString();
    soottocfg.randoop.Javac javac10 = javac7.bootClasspath(classpath8);
    soottocfg.randoop.Classpath classpath11 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array12 = new java.io.File[] {  };
    classpath11.addAll(file_array12);
    java.util.List list14 = javac10.compile(file_array12);
    java.lang.String[] str_array15 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list14);
    soottocfg.cfg.ClassVariable classVariable16 = new soottocfg.cfg.ClassVariable("hi!", (java.util.Collection)list14);
    java.lang.String str17 = classVariable16.toString();
    boolean b18 = classVariable16.isConstant();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + ""+ "'", str9.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str17 + "' != '" + "hi!"+ "'", str17.equals("hi!"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b18 == true);

  }

  @Test
  public void test83() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test83"); }


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
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.princess.PrincessProver princessProver30 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType32 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr33 = princessProver30.mkHornVariable("", (jayhorn.solver.ProverType)boolType32);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType36 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr37 = princessProver34.mkHornVariable("", (jayhorn.solver.ProverType)boolType36);
    jayhorn.solver.ProverExpr[] proverExpr_array38 = new jayhorn.solver.ProverExpr[] { proverExpr37 };
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.ProverExpr[] proverExpr_array43 = new jayhorn.solver.ProverExpr[] { proverExpr42 };
    jayhorn.solver.ProverExpr proverExpr44 = princessProver26.substitute(proverExpr33, proverExpr_array38, proverExpr_array43);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType47 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr48 = princessProver45.mkHornVariable("", (jayhorn.solver.ProverType)boolType47);
    jayhorn.solver.ProverExpr proverExpr49 = princessProver25.mkLeq(proverExpr33, proverExpr48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = princessProver0.freeVariables(proverExpr49);
    jayhorn.solver.ProverResult proverResult52 = princessProver0.checkSat(false);
    jayhorn.solver.ArrayType arrayType55 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.princess.PrincessProver princessProver56 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType58 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr59 = princessProver56.mkHornVariable("", (jayhorn.solver.ProverType)boolType58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType62 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr63 = princessProver60.mkHornVariable("", (jayhorn.solver.ProverType)boolType62);
    jayhorn.solver.princess.PrincessProver princessProver64 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType66 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr67 = princessProver64.mkHornVariable("", (jayhorn.solver.ProverType)boolType66);
    jayhorn.solver.ProverExpr[] proverExpr_array68 = new jayhorn.solver.ProverExpr[] { proverExpr67 };
    jayhorn.solver.princess.PrincessProver princessProver69 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType71 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr72 = princessProver69.mkHornVariable("", (jayhorn.solver.ProverType)boolType71);
    jayhorn.solver.ProverExpr[] proverExpr_array73 = new jayhorn.solver.ProverExpr[] { proverExpr72 };
    jayhorn.solver.ProverExpr proverExpr74 = princessProver56.substitute(proverExpr63, proverExpr_array68, proverExpr_array73);
    boolean b75 = arrayType55.equals((java.lang.Object)proverExpr_array68);
    jayhorn.solver.ProverExpr proverExpr76 = princessProver0.mkVariable("hi!", (jayhorn.solver.ProverType)arrayType55);
    java.lang.String str77 = arrayType55.toString();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType58);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr59);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b75 == false);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr76);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str77 + "' != '" + "Array(10)"+ "'", str77.equals("Array(10)"));

  }

  @Test
  public void test84() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test84"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType2 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr3 = princessProver0.mkHornVariable("", (jayhorn.solver.ProverType)boolType2);
    jayhorn.solver.ProverResult proverResult4 = princessProver0.stop();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverResult4);

  }

  @Test
  public void test85() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test85"); }


    jayhorn.solver.Main main0 = new jayhorn.solver.Main();
    jayhorn.solver.princess.PrincessProver princessProver1 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr9 = princessProver6.mkHornVariable("", (jayhorn.solver.ProverType)boolType8);
    jayhorn.solver.princess.PrincessProver princessProver10 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType12 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr13 = princessProver10.mkHornVariable("", (jayhorn.solver.ProverType)boolType12);
    jayhorn.solver.ProverExpr[] proverExpr_array14 = new jayhorn.solver.ProverExpr[] { proverExpr13 };
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.ProverExpr proverExpr20 = princessProver2.substitute(proverExpr9, proverExpr_array14, proverExpr_array19);
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.ProverExpr proverExpr25 = princessProver1.mkLeq(proverExpr9, proverExpr24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver27 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType29 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr30 = princessProver27.mkHornVariable("", (jayhorn.solver.ProverType)boolType29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType33 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr34 = princessProver31.mkHornVariable("", (jayhorn.solver.ProverType)boolType33);
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.ProverExpr[] proverExpr_array39 = new jayhorn.solver.ProverExpr[] { proverExpr38 };
    jayhorn.solver.princess.PrincessProver princessProver40 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType42 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr43 = princessProver40.mkHornVariable("", (jayhorn.solver.ProverType)boolType42);
    jayhorn.solver.ProverExpr[] proverExpr_array44 = new jayhorn.solver.ProverExpr[] { proverExpr43 };
    jayhorn.solver.ProverExpr proverExpr45 = princessProver27.substitute(proverExpr34, proverExpr_array39, proverExpr_array44);
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr proverExpr50 = princessProver26.mkLeq(proverExpr34, proverExpr49);
    jayhorn.solver.ProverExpr[] proverExpr_array51 = princessProver1.freeVariables(proverExpr50);
    main0.test03((jayhorn.solver.Prover)princessProver1);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory53 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover54 = princessProverFactory53.spawn();
    main0.test06(prover54);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory56 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover57 = princessProverFactory56.spawn();
    jayhorn.solver.Prover prover58 = princessProverFactory56.spawn();
    main0.test01(prover58);
    jayhorn.solver.princess.PrincessProver princessProver60 = new jayhorn.solver.princess.PrincessProver();
    princessProver60.setPartitionNumber(10);
    main0.test02((jayhorn.solver.Prover)princessProver60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover58);

  }

  @Test
  public void test86() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test86"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.Variable[] variable_array2 = program0.getGlobalVariables();
    soottocfg.randoop.Javac javac4 = new soottocfg.randoop.Javac();
    java.util.List list5 = javac4.version();
    java.util.List list6 = javac4.version();
    soottocfg.randoop.Classpath classpath7 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array8 = new java.io.File[] {  };
    classpath7.addAll(file_array8);
    soottocfg.randoop.Javac javac10 = javac4.classpath(file_array8);
    soottocfg.randoop.Javac javac11 = new soottocfg.randoop.Javac();
    java.util.List list12 = javac11.version();
    java.util.List list13 = javac11.version();
    soottocfg.randoop.Classpath classpath14 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array15 = new java.io.File[] {  };
    classpath14.addAll(file_array15);
    soottocfg.randoop.Javac javac17 = javac11.classpath(file_array15);
    soottocfg.randoop.Javac javac18 = javac4.sourcepath(file_array15);
    soottocfg.randoop.Javac javac19 = new soottocfg.randoop.Javac();
    java.util.List list20 = javac19.version();
    java.util.List list21 = javac19.version();
    soottocfg.randoop.Classpath classpath22 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array23 = new java.io.File[] {  };
    classpath22.addAll(file_array23);
    soottocfg.randoop.Javac javac25 = javac19.classpath(file_array23);
    soottocfg.randoop.Classpath classpath26 = new soottocfg.randoop.Classpath();
    java.lang.String str27 = classpath26.toString();
    soottocfg.randoop.Javac javac28 = javac25.bootClasspath(classpath26);
    soottocfg.randoop.Classpath classpath29 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array30 = new java.io.File[] {  };
    classpath29.addAll(file_array30);
    java.util.List list32 = javac28.compile(file_array30);
    java.lang.String[] str_array33 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list32);
    soottocfg.randoop.Javac javac34 = javac18.extraArgs(list32);
    soottocfg.cfg.ClassVariable classVariable35 = new soottocfg.cfg.ClassVariable("Bool", (java.util.Collection)list32);
    soottocfg.cfg.type.Type type36 = classVariable35.getType();
    java.lang.String str37 = classVariable35.getName();
    program0.setExceptionGlobal((soottocfg.cfg.Variable)classVariable35);
    boolean b39 = classVariable35.isUnique();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(variable_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str27 + "' != '" + ""+ "'", str27.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(type36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str37 + "' != '" + "Bool"+ "'", str37.equals("Bool"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue(b39 == true);

  }

  @Test
  public void test87() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test87"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverType proverType1 = princessProver0.getIntType();
    jayhorn.solver.princess.PrincessProver princessProver2 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr5 = princessProver2.mkHornVariable("", (jayhorn.solver.ProverType)boolType4);
    jayhorn.solver.princess.PrincessProver princessProver6 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver7 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType9 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr10 = princessProver7.mkHornVariable("", (jayhorn.solver.ProverType)boolType9);
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType13 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr14 = princessProver11.mkHornVariable("", (jayhorn.solver.ProverType)boolType13);
    jayhorn.solver.princess.PrincessProver princessProver15 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType17 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr18 = princessProver15.mkHornVariable("", (jayhorn.solver.ProverType)boolType17);
    jayhorn.solver.ProverExpr[] proverExpr_array19 = new jayhorn.solver.ProverExpr[] { proverExpr18 };
    jayhorn.solver.princess.PrincessProver princessProver20 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType22 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr23 = princessProver20.mkHornVariable("", (jayhorn.solver.ProverType)boolType22);
    jayhorn.solver.ProverExpr[] proverExpr_array24 = new jayhorn.solver.ProverExpr[] { proverExpr23 };
    jayhorn.solver.ProverExpr proverExpr25 = princessProver7.substitute(proverExpr14, proverExpr_array19, proverExpr_array24);
    jayhorn.solver.princess.PrincessProver princessProver26 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType28 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr29 = princessProver26.mkHornVariable("", (jayhorn.solver.ProverType)boolType28);
    jayhorn.solver.ProverExpr proverExpr30 = princessProver6.mkLeq(proverExpr14, proverExpr29);
    jayhorn.solver.princess.PrincessProver princessProver31 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType33 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr34 = princessProver31.mkHornVariable("", (jayhorn.solver.ProverType)boolType33);
    jayhorn.solver.princess.PrincessProver princessProver35 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType37 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr38 = princessProver35.mkHornVariable("", (jayhorn.solver.ProverType)boolType37);
    jayhorn.solver.princess.PrincessProver princessProver39 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType41 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr42 = princessProver39.mkHornVariable("", (jayhorn.solver.ProverType)boolType41);
    jayhorn.solver.princess.PrincessProver princessProver43 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType45 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr46 = princessProver43.mkHornVariable("", (jayhorn.solver.ProverType)boolType45);
    jayhorn.solver.ProverExpr[] proverExpr_array47 = new jayhorn.solver.ProverExpr[] { proverExpr46 };
    jayhorn.solver.princess.PrincessProver princessProver48 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType50 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr51 = princessProver48.mkHornVariable("", (jayhorn.solver.ProverType)boolType50);
    jayhorn.solver.ProverExpr[] proverExpr_array52 = new jayhorn.solver.ProverExpr[] { proverExpr51 };
    jayhorn.solver.ProverExpr proverExpr53 = princessProver35.substitute(proverExpr42, proverExpr_array47, proverExpr_array52);
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.princess.PrincessProver princessProver58 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType60 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr61 = princessProver58.mkHornVariable("", (jayhorn.solver.ProverType)boolType60);
    jayhorn.solver.princess.PrincessProver princessProver62 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType64 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr65 = princessProver62.mkHornVariable("", (jayhorn.solver.ProverType)boolType64);
    jayhorn.solver.ProverExpr[] proverExpr_array66 = new jayhorn.solver.ProverExpr[] { proverExpr65 };
    jayhorn.solver.princess.PrincessProver princessProver67 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType69 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr70 = princessProver67.mkHornVariable("", (jayhorn.solver.ProverType)boolType69);
    jayhorn.solver.ProverExpr[] proverExpr_array71 = new jayhorn.solver.ProverExpr[] { proverExpr70 };
    jayhorn.solver.ProverExpr proverExpr72 = princessProver54.substitute(proverExpr61, proverExpr_array66, proverExpr_array71);
    jayhorn.solver.ProverExpr proverExpr73 = princessProver6.mkStore(proverExpr34, proverExpr_array47, proverExpr61);
    jayhorn.solver.ProverExpr[] proverExpr_array74 = new jayhorn.solver.ProverExpr[] { proverExpr61 };
    jayhorn.solver.ProverExpr proverExpr75 = princessProver2.mkPlus(proverExpr_array74);
    jayhorn.solver.princess.PrincessProver princessProver77 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType79 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr80 = princessProver77.mkHornVariable("", (jayhorn.solver.ProverType)boolType79);
    jayhorn.solver.ProverType[] proverType_array81 = new jayhorn.solver.ProverType[] { boolType79 };
    jayhorn.solver.BoolType boolType82 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str83 = boolType82.toString();
    jayhorn.solver.ProverFun proverFun84 = princessProver2.mkUnintFunction("hi!", proverType_array81, (jayhorn.solver.ProverType)boolType82);
    jayhorn.solver.ArrayType arrayType86 = new jayhorn.solver.ArrayType(10);
    jayhorn.solver.ProverType proverType87 = princessProver0.getArrayType(proverType_array81, (jayhorn.solver.ProverType)arrayType86);
    jayhorn.solver.ProverType proverType88 = princessProver0.getIntType();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType33);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType64);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr65);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr70);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType79);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr80);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType_array81);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType82);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str83 + "' != '" + "Bool"+ "'", str83.equals("Bool"));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverFun84);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType87);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType88);

  }

  @Test
  public void test88() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test88"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    jayhorn.solver.Main main6 = new jayhorn.solver.Main();
    jayhorn.solver.princess.PrincessProver princessProver7 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver8 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType10 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr11 = princessProver8.mkHornVariable("", (jayhorn.solver.ProverType)boolType10);
    jayhorn.solver.princess.PrincessProver princessProver12 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType14 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr15 = princessProver12.mkHornVariable("", (jayhorn.solver.ProverType)boolType14);
    jayhorn.solver.princess.PrincessProver princessProver16 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType18 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr19 = princessProver16.mkHornVariable("", (jayhorn.solver.ProverType)boolType18);
    jayhorn.solver.ProverExpr[] proverExpr_array20 = new jayhorn.solver.ProverExpr[] { proverExpr19 };
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType23 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr24 = princessProver21.mkHornVariable("", (jayhorn.solver.ProverType)boolType23);
    jayhorn.solver.ProverExpr[] proverExpr_array25 = new jayhorn.solver.ProverExpr[] { proverExpr24 };
    jayhorn.solver.ProverExpr proverExpr26 = princessProver8.substitute(proverExpr15, proverExpr_array20, proverExpr_array25);
    jayhorn.solver.princess.PrincessProver princessProver27 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType29 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr30 = princessProver27.mkHornVariable("", (jayhorn.solver.ProverType)boolType29);
    jayhorn.solver.ProverExpr proverExpr31 = princessProver7.mkLeq(proverExpr15, proverExpr30);
    jayhorn.solver.princess.PrincessProver princessProver32 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.princess.PrincessProver princessProver37 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType39 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr40 = princessProver37.mkHornVariable("", (jayhorn.solver.ProverType)boolType39);
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType43 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr44 = princessProver41.mkHornVariable("", (jayhorn.solver.ProverType)boolType43);
    jayhorn.solver.ProverExpr[] proverExpr_array45 = new jayhorn.solver.ProverExpr[] { proverExpr44 };
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType48 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr49 = princessProver46.mkHornVariable("", (jayhorn.solver.ProverType)boolType48);
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] { proverExpr49 };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver33.substitute(proverExpr40, proverExpr_array45, proverExpr_array50);
    jayhorn.solver.princess.PrincessProver princessProver52 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr55 = princessProver52.mkHornVariable("", (jayhorn.solver.ProverType)boolType54);
    jayhorn.solver.ProverExpr proverExpr56 = princessProver32.mkLeq(proverExpr40, proverExpr55);
    jayhorn.solver.ProverExpr[] proverExpr_array57 = princessProver7.freeVariables(proverExpr56);
    main6.test03((jayhorn.solver.Prover)princessProver7);
    jayhorn.solver.princess.PrincessProverFactory princessProverFactory59 = new jayhorn.solver.princess.PrincessProverFactory();
    jayhorn.solver.Prover prover60 = princessProverFactory59.spawn();
    main6.test06(prover60);
    jayhorn.solver.princess.PrincessProver princessProver62 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverType proverType63 = princessProver62.getIntType();
    main6.test02((jayhorn.solver.Prover)princessProver62);
    // The following exception was thrown during execution in test generation
    try {
      soottocfg.cfg.util.UnreachableNodeRemover unreachableNodeRemover65 = new soottocfg.cfg.util.UnreachableNodeRemover(directedGraph4, (java.lang.Object)1.0f, (java.lang.Object)main6);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array45);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr49);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(prover60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverType63);

  }

  @Test
  public void test89() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test89"); }


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
    princessProver0.setHornLogic(true);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr63);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr69);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr75);

  }

  @Test
  public void test90() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test90"); }


    soottocfg.randoop.Javac javac0 = new soottocfg.randoop.Javac();
    java.util.List list1 = javac0.version();
    java.util.List list2 = javac0.version();
    soottocfg.randoop.Classpath classpath3 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array4 = new java.io.File[] {  };
    classpath3.addAll(file_array4);
    soottocfg.randoop.Javac javac6 = javac0.classpath(file_array4);
    soottocfg.randoop.Javac javac7 = new soottocfg.randoop.Javac();
    java.util.List list8 = javac7.version();
    java.util.List list9 = javac7.version();
    soottocfg.randoop.Classpath classpath10 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array11 = new java.io.File[] {  };
    classpath10.addAll(file_array11);
    soottocfg.randoop.Javac javac13 = javac7.classpath(file_array11);
    soottocfg.randoop.Javac javac14 = javac0.sourcepath(file_array11);
    java.io.File[] file_array15 = new java.io.File[] {  };
    java.util.List list16 = javac0.compile(file_array15);
    soottocfg.randoop.Javac javac17 = new soottocfg.randoop.Javac();
    java.util.List list18 = javac17.version();
    java.util.List list19 = javac17.version();
    soottocfg.randoop.Classpath classpath20 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array21 = new java.io.File[] {  };
    classpath20.addAll(file_array21);
    soottocfg.randoop.Javac javac23 = javac17.classpath(file_array21);
    soottocfg.randoop.Classpath classpath24 = new soottocfg.randoop.Classpath();
    java.lang.String str25 = classpath24.toString();
    soottocfg.randoop.Javac javac26 = javac23.bootClasspath(classpath24);
    soottocfg.randoop.Classpath classpath27 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array28 = new java.io.File[] {  };
    classpath27.addAll(file_array28);
    java.util.List list30 = javac26.compile(file_array28);
    java.lang.String[] str_array31 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list30);
    soottocfg.randoop.Javac javac32 = javac0.extraArgs(list30);
    soottocfg.randoop.Javac javac33 = new soottocfg.randoop.Javac();
    java.util.List list34 = javac33.version();
    java.util.List list35 = javac33.version();
    soottocfg.randoop.Classpath classpath36 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array37 = new java.io.File[] {  };
    classpath36.addAll(file_array37);
    soottocfg.randoop.Javac javac39 = javac33.classpath(file_array37);
    soottocfg.randoop.Classpath classpath40 = new soottocfg.randoop.Classpath();
    java.lang.String str41 = classpath40.toString();
    soottocfg.randoop.Javac javac42 = javac39.bootClasspath(classpath40);
    soottocfg.randoop.Classpath classpath43 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array44 = new java.io.File[] {  };
    classpath43.addAll(file_array44);
    java.util.List list46 = javac42.compile(file_array44);
    soottocfg.randoop.Classpath classpath47 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array48 = new java.io.File[] {  };
    classpath47.addAll(file_array48);
    soottocfg.randoop.Javac javac50 = javac42.classpath(file_array48);
    soottocfg.randoop.Javac javac51 = javac32.sourcepath(file_array48);
    soottocfg.randoop.Javac javac52 = new soottocfg.randoop.Javac();
    java.util.List list53 = javac52.version();
    java.util.List list54 = javac52.version();
    soottocfg.randoop.Classpath classpath55 = soottocfg.randoop.Classpath.of((java.util.Collection)list54);
    soottocfg.randoop.Javac javac56 = javac32.classpath(classpath55);
    soottocfg.randoop.Javac javac57 = javac56.debug();
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac13);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list18);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str25 + "' != '" + ""+ "'", str25.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac26);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list30);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac39);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str41 + "' != '" + ""+ "'", str41.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array44);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac50);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac51);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list54);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(classpath55);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac57);

  }

  @Test
  public void test91() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test91"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    jayhorn.solver.princess.PrincessProver princessProver5 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType7 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr8 = princessProver5.mkHornVariable("", (jayhorn.solver.ProverType)boolType7);
    jayhorn.solver.princess.PrincessProver princessProver9 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType11 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr12 = princessProver9.mkHornVariable("", (jayhorn.solver.ProverType)boolType11);
    jayhorn.solver.princess.PrincessProver princessProver13 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType15 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr16 = princessProver13.mkHornVariable("", (jayhorn.solver.ProverType)boolType15);
    jayhorn.solver.ProverExpr[] proverExpr_array17 = new jayhorn.solver.ProverExpr[] { proverExpr16 };
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType20 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr21 = princessProver18.mkHornVariable("", (jayhorn.solver.ProverType)boolType20);
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] { proverExpr21 };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver5.substitute(proverExpr12, proverExpr_array17, proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver24 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver25 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType27 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr28 = princessProver25.mkHornVariable("", (jayhorn.solver.ProverType)boolType27);
    jayhorn.solver.princess.PrincessProver princessProver29 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr32 = princessProver29.mkHornVariable("", (jayhorn.solver.ProverType)boolType31);
    jayhorn.solver.princess.PrincessProver princessProver33 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType35 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr36 = princessProver33.mkHornVariable("", (jayhorn.solver.ProverType)boolType35);
    jayhorn.solver.ProverExpr[] proverExpr_array37 = new jayhorn.solver.ProverExpr[] { proverExpr36 };
    jayhorn.solver.princess.PrincessProver princessProver38 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType40 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr41 = princessProver38.mkHornVariable("", (jayhorn.solver.ProverType)boolType40);
    jayhorn.solver.ProverExpr[] proverExpr_array42 = new jayhorn.solver.ProverExpr[] { proverExpr41 };
    jayhorn.solver.ProverExpr proverExpr43 = princessProver25.substitute(proverExpr32, proverExpr_array37, proverExpr_array42);
    jayhorn.solver.princess.PrincessProver princessProver44 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType46 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr47 = princessProver44.mkHornVariable("", (jayhorn.solver.ProverType)boolType46);
    jayhorn.solver.ProverExpr proverExpr48 = princessProver24.mkLeq(proverExpr32, proverExpr47);
    jayhorn.solver.princess.PrincessProver princessProver49 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver50 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType52 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr53 = princessProver50.mkHornVariable("", (jayhorn.solver.ProverType)boolType52);
    jayhorn.solver.princess.PrincessProver princessProver54 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType56 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr57 = princessProver54.mkHornVariable("", (jayhorn.solver.ProverType)boolType56);
    jayhorn.solver.princess.PrincessProver princessProver58 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType60 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr61 = princessProver58.mkHornVariable("", (jayhorn.solver.ProverType)boolType60);
    jayhorn.solver.ProverExpr[] proverExpr_array62 = new jayhorn.solver.ProverExpr[] { proverExpr61 };
    jayhorn.solver.princess.PrincessProver princessProver63 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType65 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr66 = princessProver63.mkHornVariable("", (jayhorn.solver.ProverType)boolType65);
    jayhorn.solver.ProverExpr[] proverExpr_array67 = new jayhorn.solver.ProverExpr[] { proverExpr66 };
    jayhorn.solver.ProverExpr proverExpr68 = princessProver50.substitute(proverExpr57, proverExpr_array62, proverExpr_array67);
    jayhorn.solver.princess.PrincessProver princessProver69 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType71 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr72 = princessProver69.mkHornVariable("", (jayhorn.solver.ProverType)boolType71);
    jayhorn.solver.ProverExpr proverExpr73 = princessProver49.mkLeq(proverExpr57, proverExpr72);
    jayhorn.solver.ProverExpr proverExpr74 = princessProver5.mkPlus(proverExpr47, proverExpr57);
    jayhorn.solver.princess.PrincessProver princessProver75 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.BoolType boolType77 = jayhorn.solver.BoolType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr78 = princessProver75.mkHornVariable("", (jayhorn.solver.ProverType)boolType77);
    jayhorn.solver.ProverExpr proverExpr79 = princessProver5.mkNeg(proverExpr78);
    // The following exception was thrown during execution in test generation
    try {
      soottocfg.cfg.util.BfsIterator bfsIterator80 = new soottocfg.cfg.util.BfsIterator(directedGraph4, (java.lang.Object)princessProver5);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr8);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType11);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType15);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType20);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr23);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr28);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType31);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType35);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType40);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr41);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array42);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr43);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType46);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr47);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr48);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType52);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr53);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType56);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr57);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType60);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr61);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array62);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType65);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr66);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr_array67);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr68);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType71);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr72);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr73);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr74);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(boolType77);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr78);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(proverExpr79);

  }

  @Test
  public void test92() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test92"); }


    soottocfg.randoop.Javac javac1 = new soottocfg.randoop.Javac();
    java.util.List list2 = javac1.version();
    java.util.List list3 = javac1.version();
    soottocfg.randoop.Classpath classpath4 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array5 = new java.io.File[] {  };
    classpath4.addAll(file_array5);
    soottocfg.randoop.Javac javac7 = javac1.classpath(file_array5);
    soottocfg.randoop.Classpath classpath8 = new soottocfg.randoop.Classpath();
    java.lang.String str9 = classpath8.toString();
    soottocfg.randoop.Javac javac10 = javac7.bootClasspath(classpath8);
    soottocfg.randoop.Classpath classpath11 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array12 = new java.io.File[] {  };
    classpath11.addAll(file_array12);
    java.util.List list14 = javac10.compile(file_array12);
    java.lang.String[] str_array15 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list14);
    soottocfg.cfg.ClassVariable classVariable16 = new soottocfg.cfg.ClassVariable("hi!", (java.util.Collection)list14);
    soottocfg.randoop.Command command17 = new soottocfg.randoop.Command(list14);
    // The following exception was thrown during execution in test generation
    try {
      java.io.InputStream inputStream18 = command17.getInputStream();
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalStateException");
    } catch (java.lang.IllegalStateException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalStateException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalStateException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac7);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str9 + "' != '" + ""+ "'", str9.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array15);

  }

  @Test
  public void test93() throws Throwable {

    if (debug) { System.out.format("%n%s%n","RegressionTest0.test93"); }


    soottocfg.cfg.Program program0 = new soottocfg.cfg.Program();
    soottocfg.cfg.method.Method[] method_array1 = program0.getEntryPoints();
    soottocfg.cfg.method.Method[] method_array2 = program0.getMethods();
    java.util.Map map3 = program0.getModifiedGlobals();
    org.jgrapht.DirectedGraph directedGraph4 = program0.getCallGraph();
    java.lang.Object obj5 = soottocfg.cfg.util.GraphUtil.getSink(directedGraph4);
    java.lang.Object obj6 = soottocfg.cfg.util.GraphUtil.getSource(directedGraph4);
    soottocfg.randoop.Javac javac8 = new soottocfg.randoop.Javac();
    java.util.List list9 = javac8.version();
    java.util.List list10 = javac8.version();
    soottocfg.randoop.Classpath classpath11 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array12 = new java.io.File[] {  };
    classpath11.addAll(file_array12);
    soottocfg.randoop.Javac javac14 = javac8.classpath(file_array12);
    soottocfg.randoop.Javac javac15 = new soottocfg.randoop.Javac();
    java.util.List list16 = javac15.version();
    java.util.List list17 = javac15.version();
    soottocfg.randoop.Classpath classpath18 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array19 = new java.io.File[] {  };
    classpath18.addAll(file_array19);
    soottocfg.randoop.Javac javac21 = javac15.classpath(file_array19);
    soottocfg.randoop.Javac javac22 = javac8.sourcepath(file_array19);
    soottocfg.randoop.Javac javac23 = new soottocfg.randoop.Javac();
    java.util.List list24 = javac23.version();
    java.util.List list25 = javac23.version();
    soottocfg.randoop.Classpath classpath26 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array27 = new java.io.File[] {  };
    classpath26.addAll(file_array27);
    soottocfg.randoop.Javac javac29 = javac23.classpath(file_array27);
    soottocfg.randoop.Classpath classpath30 = new soottocfg.randoop.Classpath();
    java.lang.String str31 = classpath30.toString();
    soottocfg.randoop.Javac javac32 = javac29.bootClasspath(classpath30);
    soottocfg.randoop.Classpath classpath33 = new soottocfg.randoop.Classpath();
    java.io.File[] file_array34 = new java.io.File[] {  };
    classpath33.addAll(file_array34);
    java.util.List list36 = javac32.compile(file_array34);
    java.lang.String[] str_array37 = soottocfg.randoop.Strings.generateArrayOfStrings((java.util.Collection)list36);
    soottocfg.randoop.Javac javac38 = javac22.extraArgs(list36);
    soottocfg.cfg.ClassVariable classVariable39 = new soottocfg.cfg.ClassVariable("Bool", (java.util.Collection)list36);
    soottocfg.cfg.type.Type type40 = classVariable39.getType();
    // The following exception was thrown during execution in test generation
    try {
      boolean b41 = soottocfg.cfg.util.GraphUtil.isReducibleGraph(directedGraph4, (java.lang.Object)classVariable39);
      org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException");
    } catch (java.lang.IllegalArgumentException e) {
      // Expected exception.
      if (! e.getClass().getCanonicalName().equals("java.lang.IllegalArgumentException")) {
        org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException, got " + e.getClass().getCanonicalName());
      }
    }
    
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array1);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(method_array2);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(map3);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(directedGraph4);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(obj5);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNull(obj6);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list9);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list10);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array12);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac14);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list16);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list17);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array19);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac21);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac22);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list24);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list25);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array27);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac29);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertTrue("'" + str31 + "' != '" + ""+ "'", str31.equals(""));
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac32);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(file_array34);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(list36);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(str_array37);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(javac38);
    
    // Regression assertion (captures the current behavior of the code)
    org.junit.Assert.assertNotNull(type40);

  }

}
