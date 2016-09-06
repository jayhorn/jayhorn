#!/bin/sh

if [ -n "$GITHUB_API_KEY" ]; then 
  if [ -n "$TRAVIS_BRANCH" ]; then
    echo "$PWD"
    cd "$TRAVIS_BUILD_DIR"
    echo "$PWD"
    git checkout -b gh-pages origin/gh-pages
    echo $TRAVIS_BRANCH
    mkdir ./$TRAVIS_BRANCH
    cp -r ./jayhorn/build/reports/tests ./$TRAVIS_BRANCH/
    git add ./$TRAVIS_BRANCH
    git -c user.name='martinschaef' -c user.email='martinschaef@gmail.com' commit -m "travis update to test results." --no-verify
    git push -f -q https://martinschaef:$GITHUB_API_KEY@github.com/jayhorn/jayhorn gh-pages &2>/dev/null
    git checkout $TRAVIS_BRANCH
    cd "$TRAVIS_BUILD_DIR"
  fi
fi
