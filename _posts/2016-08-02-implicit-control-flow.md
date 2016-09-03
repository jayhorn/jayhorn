---
layout: post
title:  "Getting rid of implicit control-flow"
date:   2016-08-02 00:01:00
categories: jekyll
---

As discussed in our overview [post]( {{ site.github.url }}{% post_url 2016-08-01-model-checking-java %} ), the first step of building a software model checker usually is to simplify the program as much as possible without changing its behavior. 

Everything that we can simplify without changing the behavior of the program can be tested easily. We can simply run both versions and make sure that their output is the same. This is extremely useful because it is very easy to get code transformations wrong and testable transformations and not only less likely to have bugs, they are also a lot easier to debug. So let's spend some time to simplify the program.

Tools based on C/C++ usually use Clang and LLVM for this step which comes with a built in set of transformations that can get the program into a somewhat “canonical” form. For Java, we have a few frameworks to choose from: Soot and Wala are high level framework that come with a lot of off-the-shelf algorithms. For people with a lot of time, there is also ASM and BCEL to modify bytecode with your bare hands. We decided to use Soot, mostly because we had prior experience with it. Wala probably would have worked equally well.
Soot parses the class files of a Java program for us and produces [Jimple](https://en.wikipedia.org/wiki/Soot_%28software%29) representation. Jimple already simplifies the many bytecode operations to 15 simple operations. Still, before we can start proving properties of Java code, we have to get rid of all that implicit control-flow introduced by exceptions and virtual calls. 

Let’s look at the following example:

```java
public class A extends OtherClass {
  @Override
  public int foo(String s) {
    return s.length;
  }
}

public class C {
  public void bar(OtherClass a) {
    int i = a.foo("3");
    assert(i>0);
  }
}
```

Note that, for readability, we use source code but the actual implementation operates on Jimple  which makes things a bit simple because we don’t have to worry about execution order, etc.

Let’s look at method `C.bar(OtherClass a)` in the example above. There is a lot of stuff happening that is not written anywhere in the code: When calling `foo`, we first dereference `a`. If `a` happens to be `null` the execution of `bar` ends here and we throw a `RuntimeException`. If `a` is ok, we call a method `foo` but which `foo` depends on the actual type of `a`. If `a instanceof A`, we call the method defined further up in the example. But `a` might also be of a different type as long as it is a subtype of `OtherClass` in which case a different method would be called. 
Our verifier doesn’t know these things so we have to make them explicit by transforming the program a bit. Let’s start with the runtime exceptions. The easiest way to get rid of exceptions is to add a static variable that keeps the last thrown exception. To that end, we generate the following class:

```java
public class JayHornHelper {
  public static Throwable lastException=null;
}
```

Now we can make the exceptional control-flow from our example above explicit by guarding each statement that may throw a runtime exception with an if-statement in which we update the exception variable if necessary:

```java
public class A extends OtherClass {
  @Override
  public int foo(String s) {
    if (s==null) {
      JayHornHelper.lastException = 
        new NullPointerException(...);
      return 0;
    }
    return s.length;
  }
}

public class C {
  public void bar(OtherClass a) {
    if (a==null) {
        JayHornHelper.lastException = 
          new NullPointerException(...);
        return;
    }
    int i = a.foo("3");
    if (JayHornHelper.lastException!=null) 
      return;
    assert(i>0);
  }
}
```

There are a few things worth noting in this step: in `foo`, after setting `lastException` to a new exception, we return the default value zero. This, of course, is only sound if we also introduce the check if `lastException` is non-null before using the return value of `foo`.

Note that this transformation is only sound (i.e., strictly over-approximates the possible error traces of the original program) if we assume that there is only one thread. Otherwise we would have to wrap a few things in synchronized blocks.

Using our global `lastException` variable, we can eliminate catch- and finally-blocks in the same way. While in source code, it is easy to get finally blocks wrong, the situation is a lot easier because everything is already translated into conditional branches. The only special instruction that needs to be handled is ‘caughtExceptionRef’ which reads the last exception. Again, this is simple because this is just our `lastException`. 
As a side note: even the compiler gets finally blocks wrong occasionally as you can read [here](http://stackoverflow.com/questions/25615417/try-with-resources-introduce-unreachable-bytecode).

Now, the exceptional control-flow should be gone from our program. Next, we have to handle virtual calls. In our example from above, we cannot decide which `foo` is invoked by the statement `int i = a.foo("3");` until we know the type of `a` at this point. 
The easiest way to handle this is to have a big switch case over the possible types of `a` at this point. Further, once we have added these case splits and know that no virtual calls occur, we can make all methods static and pass the instance pointer as first argument. In our example class `A` this would look as follows:

```java
public static void bar(C _this, OtherClass a) {
  //...
  int i;
  if (i instanceof A) i = A.foo((A)a, "3");
  else i = OtherClass.foo(a, "3");
  if (JayHornHelper.lastException!=null) 
    return;
  assert(i>0);
}
```

Note that if we make all methods static, we also have to make all fields public to avoid permission errors. Since we assume that we start from compilable code this does not introduce any problems.

In practice, the switch case over the possible types of an object at runtime can become really large. Assume the object that we are interested in is declared as `java.lang.Object` and we call its `toString` method. In the worst case, we have to make a case split over all possible types in our classpath. 
For programs where this becomes a problem, a points-to analysis can help to get a better approximation of the possible types of an object at runtime. Soot already provides several built in algorithms for that. However, they are computationally very expensive and should be only used if enumerating the possible subclasses is not an option.
Another option is to replace the virtual call by a call to a method that is treated as something abstract by the verifier. But we will discuss that in a different article. 

Now we have eliminated most implicit control-flow and can start thinking about translating the transformed program into our intermediate verification language which we discuss in the next section. Note that, even if we did some good work so far, we are not done yet. There is still things like static initializers, implicit initialization of fields, or calls to library methods that needs to be handled. However, we will ignore these problems for now and first look into our intermediate representation for verification.

In a future [post]( {{ site.github.url }}{% post_url 2016-08-08-intermediate-language %} ) we discuss how to get from this simplified version of Java into an intermediate representation that is suitable for verification.
