package jayhorn.solver;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.Files;

import jayhorn.solver.princess.PrincessProverFactory;

public class Main {

	public void test01(Prover p) {
		System.out.println("\n\n\nDSN Testing interpolation and abduction");
		Map<String, ProverExpr> vars = new HashMap<String, ProverExpr>();
		ProverType intType = p.getIntType();
		final ProverExpr ap = getVar("a'", intType, vars, p);
		final ProverExpr bp = getVar("b'", intType, vars, p);
		final ProverExpr a = getVar("a", intType, vars, p);
		final ProverExpr b = getVar("b", intType, vars, p);
		final ProverExpr i = getVar("i", intType, vars, p);
		final ProverExpr j = getVar("j", intType, vars, p);
		final ProverExpr a1 = getVar("a1", intType, vars, p);
		final ProverExpr b1 = getVar("b1", intType, vars, p);
		final ProverExpr zero = p.mkLiteral(0);
		final ProverExpr one = p.mkLiteral(1);
		final ProverExpr two = p.mkLiteral(2);
		// ProverExpr[] toIgnore = new ProverExpr[] { a1, b1 };

		// ProverExpr hypo = p.mkEq(ap,bp);
		ProverExpr hypo = p.mkImplies(p.mkEq(a, b), p.mkEq(ap, bp));
		ProverExpr axiomAnds[] = { p.mkEq(a, b),
				p.mkNot(p.mkEq(p.mkEMod(i, two), zero)),
				p.mkEq(a1, p.mkPlus(a, one)),
				p.mkEq(b1, p.mkPlus(b, p.mkMinus(j, i))),
				// p.mkEq(j, p.mkMinus(i, one)),
				/*
				 * p.mkImplies( p.mkAnd(new ProverExpr [] { p.mkEq(a1,b1),
				 * p.mkNot(p.mkEq(p.mkEMod(p.mkPlus(i,two), two),zero)),
				 * p.mkEq(j, i)}), p.mkEq(a1,b1))
				 */
				p.mkImplies(p.mkEq(a1, b1), p.mkEq(ap, bp)) };

		final ProverExpr axiom = p.mkAnd(axiomAnds);
		System.out.println("Hypo is: " + hypo);
		System.out.println("Axioms are: " + axiom);

		// ProverExpr[] explanations =
		// ((PrincessProver)p).explainSimply(axiom, hypo, toIgnore);
		//
		// System.out.println("There were " + explanations.length +
		// " abductions");
		// for (ProverExpr e : explanations){
		// System.out.println("Explanation was: " + e);
		// System.out.println(p.mkImplies(e, hypo));
		// }
	}

	public void test02(Prover p) {
		ProverExpr c = p.mkVariable("c", p.getIntType());
		ProverExpr d = p.mkVariable("d", p.getIntType());
		ProverExpr r = p.mkVariable("r", p.getBooleanType());
		ProverExpr s = p.mkVariable("s", p.getBooleanType());

		p.addAssertion(p.mkAnd(r, p.mkEq(c, p.mkPlus(d, p.mkLiteral(15)))));
		p.addAssertion(p.mkGeq(d, p.mkLiteral(100)));

		p.addAssertion(p.mkOr(p.mkNot(r), s));
		System.out.println(p.checkSat(true));

		System.out.println("c = " + p.evaluate(c));
		System.out.println("r = " + p.evaluate(r));

		p.push();

		p.addAssertion(p.mkOr(p.mkNot(s), p.mkLeq(c, p.mkLiteral(-100))));
		System.out.println(p.checkSat(true));

		p.pop();
		System.out.println(p.checkSat(true));
	}

	public void test03(Prover p) {
		p.push();
		ProverExpr c = p.mkVariable("c", p.getIntType());
		ProverExpr d = p.mkVariable("d", p.getIntType());

		ProverFun f = p.mkUnintFunction("f",
				new ProverType[] { p.getIntType() }, p.getIntType());
		p.addAssertion(p.mkEq(f.mkExpr(new ProverExpr[] { c }), p.mkLiteral(5)));
		p.addAssertion(p.mkEq(f.mkExpr(new ProverExpr[] { d }), p.mkLiteral(6)));

		System.out.println(p.checkSat(true));

		System.out.println("f(c) = "
				+ p.evaluate(f.mkExpr(new ProverExpr[] { c })));
		p.addAssertion(p.mkEq(c, d));

		System.out.println(p.checkSat(true));

		p.pop();

	}

