/**
 * 
 */
package soottocfg.cfg.method;

import org.jgrapht.graph.DefaultEdge;

import com.google.common.base.Optional;

import soottocfg.cfg.expression.Expression;

/**
 * @author schaef
 *
 */
public class CfgEdge extends DefaultEdge {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5682469643400808759L;

	private Optional<Expression> label;

	/**
	 * 
	 */
	public CfgEdge() {
		label = Optional.absent();
	}

	public void setLabel(Expression l) {
		label = Optional.of(l);
	}

	public void removeLabel() {
		label = Optional.absent();
	}

	public Optional<Expression> getLabel() {
		return label;
	}

	public CfgBlock getSource() {
		return (CfgBlock) super.getSource();
	}

	/**
	 * Retrieves the target of this edge. This is protected, for use by
	 * subclasses only (e.g. for implementing toString).
	 *
	 * @return target of this edge
	 */
	public CfgBlock getTarget() {
		return (CfgBlock) super.getTarget();
	}
}
