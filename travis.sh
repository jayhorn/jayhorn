#!/bin/bash
set -ev
./gradlew check
if [ "${TRAVIS_PULL_REQUEST}" = "true" ]; then	
	./gradlew integration_test
fi