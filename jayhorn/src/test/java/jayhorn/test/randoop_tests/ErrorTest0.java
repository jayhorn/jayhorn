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
    princessProver0.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver3 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array4 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr5 = princessProver3.mkOr(proverExpr_array4);
    jayhorn.solver.ProverExpr proverExpr6 = princessProver0.mkOr(proverExpr_array4);
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str9 = boolType8.toString();
    jayhorn.solver.ProverType[] proverType_array10 = new jayhorn.solver.ProverType[] { boolType8 };
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array12 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr13 = princessProver11.mkOr(proverExpr_array12);
    jayhorn.solver.ProverFun proverFun14 = princessProver0.mkDefinedFunction("hi!", proverType_array10, proverExpr13);
    jayhorn.solver.princess.PrincessProver princessProver16 = new jayhorn.solver.princess.PrincessProver();
    princessProver16.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver19 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array20 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr21 = princessProver19.mkOr(proverExpr_array20);
    jayhorn.solver.ProverExpr proverExpr22 = princessProver16.mkOr(proverExpr_array20);
    jayhorn.solver.BoolType boolType24 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str25 = boolType24.toString();
    jayhorn.solver.ProverType[] proverType_array26 = new jayhorn.solver.ProverType[] { boolType24 };
    jayhorn.solver.princess.PrincessProver princessProver27 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array28 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr29 = princessProver27.mkOr(proverExpr_array28);
    jayhorn.solver.ProverFun proverFun30 = princessProver16.mkDefinedFunction("hi!", proverType_array26, proverExpr29);
    jayhorn.solver.BoolType boolType31 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str32 = boolType31.toString();
    jayhorn.solver.ProverFun proverFun33 = princessProver0.mkUnintFunction("", proverType_array26, (jayhorn.solver.ProverType)boolType31);
    int[][] i_array_array34 = new int[][] {  };
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr[] proverExpr_array35 = princessProver0.interpolate(i_array_array34);

  }

  @Test
  public void test2() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test2"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    princessProver0.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver3 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array4 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr5 = princessProver3.mkOr(proverExpr_array4);
    jayhorn.solver.ProverExpr proverExpr6 = princessProver0.mkOr(proverExpr_array4);
    int[][] i_array_array7 = new int[][] {  };
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr[] proverExpr_array8 = princessProver0.interpolate(i_array_array7);

  }

  @Test
  public void test3() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test3"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverType proverType1 = princessProver0.getBooleanType();
    princessProver0.push();
    int[][] i_array_array3 = new int[][] {  };
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr[] proverExpr_array4 = princessProver0.interpolate(i_array_array3);

  }

  @Test
  public void test4() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test4"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array1 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr2 = princessProver0.mkOr(proverExpr_array1);
    jayhorn.solver.BoolType boolType4 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str5 = boolType4.toString();
    jayhorn.solver.ProverExpr proverExpr6 = princessProver0.mkHornVariable("hi!", (jayhorn.solver.ProverType)boolType4);
    int[][] i_array_array7 = new int[][] {  };
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr[] proverExpr_array8 = princessProver0.interpolate(i_array_array7);

  }

  @Test
  public void test5() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test5"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    princessProver0.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver3 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array4 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr5 = princessProver3.mkOr(proverExpr_array4);
    jayhorn.solver.ProverExpr proverExpr6 = princessProver0.mkOr(proverExpr_array4);
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str9 = boolType8.toString();
    jayhorn.solver.ProverType[] proverType_array10 = new jayhorn.solver.ProverType[] { boolType8 };
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array12 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr13 = princessProver11.mkOr(proverExpr_array12);
    jayhorn.solver.ProverFun proverFun14 = princessProver0.mkDefinedFunction("hi!", proverType_array10, proverExpr13);
    princessProver0.setPartitionNumber(10);
    jayhorn.solver.ProverType proverType17 = princessProver0.getBooleanType();
    jayhorn.solver.princess.PrincessProver princessProver18 = new jayhorn.solver.princess.PrincessProver();
    princessProver18.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver21 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array22 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr23 = princessProver21.mkOr(proverExpr_array22);
    jayhorn.solver.ProverExpr proverExpr24 = princessProver18.mkOr(proverExpr_array22);
    jayhorn.solver.princess.PrincessProver princessProver25 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array26 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr27 = princessProver25.mkOr(proverExpr_array26);
    jayhorn.solver.BoolType boolType29 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str30 = boolType29.toString();
    jayhorn.solver.ProverExpr proverExpr31 = princessProver25.mkHornVariable("hi!", (jayhorn.solver.ProverType)boolType29);
    jayhorn.solver.ProverExpr proverExpr32 = princessProver18.mkNeg(proverExpr31);
    jayhorn.solver.ProverExpr proverExpr33 = princessProver0.mkNot(proverExpr31);
    jayhorn.solver.princess.PrincessProver princessProver34 = new jayhorn.solver.princess.PrincessProver();
    princessProver34.setPartitionNumber((-1));
    princessProver34.reset();
    jayhorn.solver.princess.PrincessProver princessProver38 = new jayhorn.solver.princess.PrincessProver();
    princessProver38.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver41 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array42 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr43 = princessProver41.mkOr(proverExpr_array42);
    jayhorn.solver.ProverExpr proverExpr44 = princessProver38.mkOr(proverExpr_array42);
    jayhorn.solver.princess.PrincessProver princessProver45 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.princess.PrincessProver princessProver46 = new jayhorn.solver.princess.PrincessProver();
    princessProver46.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver49 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array50 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr51 = princessProver49.mkOr(proverExpr_array50);
    jayhorn.solver.ProverExpr proverExpr52 = princessProver46.mkOr(proverExpr_array50);
    jayhorn.solver.BoolType boolType54 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str55 = boolType54.toString();
    jayhorn.solver.ProverType[] proverType_array56 = new jayhorn.solver.ProverType[] { boolType54 };
    jayhorn.solver.princess.PrincessProver princessProver57 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array58 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr59 = princessProver57.mkOr(proverExpr_array58);
    jayhorn.solver.ProverFun proverFun60 = princessProver46.mkDefinedFunction("hi!", proverType_array56, proverExpr59);
    jayhorn.solver.ProverExpr proverExpr62 = princessProver46.mkLiteral((-1));
    jayhorn.solver.IntType intType63 = jayhorn.solver.IntType.INSTANCE;
    jayhorn.solver.ProverExpr proverExpr64 = princessProver45.mkAll(proverExpr62, (jayhorn.solver.ProverType)intType63);
    jayhorn.solver.ProverExpr proverExpr65 = princessProver34.mkAnd(proverExpr44, proverExpr64);
    jayhorn.solver.princess.PrincessProver princessProver66 = new jayhorn.solver.princess.PrincessProver();
    princessProver66.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver69 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array70 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr71 = princessProver69.mkOr(proverExpr_array70);
    jayhorn.solver.ProverExpr proverExpr72 = princessProver66.mkOr(proverExpr_array70);
    jayhorn.solver.BoolType boolType74 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str75 = boolType74.toString();
    jayhorn.solver.ProverType[] proverType_array76 = new jayhorn.solver.ProverType[] { boolType74 };
    jayhorn.solver.princess.PrincessProver princessProver77 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array78 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr79 = princessProver77.mkOr(proverExpr_array78);
    jayhorn.solver.ProverFun proverFun80 = princessProver66.mkDefinedFunction("hi!", proverType_array76, proverExpr79);
    jayhorn.solver.ProverExpr proverExpr82 = princessProver66.mkLiteral((-1));
    jayhorn.solver.princess.PrincessProver princessProver83 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array84 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr85 = princessProver83.mkOr(proverExpr_array84);
    jayhorn.solver.ProverExpr proverExpr86 = princessProver66.mkAnd(proverExpr_array84);
    jayhorn.solver.princess.PrincessProver princessProver87 = new jayhorn.solver.princess.PrincessProver();
    princessProver87.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver91 = new jayhorn.solver.princess.PrincessProver();
    princessProver91.setPartitionNumber((-1));
    princessProver91.reset();
    jayhorn.solver.ProverType proverType95 = princessProver91.getIntType();
    jayhorn.solver.ProverExpr proverExpr96 = princessProver87.mkVariable("Array(10)", proverType95);
    jayhorn.solver.ProverExpr[] proverExpr_array97 = new jayhorn.solver.ProverExpr[] { proverExpr96 };
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr proverExpr98 = princessProver0.substitute(proverExpr64, proverExpr_array84, proverExpr_array97);

  }

  @Test
  public void test6() throws Throwable {

    if (debug) { System.out.format("%n%s%n","ErrorTest0.test6"); }


    jayhorn.solver.princess.PrincessProver princessProver0 = new jayhorn.solver.princess.PrincessProver();
    princessProver0.setPartitionNumber((-1));
    jayhorn.solver.princess.PrincessProver princessProver3 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array4 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr5 = princessProver3.mkOr(proverExpr_array4);
    jayhorn.solver.ProverExpr proverExpr6 = princessProver0.mkOr(proverExpr_array4);
    jayhorn.solver.BoolType boolType8 = jayhorn.solver.BoolType.INSTANCE;
    java.lang.String str9 = boolType8.toString();
    jayhorn.solver.ProverType[] proverType_array10 = new jayhorn.solver.ProverType[] { boolType8 };
    jayhorn.solver.princess.PrincessProver princessProver11 = new jayhorn.solver.princess.PrincessProver();
    jayhorn.solver.ProverExpr[] proverExpr_array12 = new jayhorn.solver.ProverExpr[] {  };
    jayhorn.solver.ProverExpr proverExpr13 = princessProver11.mkOr(proverExpr_array12);
    jayhorn.solver.ProverFun proverFun14 = princessProver0.mkDefinedFunction("hi!", proverType_array10, proverExpr13);
    jayhorn.solver.princess.PrincessProver princessProver16 = new jayhorn.solver.princess.PrincessProver();
    princessProver16.setConstructProofs(true);
    jayhorn.solver.ProverResult proverResult20 = princessProver16.getResult(true);
    jayhorn.solver.ProverType proverType21 = princessProver16.getIntType();
    // during test generation this statement threw an exception of type java.lang.AssertionError in error
    jayhorn.solver.ProverExpr proverExpr22 = princessProver0.mkBoundVariable((-1), proverType21);

  }

}
