#!/bin/bash
MAX_HEAP=$1
LEAK=$2

CLASSPATH=build/libs/java-perf-tests.jar

JAVA_FULL_VERSION=`java -version 2>&1`
JAVA_VERSION=`echo $JAVA_FULL_VERSION | grep version | awk '{print $3}'`

echo "using ${JAVA_FULL_VERSION}"

if [[ "$JAVA_VERSION" == *"1.8"* ]]; then
    LOGS="-Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=50M"
    LOGS="${LOGS} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintReferenceGC"
    LOGS="${LOGS} -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime"
else
    LOGS="-Xlog:gc*,gc+ref=debug,gc+age=trace,gc+heap=debug:file=gc%p%t.log:tags,uptime,time:filecount=10,filesize=50m"
fi

if [[ "${MAX_HEAP}" == "" ]]; then
    echo "max heap not specified; using 1G"
    MAX_HEAP="1G"
fi;

OPTS="-Xmx${MAX_HEAP}"

gradle build

echo "using Java ${JAVA_FULL_VERSION}"
echo 
echo ${LOGS}
echo

java -classpath $CLASSPATH -Djava.net.preferIPv4Stack=true ${LOGS} ${OPTS} com.jrandrews.jsc.perf.Main ${LEAK}

