
class A
{
  public int i;
};

class SatAliasing01
{

	public void m(A a1, A a2) {
	  a1.i = 41;
	  a2.i = 42;
	}

	  public static void main(String[] args)
	  {
	    A a1 = new A();
	    A a2 = new A();

	SatAliasing01 as = new SatAliasing01();

	    as.m(a1,a2);
	   assert a1.i < a2.i;
	  }
}
