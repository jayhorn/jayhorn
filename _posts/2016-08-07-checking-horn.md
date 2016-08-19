---
layout: post
title:  "Generating and Checking Horn clauses"
date:   2016-08-07 00:01:00
categories: jekyll
---

Remember our example from the Go back to [overview]( {{ site.github.url }}{% post_url 2016-08-01-model-checking-java %}):


```java
void foo(int k) {
  int r = 0;
  for (int i=0;i<k;i++) 
    r+=2;
  assert r==2*k;
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

There are a few things missing that we will explain in this section.

Go back to [intermediate language]({{ site.github.url }}{% post_url 2016-08-08-intermediate-language %}).

Go back to [overview]( {{ site.github.url }}{% post_url 2016-08-01-model-checking-java %} )