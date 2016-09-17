
public class UnsatIntReturn {

	public static int foo() {
		return 3;
	}
	
	public static void main(String[] args) {
		assert foo()==2;
	}

}
