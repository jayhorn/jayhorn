import java.util.ArrayList;

class SatArrayListValues {
	public static void main(String args[]) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		boolean test = list.add(1);
		assert(list.get(0)==1);
	}
}
