package instanceof_test;

/**
 * @author schaef
 *
 */
//@SuppressWarnings("unused")
public class FaultLocalization01 {

	public int foo(Object s) {
		if (s instanceof String) {
			s.hashCode();
		}
		return (Integer)s;
	}

	
}



