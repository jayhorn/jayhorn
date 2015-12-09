package dynamic_tests;

public class DynamicTest01 {

	//TODO: org.apache.tools.ant.taskdefs.condition.Socket: boolean eval()

	public String simpleTest01() {
		StringBuilder sb = new StringBuilder();
		sb.append(simpleTestMethod01(0));
		sb.append(simpleTestMethod02(0));
		sb.append(simpleTestMethod01(2));
		sb.append(simpleTestMethod02(2));		
		return sb.toString();
	}
		
	private int simpleTestMethod01(int i) {
		if (i == 0) {
			return 1;
		} else if (i == 2) {
			return 2;
		}
		return 3;
	}

	String s1="Foo";
	
	private String simpleTestMethod02(int i) {
		Object o;
		if (i>0) {
			o = s1;
		} else {
			o = new DynamicTest01();
		}
		return o.toString(); // may not be null.
	}
	@Override
	public String toString() {return "Bar";}

	
	public int exceptionTest() {
		int result = withExceptionMethod(0);
		result += withExceptionMethod(5);
		return result;
	}

	private void foo() {}
	
	private int withExceptionMethod(int i) {
		int b=0;
		try {
			int a[] = new int[2];
			b=a[i];
			foo();			
		} catch (ArrayIndexOutOfBoundsException e) {
			b = 20;				
		} finally {
			b+=2;			
		}
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

//    public int dsn01() {
//	int a = 10;
//	int b = 20;
//	if (a < b) {
//	    x = a;
//	    return x;
//	} else {
//	    x = b;
//	    return x;
//	}
//    }
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
	
	
//	private Object getProcessor(String s) throws ArithmeticException {
//		throw new ArithmeticException();
//	}
//	
//   public Object excpetions04(InputStream is)
//           throws IOException {
//       InputStreamReader isr = null;
//       try {
//           try {
//               isr = new InputStreamReader(is, "UTF-8");
//           } catch (java.io.UnsupportedEncodingException e) {
//               isr = new InputStreamReader(is);
//           }
//           BufferedReader rd = new BufferedReader(isr);
//           String processorClassName = rd.readLine();
//           if (processorClassName != null && !"".equals(processorClassName)) {
//               return getProcessor(processorClassName);
//           }
//       } finally {
//           try {
//               isr.close(); //example of tricky nested traps
//           } catch (IOException e) {
//               // ignore
//           }
//       }
//       return null;
//   }	
	
}
