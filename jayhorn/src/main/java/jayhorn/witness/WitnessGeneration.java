package jayhorn.witness;

import jayhorn.Options;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverHornClause;
import jayhorn.solver.ProverTupleExpr;
import jayhorn.solver.ProverTupleType;
import soottocfg.cfg.statement.HavocStatement;
import soottocfg.cfg.statement.Statement;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.File;

public  class WitnessGeneration {

    public static void generateWitnessViolation(Stack<ProverExpr[]> argsVal, Stack<Map.Entry<Statement, List<ProverHornClause>>> havocStatementEntries)
    {


        try {
            if(Witness.getCurrentNode() != 0) return;
            Witness.resetCurrentNode();
//new File(ClassLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath())+
            //File file = new File(WitnessGeneration.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
            String pathToWitnessFile = Options.v().violationWitness+"/witness.GraphML";
            PrintStream witnessViolFile = new PrintStream( pathToWitnessFile,"UTF-8");
            Witness.setHeader("",witnessViolFile);
            Witness.setEntry(witnessViolFile);
            while (!havocStatementEntries.empty())
            {
                ProverExpr[] argVal = argsVal.pop();
                Map.Entry<Statement, List<ProverHornClause> > havocStatementEntry = havocStatementEntries.pop();
                NodeData nd =  getNonDefunctInvocationInfo(argVal, havocStatementEntry);

                Witness.setTransition(nd.funcName, nd.SourceFileName, nd.SourceLineNumber, nd.Scope,nd.FuncResult,witnessViolFile);

            }
            Witness.setTransitionToViolation(witnessViolFile);
            Witness.setFooter(witnessViolFile);
            witnessViolFile.close();
        }
        catch (RuntimeException e)
        {
            throw e;
        }
        catch (Exception e) {
        }

    }

    private static NodeData getNonDefunctInvocationInfo(ProverExpr[] ArgsValue, Map.Entry<Statement, List<ProverHornClause>> entry)
    {
        NodeData nd = new NodeData();
        try {
            String nonDetCallReturnVal = findNonDetValue(ArgsValue, entry);
            Statement stmt = entry.getKey();
            int nonDetCallLineNum = stmt.getJavaSourceLine();
            String benchmarkRoot = Options.v().getJavaSrcInput();//Options.v().getJavaInput().substring(0,Options.v().getJavaInput().indexOf('/'));

            Path path = Paths.get(String.format( "%s/%s",benchmarkRoot,stmt.getSourceLocation().getSourceFileName()));
            List<String> lines = Files.readAllLines(path);
            String nonDetCallWholeLine = getLines(lines,nonDetCallLineNum-1,nonDetCallLineNum-1).stream().findFirst().orElse(null);

            int nonDetCallIndx = nonDetCallWholeLine.indexOf("Verifier.nondet");
            String nonDetFuncName=nonDetCallWholeLine.substring(nonDetCallIndx, nonDetCallWholeLine.indexOf('(',nonDetCallIndx));

            nd.funcName = nonDetFuncName;
            nd.SourceFileName = stmt.getSourceLocation().getSourceFileName();
            nd.SourceLineNumber = String.valueOf(nonDetCallLineNum);
            nd.FuncResult = nonDetCallReturnVal;
            nd.Scope = ((HavocStatement)stmt).getHavocScope();
        }
        catch (IOException xIo) {
            xIo.printStackTrace();
        }
        return nd;
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
    private static String findNonDetValue(ProverExpr [] ArgsValue, Map.Entry<Statement, List<ProverHornClause>> entry)
    {
        ProverExpr [] entryHeadArgs = entry.getValue().get(0).getHeadArgs();
        int havocArgIndx = FindHavocArgIndex(entryHeadArgs);
        String havocArgValue = FindHavocArgValue(havocArgIndx, ArgsValue);
        return havocArgValue;
    }
    private static int FindHavocArgIndex(ProverExpr [] Args)
    {
        ProverExpr havocExpr = Arrays.stream(Args).filter(x -> x.toString().contains("_havoc")).findFirst().orElse(null);
        List<ProverExpr> headArgsList = Arrays.asList(Args);
        return headArgsList.indexOf(havocExpr);
    }
    private static String FindHavocArgValue(int havocArgIndex, ProverExpr [] Args )
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
    }

}
