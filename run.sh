#!/bin/bash

CLASSPATH=build/libs/java-perf-tests.jar
JMX_CONFIG="-Dcom.sun.management.jmxremote.port=7091 -Dcom.sun.management.jmxremote.password.file=/home/opc/jmxremote-password -Dcom.sun.management.jmxremote.ssl=false"

gradle build

echo java -classpath $CLASSPATH  $JMX_CONFIG com.oracle.jsc.perf.Main
java -classpath $CLASSPATH  $JMX_CONFIG com.oracle.jsc.perf.Main

