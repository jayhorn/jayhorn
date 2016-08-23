---
layout: post
title:  "Less is More: Moving pull and push"
date:   2016-08-04 00:01:00
categories: jekyll
---

Our goal is to translate Java to Horn clauses that are actually solvable by off-the-shelf tools. This requires some insight into the inner workings of these solvers. 

Traditional heap models often introduce quantifiers in multiple dimensions, which result in major headaches for the solver. Since that poor guy is already doing all of our dirty work, we try to make life a bit easier for him by using a simpler than usual, lightweight heap model. 

That *push-pull* model and the reasoning behind it are described in [this post]({{ site.github.url }}{% post_url 2016-08-03-pull-and-push %}), here we discuss why and how to limit access to the heap.

In a nutshell, our heap model entails the following: any time we access an object, we first pull it from the heap into local variables; optionally, these locals are updated; then we push the locals back to the heap. On each pull and push, the class invariant must hold. The naive approach to adding pulls and pushes is thus to add a pair of them around each field access. This, however, is not precise enough.

Consider the following example:

```java
void foo() {
  A a = new A();
  a.i = 42;
  assert a.i==42;
}
```

For the sake of the example, assume that the constructor for `A` ends with a push of `0` to the `i` field. When we add pulls and pushes around the field accesses, we end up with the following:

```java
void foo() {
  int i;
  A a = new A();
  i = pull(a);
  i = 42;
  push(a,i);
  i = pull(a);
  assert i==42;
  push(a,i);
}
```

The problem here are of course the pull and push between the assignment and the assertion. Not only is having these there unnecessarily complex, it also means that the solver will not be able to prove the assertion. 

While it may seem counter-intuitive at first that having too many pulls and pushes decreases precision, the reason is in the search for invariants by the solver. Remember that the class invariant must hold on every pull and push operation. After a pull, it is also the only thing we know about the pulled object. In this case, the value `0` is pushed to field `a.i` by the constructor. This value is later updated to `42` and pushed. As the invariant inferred by the solver must hold at both these location, it is determined to be $$i==0 \vee i==42$$. We therefore cannot prove the assertion that $$i==42$$.

The solution is that, after adding pulls and pushes to the program, we apply a series of simplification rules to minimize heap access. The rationale here is that soundness of such rules is easier to ensure than soundness of a more complex algorithm to add only limited pulls and pushes, i.e. we err on the safe side.

A formal definition of the simplification rules will be given in an upcoming paper, we only discuss intuition here. One set of rules eliminates successive pulls and pushes of the same object. Another set of rules moves pulls up and pushes down. As these are only allowed to go in one direction, the application of the rules eventually converges to a fixpoint.

In this case, this results in the following (provable) code:

```java
void foo() {
  int i;
  A a = new A();
  i = pull(a);
  i = 42;
  assert i==42;
  push(a,i);
}
```

Note that in reality, our memory model is at the level of our intermediate language and, consequently, we perform these simplifications there. It is presented at the Java level here for readability.

While we increase precision here mainly for the intra-procedural case, there is still a lot more we can do to facilitate generation of more precise invariants. However, those ideas need some more pushing and pulling around, so check back later for more!

Go back to [Pull and Push]({{ site.github.url }}{% post_url 2016-08-03-pull-and-push %})

Go back to [intermediate language]({{ site.github.url }}{% post_url 2016-08-08-intermediate-language %}).

Go back to [overview]( {{ site.github.url }}{% post_url 2016-08-01-model-checking-java %} )
