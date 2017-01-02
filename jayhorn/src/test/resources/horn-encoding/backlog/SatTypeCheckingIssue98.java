import java.util.Random;

public class SatTypeCheckingIssue98 {

    static Random rand = new Random();

    static int equals(int m) {
        int counter = 0;
        for (int j = 0; j < m; j++) {
            //counter++;
            if (rand.nextBoolean()) {
                return counter; //false
            }
        }
        return counter; //true
    }
	
    public static void main(String args[]) {
        final int KEY_SIZE = 2;
        final int n = rand.nextInt(); 
        if (n <= 0) return;

        int count = 0;
        for (int i = 0; i < n; i++) {
			
            count += equals(KEY_SIZE);

            if (rand.nextBoolean()) 
                return; //false
        }
        assert (count <= n*KEY_SIZE + 1);
        
    }
}
