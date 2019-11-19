
class UnsatException2 {
	public static void main(String[] args) {
		RuntimeException a = new ArrayIndexOutOfBoundsException("message");
		assert false;
	}
}
