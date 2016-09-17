/**
 * 
 */
package soottocfg.cfg.method;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.DOTExporter;
import org.jgrapht.ext.StringNameProvider;
import org.jgrapht.graph.AbstractBaseGraph;
import org.jgrapht.graph.ClassBasedEdgeFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

import soot.RefType;
import soot.SootMethod;
import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Node;
import soottocfg.cfg.Program;
import soottocfg.cfg.SourceLocation;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.statement.AssignStatement;
import soottocfg.cfg.type.Type;
import soottocfg.cfg.variable.Variable;
import soottocfg.soot.util.SootTranslationHelpers;
import soottocfg.util.SetOperations;

/**
 * @author schaef extends DefaultDirectedGraph<Statement, DefaultEdge>
 */
public class Method extends AbstractBaseGraph<CfgBlock, CfgEdge> implements Node, DirectedGraph<CfgBlock, CfgEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3367382274895641548L;

	private final transient SourceLocation location; // TODO remove the
														// transient
	private final String methodName;
	private final List<Type> returnTypes;
	private Variable thisVariable;
	private List<Variable> returnVariable;
	private final List<Variable> parameterList;
	private Set<Variable> locals;
	private CfgBlock source, sink;
	private boolean isProgramEntry = false;

	public static Method createMethodInProgram(Program p, String uniqueName, List<Variable> params, List<Type> outTypes,
			SourceLocation sourceLocation) {
		Preconditions.checkArgument(p.loopupMethod(uniqueName) == null,
				"Method with name " + uniqueName + " already exists");
		//add the exceptional return type to all methods that are generated.
		List<Type> returnTypes = new LinkedList<Type>();
		RefType reftype = RefType.v("java.lang.Throwable");
		Verify.verifyNotNull(reftype);
		returnTypes.add( SootTranslationHelpers.v().getMemoryModel().lookupType(reftype) );
		returnTypes.addAll(outTypes);
		Method m = new Method(sourceLocation, uniqueName, params, returnTypes);
		p.addMethod(m);
		return m;
	}

	private Method(SourceLocation loc, String uniqueName, List<Variable> params, List<Type> outTypes) {
		super(new ClassBasedEdgeFactory<CfgBlock, CfgEdge>(CfgEdge.class), true, true);
		location = loc;
		methodName = uniqueName;
		returnTypes = outTypes;
		locals = new HashSet<Variable>();
		this.parameterList = Collections.unmodifiableList(params);
	}

	public Method createMethodFromSubgraph(DirectedGraph<CfgBlock, CfgEdge> subgraph, String newMethodName) {
		Preconditions.checkArgument(vertexSet().containsAll(subgraph.vertexSet()),
				"Method does not contain all nodes from subgraph.");
		Method subgraphMethod = new Method(location, newMethodName, this.parameterList, this.returnTypes);

		for (CfgBlock v : subgraph.vertexSet()) {
			subgraphMethod.addVertex(v);
		}
		for (CfgEdge e : subgraph.edgeSet()) {
			subgraphMethod.addEdge(subgraph.getEdgeSource(e), subgraph.getEdgeTarget(e));
		}
		subgraphMethod.initialize(thisVariable, returnVariable, locals, source, isProgramEntry);
		return subgraphMethod;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public SourceLocation getLocation() {
		return location;
	}

	public boolean isConstructor() {
		return this.methodName.contains(SootMethod.constructorName);
	}

	public boolean isStaticInitializer() {
		return this.methodName.contains(SootMethod.staticInitializerName);
	}

	public void initialize(Variable thisVariable, List<Variable> returnVariables, Collection<Variable> locals,
			CfgBlock source, boolean isEntryPoint) {
		Preconditions.checkNotNull(parameterList, "Parameter list must not be null");
		Preconditions.checkNotNull(source);

		this.thisVariable = thisVariable;
		this.returnVariable = returnVariables;
		this.locals = new HashSet<Variable>(locals);
		this.source = source;
		this.isProgramEntry = isEntryPoint;

		SourceLocation loc = null; // TODO
		//return var 0 is the exceptional return variable.
		//first statement has to assign the exception local to null
		this.source.addStatement(0, new AssignStatement(loc, new IdentifierExpression(loc, this.returnVariable.get(0)),
				SootTranslationHelpers.v().getMemoryModel().mkNullConstant()  ));

		if (this.thisVariable != null) {
			// then the first parameter must be the reference to the current
			// instance
			// and we have to add an assignment to the source block.
			Verify.verify(!this.parameterList.isEmpty());
			Verify.verifyNotNull(this.source);
			AssignStatement thisAssign = new AssignStatement(loc, new IdentifierExpression(loc, this.thisVariable),
					new IdentifierExpression(loc, this.parameterList.get(0)));
			this.source.addStatement(0, thisAssign);
		}
	}

	/**
	 * Adds a guard expression as label to an edge. The label must not be null
	 * 
	 * @param edge
	 *            Existing edge in this Method.
	 * @param label
	 *            Non-null guard expression.
	 */
	public void setEdgeLabel(CfgEdge edge, Expression label) {
		edge.setLabel(label);
	}

	public boolean isProgramEntryPoint() {
		return this.isProgramEntry;
	}

	public void isProgramEntryPoint(boolean b) {
		this.isProgramEntry = b;
	}

	public CfgBlock getSource() {
		if (source == null) {
			for (CfgBlock b : vertexSet()) {
				if (inDegreeOf(b) == 0) {
					Verify.verify(source == null, "More than one source in graph!");
					source = b;
				}
			}
		}
		return source;
	}

	public CfgBlock getSink() {
		if (sink == null) {
			for (CfgBlock b : vertexSet()) {
				if (outDegreeOf(b) == 0) {
					Verify.verify(sink == null, "More than one source in graph!");
					sink = b;
				}
			}
		}
		return sink;
	}

	/**
	 * Checks if the graph has a unique sink vertex and returns it. If more than
	 * one such vertex exists, it collects all sink vertices and connects them
	 * to a new unique sink.
	 * 
	 * @return The unique sink vertex of the graph.
	 */
	public CfgBlock findOrCreateUniqueSink() {
		if (sink == null) {
			Set<CfgBlock> currentSinks = new HashSet<CfgBlock>();
			for (CfgBlock b : this.vertexSet()) {
				if (this.outDegreeOf(b) == 0) {
					currentSinks.add(b);
				}
			}
			if (currentSinks.isEmpty()) {
				sink = null;
			} else if (currentSinks.size() == 1) {
				sink = currentSinks.iterator().next();
			} else {
				CfgBlock newSink = new CfgBlock(this);
				for (CfgBlock b : currentSinks) {
					this.addEdge(b, newSink);
				}
				sink = newSink;
			}
		}
		return sink;
	}

	public Set<CfgBlock> getExitBlocks() {
		Set<CfgBlock> ret = new HashSet<CfgBlock>();
		for (CfgBlock b : this.vertexSet()) {
			if (this.outDegreeOf(b) == 0) {
				ret.add(b);
			}
		}
		return ret;
	}

	/**
	 * Returns the in parameter at position {@literal pos}. Throws
	 * an exception it {@literal pos} is not a legal index.
	 * 
	 * @param pos
	 * @return The parameter at position pos.
	 * @throws IndexOutOfBoundsException
	 */
	public Variable getInParam(int pos) {
		return this.parameterList.get(pos);
	}

	/**
	 * Returns immutable view of the list of parameters.
	 * 
	 * @return immutable view of the list of parameters.
	 */
	public List<Variable> getInParams() {
		return Collections.unmodifiableList(this.parameterList);
	}

	/**
	 * Returns an {@link Optional} Variable of the return variable of the
	 * current Method.
	 * 
	 * @return Optional return variable.
	 */
	public List<Variable> getOutParam() {
		if (this.returnVariable == null) {
			return new LinkedList<Variable>();
		}
		return new LinkedList<Variable>(this.returnVariable);
	}

	public void setOutParam(List<Variable> returnVariables) {
		this.returnVariable = returnVariables;
	}

	/**
	 * Returns an {@link Optional} return type of the method.
	 * I.e., either a type or None if the method returns void
	 * 
	 * @return Optional return type.
	 */
	public List<Type> getReturnType() {
		return this.returnTypes;
	}

	/**
	 * TODO:
	 * Add a local variable. This method should not be used. Its only
	 * use is in the SSA computation to create a non-deterministic assignment.
	 * Hopefully we find a better way todo this.
	 * 
	 * @param local
	 */
	public void addLocalVariable(Variable local) {
		locals.add(local);
	}

	/**
	 * Returns an immutable view of the set of local variables.
	 * 
	 * @return immutable view of the set of local variables.
	 */
	public Collection<Variable> getLocals() {
		return Collections.unmodifiableSet(locals);
	}

	public void toDot(File dotFile) {
		try (FileOutputStream fileStream = new FileOutputStream(dotFile);
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");) {
			DOTExporter<CfgBlock, CfgEdge> dot = new DOTExporter<CfgBlock, CfgEdge>(new StringNameProvider<CfgBlock>(),
					null, null);
			dot.export(writer, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void toSimpleDot(File dotFile) {
		try (FileOutputStream fileStream = new FileOutputStream(dotFile);
				OutputStreamWriter writer = new OutputStreamWriter(fileStream, "UTF-8");) {
			DOTExporter<CfgBlock, CfgEdge> dot = new DOTExporter<CfgBlock, CfgEdge>(new StringNameProvider<CfgBlock>() {
				@Override
				public String getVertexName(CfgBlock vertex) {
					StringBuilder sb = new StringBuilder();
					sb.append("\"");
					sb.append(vertex.getLabel());
					sb.append("\"");
					return sb.toString();
				}
			}, null, null);
			dot.export(writer, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Method ");
		sb.append(this.methodName);
		String comma = "";
		sb.append("(");
		for (Variable v : this.parameterList) {
			sb.append(comma);
			sb.append(v.getName());
			comma = ", ";
		}
		sb.append(")\n");
		if (this.returnTypes.size() > 0) {
			sb.append("  Return types: ");
			comma = "";
			for (Type t : this.returnTypes) {
				sb.append(comma);
				sb.append(t.toString());
				comma = ", ";
			}
			sb.append("\n");
		}
		if (this.returnVariable != null) {
			sb.append("  Returns: ");
			comma = "";
			for (Variable v : this.returnVariable) {
				sb.append(comma);
				sb.append(v.toString());
				comma = ", ";
			}
			sb.append("\n");
			sb.append("\n");
		}
		comma = "";
		if (this.locals != null && !this.locals.isEmpty()) {
			sb.append("  locals:\n");
			for (Variable v : this.locals) {
				sb.append("    ");
				sb.append(v.getName());
				sb.append(":    ");
				sb.append(v.getType());
				sb.append("\n");
			}
		}

		for (CfgBlock b : this.vertexSet()) {
			if (this.source == b) {
				sb.append("Root ->");
			}
			sb.append(b);
		}

		return sb.toString();
	}

	@Override
	public Set<Variable> getUseVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (CfgBlock b : this.vertexSet()) {
			used.addAll(b.getUseVariables());
		}
		return used;
	}

	@Override
	public Set<Variable> getDefVariables() {
		Set<Variable> rval = new HashSet<Variable>();
		for (CfgBlock b : this.vertexSet()) {
			rval.addAll(b.getDefVariables());
		}
		return rval;
	}

	/**
	 * Return the set of live variable at the entry of each block. A variable is
	 * live between its first and last use. Following the algorithm on p610 of
	 * the dragon book, 2nd ed.
	 * 
	 * @return
	 */
	public LiveVars<CfgBlock> computeBlockLiveVariables() {
		Set<CfgBlock> cfg = this.vertexSet();

		// Reserve the necessary size in the hashmap
		Map<CfgBlock, Set<Variable>> in = new HashMap<CfgBlock, Set<Variable>>(cfg.size());
		Map<CfgBlock, Set<Variable>> out = new HashMap<CfgBlock, Set<Variable>>(cfg.size());

		// cache these to save time
		Map<CfgBlock, Set<Variable>> use = new HashMap<CfgBlock, Set<Variable>>(cfg.size());
		Map<CfgBlock, Set<Variable>> def = new HashMap<CfgBlock, Set<Variable>>(cfg.size());

		// Start by initializing in to empty. The book does this separately for
		// exit and non exit blocks, but that's not necessary
		// TODO can exit blocks have variables? E.g. can they return values? In
		// which case we should actually recurse over all blocks!
		for (CfgBlock b : cfg) {
			in.put(b, new HashSet<Variable>());
			use.put(b, b.getUseVariables());
			def.put(b, b.getDefVariables());
		}

		boolean changed = false;

		do {
			changed = false;
			for (CfgBlock b : cfg) {
				out.put(b, b.computeLiveOut(in));
				Set<Variable> newIn = SetOperations.union(use.get(b), SetOperations.minus(out.get(b), def.get(b)));

				if (!newIn.equals(in.get(b))) {
					changed = true;
					in.put(b, newIn);
				}
			}
		} while (changed);

		return new LiveVars<CfgBlock>(in, out);
	}
}
