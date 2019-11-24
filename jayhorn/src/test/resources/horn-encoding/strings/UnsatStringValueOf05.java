/*
 * Origin of the benchmark:
 *     license: 4-clause BSD (see /java/jbmc-regression/LICENSE)
 *     repo: https://github.com/diffblue/cbmc.git
 *     branch: develop
 *     directory: regression/jbmc-strings/StringValueOf05
 * The benchmark was taken from the repo: 24 January 2018
 */
//import org.sosy_lab.sv_benchmarks.Verifier;

class Main {
    public static void main(String[] args) {
        args = new String[1];
        args[0] = nondetString();
        if (args[0].length() < 1)
            return;

        char characterValue = args[0].charAt(0);
        String tmp = String.valueOf(characterValue);
        assert tmp.equals("A");
    }
    public static String nondetString()
    {
        java.util.Random random = new java.util.Random();
        int size = random.nextInt();
        assume(size >= 0);
        byte[] bytes = new byte[size];
        random.nextBytes(bytes);
        return new String(bytes);
    }
    public static void assume(boolean condition)
    {
        if(!condition) {
            Runtime.getRuntime().halt(1);
        }
    }
}
