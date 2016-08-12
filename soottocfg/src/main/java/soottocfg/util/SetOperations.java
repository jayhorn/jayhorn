package soottocfg.util;

import java.util.HashSet;
import java.util.Set;

public class SetOperations {

	// These create new sets, and so are non-destructive.
	public static <T> Set<T> intersect(Set<T> s1, Set<T> s2) {
		Set<T> intersection = new HashSet<T>(s1);
		intersection.retainAll(s2);
		return intersection;
	}

	public static <T> Set<T> union(Set<T> s1, Set<T> s2) {
		Set<T> rval = new HashSet<T>(s1);
		rval.addAll(s2);
		return rval;
	}

	public static <T> Set<T> minus(Set<T> s1, Set<T> s2) {
		Set<T> rval = new HashSet<T>(s1);
		rval.removeAll(s2);
		return rval;
	}
}
