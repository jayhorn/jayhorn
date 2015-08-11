/**
 * 
 */
package jayhorn.test.prover_test;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import jayhorn.solver.Main;
import jayhorn.solver.Prover;
import jayhorn.solver.ProverFactory;
import jayhorn.solver.princess.PrincessProverFactory;
import jayhorn.solver.z3.Z3ProverFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * @author schaef
 *
 */
@RunWith(Parameterized.class)
public class AllProverTest {

	private final String proverName;
	private final ProverFactory proverFactory;
	
	@Parameterized.Parameters (name = "{index}: ({0})")
	public static Collection<Object[]> data() {
		List<Object[]> testData = new LinkedList<Object[]>();
		testData.add(new Object[] {"Princess", new PrincessProverFactory()});
		testData.add(new Object[] {"Z3", new Z3ProverFactory()});
	   return testData;
   }

	public AllProverTest(String proverName, ProverFactory factory) {
		this.proverFactory = factory;
		this.proverName = proverName;
	}
	
	@Test
	public void test() {
		Prover p = null;
		try {
			System.out.println("Checking prover: "+ this.proverName);
			Main proverMain = new Main();
			proverMain.runTests(this.proverFactory);
		} catch (Throwable e) {
			e.printStackTrace();
			fail(e.toString());
		} finally {
			if (p!=null) {
				p.shutdown();
			}
		}
	}

}