	public void test04(Prover p) {
		p.push();
		final ProverExpr a = p.mkVariable("a", p.getIntType());
		final ProverExpr b = p.mkVariable("b", p.getIntType());

		final ProverFun geInt = p.mkDefinedFunction("geInt", new ProverType[] {
				p.getIntType(), p.getIntType() }, p.mkIte(
				p.mkGeq(p.mkBoundVariable(0, p.getIntType()),
						p.mkBoundVariable(1, p.getIntType())), p.mkLiteral(1),
				p.mkLiteral(0)));
		System.out.println(geInt);
		p.addAssertion(p.mkEq(geInt.mkExpr(new ProverExpr[] { a, b }),
				p.mkLiteral(1)));
		p.addAssertion(p.mkEq(geInt.mkExpr(new ProverExpr[] { b, a }),
				p.mkLiteral(1)));

		System.out.println(p.checkSat(true));
		p.addAssertion(p.mkNot(p.mkEq(a, b)));
		System.out.println(p.checkSat(true));
		p.pop();
	}

	public void test05(Prover p) {
		System.out.println(p.checkSat(false));
		ProverResult res;
		while ((res = p.getResult(false)) == ProverResult.Running) {
			System.out.println("Running ... ");
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
			}
		}

		System.out.println(res);
		System.out.println("-----");

		System.out.println(p.checkSat(false));
		try {
			Thread.sleep(3);
		} catch (InterruptedException e) {
		}
		System.out.println(p.stop());
		System.out.println("-----");

