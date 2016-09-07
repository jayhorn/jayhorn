/**
 * 
 */
package soottocfg.cfg;

import java.util.Set;

import soottocfg.cfg.variable.Variable;

/**
 * @author schaef
 *
 */
public interface Node {

	public Set<Variable> getUseVariables();

	public Set<Variable> getDefVariables();
}
