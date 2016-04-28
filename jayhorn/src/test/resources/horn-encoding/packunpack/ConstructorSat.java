import java.util.Random;

class AAAA
{
  public int i;

  public AAAA(int i) {
    this.i = i;
  }
};


class ConstructorSat 
{
	  public static void main(String[] args)
	  {
	    AAAA a = new AAAA(42);
	    assert a.i == 42;
	  }

	// pack everything after constructor?
}
