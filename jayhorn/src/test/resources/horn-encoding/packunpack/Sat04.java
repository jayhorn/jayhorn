import java.util.Random;

class A
{
  public int i;
};

class Sat04
{
	  public static void main(String[] args)
	  {
		Random rand = new Random(42);
		int N = rand.nextInt();

	    A a=new A();
	    a.i = 0;
	    for (int i=0; i < N; i++)
	      a.i++; 
	    assert N == a.i;
	  }
}
