#!/bin/sh
export PR=https://api.github.com/repos/$TRAVIS_REPO_SLUG/pulls/$TRAVIS_PULL_REQUEST
export BRANCH=$(if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then echo $TRAVIS_BRANCH; else echo `curl -s $PR | jq -r .head.ref`; fi)

if [ -n "$GITHUB_API_KEY"] && [[ "$TRAVIS_PULL_REQUEST" == "false" ]]; then
  cd "$TRAVIS_BUILD_DIR"
  git checkout -b gh-pages
  mkdir $BRANCH
  cp -r jayhorn/build/reports/tests $BRANCH/
  git add $BRANCH
  git -c user.name='martinschaef' -c user.email='martinschaef@gmail.com' commit -m "travis update to test results." --no-verify
  git push -f -q https://martinschaef:$GITHUB_API_KEY@github.com/jayhorn/jayhorn gh-pages &2>/dev/null
  cd "$TRAVIS_BUILD_DIR"
fi
