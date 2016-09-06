#!/bin/sh

if [ -n "$GITHUB_API_KEY" ]; then 
  if [ -n "$TRAVIS_BRANCH" ]; then
    mkdir -p web
    git clone https://github.com/jayhorn/jayhorn.git  --branch gh-pages --single-branch web/
    cd web
    mkdir -p ./$TRAVIS_BRANCH
    cp -r ./../jayhorn/build/reports/tests ./$TRAVIS_BRANCH/
    echo "$PWD"    
    git add ./$TRAVIS_BRANCH
    git -c user.name='martinschaef' -c user.email='martinschaef@gmail.com' commit -m "travis update to test results." --no-verify
    git push -f -q https://martinschaef:$GITHUB_API_KEY@github.com/jayhorn/jayhorn gh-pages &2>/dev/null
    cd ..    
  fi
fi


