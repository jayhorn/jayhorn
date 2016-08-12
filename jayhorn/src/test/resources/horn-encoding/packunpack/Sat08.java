class AAA
{
  public int i;
};

class BBB extends AAA
{
  public int j;
}

class Sat08
{
	  public static void main(String[] args)
	  {
	    BBB b = new BBB();
	    b.i = 42;
	    b.j = b.i;
	    assert b.j == b.i;
	  }
}
