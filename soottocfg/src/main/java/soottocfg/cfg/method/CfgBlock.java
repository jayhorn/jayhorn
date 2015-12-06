/**
 * 
 */
package soottocfg.cfg.method;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Node;
import soottocfg.cfg.Variable;
import soottocfg.cfg.statement.Statement;
import soottocfg.util.SetOperations;

/**
 * @author schaef
 *
 */
public class CfgBlock implements Node, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8807957025110526199L;

	protected final String label;
	protected List<Statement> statements;

	protected final Method method;

	public CfgBlock(Method m) {
		this.label = "Block" + m.vertexSet().size();
		this.statements = new LinkedList<Statement>();
		this.method = m;
		this.method.addVertex(this);
	}

	public Method getMethod() {
		return method;
	}

	public String getLabel() {
		return this.label;
	}

	public void addStatement(Statement s) {
		this.statements.add(s);
	}

	public List<Statement> getStatements() {
		return this.statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	@Override
	public String toString() {
		Preconditions.checkArgument(this.method.containsVertex(this),
				String.format(
						"Block %s has never been added to the method. This should have happened in the constructor fo CfgBlock.",
						this.label));
		StringBuilder sb = new StringBuilder();
		sb.append(this.label);
		sb.append(":\n");
		for (Statement s : this.statements) {
			sb.append("(ln ");
			sb.append(s.getJavaSourceLine());
			sb.append(")\t");
			sb.append(s.toString());
			sb.append("\n");
		}
		if (this.method.outDegreeOf(this) != 0) {
			sb.append("\tgoto:\n");
			for (CfgEdge edge : this.method.outgoingEdgesOf(this)) {
				sb.append("\t  ");
				if (edge.getLabel().isPresent()) {
					sb.append("if ");
					sb.append(edge.getLabel().get());
					sb.append(": ");
				}
				sb.append(method.getEdgeTarget(edge).getLabel());
				sb.append("\n");
			}
		} else {
			sb.append("\treturn\n");
		}
		return sb.toString();
	}

	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (Statement s : statements) {
			used.addAll(s.getUsedVariables());
		}
		// TODO: do the variables in the conditional belong to this block?
		for (CfgEdge edge : this.method.outgoingEdgesOf(this)) {
			if (edge.getLabel().isPresent()) {
				used.addAll(edge.getLabel().get().getUsedVariables());
			}
		}
		return used;
	}

	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (Statement s : statements) {
			used.addAll(s.getLVariables());
		}
		return used;
	}

	public boolean isExit() {
		return this.method.outDegreeOf(this) == 0;
	}

	// Calculates the live-in variables for each statement
	public LiveVars<Statement> computeLiveVariables(LiveVars<CfgBlock> vars) {

		// Reserve the necessary size in the hashmap
		Map<Statement, Set<Variable>> in = new HashMap<Statement, Set<Variable>>(getStatements().size());
		Map<Statement, Set<Variable>> out = new HashMap<Statement, Set<Variable>>(getStatements().size());

		// Start by initializing in to empty.
		for (Statement s : getStatements()) {
			in.put(s, new HashSet<Variable>());
		}

		// Start with the variables that are live out of the block are also live
		// out of the last statement
		Set<Variable> currentLiveOut = vars.liveOut.get(this);

		// Go through the statements in reverse order
		for (ListIterator<Statement> li = getStatements().listIterator(getStatements().size()); li.hasPrevious();) {
			Statement stmt = li.previous();
			out.put(stmt, currentLiveOut);
			Set<Variable> liveIn = SetOperations.union(stmt.getUsedVariables(),
					SetOperations.minus(currentLiveOut, stmt.getLVariables()));
			in.put(stmt, liveIn);
			currentLiveOut = liveIn;
		}

		// The live in of the 0th statement should be the same as the live in of
		// the whole block
		assert(currentLiveOut.equals(vars.liveIn.get(this)));
		return new LiveVars<Statement>(in, out);
	}

	public Set<Variable> computeLiveOut(Map<CfgBlock, Set<Variable>> in) {
		Set<Variable> out = new HashSet<Variable>();

		// Exit blocks have all globals and all out params live at exit
		if (isExit()) {
			out.addAll(getMethod().getModifiedGlobals());
			out.addAll(getMethod().getOutParams());
		} else {
			for (CfgEdge edge : this.method.outgoingEdgesOf(this)) {
				out.addAll(in.get(method.getEdgeTarget(edge)));
			}
		}
		return out;
	}

}
