package benchtop;

import java.io.File;
import java.util.List;

import org.junit.Test;

import benchtop.utils.Classes;
import benchtop.utils.IO;

public class SystemTest01 {

	@Test public void testOldWay() throws Exception {
		final String cpString = "/Users/schaef/git/graphgen/build/classes/main:/Users/schaef/git/graphgen/lib/soot-trunk.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/args4j/args4j/2.32/1ccacebdf8f2db750eb09a402969050f27695fb7/args4j-2.32.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/com.google.code.findbugs/annotations/3.0.0/e20984c024b3baf63d06cf481c65c242dcd541de/annotations-3.0.0.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/org.apache.commons/commons-lang3/3.4/5fe28b9518e58819180a43a850fbc0dd24b7c050/commons-lang3-3.4.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/org.jgrapht/jgrapht-core/0.9.1/872b4fd3a6d8a4f0bdfb0f6e26c4499e752ce39/jgrapht-core-0.9.1.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/org.jgrapht/jgrapht-ext/0.9.1/58672d07b20aa5fe46a8e84403c92d13cddf1e4d/jgrapht-ext-0.9.1.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/org.tinyjee.jgraphx/jgraphx/2.0.0.1/ba31c747a124ea12652d453cf174cb910a702ca3/jgraphx-2.0.0.1.jar:/Users/schaef/.gradle/caches/modules-2/files-2.1/jgraph/jgraph/5.13.0.0/577a30b3c2cf7decbb68471f5c96bfa1647b98dd/jgraph-5.13.0.0.jar";
		final Classpath cp = Classpath.of(cpString.split(":"));

//		final String classes = "/Users/schaef/git/graphgen/build/classes/main";
		final String classes = "somethingMeaningful";
		final File directory = new File(classes);

		final List<File> allFiles = IO.collectFiles(directory, "class");

		final List<String> classList = IO.resolveFullyQualifiedNames(classes, allFiles);
		File randoopOutput = new File("randoop");
		Benchtop.randoop(cp, randoopOutput, 2, classList.toArray(new String[classList.size()]));
		cp.addAll(randoopOutput);

		// add junit and hamcrest to the cp before we run the tests.
		for (String each : System.getProperty("java.class.path").split(":")) {
			if (each.contains("hamcrest-core")) {
				cp.addAll(new File(each));
			} else if (each.contains("junit")) {
				cp.addAll(new File(each));
			}
		}

		for (Class<?> eachClass : compileRandoopTests(cp, randoopOutput)) {
			Benchtop.junit(cp, eachClass.getCanonicalName());
		}

	}

	@Test public void testNewWay() throws Exception {
		Benchtop.consumes(new ExecutionBundle() {
			@Override public void configure(Environment host) {
				host.bundleTarget(new File("graphgen/build/classes/main"));
				host.bundleOutput(new File("randoop"));
				// classpath is auto resolved..(.ivy2, .m2, and .gradle are ignored if on Windows)
				host.bundleClasspath();
				host.bundleTimeout(2);
				host.bundleFocus(); // default is regression tests
			}
		});
	}

	private final List<Class<?>> compileRandoopTests(Classpath classpath, File DIR) throws Exception {
		final List<File> files = IO.collectFiles(DIR, "java");
		return Classes.compileJava(classpath, DIR, files.toArray(new File[files.size()]));
	}

}
