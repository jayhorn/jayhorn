#!/bin/sh

if [ -n "$GITHUB_API_KEY" ]; then 
  if [ -n "$TRAVIS_BRANCH" ]; then
    mkdir -p web
    git clone https://github.com/jayhorn/jayhorn.git  --branch gh-pages --single-branch web/
    cd web
    git rm -r ./$TRAVIS_BRANCH || true 
    mkdir -p ./$TRAVIS_BRANCH
    cp -r ./../jayhorn/build/reports/tests ./$TRAVIS_BRANCH/
    echo "$PWD"
    git add ./$TRAVIS_BRANCH
    git -c user.name='martinschaef' -c user.email='martinschaef@gmail.com' commit -am "AUTO UPDATE from Travis." --no-verify
    git push -f -q https://martinschaef:$GITHUB_API_KEY@github.com/jayhorn/jayhorn gh-pages 
    cd .. 
    echo "DONE updating results"   
    echo "$PWD"
  fi
fi


