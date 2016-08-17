---
layout: post
title:  "From Java to intermediate form"
date:   2016-08-08 00:01:00
categories: jekyll
---

In a previous [post]({{ site.github.url }}{% post_url 2016-08-02-implicit-control-flow %}) we have discussed how to eliminate implicit control-flow from exception handling and virtual calls. However, there was some implicit control-flow that we couldn't eliminate easily, like calls to static initializers. Even if we would make calls to those explicit, the JVM would still call the original static initializers when the class is loaded. Its easy to get this wrong because the static initializer may get called twice and thus we probably do not want to do this on the Java level.

There are other good reasons to have an intermediate program representation before translating into Horn clauses. Just to name a few:

   * We can keep a human-readable form of the program for debugging but still use language constructs that are not available in Java. E.g., methods that return tuples. 

   * We can simplify the memory model (which we describe in this [post]({{ site.github.url }}{% post_url 2016-08-03-pull-and-push %})) while still having access to the structure of the program and, in particular, the type hierarchy. 

   * We can build different translations from Java into this intermediate language to allow for different level of precision for the verifier. E.g., we can use different approaches to model Integers and Floats.

The most important decision for such an intermediate language is how we model interaction with objects. Dealing with the heap is always a problem when analyzing software because this is the point where we have to compromise a lot of precision in order to remain reasonably scalable. One of the key contributions of JayHorn is its way to model memory which we describe in detail in this [post]({{ site.github.url }}{% post_url 2016-08-03-pull-and-push %}).

Another important question when building an intermediate language is how to handle library code. For example, where do we get the code `java.lang.Object` from? For some classes we could use mock objects as a specification but this doesn't always work (as in the case of `Object`). Also, we may want to replace some methods by more abstract code. E.g., for a `HashMap` it is, in most cases, sufficient to know that we have to an element in there before we can look it up but it is not necessary to model the actual hash function. In a future [post]({{ site.github.url }}{% post_url 2016-08-05-open-programs %}) we will discuss different approaches to tackle this problem.

Finally, we have to discuss how to handle primitive types. Do we model integers as mathematical integers without bounds, or do we want to track overflows? How about floats and doubles? Are they real numbers or do we model rounding errors? And strings: objects or primitive types? There are many ways to answer these questions and it is important to have some dials and knobs to balance between precision and scalability. The post for this topic has yet to be written (and to code in JayHorn as well).



