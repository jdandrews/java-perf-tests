#!/bin/bash

CLASSPATH=build/libs/java-perf-tests.jar

gradle build
java -classpath $CLASSPATH com.oracle.jsc.perf.Main

