---
layout: post
title:  "Model Checking Java"
date:   2016-08-01 00:01:00
categories: jekyll
---

So we decided to build a model checker for Java programs. The first thing you need is a catchy name: JayHorn. That is Java + Horn clauses. Teme came up with that name because he was working on SeaHorn (C + Horn clauses) already. We’ll talk about Horn clauses in a bit, but first: What exactly is a model checker for Java? For us, this is a program that tries to find a proof that certain bad states in a Java program are never reachable. These bad states can are specified by adding runtime assertions (where some assertions may be generated, e.g., that an object reference must not be Null before being accessed). Java can be a nasty language (try [yourself](http://www.javadeathmatch.com/)) so we set our goals humble: let’s focus on simple Java programs first. No threads, no dynamic class loading, no strange corner cases of static initialization. 

First, we looked at what’s out there. In particular for C, there is a set of tools, and even a [tool competition](https://sv-comp.sosy-lab.org/ ). All these tools share a basic organization into three steps: 

   * Parse the input program and simplify it as much as possible.

   * Translate the simplified program and the property that needs to be verified into an intermediate form that makes various lemmas about the language explicit.
   
   * Use a reasoning engine to prove the desired property and compute a certificate that the property holds or a counterexample that shows how it can be violated.


