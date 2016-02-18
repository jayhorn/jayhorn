package soottocfg.randoop;

import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Huascar Sanchez
 */
public class JUnit4TestMaker {
  private JUnit4TestMaker(){}

  /**
   * Creates lazy dynamic test instances from the given test
   * case or test suite.
   *
   * @param args if non-empty, this is the list of test method names.
   * @return list of dynamic tests.
   */
  public static List<UnitTest> makeUnitTests(Class<?> testClass, String... args) {
    final List<UnitTest> result = new ArrayList<>();
    invokeSuiteMethods(result, testClass, args);
    return result;
  }

  /**
   * Checks if a given class is a JUnit4 test.
   *
   * @param klass the test to check
   * @return true if it is; false otherwise.
   */
  public static boolean isJunit4Test(Class<?> klass) {
    return isParameterizedTest(klass)
      || IsSingleTestMethod(klass);
  }

  private static boolean isParameterizedTest(Class<?> klass) {
    boolean isTestSuite     = false;
    boolean hasSuiteClasses = false;

    // @RunWith(Suite.class) @SuiteClasses( ... ) public class MyTest { ... }
    //   or
    // @RunWith(Parameterized.class) public class MyTest { ... }
    for (Annotation eachAnnotation : klass.getAnnotations()) {
      final Class<?> annotationClass = eachAnnotation.annotationType();

      if (Parameterized.class.isAssignableFrom(annotationClass)) {
        return true;
      } else if (RunWith.class.isAssignableFrom(annotationClass)
        && Suite.class.isAssignableFrom(((RunWith) eachAnnotation).value())) {
        isTestSuite = true;
      } else if (Suite.SuiteClasses.class.isAssignableFrom(annotationClass)) {
        hasSuiteClasses = true;
      }

      if (isTestSuite && hasSuiteClasses) {
        return true;
      }
    }
    return false;
  }

  private static boolean IsSingleTestMethod(Class<?> klass) {
    // public class MyTest {
    //     @Test public void example() { ... }
    // }
    for (Method eachMethod : klass.getDeclaredMethods()) {
      for (Annotation eachAnnotation : eachMethod.getAnnotations()) {
        if (org.junit.Test.class.isAssignableFrom(eachAnnotation.annotationType())) {
          return true;
        }
      }
    }
    return false;
  }

  private static void invokeSuiteMethods(List<UnitTest> out, Class<?> testClass, String... args) {
    boolean isJunit4TestClass = false;

    Collection<Object[]> argCollection = findParameters(testClass);

    /* JUnit 4.x: methods marked with @Test annotation. */
    if (args.length == 0) {
      for (Method eachMethod : testClass.getMethods()) {
        if (!eachMethod.isAnnotationPresent(org.junit.Test.class)) continue;

        isJunit4TestClass = true;

        if (eachMethod.isAnnotationPresent(Ignore.class)) {
          out.add(new IgnoredTest(testClass, eachMethod));
        } else if (eachMethod.getParameterTypes().length == 0) {
          addAllParameterizedTests(out, testClass, eachMethod, argCollection);
        } else {
          out.add(new JUnitConstructionError(testClass.getName() + "#" + eachMethod.getName(),
            new IllegalStateException("Tests may not have parameters!")));
        }
      }
    } else {
      for (String arg : args) {
        try {
          addAllParameterizedTests(
            out,
            testClass,
            testClass.getMethod(arg), argCollection
          );
        } catch (final NoSuchMethodException e) {
          out.add(
            new JUnitConstructionError(testClass.getName() + "#" + arg, e)
          );
        }
      }
    }

    isJunit4TestClass |= foundSuiteTests(out, testClass);

    if (!isJunit4TestClass) {
      out.add(new JUnitConstructionError(testClass.getName(),
        new IllegalStateException("Not a test case: " + testClass)));
    }
  }

