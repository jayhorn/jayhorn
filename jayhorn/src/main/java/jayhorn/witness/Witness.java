package jayhorn.witness;
import java.io.PrintStream;


public class Witness {

    private static int currentNode = 0;

    public  static  void resetCurrentNode() {currentNode = 0;}

    public static int getCurrentNode(){return currentNode;}
    public static  int bits = 64;
    private static String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <key attr.name=\"originFileName\" attr.type=\"string\" for=\"edge\" id=\"originfile\">\n" +
            "  <default>%s </default>\n" +
            " </key>\n" +
            " <key attr.name=\"invariant\" attr.type=\"string\" for=\"node\" id=\"invariant\"/>\n" +
            " <key attr.name=\"invariant.scope\" attr.type=\"string\" for=\"node\" id=\"invariant.scope\"/>\n" +
            " <key attr.name=\"namedValue\" attr.type=\"string\" for=\"node\" id=\"named\"/>\n" +
            " <key attr.name=\"nodeType\" attr.type=\"string\" for=\"node\" id=\"nodetype\">\n" +
            "  <default>path</default>\n" +
            " </key>\n" +
            " <key attr.name=\"isFrontierNode\" attr.type=\"boolean\" for=\"node\" id=\"frontier\">\n" +
            "  <default>false</default>\n" +
            " </key>\n" +
            " <key attr.name=\"isViolationNode\" attr.type=\"boolean\" for=\"node\" id=\"violation\">\n" +
            "  <default>false</default>\n" +
            " </key>\n" +
            " <key attr.name=\"isEntryNode\" attr.type=\"boolean\" for=\"node\" id=\"entry\">\n" +
            "  <default>false</default>\n" +
            " </key>\n" +
            " <key attr.name=\"isSinkNode\" attr.type=\"boolean\" for=\"node\" id=\"sink\">\n" +
            "  <default>false</default>\n" +
            " </key>\n" +
            " <key attr.name=\"enterLoopHead\" attr.type=\"boolean\" for=\"edge\" id=\"enterLoopHead\">\n" +
            "  <default>false</default>\n" +
            " </key>\n" +
            " <key attr.name=\"violatedProperty\" attr.type=\"string\" for=\"node\" id=\"violatedProperty\"/>\n" +
            " <key attr.name=\"threadId\" attr.type=\"string\" for=\"edge\" id=\"threadId\"/>\n" +
            " <key attr.name=\"sourcecodeLanguage\" attr.type=\"string\" for=\"graph\" id=\"sourcecodelang\"/>\n" +
            "<key attr.name=\"programFile\" attr.type=\"string\" for=\"graph\" id=\"programfile\"/>" +
            "<key attr.name=\"programHash\" attr.type=\"string\" for=\"graph\" id=\"programhash\"/>" +
            "<key attr.name=\"specification\" attr.type=\"string\" for=\"graph\" id=\"specification\"/>" +
            "<key attr.name=\"architecture\" attr.type=\"string\" for=\"graph\" id=\"architecture\"/>" +
            "<key attr.name=\"producer\" attr.type=\"string\" for=\"graph\" id=\"producer\"/>" +
            "<key attr.name=\"sourcecode\" attr.type=\"string\" for=\"edge\" id=\"sourcecode\"/>" +
            "<key attr.name=\"startline\" attr.type=\"int\" for=\"edge\" id=\"startline\"/>" +
            "<key attr.name=\"startoffset\" attr.type=\"int\" for=\"edge\" id=\"startoffset\"/>" +
            "<key attr.name=\"lineColSet\" attr.type=\"string\" for=\"edge\" id=\"lineCols\"/>" +
            "<key attr.name=\"control\" attr.type=\"string\" for=\"edge\" id=\"control\"/>" +
            "<key attr.name=\"assumption\" attr.type=\"string\" for=\"edge\" id=\"assumption\"/>" +
            "<key attr.name=\"assumption.resultfunction\" attr.type=\"string\" for=\"edge\" id=\"assumption.resultfunction\"/>" +
            "<key attr.name=\"assumption.scope\" attr.type=\"string\" for=\"edge\" id=\"assumption.scope\"/>" +
            "<key attr.name=\"enterFunction\" attr.type=\"string\" for=\"edge\" id=\"enterFunction\"/>" +
            "<key attr.name=\"returnFromFunction\" attr.type=\"string\" for=\"edge\" id=\"returnFrom\"/>" +
            "<key attr.name=\"predecessor\" attr.type=\"string\" for=\"edge\" id=\"predecessor\"/>" +
            "<key attr.name=\"successor\" attr.type=\"string\" for=\"edge\" id=\"successor\"/>" +
            "<key attr.name=\"witness-type\" attr.type=\"string\" for=\"graph\" id=\"witness-type\"/>" +
            "<key attr.name=\"creationtime\" attr.type=\"string\" for=\"graph\" id=\"creationtime\"/>";

    private static String footer = "</graphml>";
    public static void setTransition(String fun, String originFile, String sourceFileLine, String scope, String result, PrintStream out) {

        String Ni = "N" + currentNode; // current state
        setNode(out);
        String Nj = "N" + currentNode; // successor state
        out.println(WitnessGraph.call(fun, result, Ni, Nj,originFile,sourceFileLine,scope));
    }
    public static void setTransitionToViolation(PrintStream out) {

        String Ni = "N" + currentNode; // current state
        SetViolation(out);
        String Nj = "N" + currentNode; // successor state
        out.println(WitnessGraph.TransitiontoViolation(Ni, Nj));
    }
    public static void setHeader(String file ,PrintStream out)
    {
        out.println( String.format(header,file));
        long time = System.currentTimeMillis();
        try {
            /*Tool.hash(file)*/
            out.println(WitnessGraph.header(file, String.valueOf(time) ,"e2d5365a863c1c57fbe2870942676040efc3aea2d9bb085092800d6e256daf06", bits, "violation_witness"));
        }
        finally {

        }
    }
    public static void setEntry(PrintStream out)
    {
         out.println(WitnessGraph.entry("N0"));
    }
    public static void setNode(PrintStream out)
    {
        currentNode++;
        String Ni = "N" + currentNode;
        out.println(WitnessGraph.node(Ni));

    }
    public static void SetViolation(PrintStream out)
    {
        currentNode++;
        String Ni = "N" + currentNode;
        out.println(WitnessGraph.violation(Ni));

    }
    public static void setFooter(PrintStream out)
    {
        out.println(WitnessGraph.footer);
        out.println(footer);
    }


}

