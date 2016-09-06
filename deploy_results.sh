#!/bin/sh

if [ -n "$GITHUB_API_KEY" ]; then 
  if [ -n "$TRAVIS_BRANCH" ]; then
    echo $TRAVIS_BRANCH
    mkdir ./$TRAVIS_BRANCH
    cp -r ./jayhorn/build/reports/tests ./$TRAVIS_BRANCH/
    echo "$PWD"
    git checkout -b gh-pages origin/gh-pages
    #git add ./$TRAVIS_BRANCH
    #git -c user.name='martinschaef' -c user.email='martinschaef@gmail.com' commit -m "travis update to test results." --no-verify
    #git push -f -q https://martinschaef:$GITHUB_API_KEY@github.com/jayhorn/jayhorn gh-pages &2>/dev/null
    #git checkout $TRAVIS_BRANCH    
  fi
fi
