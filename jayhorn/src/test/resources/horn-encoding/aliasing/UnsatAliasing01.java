
class AA
{
  public int i;
};

class UnsatAliasing01
{

	  public static void main(String[] args)
	  {
	    AA a1 = new AA();
	    a1.i = 10;
	    AA a2 = a1;
	    a2.i = 20;

	    assert a1.i==10;
	  }
}
