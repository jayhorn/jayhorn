package soottocfg.soot.memory_model;

import java.util.Set;

import soottocfg.cfg.Program;
import soottocfg.cfg.method.CfgBlock;
import soottocfg.cfg.method.Method;
import soottocfg.cfg.statement.PullStatement;
import soottocfg.cfg.statement.PushStatement;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.util.InterProceduralPullPushOrdering;

/**
 * @author Rody Kersten
 *
 */
public class PushIdentifierAdder {

	private static boolean debug = false;

	public void addIDs(Program p) {
		InterProceduralPullPushOrdering ordering = new InterProceduralPullPushOrdering(p.getEntryPoint());
		Method[] ms = p.getMethods();
		for (Method m : ms) {
			
			if (debug) {
				System.out.println("Adding IDs to method " + m.getMethodName());
				// System.out.println(m);
			}

			Set<CfgBlock> blocks = m.vertexSet();
			for (CfgBlock b : blocks) {

				for (Statement s : b.getStatements()) {
					if (s instanceof PullStatement) {
						PullStatement pull = (PullStatement) s;
						Set<PushStatement> pushes = ordering.getPushsInfluencing(pull);
						pull.canAffect(pushes);
						if (debug) {
							System.out.println("Pushes influencing " + pull + ": ");
							for (PushStatement push : pushes)
								System.out.println(push);
						}
					}
				}
			}

			if (debug)
				System.out.println("DONE:\n" + m);
		}
	}
}
