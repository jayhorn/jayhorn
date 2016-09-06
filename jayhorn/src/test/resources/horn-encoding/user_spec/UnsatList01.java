import java.util.LinkedList;
import java.util.List;

public class UnsatList01 {

	public static void main(String[] args) {
		List<Integer> l = new LinkedList<Integer>();
		l.add(42);
		assert l.size()==0;
	}
}
