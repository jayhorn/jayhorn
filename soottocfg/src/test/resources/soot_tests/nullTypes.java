package soot_tests;

/**
 * @author Tobias Hamann
 */
public class nullTypes {

    Integer doStuff(Integer i) {
        if (i == null) {
            return null;
        }
        return 1;
    }

}
