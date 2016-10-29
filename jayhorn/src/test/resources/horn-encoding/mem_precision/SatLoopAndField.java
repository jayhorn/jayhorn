class A
{
  public int i;
};

class SatLoopAndField
{
	  public static void main(String[] args)
	  {
	    A a=new A();
	    a.i = 0;
	    int N = 10;
	    for (int i=0; i < N; i++)
	      a.i++; 
	    assert N == a.i;
	  }
}
