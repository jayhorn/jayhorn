// use jayhorn.Options.setInsertRuntimeAssertions(boolean)
// to insert runtime assertion.
public class UnsatIssue68 {
    
    public static void main(String[] args) {
    	assert args.length==42;
    }
}