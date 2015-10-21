package translation_tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;

public class TranslationTest01 {

    
//	public int noExceptions(int i) {
//		if (i == 0) {
//			return 1;
//		} else if (i == 2) {
//			return 2;
//		}
//		assert 2 == 1;
//		return 3;
//	}

//	String s1;
//	
//	public void virtualCalls(int i) {
//		Object o;
//		if (i>0) {
//			o = s1;
//		} else {
//			o = new TranslationTest01();
//		}
//		o.toString(); // may not be null.
//	}
//	@Override
//	public String toString() {return "";}
	
	
	int x,y;
	
	// public void excpetions02() {
	// 	File myFile = new File(new File("doesntexist"), "test.txt"); 
	// 	try {
	// 		myFile.createNewFile();
	// 	} catch (FileNotFoundException e) {
	// 		x =10;
	// 	} catch (IOException e) {
	// 		y = 20;
	// 	}
	// 	x = 30;
	// }

    public int dsn01() {
	int a = 10;
	int b = 20;
	if (a < b) {
	    x = a;
	    return x;
	} else {
	    x = b;
	    return x;
	}
    }
	public void foo() {}
	
	public int withException() {
		int b;
		try {
//			int a[] = new int[2];
//			b=a[3];
			foo();
		} catch (ArrayIndexOutOfBoundsException e) {
			b = 20;
		}
		b=30;
		return b;
	}
	
//	public void excpetions02() {
//		File myFile = new File(new File("doesntexist"), "test.txt"); 
//		try {
//			myFile.createNewFile();
//		} catch (FileNotFoundException e) {
//			x =10;
//		} catch (IOException e) {
//			y = 20;
//		}
//		x = 30;
//	}
//	
//	public void excpetions03() {
//		File myFile = new File(new File("doesntexist"), "test.txt"); 
//		try {
//			myFile.createNewFile();
//		} catch (IOException e) {
//			x =10;
//		} finally {
//			y = 20;
//		}
//		x = 30;
//	}
	
	
	private Object getProcessor(String s) throws ArithmeticException {
		throw new ArithmeticException();
	}
	
   public Object excpetions04(InputStream is)
           throws IOException {
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
               isr.close(); //example of tricky nested traps
           } catch (IOException e) {
               // ignore
           }
       }
       return null;
   }	
	
}
