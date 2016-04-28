
class A
{
  public int i;
};

class Unsat01
{
	  public static void main(String[] args)
	  {
	    A a=new A();
	    a.i = 999;
	    assert 999 != a.i;
	  }
}
