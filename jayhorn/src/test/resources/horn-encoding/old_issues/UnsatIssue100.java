class A {
        public int x;
}

class B extends A {
        public int y;
}

public class UnsatIssue100 {

        public static void main(String args[]) {
                B b = new B();
                b.x = 40;
                b.y = 42;
                assert b.x==b.y;
        }
}