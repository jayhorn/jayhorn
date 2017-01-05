
public class UnsatHavoc01 {
	public static void main(String[] args) {
		long l1 = 10L;
		long l2 = 11L;
		int x = (int)l1;
		int y = (int)l2;
		
		char c = (char)1L;

		int i = c;
		
		assert (x==y);
	}
}
