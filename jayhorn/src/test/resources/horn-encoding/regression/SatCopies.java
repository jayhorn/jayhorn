import java.util.Random;

class SatCopies {
  int i;

  public static void main(String[] args)  { 
    Random r = new Random();
    SatCopies a = new SatCopies();
    SatCopies b = new SatCopies();
    SatCopies c = new SatCopies();
    a.i = 41;
    b.i = 42;
    c.i = 43;
    if (r.nextBoolean()) {
      a = c;
    }
    assert b.i>=0;
  }
}
