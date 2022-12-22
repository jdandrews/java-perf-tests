#!/bin/bash
MAX_HEAP=$1
LEAK=$2

CLASSPATH=build/libs/java-perf-tests.jar

JMX_PASSWORD_FILE=~/jmxremote-password
JMX=com.sun.management.jmxremote
JMX_CONFIG="-D${JMX} -D${JMX}.port=7091 -D${JMX}.rmi.port=7091 -D${JMX}.ssl=false -D${JMX}.password.file=${JMX_PASSWORD_FILE}  -D${JMX}.host=localhost"

OPTS="-Djava.net.preferIPv4Stack=true -Djava.rmi.server.hostname=localhost -Xmx${MAX_HEAP} -XX:+UseParallelGC -XX:FlightRecorderOptions=stackdepth=128"

gradle build

echo ${OPTS} ${JMX_CONFIG} ${LEAK}
java -classpath $CLASSPATH ${OPTS} ${JMX_CONFIG} com.jrandrews.jsc.perf.Main ${LEAK}

