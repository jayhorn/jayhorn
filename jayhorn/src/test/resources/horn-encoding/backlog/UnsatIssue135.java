public class UnsatIssue135 {

	private Node mRootNode;

	public UnsatIssue135() {
		this.mRootNode = new Node();
		this.mRootNode.mIsLeafNode = true;
	}

	public void add() {
		Node rootNode = this.mRootNode;
		//int test = rootNode.mNumKeys;
		assert(rootNode.mNumKeys < 0);
	}

	public class Node {
		public int mNumKeys;
		public boolean mIsLeafNode;
	}

	public static void main(String args[]) {
		try {
			UnsatIssue135 bt = new UnsatIssue135();
			bt.add();
		} catch (NullPointerException e) {
		}
	}
}
