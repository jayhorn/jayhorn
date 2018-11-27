/**
 * 
 */
package soottocfg.cfg.method;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graphs;

import com.google.common.base.Preconditions;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Node;
import soottocfg.cfg.statement.Statement;
import soottocfg.cfg.variable.Variable;
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

	/**
	 * Try not to use this one, only if you clone blocks.
	 * @param m
	 * @param label
	 */
	public CfgBlock(Method m, String label) {
		this.label = label;
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

	/**
	 * Adds a {@link Statement} to the end of the block.
	 * @param s Statement to be added.
	 */
	public void addStatement(Statement s) {
		this.statements.add(s);
	}

	/**
	 * Adds a {@link Statement} at a given {@code position} to the block.
	 * Does not check if {@code position} is a valid position within the
	 * block.
	 * @param s
	 * @param position
	 * @exception IndexOutOfBoundsException If position is not a valid index in the body.
	 */
	public void addStatement(int position, Statement s) {
		Preconditions.checkNotNull(s);
		this.statements.add(position, s);
	}
	
	/**
	 * Get the {@link Statement} list of this block.
	 * @return The {@link Statement} list
	 */
	public List<Statement> getStatements() {
		return this.statements;
	}
	
	public void removeStatement(Statement toRemove) {
		this.statements.remove(toRemove);
	}

	public void removeStatement(int index) {
		this.statements.remove(index);
	}
	
	public void removeStatements(Collection<Statement> toRemove) {
		this.statements.removeAll(toRemove);
	}
	
	public void swapStatements(int i, int j) {
		Collections.swap(this.statements, i, j);
	}

	/**
	 * Replaces the statements inside the block by the statements in 
	 * {@param statements}.
	 * @param statements Collection of {@link Statement}s to be added to the block.
	 */
	public void setStatements(Collection<Statement> statements) {
		this.statements = new LinkedList<Statement>(statements);
	}

	@Override
	public String toString() {
		Preconditions.checkArgument(this.method.containsVertex(this),
				String.format(
						"Block %s in not %s.",
						this.label, this.method.getMethodName()));
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
	public Set<Variable> getUseVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (Statement s : statements) {
			used.addAll(s.getUseVariables());
		}
		// The variables in the conditional belong to this block.
		//Think of it as a if (cond) goto L1 else goto L2;
		//as the last stmt in the block.
		for (CfgEdge edge : this.method.outgoingEdgesOf(this)) {
			if (edge.getLabel().isPresent()) {
				used.addAll(edge.getLabel().get().getUseVariables());
			}
		}

		//TODO: Martin also added the incoming edges.
		for (CfgEdge edge : this.method.incomingEdgesOf(this)) {
			if (edge.getLabel().isPresent()) {
				used.addAll(edge.getLabel().get().getUseVariables());
			}
		}

		
		return used;
	}

	@Override
	public Set<Variable> getDefVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (Statement s : statements) {
			used.addAll(s.getDefVariables());
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
			Set<Variable> liveIn = SetOperations.union(stmt.getUseVariables(),
					SetOperations.minus(currentLiveOut, stmt.getDefVariables()));
			in.put(stmt, liveIn);
			currentLiveOut = liveIn;
		}

		// The live in of the 0th statement should be the same as the live in of
		// the whole block
//		assert (currentLiveOut.equals(vars.liveIn.get(this)));
		//TODO: Martin says this assertion is not true anymore because the conditional
		//also has to be live. Because in reality, there is an assume from the incoming
		//edge at the beginning of the block. Otherwise the dead code elimination would
		//eliminate the statement that assigns stuff to the conditional.
		return new LiveVars<Statement>(in, out);
	}

	public Set<Variable> computeLiveOut(Map<CfgBlock, Set<Variable>> in) {
		Set<Variable> out = new HashSet<Variable>();

		// Exit blocks have all globals and all out params live at exit
		if (isExit()) {
			//TODO
//			out.addAll(getMethod().getModifiedGlobals());
			if (!getMethod().getOutParams().isEmpty()) {
				out.addAll(getMethod().getOutParams());
			}
		} else {
			for (CfgBlock suc : Graphs.successorListOf(method, this)) {
				out.addAll(in.get(suc));
			}
			//TODO: is this correct? Add all variable used in the
			//edges to live out.
			for (CfgEdge edge : method.outgoingEdgesOf(this)) {
				if (edge.getLabel().isPresent()) {
					out.addAll(edge.getLabel().get().getUseVariables());
				}
			}
		}
		return out;
	}

	/**
	 * Returns a new block with a deep copy of the statements of this
	 * block.
	 * The new block has a different label from this block.
	 * @return A deep copy of this block where all statements have been
	 *         copied as well.
	 */
	public CfgBlock deepCopy() {
		CfgBlock copy = new CfgBlock(method);
		List<Statement> stmtCopy = new LinkedList<Statement>(); 
		for (Statement s : statements) {
			stmtCopy.add(s.deepCopy());
		}
		copy.setStatements(stmtCopy);
		return copy;
	}
} 
