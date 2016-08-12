/**
 * 
 */
package soottocfg.cfg;

/**
 * @author schaef
 *
 */
public class SourceLocation {
	
	private final String sourceFileName;
	private final int lineNumber;
	
	public static final SourceLocation ANALYSIS = new SourceLocation("-",-1);

	public SourceLocation(String sourceFileName, int lineNumber) {
		this.sourceFileName = sourceFileName;
		this.lineNumber = lineNumber;
	}

	/**
	 * @return the lineNumber
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return the sourceFileName
	 */
	public String getSourceFileName() {
		return sourceFileName;
	}

}
