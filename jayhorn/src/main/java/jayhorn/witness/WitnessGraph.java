package jayhorn.witness;

import java.math.BigInteger;
import jayhorn.Main;

public class WitnessGraph {


    public static String header(String file, String time, String hash, int bits, String typ) {
        return String.format(" <graph edgedefault=\"directed\">%n" +
                "  <data key=\"witness-type\">%s</data>%n" +
                "  <data key=\"creationtime\">%s</data>%n" +
                "  <data key=\"sourcecodelang\">Java</data>%n" +
                "  <data key=\"producer\">JayHorn %s</data>%n" +
                "  <data key=\"specification\">CHECK( init(Main.main()), LTL(G assert) )</data>%n" +
                "  <data key=\"programfile\">%s</data>%n" +
                "  <data key=\"programhash\">%s</data>%n" +
                "  <data key=\"architecture\">%sbit</data>%n", typ, time, Main.getVersion(), file, hash, bits);
    }

    public static String entry(String id) {
        return String.format("  <node id=\"%s\">%n" +
                "   <data key=\"entry\">true</data>%n" +
                "  </node>%n", id);
    }

    public static String violation(String id) {
        return String.format("  <node id=\"%s\">%n" +
                "   <data key=\"violation\">true</data>%n" +
                "  </node>%n", id);
    }

    public static String node(String id) {
        return String.format("  <node id=\"%s\"></node>%n", id);
    }

    public static String loop(String id, String inv) {
        return String.format("  <node id=\"%s\">%n" +
                "   <data key=\"invariant\">%s</data>%n" +
                "  </node>%n", id, inv);
    }

    public static String loop(String id, String inv, String scope) {
        return String.format("  <node id=\"%s\">%n" +
                "   <data key=\"invariant\">%s</data>%n" +
                "   <data key=\"invariant.scope\">%s</data>%n" +
                "  </node>%n", id, inv, scope);
    }

    public static String call(String fun, String src, String dst) {
        return String.format("  <edge source=\"%s\" target=\"%s\">%n" +
                "   <data key=\"enterFunction\">%s</data>%n" +
                "  </edge>%n", src, dst, fun);
    }

    public static String call(String fun, String res, String src, String dst,String originFile, String startLine,String scope) {
        return String.format("  <edge source=\"%s\" target=\"%s\">%n" +
                "   <data key=\"originfile\">%s</data>%n" +
                "   <data key=\"startline\">%s</data>%n" +
                "   <data key=\"assumption\">\\result == %s</data>%n" +
                "   <data key=\"assumption.scope\">%s</data>%n" +
                "   <data key=\"assumption.resultfunction\">%s</data>%n" +
                "   </edge>%n", src, dst,originFile,startLine, res, scope,fun);
    }
    public static String transitiontoViolation(String src, String dst) {
        return String.format("  <edge source=\"%s\" target=\"%s\">%n" +
                "   </edge>%n", src, dst);
    }
    public final static String footer =" </graph>";
}
