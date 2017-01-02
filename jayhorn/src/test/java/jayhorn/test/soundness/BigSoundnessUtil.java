/**
 * 
 */
package jayhorn.test.soundness;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

/**
 * @author schaef
 *
 */
public class BigSoundnessUtil {

	private static final int MAX_ELEMENTS = 3;
	
	public static void storeNewTestRun(Map<String, TestOutcome> testResults) {
		TestHistory results = getPreviousResults(SOUNDNESS_TEST_RESULTS);
		String commit_hash = "unknown_"+LocalDateTime.now();
		try {
			commit_hash = new String(Files.readAllBytes(Paths.get(COMMIT_HASH_PATH)), Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TestRun test = new TestRun();
		test.commitHash = commit_hash;
		test.timeStamp = LocalDateTime.now();
		test.testResults = testResults;
		for (Entry<String, TestOutcome> entry : testResults.entrySet()) {
			if (entry.getValue().equals(TestOutcome.CORRECT)) {
				test.correct++;
			} else if (entry.getValue().equals(TestOutcome.IMPRECISE)) {
				test.imprecise++;
			} else if (entry.getValue().equals(TestOutcome.UNSOUND)) {
				test.unsound++;
			} else if (entry.getValue().equals(TestOutcome.EXCEPTION)) {
				test.exception++;
			}
		}
		
		//print the diff if we have a history.
		if (!results.testRuns.isEmpty()) {
			printDiff(results.testRuns.getLast(), test);
		}
		
		results.testRuns.add(test);
		while (results.testRuns.size()>MAX_ELEMENTS) {
			results.testRuns.remove(0);
		}

		writeResults(SOUNDNESS_TEST_RESULTS, results);		
	}
	
	private static boolean appendNumDiff(StringBuilder sb, int oldNum, int newNum) {
		if (oldNum!=newNum) {
			sb.append("(");		
			sb.append(((newNum-oldNum)>0)?"+":"-");
			sb.append(Math.abs(newNum-oldNum));
			sb.append(")");
			return true;
		}
		return false;
	}
	
	private static void printDiff(TestRun oldRun, TestRun newRun) {
		StringBuilder sb = new StringBuilder();
		boolean change = false;
		sb.append(String.format("Correct: %1$3d", newRun.correct));
		change = appendNumDiff(sb, oldRun.correct, newRun.correct) ? true : change;
		sb.append(String.format("\tImprecise: %1$3d", newRun.imprecise));
		change = appendNumDiff(sb, oldRun.imprecise, newRun.imprecise) ? true : change;
		sb.append(String.format("\tUnsound: %1$3d", newRun.unsound));
		change = appendNumDiff(sb, oldRun.unsound, newRun.unsound) ? true : change;
		sb.append(String.format("\tException: %1$3d", newRun.exception));
		change = appendNumDiff(sb, oldRun.exception, newRun.exception) ? true : change;
		sb.append("\n");
		
		if (change) {
			sb.append("*** Changes *** \n");
			for (Entry<String, TestOutcome> entry : newRun.testResults.entrySet()) {
				if (!oldRun.testResults.containsKey(entry.getKey())) {
					sb.append(entry.getKey());
					sb.append(" has been added.\n");
				} else if (!entry.getValue().equals(oldRun.testResults.get(entry.getKey()))){					
					sb.append(entry.getKey());
					sb.append(String.format(" changed from %1s to %2s.", oldRun.testResults.get(entry.getKey()), entry.getValue()));
					sb.append("\n");
				}
			}
		}
		sb.append("*** End of Report *** \n");
		System.out.println(sb.toString());
	}
	
	private static String COMMIT_HASH_PATH = "../.git/ORIG_HEAD";

	private static String SOUNDNESS_TEST_RESULTS = "../bigSoundnessHistory.json";

	public static enum TestOutcome {
		CORRECT("Correct"), IMPRECISE("Imprecise"), UNSOUND("Unsound"), EXCEPTION("Exception");
		private final String text;

		private TestOutcome(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	public static class TestHistory {
		public LinkedList<TestRun> testRuns;
	}
	
	public static class TestRun {
		public String commitHash;
		public LocalDateTime timeStamp;
		public Map<String, TestOutcome> testResults;
		public int correct, imprecise, unsound, exception;
		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();
			s.append(commitHash);
			s.append("\t" + timeStamp);
			s.append("\n");
			s.append("Correct " + correct);
			s.append("\timprecise " + imprecise);
			s.append("\tunsound " + unsound);
			s.append("\texception" + exception);
			s.append("\n");
			s.append(testResults);
			return s.toString();
		}
	}

	private static TestHistory getPreviousResults(final String fileName) {
		Gson g = new Gson();
		String jsonString = "";
		try {
		jsonString = new String(Files.readAllBytes(Paths.get(fileName)), Charset.forName("UTF-8"));
		} catch (IOException e) {
			
		}	
		TestHistory testHistory = g.fromJson(jsonString, TestHistory.class);
		if (testHistory==null) {
			testHistory = new TestHistory();
			testHistory.testRuns = new LinkedList<TestRun>();
		}
		return testHistory;
	}

	private static void writeResults(final String fileName, TestHistory results) {
		Gson g = new Gson();
		final String jsonString = g.toJson(results);
		try ( OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(fileName), Charset.forName("UTF-8"));
				PrintWriter out = new PrintWriter(outputStreamWriter) ) {
			out.println(jsonString);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			Map<String, TestOutcome> testResults = new LinkedHashMap<>();
			testResults.put("test01", TestOutcome.EXCEPTION);
			testResults.put("test02", TestOutcome.CORRECT);
			testResults.put("test03", TestOutcome.CORRECT);
			storeNewTestRun(testResults);
	}

}
