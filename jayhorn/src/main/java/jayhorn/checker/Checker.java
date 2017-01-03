package jayhorn.checker;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import jayhorn.hornify.HornEncoderContext;
import jayhorn.hornify.HornHelper;
import jayhorn.hornify.HornPredicate;
import jayhorn.hornify.encoder.S2H;
import jayhorn.solver.Prover;
import soottocfg.cfg.Program;
import soottocfg.cfg.variable.ClassVariable;
import soottocfg.cfg.variable.Variable;


/**
 * @author teme
 */


public abstract class Checker {
	
	public Checker() {
		S2H.resetInstance();
		HornHelper.resetInstance();
	}
	
	public abstract boolean checkProgram(Program program);
	
	protected void printHeapInvariants(Prover prover, HornEncoderContext hornContext) {
		if (prover.getLastSolution()!=null) {
			StringBuilder sb = new StringBuilder();
			sb.append("No assertion can fail using the following heap invarians:\n");
			
			Map<ClassVariable, TreeMap<Long,String>> heapInvariants = new LinkedHashMap<ClassVariable, TreeMap<Long,String>>();
			
			for (Entry<String, String> entry : prover.getLastSolution().entrySet()) {
				boolean found = false;
				for (Entry<ClassVariable, Map<Long, HornPredicate>> pentry : hornContext.getInvariantPredicates().entrySet()) {
					
					for (Entry<Long, HornPredicate> predEntry : pentry.getValue().entrySet()) {
						HornPredicate hp = predEntry.getValue(); 
						if (hp.predicate.toString().contains(entry.getKey())) {
							//we found one.
							if (!heapInvariants.containsKey(pentry.getKey())) {
								heapInvariants.put(pentry.getKey(), new TreeMap<Long,String>());
							}
							String readable = entry.getValue();
							for (int i = 0; i<hp.variables.size(); i++) {
								readable = readable.replace("_"+i, hp.variables.get(i).getName());
							}
							heapInvariants.get(pentry.getKey()).put(predEntry.getKey(), readable);
//							
//							sb.append(pentry.getKey().getName());
//							sb.append(":\n\t");
//							int i=0;
//							String readable = entry.getValue();
//							for (Variable v : hp.variables) {
//								readable = readable.replace("_"+(i++), v.getName());
//							}
//							sb.append(readable);
//							sb.append("\n");
//							found = true;
//							break;
						}
					}
					if (found) {
						break;
					}
				}
			}
			for (Entry<ClassVariable, TreeMap<Long,String>> entry : heapInvariants.entrySet()) {
				sb.append(entry.getKey());
				sb.append("\n  ");
				for (Variable v : entry.getKey().getAssociatedFields()) {
					sb.append(", ");
					sb.append(v.getName());
				}
				sb.append(":\n");
				for (Entry<Long,String> e2 : entry.getValue().entrySet()) {
					sb.append("\t");
					sb.append(e2.getKey());
					sb.append(":  ");
					sb.append(e2.getValue());
					sb.append("\n");
				}
				sb.append("--\n");
			}
			sb.append("----\n");
			System.err.println(sb.toString());			
		}
	}
}



