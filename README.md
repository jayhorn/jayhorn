Run locally with:
bundle install
bundle exec jekyll serve -w


## Create new content

If you want to add a post, just create just add a file to _post/. The file name has to be in this hyphen separated format. No underscores or fancy stuff. So: YYYY-MM-DD-hello-world.md is of, but YYYY-MM-DD-hello_world.md is NOT ok.

If you want to link from one blog post to another please look at the model-checking-java example how this is done. Jekyll is crazy, so don't assume that you know how it works. Copy the example from there.

The posts use [kramdown](http://kramdown.gettalong.org/syntax.html) as markdown language. I have no idea if its a good choice. It was the one that came with the template.

