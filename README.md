| **master** | [![Build Status](https://travis-ci.org/jayhorn/jayhorn.svg?branch=master)](https://travis-ci.org/jayhorn/jayhorn) | [![Coverage Status](https://coveralls.io/repos/jayhorn/jayhorn/badge.svg?branch=master&service=github)](https://coveralls.io/github/jayhorn/jayhorn?branch=master) | [![Coverity Scan](https://scan.coverity.com/projects/6013/badge.svg)](https://scan.coverity.com/projects/6013) | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/28a133096dce4f4396f27459af39afd4)](https://www.codacy.com/app/martinschaef/jayhorn?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jayhorn/jayhorn&amp;utm_campaign=Badge_Grade) |
| --- | --- | --- | --- | --- |
| **devel** | [![Build Status](https://travis-ci.org/jayhorn/jayhorn.svg?branch=devel)](https://travis-ci.org/jayhorn/jayhorn) | [![Coverage Status](https://coveralls.io/repos/jayhorn/jayhorn/badge.svg?branch=devel&service=github)](https://coveralls.io/github/jayhorn/jayhorn?branch=devel) | | [![Codacy Badge](https://api.codacy.com/project/badge/Grade/28a133096dce4f4396f27459af39afd4)](https://www.codacy.com/app/martinschaef/jayhorn?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=jayhorn/jayhorn&amp;utm_campaign=Badge_Grade) | 


<img src="http://jayhorn.github.io/jayhorn/images/rhino.png" height=100> Java and Horn clauses

JayHorn is a software model checking tool for Java. JayHorn tries to find a proof that certain bad states in a Java program are never reachable. These bad states are specified by adding runtime assertions (where some assertions may be generated, e.g., that an object reference must not be Null before being accessed). 


JayHorn tries to err on the side of precision that is, when it is not able to proof that an assertion always holds, it will claim that the assertion may be violated (this is called soundness). JayHorn is currently sound (modulo bugs) for Java that use a single thread, have no dynamic class loading, and do not perform complex operations in static initializers.

For information on how to download and run JayHorn check [our website](http://jayhorn.github.io/jayhorn/). For information on how JayHorn is implemented check
[our JayHorn development blog](http://jayhorn.github.io/jayhorn/jekyll/2016/08/01/model-checking-java/).


Join the chat [![Join the chat at https://gitter.im/jayhorn/Lobby](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/jayhorn/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Quick Guide

```bash
./gradlew assemble
java -jar jayhorn/build/libs/jayhorn.jar -help
java -jar jayhorn/build/libs/jayhorn.jar -j example/classes -solution -trace
```



## Soundines Statement
This project has been done in the spirit of soundiness. When building practical program analyses, it is often necessary to cut corners. In order to be open about language features that we do not support or support only partially, we are attaching this soundiness statement.

Our analysis does not have a fully sound handling of the following features:
- JNI, implicit method invocations (finalizers, class initializers, Thread.&lt;init&gt;, etc.)
- integer overflow
- exceptions and flow related to that
- reflection API (e.g., Method.invoke(), Class.newInstance )
- invokedynamic
- code generation at runtime, dynamic loading
- different class loaders
- key native methods (Object.run, Object.doPrivileged)

This statement has been produced with the Soundiness Statement Generator from [soundiness.org](http://soundiness.org).


## Licenses

JayHorn is open-source and distributed under [MIT license](https://github.com/jayhorn/jayhorn/blob/devel/LICENSE).

Libraries used in JayHorn include, in particular:
- [Soot](https://github.com/soot-oss/soot), which is distributed under [LGPL 2.1 or later](https://github.com/jayhorn/jayhorn/blob/devel/LGPL_LICENSE).
- [Eldarica](https://github.com/uuverifiers/eldarica), which is distributed under [BSD license](https://github.com/uuverifiers/eldarica/blob/master/LICENSE).
- [Princess](http://www.philipp.ruemmer.org/princess.shtml), which is distributed under [LGPL 2.1 or later](https://github.com/jayhorn/jayhorn/blob/devel/LGPL_LICENSE).

## Acknowledgments and Disclaimers

JayHorn is partially funded by:

   * AFRL contract No. FA8750- 15-C-0010.
   * DARPA under agreement FA8750-15-2-0087
   * NSF award No. 1422705
   * The Swedish Research Council grant 2014-5484
   
Any opinions, findings and conclusions or recommendations expressed in
this material are those of the author(s) do not necessarily
reflect the views of AFRL, DARPA, NSF or the Swedish Research Council.
