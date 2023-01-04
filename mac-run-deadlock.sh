#!/bin/bash
CLASSPATH=build/libs/java-perf-tests.jar

gradle build

echo
echo "Running Deadlock; ^C to stop"
echo

java -classpath $CLASSPATH com.jrandrews.jsc.perf.Deadlock

