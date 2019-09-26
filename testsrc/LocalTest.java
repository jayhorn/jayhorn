public class LocalTest {

    public static void main(String[] args) {
        String a = "a";
        String s = a.concat("b");
        assert (s.equals("ab"));

//        String e = "";
//        String r = e.concat("");
//        assert (r.equals(""));

//        String x = "A";
//        String s = x.concat("B");
//        assert (s.equals("AB"));

//        String e = "";
//        String r = e.concat("");
//        assert (r.equals(""));
//
//        String r2 = r.concat(e);
//        assert (r2.equals(e));
//
//        String s = e.concat("b");
//        assert (s.equals("b"));
//
//        String c = "c";
//        String t = c.concat(e);
//        assert (t.equals("c"));
    }

}

/*

    public static void main(String[] args) {
        String s = "abc";       // r1
        String t = "abc";       // r2
        assert (s.equals(t));   // $z1 = s.equals(t)
    }

    ===>

    public static void main(JayArray_java_lang_String)
    {
        JayArray_java_lang_String r0;
        java.lang.String r1, r2;
        boolean $z0, $z1, $assert_6, $assert_8;
        java.lang.AssertionError $r3;
        java.lang.Throwable $helper1;

        r0 := @parameter0: JayArray_java_lang_String;

        staticinvoke <SatStr: void <clinit>()>();

        staticinvoke <JayHornAssertions: void <clinit>()>();

        r1 = "abc";

        r2 = "abc";

        $z1 = virtualinvoke r1.<java.lang.String: boolean equals(java.lang.Object)>(r2);

        $helper1 = <JayHornAssertions: java.lang.Throwable lastExceptionThrown>;

        $assert_8 = $helper1 == null;

        staticinvoke <JayHornAssertions: void super_crazy_assertion(boolean)>($assert_8);

        if $z1 != 0 goto label1;

        $assert_6 = 0;

        staticinvoke <JayHornAssertions: void super_crazy_assertion(boolean)>($assert_6);

     label1:
        return;
    }

*/
