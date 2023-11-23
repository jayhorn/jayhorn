package jayhorn.solver.princess;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.encoder.S2H;
import jayhorn.solver.*;
import jayhorn.witness.NodeData;
import jayhorn.witness.Witness;
import lazabs.horn.bottomup.Util;
import scala.Tuple2;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.statement.HavocStatement;
import soottocfg.cfg.statement.Statement;
import jayhorn.Options;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CexPrinter {


    public Stack<ProverExpr[]> argsVal = new Stack<ProverExpr[]>();
    public Stack<Map.Entry<Statement, List<ProverHornClause>>> havocStatementEntries = new Stack<Map.Entry<Statement, List<ProverHornClause>>>();

    public String cexTraceToString(List<Statement> trace) {

        ///soottocfg.cfg.ast2cfg.Cfg2AstPrinter.printProgramToString()

        //final List<Statement> trace = getProverCexTrace(cex);
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Counterexample Trace:\n");
        for (int i = trace.size() - 1; i >= 0; --i) {
            Statement stmt = trace.get(i);
            final SourceLocation loc = stmt.getSourceLocation();
            stringBuilder.append(String.format("%20s:%-5d %s%n",
                                               loc.getSourceFileName(),
                                               loc.getLineNumber(),
                                               stmt.toString()));
        }

        return stringBuilder.toString();
    }
    public List<Statement> getProverCexTrace(Util.Dag<Tuple2<ProverFun, ProverExpr[]>> cex,HornEncoderContext hornContext)
    {



        final List<Statement> trace = new ArrayList<>();

        Statement lastStatement = null;
        scala.collection.Iterator<Tuple2<ProverFun, ProverExpr[]>> iter =
                cex.iterator();
        while (iter.hasNext()) {
            final Tuple2<ProverFun, ProverExpr[]> tuple = iter.next();

            if (tuple != null && tuple._1() != null) {
                final ProverFun proverFun = tuple._1();
                Map.Entry<Statement, List<ProverHornClause>> entry =  findStatement(proverFun);
                Statement stmt = null;
                if(entry != null)  stmt = entry.getKey();

                // for helper statements, we might not have a location.
                if (stmt != null && stmt.getSourceLocation() != null &&
                        stmt.getSourceLocation().getSourceFileName() != null
                        && stmt.getSourceLocation().getLineNumber() > 0) {
                    /*                    if (lastStatement != null &&
                        lastStatement.getSourceLocation() != null &&
                        stmt.getSourceLocation()
                            .getSourceFileName()
                            .equals(lastStatement.getSourceLocation().getSourceFileName()) &&
                        stmt.getSourceLocation().getLineNumber() ==
                        lastStatement.getSourceLocation().getLineNumber()) */

                    if (lastStatement == stmt) {
                        // do not add the same statement several times in a row.
                                //PushNonDefunctInvocation(tuple._2(), entry);
                    } else {
                        trace.add(stmt);
                        if(Options.v().violationWitness != null && !Options.v().violationWitness.isEmpty())
                            if(stmt.getClass().getTypeName().contains("Havoc"))
                            {
                                havocStatementEntries.push(entry);
                                argsVal.push(tuple._2());
                            }
                    }
                    lastStatement = stmt;
                }
            }
        }
        return trace;

    }
   // private   List<Map.Entry<Statement, List<ProverHornClause>>> getEntries
  /*  private void GenerateWitnessViolation()
    {
        try {
            if(Witness.getCurrentNode() != 0) return;
            Witness.resetCurrentNode();
            PrintStream witnessViolFile = new PrintStream("Witness.GraphML");
            Witness.setHeader("",witnessViolFile);
            Witness.setEntry(witnessViolFile);
            while (!NonDetFuncInvocations.empty()) {

                NodeData nd = NonDetFuncInvocations.pop();
                Witness.setTransition(nd.funcName, nd.SourceFileName, nd.SourceLineNumber, nd.Scope,nd.FuncResult,witnessViolFile);
            }
            Witness.setTransitionToViolation(witnessViolFile);
            Witness.setFooter(witnessViolFile);
            witnessViolFile.close();
        }
        catch (Exception e)
        {

        }
    }
    Stack<NodeData> NonDetFuncInvocations = new Stack<NodeData>();
    private void PushNonDefunctInvocation(ProverExpr [] ArgsValue, Map.Entry<Statement, List<ProverHornClause>> entry)
    {
        try {
            String nonDetCallReturnVal = findNonDetValue(ArgsValue, entry);
            Statement stmt = entry.getKey();
            int nonDetCallLineNum = stmt.getJavaSourceLine();
            String exampleRoot = Options.v().getJavaInput().substring(0,Options.v().getJavaInput().indexOf('/'));
            Path path = Paths.get(String.format( exampleRoot+"/src/%s",stmt.getSourceLocation().getSourceFileName()));
            List<String> lines = Files.readAllLines(path);
            String nonDetCallWholeLine = getLines(lines,nonDetCallLineNum-1,nonDetCallLineNum-1).stream().findFirst().orElse(null);

            int nonDetCallIndx = nonDetCallWholeLine.indexOf("Verifier.nondet");
            String nonDetFuncName=nonDetCallWholeLine.substring(nonDetCallIndx, nonDetCallWholeLine.indexOf('(',nonDetCallIndx));
            NodeData nd = new NodeData();
            nd.funcName = nonDetFuncName;
            nd.SourceFileName = stmt.getSourceLocation().getSourceFileName();
            nd.SourceLineNumber = String.valueOf(nonDetCallLineNum);
            nd.FuncResult = nonDetCallReturnVal;
            nd.Scope = ((HavocStatement)stmt).getHavocScope();
            NonDetFuncInvocations.push(nd);
        }
        catch (IOException xIo) {
            xIo.printStackTrace();
        }
    }
    private static List<String> getLines(List<String> lines, int first, int last) {
        List<String> list = new ArrayList<>();
        if (lines != null  &&  first >= 0  &&  last < lines.size()  &&  first <= last) {
            for (int i = first; i <= last; i++) {
                list.add(lines.get(i));
            }
        }
        return list;
    }
    private String findNonDetValue(ProverExpr [] ArgsValue, Map.Entry<Statement, List<ProverHornClause>> entry)
    {
            ProverExpr [] entryHeadArgs = entry.getValue().get(0).getHeadArgs();
            int havocArgIndx = FindHavocArgIndex(entryHeadArgs);
            String havocArgValue = FindHavocArgValue(havocArgIndx, ArgsValue);
            return havocArgValue;
    }
    private int FindHavocArgIndex(ProverExpr [] Args)
    {
        ProverExpr havocExpr = Arrays.stream(Args).filter(x -> x.toString().contains("_havoc")).findFirst().orElse(null);
        List<ProverExpr> headArgsList = Arrays.asList(Args);
        return headArgsList.indexOf(havocExpr);
    }
    private String FindHavocArgValue(int havocArgIndex, ProverExpr [] Args )
    {
        int TotalIndx = -1;
        for (ProverExpr pe: Args) {

            if(pe.getType() instanceof ProverTupleType)
                TotalIndx += ((ProverTupleExpr)pe).getArity();
            else {
                TotalIndx++;
                if(TotalIndx == havocArgIndex)
                    return pe.toString();
            }
        }
        return "";
    }*/
    private Map.Entry<Statement, List<ProverHornClause>> findStatement(final ProverFun proverFun) {
        for (Map.Entry<Statement, List<ProverHornClause>> entry : S2H.sh().getStatToClause().entrySet()) {
            for (ProverHornClause hc : entry.getValue()) {
                if (hc.getHeadFun() != null && hc.getHeadFun().equals(proverFun)) {
                    return entry;//entry.getKey();
                }
            }
        }
        return null;

    }
}
