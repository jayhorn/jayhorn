---
layout: post
title:  "Reality Strikes Back: Testing a Horn clause verifier"
date:   2016-08-06 00:01:00
categories: jekyll
---

While the new [memory model]({{ site.github.url }}{% post_url 2016-08-03-pull-and-push %}) using pull and push is conceptually neat and relatively easy to implement (at least the basic version) it has one significant disadvantage: it is tricky to test. If something goes wrong in the encoding and we try to pull an object from the heap that has not been initialized (by a preceding push), the Horn clause solver will try to use `false` as an invariant. That is, we just added an `assume false` somewhere in our program and things might be verified for all the wrong reasons.
This has been a major issue for us throughout the development. There are always bugs. And there is always confirmation bias. If JayHorn finally verifies a program, or always has verified it, it is easy to not look for an assume false.
While this still remains an issue, we want to discuss which steps we take to identify and prevent bugs that could introduce an assume false. 

The first line of defense against introducing an assume false into the program is that, whenever we translate a pull statement into Horn, we check in the inter-procedural control-flow graph (ICFG) if there is a preceding push for the same type. If we find a path where this is not the case we surely have a bug and raise an exception. 
As we discuss elsewhere, there are optimizations of the heap model that enumerate all push statements that can affect a pull and we simply add our check to this optimization so it can be performed without additional cost.

Testing is another important step to avoid assume false statements. We keep a set of regression tests (which are Java programs with a single assertion) for which we know that JayHorn should prove correctness, and we keep a version of these test which a slightly altered assertion that does not hold. If JayHorn verifies the program with the altered assertion, we know we have a soundness issue. The challenge when creating these test cases is to find good ways to alter the passing test case that could trigger corner cases in the translation.
This technique has proved particularly useful to find bugs in the translation of sub-typing. By slightly altering class hierarchies, declaring variables using a more general type than their dynamic type, and adding various `instanceof` assertions in the code.

Another source of unsoundness is the program transformation that makes implicit control-flow explicit. That is, the transformations that remove exception handling, virtual calls, etc. As well as the transformations that make static initializer calls explicit, add default initializers to constructors, and stub library calls.
These transformations fall into two categories: testable ones and un-testable ones. Transformations that somewhat preserve the input/output behavior of the program can be tested. E.g., when we eliminate a virtual call of the kind `a.foo()` by wrapping the call in a set of conditional choices to distinguish the actual type of the base `a`, we preserve the behavior of the original program and we can test on a set of inputs if the output for the transformed program is the same as for the original program.
Testing the removal of exceptional control-flow is more challenging but still possible. Usually, it is sufficient to wrap the method under test in a method that has a catch-all block and returns either a string of the actual return value or the last thrown exception.
Other transformations cannot be tested easily. E.g., stubbing of library functions (e.g., the constructor of `java.lang.Object`) or the explicit calls to static initializers.
There may be ways of setting up a test infrastructure to still test these transformations, but we did not spend time and effort on this so far.