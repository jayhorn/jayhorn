#!/bin/sh
# note that the shebang in the first line is necessary even for windows! 
echo "pre commit"
# stash any unstaged changes
git stash -q --keep-index

# run the tests with the gradle wrapper
./gradlew check

# store the last exit code in a variable
RESULT=$?

# unstash the unstashed changes
git stash pop -q

# return the './gradlew test' exit code
exit $?
