class Node {
	int key;

	public Node(int key) {
		this.key = key;
	}

	public int key() {
		return key;
	}
}

class RedBlackTreeNode extends Node {
	public RedBlackTreeNode(int key) {
		super(key);
	}

	// remove this override and it works
	public int key() {
		return super.key();
	}
}

public class UnsatIssue87 {
	public static void main(String args[]) { 
		RedBlackTreeNode node = new RedBlackTreeNode(42);
		assert(node.key()==0);
	}
}
