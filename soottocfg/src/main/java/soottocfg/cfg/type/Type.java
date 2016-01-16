/**
 * 
 */
package soottocfg.cfg.type;

import java.io.Serializable;

/**
 * @author schaef Note that Type is not abstract, so we can use it as wildcard
 *         type.
 */
public class Type implements Serializable {

	private static final long serialVersionUID = -5408794248925241891L;
	private static final Type instance = new Type();

	public static Type instance() {
		return instance;
	}

	protected Type() {
	}

}
