
public class SatEmptyStringConcat {

    public static void main(String[] args) {
        String e = "";
        String r = e.concat("");
        assert (r.equals(""));
    }

}
