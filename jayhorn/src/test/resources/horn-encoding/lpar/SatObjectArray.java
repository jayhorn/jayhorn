public class SatObjectArray {

	public static class Node {
		final Node next;
		final int data; //cannot verify if data is final

		public Node(Node next, int data) {
			this.next = next;
			this.data = data;
		}
	}

	public static void main(String[] args) {
		java.util.Random random = new java.util.Random(42);

		final int[] table = new int[10] ;

		Node n = null;
		while (random.nextBoolean()) {
			final int d = random.nextInt();
			if (d >= 0 && d < table.length)
				n = new Node(n, d);
		}
		
		while (n != null) {
			table[n.data]++;
			n = n.next;
		}

		// If the next line is uncommented, verification fails
		// (would need information about allocation sites)
		 new Node(null, 100);
	}
}
