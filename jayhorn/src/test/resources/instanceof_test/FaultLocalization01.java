package instanceof_test;

/**
 * @author schaef
 *
 */
//@SuppressWarnings("unused")
public class FaultLocalization01 {

//	public int foo(Object s) {
//		if (s instanceof String) {
//			s.hashCode();
//		}
//		return (Integer)s;
//	}

	public int foo(Object s, boolean b) {
		if (b) {
			s = new String();
		}
		return (Integer)s;
	}

	
}



