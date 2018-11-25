

public class SatEnum {

    public static enum E {
        E1, E2, E3
    }

    public static void main(String[] args) {
        E x = E.E1;
        assert x != E.E2;

        int y = 0;

        switch (x) {
            case E2:
//                System.out.println("E2");
                y++;
                break;
            case E1:
//                System.out.println("E1");
                break;
            default:
//                System.out.println("other");
                break;
        }

        assert y == 0;
    }

}
