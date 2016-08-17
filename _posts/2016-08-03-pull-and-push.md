---
layout: post
title:  "A New Hope: Modeling memory using Pull and Push"
date:   2016-08-03 00:01:00
categories: jekyll
---

Reasoning about the heap is hard. Things quickly become undecidable and inferring any reasonable invariant automatically requires some careful thinking about the memory model. For memory-safe languages (like Java, in our case), the Burstall-Bornat memory encoding has been popular for a long time. In this approach, the heap is encoded by a two-dimensional array where the first dimension describes the object that we are interested in and the second describes the field of that object that we want to access.
For example, if we have a Java method like:

```java
public void foo(MyObject obj) {
  obj.x = 5;
}
```

We could encode the assignment `obj.x = 5` as follows: `heap[obj][x] := 5`. To verify this program, we would need to prove that `obj` is not `null` in the precondition and we would quickly get to a point where we have properties with two quantifiers and most of our reasoning tools would just give up.

So we need a new memory model. We want to get away with simple invariants that can be learned per object by our Horn clause solver. Of course, this has to come at a price and we may not be able to find invariants which are sufficient to verify the properties that we are interested in. Later, we will talk about how to improve precision again. 

First, we want to minimize the interactions with the heap. The idea is straightforward: if reasoning about heap interactions is complicated and expensive, try to do it a seldom as possible. To that end, we introduce two new language constructs: pull and push. Pull copies all fields of an object into local variables in a single transaction. Push updates all fields of an object on the heap in a single transaction. For example:

```java
public void foo(MyObject obj) {
  obj.x = 1;
  obj.y++;
  obj.z = 3;
}
```

Can now be written as:

```java
public void foo(MyObject obj) {
  int x,y,z = pull(obj);
  x=1; y++; z=3;
  push(obj, x, y, z);
}
```

By using push and pull, our program went from three heap updates to a single heap update (the push statement). Further, all interactions with the heap are handled through pull and push.

Think of pull and push in the same way as the git operations with the same name. Assume the heap is our shared repository. If we want to change something, we have to pull the latest version and if we want to make these changes visible to others, we have to push the diff back to our repository (here the heap).

The analogy to git also introduces another important concepts: invariants. In git, before we push, we have to make sure that we do not make the state of the central repository inconsistent. For the heap this is the same. Each object has an invariant associated with it that has to hold for the values in our update. Further, when we pull the latest version of an object from the heap, we can assume that this invariant holds (because no push is allowed to break it).

Here is where our story diverges a bit from git: in this basic encoding we assume that there is one invariant to describe an object on the heap and that this invariant never changes. So speaking in the language of git, a push is more like a pull request and we assume that all developers have a gentleman's agreement (the invariant) to ensure that all pull requests could be merged in an arbitrary order without conflicts. 

We give a more detailed explanation on how we introduce `pull` and `push` statements and how we minimize their usage in this [post]( {{ site.github.url }}{% post_url 2016-08-04-moving-pull-and-push %} ).

We can use pull and push to express all interactions with the heap. Hence, all other statements in our program will only operate on local variables. The next step how to get from here to Horn clauses is discussed [here]( {{ site.github.url }}{% post_url 2016-08-07-checking-horn %} ).



