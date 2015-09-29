#!/bin/bash
set -ev
./gradlew check
if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
	./gradlew integration_test
fi