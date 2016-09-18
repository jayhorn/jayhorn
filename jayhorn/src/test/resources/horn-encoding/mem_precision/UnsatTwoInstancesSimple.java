import java.util.Random;

class A
{
  public int i;
};

class UnsatTwoInstancesSimple
{
	  public static void main(String[] args)
	  {
	    A a1 = new A();
	    a1.i = 42;

	    A a2 = new A();
	    a2.i = 42;

	    assert a1.i != 42;
	  }
}
