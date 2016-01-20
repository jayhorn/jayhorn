package instanceof_test;

/**
 * @author schaef
 *
 */
//@SuppressWarnings("unused")
public class InstanceOfInconsistencies {

	public int m01(Object s) {
		if (s instanceof String) {
			s.hashCode();
		}
		return (Integer)s;
	}

	public int m02(Object s, boolean b) {
		if (b) {
			s = new String();
		}
		return (Integer)s;
	}

	public int m03(Object s) {		
		if (String.class.isAssignableFrom(s.getClass())) {
			s.hashCode();
		}
		return (Integer)s;
	}

	public int m04(String s) {
		Integer i = Integer.class.cast(s);
		return i;
	}

	public int m05(Object s) {		
		if (String.class.isInstance(s)) {
			s.hashCode();
		}
		return (Integer)s;
	}
	
}



