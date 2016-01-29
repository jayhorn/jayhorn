import java.util.Random;

class Unsat01
{
	  public static void main(String[] arg) {
	    int i = 0;
	    boolean b = new Random().nextBoolean();

	    while(true) {
	      i++;
	      assert(b);
	    }
	  }
}
