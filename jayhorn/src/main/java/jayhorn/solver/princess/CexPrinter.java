package jayhorn.solver.princess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.encoder.S2H;
import jayhorn.solver.ProverExpr;
import jayhorn.solver.ProverFun;
import jayhorn.solver.ProverHornClause;
import lazabs.horn.bottomup.Util;
import scala.Tuple2;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.statement.Statement;

public class CexPrinter {

    public String proverCexToCext(Util.Dag<Tuple2<ProverFun, ProverExpr[]>> cex,
                                  HornEncoderContext hornContext) {
        final List<Statement> trace = new ArrayList<>();
        Statement lastStatement = null;
        scala.collection.Iterator<Tuple2<ProverFun, ProverExpr[]>> iter =
            cex.iterator();
        while (iter.hasNext()) {
            final Tuple2<ProverFun, ProverExpr[]> tuple = iter.next();

            if (tuple != null && tuple._1() != null) {
                final ProverFun proverFun = tuple._1();
                final Statement stmt = findStatement(proverFun);
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
                    } else {
                        trace.add(stmt);
                    }
                    lastStatement = stmt;
                }
            }
        }

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

    private Statement findStatement(final ProverFun proverFun) {
        for (Map.Entry<Statement, List<ProverHornClause>> entry : S2H.sh().getStatToClause().entrySet()) {
            for (ProverHornClause hc : entry.getValue()) {
                if (hc.getHeadFun() != null && hc.getHeadFun().equals(proverFun)) {
                    return entry.getKey();
                }
            }
        }
        return null;
    }
}
