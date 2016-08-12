/**
 * 
 */
package transformation_tests.exceptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author schaef
 *
 */
public class ExceptionExamples {

	public void foo() {
		
	}
	
	public void bar() {
		foo();
	}
	
	private Object getProcessor(String s) throws ArithmeticException {
		throw new ArithmeticException();
	}
	
	public void thisThrows() {
		getProcessor("");
	}

	public Object excpetions04(InputStream is) throws IOException {
		InputStreamReader isr = null;
		try {
			try {
				isr = new InputStreamReader(is, "UTF-8");
			} catch (java.io.UnsupportedEncodingException e) {
				isr = new InputStreamReader(is);
			}
			BufferedReader rd = new BufferedReader(isr);
			String processorClassName = rd.readLine();
			if (processorClassName != null && !"".equals(processorClassName)) {
				return getProcessor(processorClassName);
			}
		} finally {
			try {
				isr.close(); // example of tricky nested traps
			} catch (IOException e) {
				// ignore
			}
		}
		return null;
	}	
}
