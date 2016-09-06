#!/bin/sh

if [ -n "$GITHUB_API_KEY"] && [[ "$TRAVIS_PULL_REQUEST" == "false" ]]; then
  # cd "$TRAVIS_BUILD_DIR"
  git checkout -b gh-pages
  mkdir $BRANCH
  cp -r jayhorn/build/reports/tests $BRANCH/
  git add $BRANCH
  git -c user.name='martinschaef' -c user.email='martinschaef@gmail.com' commit -m "travis update to test results." --no-verify
  git push -f -q https://martinschaef:$GITHUB_API_KEY@github.com/jayhorn/jayhorn gh-pages &2>/dev/null
  cd "$TRAVIS_BUILD_DIR"
fi
