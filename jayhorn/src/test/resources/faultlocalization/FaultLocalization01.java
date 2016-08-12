package faultlocalization;

/**
 * @author schaef
 *
 */
//@SuppressWarnings("unused")
public class FaultLocalization01 {
	
//	public void f1() {
//		int i = 0;
//		while (i < 9)
//			i = i + 1;
//		assert (i == 10);
//	}

	public boolean stringCompare() {
		String s = "hallo";
		if (s == "wurstsalat") return false;
		return true;
	}

	public void loopOfByOne(int [] arr) {
		for (int i=0; i<=arr.length;i++) {
			arr[i]=i; // INFEASIBLE
		}
	}

	public int nested(boolean b) {
		int i = 0;
		int y=2;
		if (b) {
			i++;	
			y+=17;
		} else {
			i--;
			y-=100;
		}
		y--;
		assert(i==0);
		return y;
	}
	
    /** <org.apache.tools.ant.taskdefs.email.EmailAddress: java.lang.String trim(java.lang.String,boolean)>
     * This one currently breaks the fault localization, resulting in checks
     * that are Sat instead of unsat.
     */
//    public String trim(String t, boolean trimAngleBrackets) {
//        int start = 0;
//        int end = t.length();
//        boolean trim = false;
//        do {
//            trim = false;
//            if (t.charAt(end - 1) == ')'
//                || (t.charAt(end - 1) == '>' && trimAngleBrackets)
//                || (t.charAt(end - 1) == '"' && t.charAt(end - 2) != '\\')
//                || t.charAt(end - 1) <= '\u0020') {
//                trim = true;
//                end--;
//            }
//            if (t.charAt(start) == '('
//                || (t.charAt(start) == '<' && trimAngleBrackets)
//                || t.charAt(start) == '"'
//                || t.charAt(start) <= '\u0020') {
//                trim = true;
//                start++;
//            }
//        } while (trim);
//        return t.substring(start, end);
//    }

	
}



