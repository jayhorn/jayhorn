---
layout: post
title:  "Model Checking Java"
date:   2016-08-01 00:01:00
categories: jekyll
---

So we decided to build a model checker for Java programs. The first thing you need is a catchy name: JayHorn. That is Java + Horn clauses. Teme came up with that name because he was working on SeaHorn (C + Horn clauses) already. We’ll talk about Horn clauses in a bit, but first: What exactly is a model checker for Java? For us, this is a program that tries to find a proof that certain bad states in a Java program are never reachable. These bad states can are specified by adding runtime assertions (where some assertions may be generated, e.g., that an object reference must not be Null before being accessed). Java can be a nasty language (try [yourself](http://www.javadeathmatch.com/)) so we set our goals humble: let’s focus on simple Java programs first. No threads, no dynamic class loading, no strange corner cases of static initialization. 

First, we looked at what’s out there. In particular for C, there is a set of tools, and even a [tool competition](https://sv-comp.sosy-lab.org/ ). All these tools share a basic organization into three steps which we also used for building JayHorn: 

   * Parse the input program and simplify it as much as possible. Read more [here]({{ site.github.url }}{% post_url 2016-08-02-implicit-control-flow %}).

   * Translate the simplified program and the property that needs to be verified into an intermediate form that makes various lemmas about the language explicit. Read more [here]( {{ site.github.url }}{% post_url 2016-08-08-intermediate-language %} ).
   
   * Use a reasoning engine to prove the desired property and compute a certificate that the property holds or a counterexample that shows how it can be violated. Read more [here]( {{ site.github.url }}{% post_url 2016-08-07-checking-horn %} ).
   
A more detailed overview with related work and a few nice pictures can be found in our CAV 2016 [tool paper](http://martinschaef.github.io/website/papers/cav2016.pdf).


# TL;DR Show me that Horn stuff #

In a nutshell, the idea behind using Horn clauses is that we want a nice logic representation of the program where we don't have to remove the loops, etc. Let's look at this Java method:

```java
void foo(int k) {
  int r = 0;
  for (int i=0;i<k;i++) 
    r+=2;
  assert r==2*k;
}
```

where we want to prove the assertion `r==2*k`. When encoding the program as Horn clauses, we create one predicate for each control-location (basically for every place where a debugger could stop) over all program variables that are live (or can be observed with a debugger), and one Horn clause for each transition (or statement). So, in this example, we would start with creating one predicate $$p0(r, k)$$ for the location where we enter `foo` over the variables `r` and `k`. Then we add a second predicate for the entrance to the `for` loop $$p1(r, k, i)$$. Now, we can add our first Horn clause:

$$
\begin{align*}
p1(0, k, 0) & \leftarrow p0(r, k)
\end{align*}
$$


Which read as: "if we are at location `p0` with any value for `r` and `k`, we can transition to `p1` by updating `r` to zero, leaving `k` as it is and initializing `i` to zero". Easy, isn't it. Now we can add the loop condition check as follows:

$$
\begin{align*}
p1(0, k, 0) & \leftarrow p0(r, k) \\
p2(r, k, i) & \leftarrow i<k \wedge p1(r, k, i) \\
p3(r, k, i) & \leftarrow i\geq k \wedge p1(r, k, i) \\
\end{align*}
$$

where `p2` is the location of the loop body and `p3` is the location after the loop (i.e., the breakpoint before the assertion). The two Horn clauses we added read as: "if we are in `p1` and `i<k` we go to the loop body `p2`", and "if we are in `p1` and `i=k` we go to `p3`". Now we only need to update the loop body and the assertion:


$$
\begin{align*}
p1(0, k, 0) & \leftarrow p0(r, k) \\
p2(r, k, i) & \leftarrow i<k \wedge p1(r, k, i) \\
p1(r+2, k, i+1) & \leftarrow p2(r, k, i) \\
p3(r, k, i) & \leftarrow i=k \wedge p1(r, k, i) \\
r=2*k & \leftarrow p3(r, k, i)
\end{align*}
$$


Now how do we get from this to a proof that the assertion holds? Short answer: get a tool that does this like [Eldarica](http://lara.epfl.ch/w/eldarica) or [Z3](https://github.com/Z3Prover/z3) and read [this](https://www7.in.tum.de/~rybal/papers/2013-sas-solving-universally-quantified-horn-clauses.pdf) paper or [this](https://www7.in.tum.de/~rybal/papers/2013-cav-solving-existentially-quantified-horn-clauses.pdf) paper. The buzzwords you need to know are property directed reachability or PDR aka IC3. If you don't like to read, there is also a [video](https://vimeo.com/36729095).

These tools try to find a formula for each of our predicates `p1`, `p2`, and `p3` such that all Horn clauses become valid. For `p0`, we have to pick `true`, because we want to prove that the assertion holds from any initial state. For example, a tool could pick:


$$
\begin{align*}
p0(r, k) & = true \\
p1(r, k, i) & = r=2*i \\
p2(r, k, i) & = r=2*i \\
p3(r, k, i) & = r=2*k
\end{align*}
$$

If we instantiate our Horn clauses with these assignments, we get the follwoing:

$$
\begin{align*}
0=2*0 & \leftarrow true \\
r=2*i & \leftarrow i<k \wedge r=2*i \\
r+2=2*(i+1) & \leftarrow r=2*i \\
r=2*k & \leftarrow i=k \wedge r=2*i \\
r=2*k & \leftarrow r=2*k
\end{align*}
$$

and obviously all these formulas are valid, which gives us a proof that we can call `foo` for any value of `k` and we have the guarantee that the assertion holds `r==2*k `.

The assignments to the predicates can be seen as invariants for the corresponding program location. You may have noted that we didn't even worry that there is a loop in the program (which is usually the tricky part in verification). The Horn clause solver does that for us. For free. Awesome!

Now you might think: "wait, what happens if we pick a large `k` that causes an integer overflow"? 

Well, you can't expect that we explain you a full blown verifier in a single blog post! Live is hard and complicated and you don't get stuff for free. Read the rest of this blog to find out what sort of terrible problems we faced while implementing JayHorn and how graceful we solved them.


