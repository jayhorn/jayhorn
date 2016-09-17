import java.util.LinkedList;

class UnsatLinkedListValues {
	public static void main(String args[]) {
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(Integer.valueOf(1));
		Integer i = list.get(0);
		assert(i==null || i.equals(new Integer(0)));
	}
}
