public class UnsatIssue135 {

	private Node mRootNode;

	public UnsatIssue135() {
		this.mRootNode = new Node();
		this.mRootNode.mIsLeafNode = true;
	}

	public void add() {
		Node rootNode = this.mRootNode;
		if (rootNode.mNumKeys >= 0) {
			 assert(false);
		} else {
			++rootNode.mNumKeys;
		}
	}

	public class Node {
		public int mNumKeys;
		public boolean mIsLeafNode;

		public Node() {
			this.mNumKeys = 0;
		}
	}

	public static void main(String args[]) {
		try {
			UnsatIssue135 bt = new UnsatIssue135();
			bt.add();
		} catch (NullPointerException e) {
		}
	}
}
