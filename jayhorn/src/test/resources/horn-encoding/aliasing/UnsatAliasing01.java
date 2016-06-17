
class A
{
  public int i;
};

class UnsatAliasing01
{

	  public static void main(String[] args)
	  {
	    A a1 = new A();
	    a1.i = 10;
	    A a2 = a1;
	    a2.i = 20;

	    assert a1.i==10;
	  }
}
