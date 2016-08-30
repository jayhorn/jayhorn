public class UnsatFloats {
	public static void main(String[] args) {
		float number = 1.2345678f;
		
		number *= 2;
		
		assert 2.4691356 == number;
	}
}
