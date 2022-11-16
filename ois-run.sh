#!/bin/bash

CLASSPATH=build/libs/java-perf-tests.jar

JMX_PASSWORD_FILE=~/jmxremote-password
JMX=com.sun.management.jmxremote
JMX_CONFIG="-D${JMX} -D${JMX}.port=7091 -D${JMX}.rmi.port=7091 -D${JMX}.ssl=false -D${JMX}.password.file=${JMX_PASSWORD_FILE}  -D${JMX}.host=localhost"

OPTS="-Djava.net.preferIPv4Stack=true -Djava.rmi.server.hostname=localhost"

gradle build

echo ${OPTS} ${JMX_CONFIG}
java -classpath $CLASSPATH ${OPTS} $JMX_CONFIG com.oracle.jsc.perf.Main

