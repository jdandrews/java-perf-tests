#!/bin/bash

CLASSPATH=build/libs/java-perf-tests.jar
JMX_PASSWORD_FILE=~/jmxremote-password
JMX=com.sun.management.jmxremote
JMX_CONFIG="-D${JMX}.port=7091 -D${JMX}.password.file=${JMX_PASSWORD_FILE} -D${JMX}.ssl=false"

gradle build

echo $JMX_CONFIG
java -classpath $CLASSPATH -Djava.net.preferIPv4Stack=true $JMX_CONFIG com.oracle.jsc.perf.Main

