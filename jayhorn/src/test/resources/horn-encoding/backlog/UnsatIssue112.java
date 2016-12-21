
public class UnsatIssue112 {
	public int i;
	public static void main(String[] args) {	
		UnsatIssue112[] as = new UnsatIssue112[2];
		UnsatIssue112 tmp = as[0];
		int x = tmp.i;
		assert false;
	}
}