		System.out.println(p.checkSat(false));
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {
		}
		System.out.println(p.stop());
		System.out.println("-----");
	}

	public void test06(Prover p) {
		p.setConstructProofs(true);
		final ProverExpr a = p.mkVariable("a", p.getArrayType(new ProverType[]{p.getIntType()}, p.getIntType())  );
		final ProverExpr b = p.mkVariable("b", p.getArrayType(new ProverType[]{p.getIntType()}, p.getIntType())  );

		p.setPartitionNumber(0);
		p.addAssertion(p.mkEq(
				p.mkStore(a, new ProverExpr[] { p.mkLiteral(0) },
						p.mkLiteral(1)), b));
		p.setPartitionNumber(1);
		p.addAssertion(p.mkEq(
				p.mkSelect(b, new ProverExpr[] { p.mkLiteral(0) }),
				p.mkLiteral(2)));

		System.out.println(p.checkSat(true));

		final ProverExpr interpolant = p.interpolate(new int[][] {
				new int[] { 0 }, new int[] { 1 } })[0];
		System.out.println(interpolant);
		System.out.print("Variables: ");
		final ProverExpr[] vars = p.freeVariables(interpolant);
		for (int i = 0; i < vars.length; ++i)
			System.out.print("" + vars[i] + " ");
		System.out.println();

		System.out.println(p.substitute(interpolant, new ProverExpr[] { b },
				new ProverExpr[] { a }));
	}

	public void testHorn(Prover p) {
		p.setHornLogic(true);
		System.out.println("Running Horn test..");
            final ProverFun r =
                p.mkHornPredicate("r", new ProverType[] { p.getIntType() });
            final ProverFun s =
                p.mkHornPredicate("s", new ProverType[] { p.getIntType() });
            final ProverExpr x =
                p.mkVariable("x", p.getIntType());

            final ProverHornClause c1 =
                p.mkHornClause(r.mkExpr(new ProverExpr[] {p.mkLiteral(0)}),
                               new ProverExpr[0],
                               p.mkLiteral(true));
            final ProverHornClause c1b =
                p.mkHornClause(r.mkExpr(new ProverExpr[] {p.mkLiteral(-10)}),
                               new ProverExpr[0],
                               p.mkLiteral(true));
            final ProverHornClause c2 =
                p.mkHornClause(r.mkExpr(new ProverExpr[] {p.mkPlus(x, p.mkLiteral(1))}),
                               new ProverExpr[] {
                                   r.mkExpr(new ProverExpr[] {x})
                               },
                               p.mkLiteral(true));
            final ProverHornClause c3 =
                p.mkHornClause(s.mkExpr(new ProverExpr[] {x}),
                               new ProverExpr[] {
                                   r.mkExpr(new ProverExpr[] {x})
                               },
                               p.mkLiteral(true));
            final ProverHornClause c4 =
                p.mkHornClause(p.mkLiteral(false),
                               new ProverExpr[] {
                                   r.mkExpr(new ProverExpr[] {x})
                               },
                               p.mkLt(x, p.mkLiteral(0)));

            p.addAssertion(c1);
            p.addAssertion(c2);
            p.addAssertion(c3);
            p.addAssertion(c4);;

            if (p.checkSat(true)!=ProverResult.Sat) {
            	throw new RuntimeException("Solver failed.");
            }

            p.addAssertion(c1b);
            if (p.checkSat(true)!=ProverResult.Unsat) {
            	throw new RuntimeException("Solver failed.");
            }
            
            System.out.println("Horn test successful.");
            p.setHornLogic(false);
        }

	public void runTests(ProverFactory factory) {
		final Prover p = factory.spawn();
		
		test01(p);
		p.reset();
		test02(p);
		p.reset();
		test03(p);
		p.reset();
		test04(p);
		p.reset();
		test05(p);
		p.reset();
		test06(p);
		p.reset();
		testHorn(p);
		p.reset();

		p.shutdown();
	}

	public static void main(String[] args) {
		final ProverFactory factory = new PrincessProverFactory();
//		final ProverFactory factory = new Z3ProverFactory();
		Main m = new Main();
		m.runTests(factory);
	}

	private static ProverExpr getVar(String name, ProverType type,
			Map<String, ProverExpr> m, Prover p) {
		ProverExpr var = m.get(name);
		if (var == null) {
			var = p.mkVariable(name, type);
			m.put(name, var);
		}
		if (!var.getType().equals(type)) {
			throw new RuntimeException("Wrong type on var: " + name);
		}
		return var;
	}

	private String demoHornProg() {
		final String hornProg = 
		"(declare-rel cp-rel-entry ())\n"
		+"(declare-rel cp-rel-ERROR.i ())\n"
		+"(declare-rel cp-rel-__UFO__0_proc ())\n"
		+"(declare-var A Real)\n"
		+"(declare-var B Real)\n"
		+"(declare-var C Real)\n"
		+"(declare-var D Bool)\n"
		+"(declare-var E Bool)\n"
		+"(declare-var F Bool)\n"
		+"(rule cp-rel-entry)\n"
		+"(rule (=> (and cp-rel-entry F (not E) (= D (> C B)) (= A (ite D B C)) (= E (> C A))) cp-rel-ERROR.i))\n"
		+"(rule (=> (and cp-rel-__UFO__0_proc D) cp-rel-__UFO__0_proc))\n"
		+"(query cp-rel-ERROR.i)\n";
		return hornProg;
	}
	
}