  private static boolean foundSuiteTests(List<UnitTest> out, Class<?> suite) {
    boolean isSuite = false;

    /* Check for @RunWith(Suite.class) */
    for (Annotation a : suite.getAnnotations()) {
      if (RunWith.class.isAssignableFrom(a.annotationType())) {
        if (Suite.class.isAssignableFrom(((RunWith) a).value())) {
          isSuite = true;
        } break;
      }
    }

    if (!isSuite) { return false; }

    /* Extract classes to run */
    for (Annotation a : suite.getAnnotations()) {
      if (Suite.SuiteClasses.class.isAssignableFrom(a.annotationType())) {
        for (Class<?> clazz : ((Suite.SuiteClasses) a).value()) {
          invokeSuiteMethods(out, clazz);
        }
      }
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  private static Collection<Object[]> findParameters(Class<?> testClass) {
    for (Method eachMethod : testClass.getMethods()) {
      for (Annotation eachAnnotation : eachMethod.getAnnotations()) {
        if (Parameterized.Parameters.class
          .isAssignableFrom(eachAnnotation.annotationType())) {

          try {
            return (Collection<Object[]>) eachMethod.invoke(testClass);
          } catch (Exception ignored) {}

        }
      }
    }

    return null;
  }

  private static void addAllParameterizedTests(List<UnitTest> out, Class<?> testClass, Method method,
                                               Collection<Object[]> argCollection) {
    if (argCollection == null) {
      out.add(
        TestMethod.create(testClass, method, null)
      );
    } else {
      for (Object[] args : argCollection) {
        out.add(
          TestMethod.create(testClass, method, args)
        );
      }
    }
  }


  /**
   * Default implementation of Jayhorn's dynamic tests.
   */
  static abstract class JUnitTest implements UnitTest {
    protected final Class<?>  testClass;
    protected final Method    method;

    protected JUnitTest(Class<?> testClass, Method method){
      this.testClass  = testClass;
      this.method     = method;
    }

    @Override public void run() throws Throwable {
      Object testCase = getTestCase();

      Throwable failure;

      // invoke pre-processing methods
      failure = invokeBeforeLikeMethods(testCase);

      // invoke post-processing methods
      failure = invokeAfterLikeMethods(testCase, failure);

      if (!meetsExpectations(failure, method)) {
        if (failure == null) {
          throw new AssertionFailedError("Expected exception not thrown");
        } else {
          throw failure;
        }
      }


    }

    private void invokeMethodWithAnnotation(Object testCase, Class<?> annotation) throws
      IllegalAccessException, InvocationTargetException {

      for (Method eachMethod : testCase.getClass().getDeclaredMethods()) {
        for (Annotation eachAnnotation : eachMethod.getAnnotations()) {
          if (annotation.isAssignableFrom(eachAnnotation.annotationType())) {
            eachMethod.invoke(testCase);
          }
        }
      }
    }

    private Throwable invokeAfterLikeMethods(Object testCase, Throwable failure){
      try {
        invokeMethodWithAnnotation(testCase, After.class);
      } catch (InvocationTargetException t) {
        if (failure == null) {
          failure = t.getCause();
        }
      } catch (Throwable t) {
        if (failure == null) {
          failure = t;
        }
      }


      try {
        invokeMethodWithAnnotation(testCase, AfterClass.class);
      } catch (InvocationTargetException t) {
        if (failure == null) {
          failure = t.getCause();
        }
      } catch (Throwable t) {
        if (failure == null) {
          failure = t;
        }
      }

      return failure;
    }

    protected boolean meetsExpectations(Throwable failure, Method method) {
      Class<?> expected = null;

      for (Annotation eachAnnotation : method.getAnnotations()) {
        if (org.junit.Test.class.isAssignableFrom(eachAnnotation.annotationType())) {
          expected = ((org.junit.Test) eachAnnotation).expected();
        }

      }

      return expected == null || org.junit.Test.None.class.isAssignableFrom(expected)
        ? (failure == null)
        : (failure != null && expected.isAssignableFrom(failure.getClass()));
    }

    private Throwable invokeBeforeLikeMethods(Object testCase) {
      Throwable failure = null;
      try {
        invokeMethodWithAnnotation(testCase, BeforeClass.class);
        invokeMethodWithAnnotation(testCase, Before.class);
        method.invoke(testCase);
      } catch (InvocationTargetException i){
        failure = i.getCause();
      } catch (Throwable t){
        failure = t;
      }
      return failure;
    }

    protected abstract Object getTestCase() throws Exception;
  }

  static class TestMethod extends JUnitTest {
    private final Constructor<?> constructor;
    private final Object[] constructorArgs;

    private TestMethod(Class<?> testClass, Method method, Constructor<?> constructor, Object[] constructorArgs){
      super(testClass, method);

      this.constructor      = constructor;
      this.constructorArgs  = constructorArgs;
    }


    static UnitTest create(Class<?> testClass, Method method, Object[] constructorArgs){

      if(constructorArgs != null){
        for(Constructor<?> eachConst : testClass.getConstructors()){
          if(eachConst.getParameterTypes().length == constructorArgs.length){
            return new TestMethod(testClass, method, eachConst, constructorArgs);
          }
        }

        return new JUnitConstructionError(testClass.getName() + "#" + method.getName(),
          new Exception("Parameterized test cases must have "
            + constructorArgs.length + " arg constructor"));
      }

      try {
        return new TestMethod(testClass, method, testClass.getConstructor(), null);
      } catch (NoSuchMethodException ignored) {
      }

      try {
        return new TestMethod(testClass, method,
          testClass.getConstructor(String.class), new Object[] { method.getName() });
      } catch (NoSuchMethodException ignored) {}

      return new JUnitConstructionError(testClass.getName() + "#" + method.getName(),
        new Exception("Test cases must have a no-arg or string constructor."));


    }


    @Override protected Object getTestCase() throws Exception {
      return constructor.newInstance(constructorArgs);
    }

    @Override public String toString() {
      return testClass.getName() + "#" + method.getName();
    }
  }

  static class JUnitConstructionError implements UnitTest {
    private final String name;
    private final Throwable cause;

    JUnitConstructionError(String name, Throwable cause) {
      this.name   = name;
      this.cause  = cause;
    }

    @Override public void run() throws Throwable {
      throw cause;
    }

    @Override public String toString() {
      return name;
    }
  }

  static class IgnoredTest extends JUnitTest {
    private IgnoredTest(Class<?> testClass, Method method) {
      super(testClass, method);
    }

    @Override public void run() throws Throwable {
      System.out.println("@Ignored.");
    }

    @Override protected Object getTestCase() {
      throw new UnsupportedOperationException();
    }

    @Override public String toString() {
      return testClass.getName() + "#" + method.getName();
    }
  }
}
