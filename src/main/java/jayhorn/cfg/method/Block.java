/**
 * 
 */
package jayhorn.cfg.method;

import java.util.LinkedList;
import java.util.List;

import jayhorn.cfg.Node;

/**
 * @author schaef
 *
 */
public class Block implements Node {

	protected final List<Block> predecessors, successors;
	
	public Block() {
		this.predecessors = new LinkedList<Block>();
		this.successors = new LinkedList<Block>();
	}
	
}
