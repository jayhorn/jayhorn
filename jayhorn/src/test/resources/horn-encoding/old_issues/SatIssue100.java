
class B {
        public int x,y;
}

public class SatIssue100 {

        public static void main(String args[]) {
                B b = new B();
                b.x = 40;
                b.y = 42;
                assert b.x+2==b.y;
        }
}