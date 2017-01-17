public class SatObjectArray2 {

	public static class Node {
		final Node next;
		final int data; //cannot verify if data is final

		public Node(Node next, int data) {
			this.next = next;
			this.data = data;
		}
	}

	public static void main(String[] args) {
		final int size = 10;
		final int[] table = new int[size] ;
		Node l1 = null;
		Node l2 = null;
		for (int i=0; i<args.length; i++) {
				int d = Integer.parseInt(args[i]);
				if (d >= 0 && d < size) {
					l1 = new Node(l1, d);
				} else {
					l2 = new Node(l2, d);
				}
		}
		while (l1 != null) {			
			table[l1.data]++;
			l1 = l1.next;
		}
	}
}
