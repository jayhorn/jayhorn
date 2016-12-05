class Node {
	int key;

	public int key() {
		return key;
	}
}

class RedBlackTreeNode extends Node {
	public RedBlackTreeNode(int key) {
		this.key = key;
	}

	// remove this override and it works
	public int key() {
		return super.key();
	}
}

public class UnsatIssue87 {
	public static void main(String args[]) { 
		RedBlackTreeNode node = new RedBlackTreeNode(42);
		node.key();
		assert false;
	}
}
