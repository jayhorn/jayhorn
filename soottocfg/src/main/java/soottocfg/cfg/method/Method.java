/**
 * 
 */
package soottocfg.cfg.method;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soottocfg.cfg.LiveVars;
import soottocfg.cfg.Node;
import soottocfg.cfg.Variable;
import soottocfg.cfg.statement.Statement;
import soottocfg.util.SetOperations;

/**
 * @author schaef
 *
 */
public class Method implements Node {

	private final String methodName;
	private Variable thisVariable, returnVariable, exceptionalReturnVariable;
	private List<Variable> parameterList;
	private Collection<Variable> locals;
	private Collection<Variable> modifiedGlobals;
	private CfgBlock source;
	private boolean isEntry = false;

	public Method(String uniqueName) {
		methodName = uniqueName;
	}

	public String getMethodName() {
		return this.methodName;
	}

	public void initialize(Variable thisVariable, Variable returnVariable, Variable exceptionalReturnVariable,
			List<Variable> parameterList, Collection<Variable> locals, CfgBlock source, boolean isEntryPoint) {
		this.thisVariable = thisVariable;
		this.returnVariable = returnVariable;
		this.exceptionalReturnVariable = exceptionalReturnVariable;
		this.parameterList = parameterList;
		this.locals = locals;
		this.source = source;
		this.isEntry = isEntryPoint;

		// compute the modifies clause.
		this.modifiedGlobals = new HashSet<Variable>();
		this.modifiedGlobals.addAll(this.getLVariables());
		this.modifiedGlobals.removeAll(locals);
		this.modifiedGlobals.removeAll(parameterList);
		this.modifiedGlobals.remove(exceptionalReturnVariable);
		this.modifiedGlobals.remove(returnVariable);
		this.modifiedGlobals.remove(thisVariable);
	}

	public boolean isEntryPoint() {
		return this.isEntry;
	}

	public CfgBlock getSource() {
		return this.source;
	}

	public Set<CfgBlock> getCfg(){
		class Visitor {
			private Set<CfgBlock> set;
			Visitor(){set = new  HashSet<CfgBlock>();}
			public Set<CfgBlock> visit(CfgBlock block){
				if (!set.contains(block)) {
					set.add(block);
					for(CfgBlock b : block.getSuccessors()){
						visit(b);
					}
				}
				return set;
			}
		}
		return (new Visitor()).visit(this.getSource());
	}

	public Set<CfgBlock> getExitBlocks(){
		Set<CfgBlock> rval = new HashSet<CfgBlock>();
		for(CfgBlock b : getCfg()){
			if(b.isExit()){
				rval.add(b);
			}
		}
		return rval;
	}

	public Collection<Variable> getInParams() {
		final List<Variable> rtr = new ArrayList<Variable>();
		if (thisVariable != null) {
			rtr.add(thisVariable);
		}
		rtr.addAll(parameterList);
		return rtr;
	}

	public Collection<Variable> getOutParams() {
		final List<Variable> rtr = new ArrayList<Variable>();
		if (returnVariable != null) {
			rtr.add(returnVariable);
		}
		if (exceptionalReturnVariable != null) {
			rtr.add(exceptionalReturnVariable);
		}
		return rtr;
	}

	public Collection<Variable> getModifiedGlobals() {
		return modifiedGlobals;
	}

	public Collection<Variable> getLocals() {
		return locals;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Method ");
		sb.append(this.methodName);
		String comma = "";
		sb.append("(");
		if (this.thisVariable != null) {
			sb.append(this.thisVariable.getName());
			comma = ", ";
		}
		for (Variable v : this.parameterList) {
			sb.append(comma);
			sb.append(v.getName());
			comma = ", ";
		}
		sb.append(")\n");
		if (this.returnVariable != null) {
			sb.append("\treturns: ");
			sb.append(this.returnVariable.getName());
			sb.append("\n");
		}
		if (this.modifiedGlobals != null && this.modifiedGlobals.size() > 0) {
			sb.append("\tmodifies: ");
			for (Variable v : this.modifiedGlobals) {
				sb.append(comma);
				sb.append(v.getName());
				comma = ", ";
			}
			sb.append("\n");
		}
		comma = "";
		sb.append("\tthrows: ");
		sb.append(this.exceptionalReturnVariable.getName());
		sb.append("\n");
		if (!this.locals.isEmpty()) {
			sb.append("\tlocals:\n");
			for (Variable v : this.locals) {
				sb.append("\t\t");
				sb.append(v.getName());
				sb.append("\n");
			}
		}

		for(CfgBlock b : getCfg()){
			if (this.source == b) {
				sb.append("Root ->");
			}
			sb.append(b);
		}

		return sb.toString();
	}

	@Override
	public Set<Variable> getUsedVariables() {
		Set<Variable> used = new HashSet<Variable>();
		for (CfgBlock b : getCfg()){
			used.addAll(b.getUsedVariables());
		}
		return used;
	}

	@Override
	public Set<Variable> getLVariables() {
		Set<Variable> rval = new HashSet<Variable>();
		for (CfgBlock b : getCfg()){
			rval.addAll(b.getLVariables());
		}
		return rval;
	}

