#!/bin/bash
MAX_HEAP=$1
LEAK=$2

CLASSPATH=build/libs/java-perf-tests.jar
JMX_PASSWORD_FILE=~/jmxremote-password
JMX=com.sun.management.jmxremote
JMX_CONFIG="-D${JMX}.port=7091 -D${JMX}.password.file=${JMX_PASSWORD_FILE} -D${JMX}.ssl=false"
AGENT_CONFIG="-javaagent:../../jmx_prometheus_javaagent-0.20.0.jar=3100:agent_config.yaml"

if [[ "${MAX_HEAP}" == "" ]]; then
    echo "max heap not specified; using 1G"
    MAX_HEAP="1G"
fi;

OPTS="-Xmx${MAX_HEAP}"

gradle build

echo ${JMX_CONFIG} ${AGENT_CONFIG}
java -classpath $CLASSPATH -Djava.net.preferIPv4Stack=true ${OPTS} ${JMX_CONFIG} ${AGENT_CONFIG} com.jrandrews.jsc.perf.Main ${LEAK}

