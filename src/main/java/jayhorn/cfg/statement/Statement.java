/**
 * 
 */
package jayhorn.cfg.statement;

import jayhorn.cfg.Node;
import jayhorn.util.Log;
import soot.Unit;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLineNumberTag;
import soot.tagkit.SourceLnPosTag;
import soot.tagkit.Tag;

/**
 * @author schaef
 *
 */
public abstract class Statement implements Node {
	
	private final int javaSourceLineNumber;
	
	public Statement(Unit createdFrom) {
		int lineNumber = -1;
		for (Tag tag : createdFrom.getTags()) {
			if (tag instanceof SourceLineNumberTag) {
				SourceLineNumberTag t = (SourceLineNumberTag)tag;
				lineNumber = t.getLineNumber();
			} else if (tag instanceof LineNumberTag) {
				LineNumberTag t = (LineNumberTag)tag;
				lineNumber = t.getLineNumber();
			} else if (tag instanceof SourceLnPosTag) {
				SourceLnPosTag t = (SourceLnPosTag)tag;
				lineNumber = t.startLn();
			} else {
				Log.error("Unprocessed tag "+tag.getClass()+ " - "+tag);
			}			
		}
		this.javaSourceLineNumber = lineNumber;
	}

	public int getJavaSourceLine() {
		return this.javaSourceLineNumber;
	}
}