	//Implemented using the algorithm in Aho 2nd ed, p 658.
	public Map<CfgBlock,Set<CfgBlock>> computeDominators() {
		Set<CfgBlock> cfg = getCfg();
		Map<CfgBlock,Set<CfgBlock>> dominators = new HashMap<CfgBlock,Set<CfgBlock>>(cfg.size());
		
		//Initialize the set
		for(CfgBlock b : cfg){
			if(b == getSource()){
				//The Source node only dominates itself
				Set<CfgBlock> tmp = new HashSet<CfgBlock>();
				tmp.add(b);
				dominators.put(b,tmp);
			} else {
				//All other nodes are initilized to be the full cfg.  They will shrink later
				Set<CfgBlock> tmp = new HashSet<CfgBlock>(cfg);
				dominators.put(b,tmp);
			}
		}
		
		boolean changed;
		do {
			changed = false;
			for(CfgBlock b : cfg){
				//only nodes with predecessors can change
				if(b != getSource() && b.getPredecessors().size()!=0){
					//This is a bit ugly way to handle the initialization of the intersection problem
					//but it should work
					Set<CfgBlock> newDom = new HashSet<CfgBlock>(cfg);
					for(CfgBlock inBlock : b.getPredecessors()){
						newDom.retainAll(dominators.get(inBlock));
					}
					//every node dominates itself
					newDom.add(b);
					if(!newDom.equals(dominators.get(b))){
						dominators.put(b,newDom);
						changed = true;
					}
				}			
			}
		} while (changed);
		
		return dominators;
	}
	
	//Really simple for now: Just get all blocks that define each variable.  Don't worry too much about 
	//dominators, etc
	//TODO worry about dominators etc
	public Map<Variable,Set<CfgBlock>> computeDefiningBlocks() {
		Map<Variable,Set<CfgBlock>> rval = new HashMap<Variable,Set<CfgBlock>>();
		for(CfgBlock b : getCfg()){
			for(Variable v : b.getLVariables()){
				Set<CfgBlock> definingBlocks = rval.get(v);
				if(definingBlocks == null){
					definingBlocks = new HashSet<CfgBlock>();
					rval.put(v, definingBlocks);
				}
				definingBlocks.add(b);
			}
		}
		return rval;
	}

	public Map<Variable,Set<Statement>> computeDefiningStatements() {
		Map<Variable,Set<Statement>> rval = new HashMap<Variable,Set<Statement>>();

		for (Map.Entry<Variable, Set<CfgBlock>> entry : computeDefiningBlocks().entrySet()){
			Variable v = entry.getKey();
			Set<Statement> set = new HashSet<Statement>();
			for(CfgBlock b : entry.getValue()){
				for(Statement s : b.getStatements()){
					if(s.getLVariables().contains(v)){
						set.add(s);
					}
				}
			}
			rval.put(v,set);
		}
		return rval;
	}

	public Map<Variable,Set<Statement>> computeUsingStatements() {
		Map<Variable,Set<Statement>> rval = new HashMap<Variable,Set<Statement>>();

		for (Map.Entry<Variable, Set<CfgBlock>> entry : computeUsingBlocks().entrySet()){
			Variable v = entry.getKey();
			Set<Statement> set = new HashSet<Statement>();
			for(CfgBlock b : entry.getValue()){
				for(Statement s : b.getStatements()){
					if(s.getUsedVariables().contains(v)){
						set.add(s);
					}
				}
			}
			rval.put(v,set);
		}
		return rval;
	}

	//Really simple for now: Just get all blocks that define each variable.  Don't worry too much about 
	//dominators, etc
	//TODO worry about dominators etc
	public Map<Variable,Set<CfgBlock>> computeUsingBlocks() {
		Map<Variable,Set<CfgBlock>> rval = new HashMap<Variable,Set<CfgBlock>>();
		for(CfgBlock b : getCfg()){
			for(Variable v : b.getUsedVariables()){
				Set<CfgBlock> usingBlocks = rval.get(v);
				if(usingBlocks == null){
					usingBlocks = new HashSet<CfgBlock>();
					rval.put(v, usingBlocks);
				}
				usingBlocks.add(b);
			}
		}
		return rval;
	}

	
	/**
	 * Return the set of live variable at the entry of each block. A variable is live between its
	 * first and last use. 
	 * Following the algorithm on p610 of the dragon book, 2nd ed.
	 * @return
	 */
	public LiveVars<CfgBlock> computeBlockLiveVariables() {
		Set<CfgBlock> cfg = this.getCfg();

		//Reserve the necessary size in the hashmap
		Map<CfgBlock,Set<Variable>> in = new HashMap<CfgBlock,Set<Variable>>(cfg.size());
		Map<CfgBlock,Set<Variable>> out =  new HashMap<CfgBlock,Set<Variable>>(cfg.size());

		//cache these to save time
		Map<CfgBlock,Set<Variable>> use = new HashMap<CfgBlock,Set<Variable>>(cfg.size());
		Map<CfgBlock,Set<Variable>> def = new HashMap<CfgBlock,Set<Variable>>(cfg.size());

		//Start by initializing in to empty.  The book does this separately for exit and non exit blocks, but that's not necessary
		//TODO can exit blocks have variables?  E.g. can they return values?  In which case we should actually recurse over all blocks!
		for (CfgBlock b: cfg){
			in.put(b, new HashSet<Variable>());
			use.put(b, b.getUsedVariables());
			def.put(b,  b.getLVariables());
		}

		boolean	changed = false;

		do {
			changed = false;
			for(CfgBlock b : cfg){
				out.put(b,b.computeLiveOut(in));
				Set<Variable> newIn = SetOperations.union(use.get(b),SetOperations.minus(out.get(b),def.get(b)));

				if (! newIn.equals(in.get(b))){
					changed=true;
					in.put(b,newIn);
				}
			}
		} while (changed);

		return new LiveVars<CfgBlock>(in,out);
	}
}
