
class A
{
  public int i;
};

class SatAliasing02 
{

	public void m(A a1, A a2) {
	  a1.i = 41;
	  a2.i = 42;
	}

	  public static void main(String[] args)
	  {
	    A a1 = new A();
	    A a2 = new A();

	SatAliasing02 as = new SatAliasing02();
	    as.m(a2,a2);
	    assert a2.i == 42;
	  }
}
