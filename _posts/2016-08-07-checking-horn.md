---
layout: post
title:  "Generating and Checking Horn clauses"
date:   2016-08-07 00:01:00
categories: jekyll
---

Remember our example from the [overview]( {{ site.github.url }}{% post_url 2016-08-01-model-checking-java %}) (here slightly modified to make it more interesting):


```java
int foo(int k) {
  int r = 0;
  for (int i=0;i<k;i++) 
    r+=2;
  assert r==2*k;
  return r;
}
```

With the Horn clause encoding: 

$$
\begin{align*}
p1(0, k, 0) & \leftarrow p0(r, k) \\
p2(r, k, i) & \leftarrow i<k \wedge p1(r, k, i) \\
p1(r+2, k, i+1) & \leftarrow p2(r, k, i) \\
p3(r, k, i) & \leftarrow i=k \wedge p1(r, k, i) \\
r=2*k & \leftarrow p3(r, k, i)
\end{align*}
$$

There are a few things missing that we will explain in this section. So far, we only considered how simple sequences of Java statements (or instructions) can be translated; for complete applications, we also need to represent methods and method calls, and object invariants (through push and pull instructions).

Since we already got rid of dynamic method dispatch, at this point we can use a fairly straightforward translation for methods. Methods will be represented using contracts consisting of pre- and post-conditions. For each method, say method `foo` from above, we will introduce two Horn predicates: a predicate $$foo\_pre$$ that will be used as pre-condition, and $$foo\_post$$ representing the post-condition. Pre-conditions can talk about the method arguments, describe the states in which a method can be called; post-conditions describe the relationship between method arguments and method results. When translating the body of a method, we can initially assume the pre-condition, and but finally have to verify that the post-condition holds, which gives us a more complete set of Horn clauses:

$$
\begin{align*}
p0(r, k) & \leftarrow foo\_pre(k) \\[0.5ex]
p1(0, k, 0) & \leftarrow p0(r, k) \\
p2(r, k, i) & \leftarrow i<k \wedge p1(r, k, i) \\
p1(r+2, k, i+1) & \leftarrow p2(r, k, i) \\
p3(r, k, i) & \leftarrow i=k \wedge p1(r, k, i) \\
r=2*k & \leftarrow p3(r, k, i) \\[0.5ex]
foo\_post(r, k) & \leftarrow p3(r, k, i)
\end{align*}
$$

When translating method invocations to Horn, pre- and post-conditions are used in the reverse way. Suppose method `foo` is called from `bar`:

```java
int bar(int x) {
  int y = foo(x+1);
  return y;
}
```

When calling `foo`, the method `bar` first has to check that `foo`'s pre-condition holds, but can then in return assume `foo`'s post-conditions. In pseudo-Java, this would correspond to saying:

```java
int bar(int x) {
  int y;
  assert foo_pre(x+1);
  assume foo_post(y, x+1);
  return y;
}
```

This code can be turned into a set of Horn clauses right away. However, in contrast to all the clauses that we have seen so far, at this point we have to introduce a non-linear clause (a Horn clause with multiple literals in its body) to conect the state invariant of `bar` with the post-condition of `foo`:

$$
\begin{align*}
q0(y, x) & \leftarrow bar\_pre(x) \\[0.5ex]
foo\_pre(x+1) & \leftarrow q0(y, x) \\
q1(y, x) & \leftarrow q0(y', x) \wedge foo\_post(y, x+1) \\[0.5ex]
bar\_post(y, x) & \leftarrow q1(y, x)
\end{align*}
$$



Go back to [intermediate language]({{ site.github.url }}{% post_url 2016-08-08-intermediate-language %}).

Go back to [overview]( {{ site.github.url }}{% post_url 2016-08-01-model-checking-java %} )