/*
 * final ProverExpr n0 = p.mkVariable("n0", p.getIntType()); final ProverExpr n1
 * = p.mkVariable("n1", p.getIntType()); final ProverExpr m0 =
 * p.mkVariable("m0", p.getIntType()); final ProverExpr m1 = p.mkVariable("m1",
 * p.getIntType()); final ProverExpr zero = p.mkLiteral(0); final ProverExpr one
 * = p.mkLiteral(1); final ProverExpr ten = p.mkLiteral(10);
 * 
 * List<ProverExpr> unrolled = new ArrayList<ProverExpr>();
 * 
 * int loops = 1; for(int i = 0; i < loops; i++){ ProverExpr m =
 * p.mkVariable("m" + i, p.getIntType()); ProverExpr mNext = p.mkVariable("m" +
 * (i+1), p.getIntType()); ProverExpr n = p.mkVariable("n" + i, p.getIntType());
 * ProverExpr nNext = p.mkVariable("n" + (i+1), p.getIntType()); ProverExpr
 * ands[] = { p.mkLeq(m,p.mkLiteral(10)), p.mkEq(nNext,p.mkPlus(n, one)),
 * p.mkEq(mNext, p.mkPlus(m,one)), }; unrolled.add(p.mkAnd(ands)); }
 * 
 * final ProverExpr finalM = p.mkVariable("m" + loops, p.getIntType()); final
 * ProverExpr finalN = p.mkVariable("n" + loops, p.getIntType());
 * 
 * unrolled.add(p.mkGt(finalM, ten));
 * 
 * 
 * final ProverExpr left = p.mkAnd(unrolled.toArray(new
 * ProverExpr[unrolled.size()])); final ProverExpr right =
 * p.mkNot(p.mkEq(finalN,p.mkLiteral(0))); ;
 * 
 * System.out.println("Tying to explain: " + right);
 * System.out.println("Given: " + left);
 * 
 * ProverExpr[] toAvoid = {finalN,finalM}; for(ProverExpr e : toAvoid){
 * System.out.println("To avoid: " + e); }
 * 
 * //ProverExpr[] explanations = ((PrincessProver)p).explain(left, right);
 * 
 * ProverExpr[] explanations = ((PrincessProver)p).explainSimply(left,
 * right,toAvoid); System.out.println("Size of explanation array is: " +
 * explanations.length); for(ProverExpr e : explanations){
 * System.out.println("Explanation: " + e); } if(false){
 * p.setPartitionNumber(0); //p.addAssertion(explanations[0]);
 * p.addAssertion(p.mkNot(p.mkEq(n0, p.mkLiteral(-1))));
 * 
 * p.setPartitionNumber(1); p.addAssertion(left);
 * 
 * p.setPartitionNumber(2); p.addAssertion(p.mkNot(right));
 * System.out.println("Checking the implication " + p.checkSat(true));
 * ProverExpr[] interpolants = p.interpolate(new int[][] { new int[] { 0 }, new
 * int[] { 1 } }); for (ProverExpr e : interpolants){
 * System.out.println("Interpolant 0-1 is " + e); }
 * 
 * interpolants = p.interpolate(new int[][] { new int[] { 1 }, new int[] { 2 }
 * }); for (ProverExpr e : interpolants){
 * System.out.println("Interpolant 1-2 is " + e); } } } }
 * 
 * boolean old = false; if(old){ final ProverExpr n0 = p.mkVariable("n0",
 * p.getIntType()); final ProverExpr n1 = p.mkVariable("n1", p.getIntType());
 * final ProverExpr n1NotZero = p.mkNot(p.mkEq(n1,p.mkLiteral(0))); final
 * ProverExpr m0 = p.mkVariable("m0", p.getIntType()); final ProverExpr m1 =
 * p.mkVariable("m1", p.getIntType()); final ProverExpr zero = p.mkLiteral(0);
 * final ProverExpr one = p.mkLiteral(1); final ProverExpr ten =
 * p.mkLiteral(10);
 * 
 * final ProverExpr ands[] = { p.mkLeq(m0,p.mkLiteral(10)),
 * p.mkEq(n1,p.mkPlus(n0, one)), p.mkEq(m1, p.mkPlus(m0,one)), p.mkGt(m1, ten),
 * }; final ProverExpr left = p.mkAnd(ands); final ProverExpr right = n1NotZero;
 * 
 * System.out.println("Tying to explain: " + right);
 * System.out.println("Given: " + left);
 * 
 * ProverExpr[] toAvoid = {n1,m1}; for(ProverExpr e : toAvoid){
 * System.out.println("To avoid: " + e); } //ProverExpr[] explanations =
 * ((PrincessProver)p).explain(left, right);
 * 
 * ProverExpr[] explanations = ((PrincessProver)p).explainSimply(left,
 * right,toAvoid); for(ProverExpr e : explanations){
 * System.out.println("Explanation: " + e); } /* p.setPartitionNumber(0);
 * //p.addAssertion(explanations[0]); p.addAssertion(p.mkNot(p.mkEq(n0,
 * p.mkLiteral(-1))));
 * 
 * p.setPartitionNumber(1); p.addAssertion(left);
 * 
 * p.setPartitionNumber(2); p.addAssertion(p.mkNot(right));
 * System.out.println("Checking the implication " + p.checkSat(true));
 * ProverExpr[] interpolants = p.interpolate(new int[][] { new int[] { 0 }, new
 * int[] { 1 } }); for (ProverExpr e : interpolants){
 * System.out.println("Interpolant 0-1 is " + e); }
 * 
 * interpolants = p.interpolate(new int[][] { new int[] { 1 }, new int[] { 2 }
 * }); for (ProverExpr e : interpolants){
 * System.out.println("Interpolant 1-2 is " + e); }
 * 
 * } else {
 */
