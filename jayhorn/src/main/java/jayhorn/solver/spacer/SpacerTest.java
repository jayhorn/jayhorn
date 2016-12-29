package jayhorn.solver.spacer;


import java.util.HashMap;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Fixedpoint;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Global;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Symbol;
import com.microsoft.z3.Z3Exception;

public class SpacerTest {

    public static void main(String[] args) throws Z3Exception {
        Global.setParameter("fixedpoint.engine", "spacer");
        HashMap<String, String> cfg = new HashMap<String, String>();
        Context ctx = new Context(cfg);
        Fixedpoint fp = ctx.mkFixedpoint();

        
        /* declare function g */
        Sort I = ctx.mkIntSort();
        FuncDecl g = ctx.mkFuncDecl("g", new Sort[] { I, I }, I);


        ctx.parseSMTLIBString(
                "(benchmark tst :formula (forall (x Int) (y Int) (implies (= x y) (= (ggd x 0) (gg 0 y)))))",
                null, null, new Symbol[] { ctx.mkSymbol("gg") },
                new FuncDecl[] { g });

        BoolExpr thm = ctx.getSMTLIBFormulas()[0];
        System.out.println("formula: " + thm);
        
        FuncDecl a = ctx.mkConstDecl("a", ctx.mkBoolSort());
        fp.registerRelation(a);
        ctx.mkConst("x", ctx.mkBoolSort());
        FuncDecl b = ctx.mkConstDecl("b", ctx.mkBoolSort());
        fp.registerRelation(b);
        FuncDecl c = ctx.mkConstDecl("c", ctx.mkBoolSort());
        fp.registerRelation(c);
        fp.addFact(b);
        fp.addRule(
                ctx.mkImplies(
                        (BoolExpr) b.apply(),
                        (BoolExpr) a.apply()
                ),
                null
        );

        fp.addRule(
                ctx.mkImplies(
                        (BoolExpr) c.apply(),
                        (BoolExpr) b.apply()
                ),
                null
        );

        System.out.println(fp);

        Status ans = fp.query((BoolExpr) a.apply());
        System.out.println(ans);
//
//    System.out.println("====================");
//
//    fp.addRule((BoolExpr) c.apply(), null);
//    System.out.println(fp);
//
//
//    ans = fp.query((BoolExpr) a.apply());
//    System.out.println(ans);


//    BoolSort b = ctx.getBoolSort();
//    BoolExpr v = (BoolExpr) ctx.mkBound(1, b);
////    BoolExpr v2 = (BoolExpr) ctx.mkBound(1, b);
//    FuncDecl p = ctx.mkFuncDecl("p", new Sort[]{b, b}, b);
//    FuncDecl p2 = ctx.mkFuncDecl("p2", new Sort[]{b, b}, b);
//    Fixedpoint fp = ctx.mkFixedpoint();
//    fp.setPredicateRepresentation(p, new Symbol[] {ctx.mkSymbol("interval_relation")});
//    fp.registerRelation(p);
//    fp.registerRelation(p2);
//    fp.addRule(ctx.mkImplies((BoolExpr) p.apply(v, v), (BoolExpr) p2.apply(v, v)), null);
//    int[] array = new int[2];
//    array[0] = 1;
//    array[1] = 0;
//    fp.addFact(p, array);
//    System.out.println(fp);

//    Status a = fp.query((BoolExpr) p2.apply(ctx.mkBool(true), ctx.mkBool(true)));
//    System.out.println(a);
//    System.out.println(fp.getAnswer());
    }
}

