import java.util.Random;

class UnsatCopies {
  int i;

  public static void main(String[] args)  { 
//    Random r = new Random();
    UnsatCopies a = new UnsatCopies();
//    UnsatCopies b = new UnsatCopies();
    UnsatCopies c = new UnsatCopies();
    a.i = 41;
//    b.i = 42;
//    c.i = 43;
//    if (r.nextBoolean()) {
//      a = c;
//    }
    assert a==c;
  }
}
