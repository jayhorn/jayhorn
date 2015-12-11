package soot_tests;

public interface Comparable extends Measurable {
	int LESS = -1;
	int EQUAL = 0;
	int GREATER = 1;

	int compareTo(Object o);
}
