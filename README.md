# java-perf-tests
Demos designed to show java metrics under stress and tuning conditions

build:

    javac -d bin -s src/main/java `find src -name "*.java"`

# Memory Performance

## Enough RAM

    java -cp bin -Xmx128m -XX:+UseParallelGC com.jrandrews.jsc.perf.Main
    java -cp bin -Xmx128m -XX:+UseG1GC com.jrandrews.jsc.perf.Main

## Not quite enough RAM

    java -cp bin -Xmx50m -XX:+UseParallelGC -XX:FlightRecorderOptions=stackdepth=128 com.jrandrews.jsc.perf.Main
    java -cp bin -Xmx50m -XX:+UseG1GC       com.jrandrews.jsc.perf.Main

Overhead from JFR might require and extra 1m of heap if you want to make recordings in addition to monitoring JMX.

## Leak

    java -cp bin -Xmx128m -XX:+UseG1GC       com.jrandrews.jsc.perf.Main leak
    java -cp bin -Xmx128m -XX:+UseParallelGC com.jrandrews.jsc.perf.Main leak

add

    -XX:FlightRecorderOptions=stackdepth=128

to avoid JFR complaints if you want to observe these JVMs with JDK Flight Control and JFR.

## GC logging:

### JDK 8

    LOGS="-Xloggc:gc.log -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -XX:GCLogFileSize=10M"
    LOGS="${LOGS} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCDateStamps -XX:+PrintReferenceGC"
    LOGS="${LOGS} -XX:+PrintTenuringDistribution -XX:+PrintGCApplicationStoppedTime"

example:

    java -cp bin -Xmx128m ${LOGS} -XX:+UseG1GC       com.jrandrews.jsc.perf.Main leak

### JDK >= 9

    LOGS="-Xlog:gc*,gc+ref=debug,gc+age=trace,gc+heap=debug:file=gc%p%t.log:tags,uptime,time:filecount=10,filesize=10m"

example:

    java -cp bin -Xmx128m ${LOGS} -XX:+UseG1GC       com.jrandrews.jsc.perf.Main leak

# Deadlock

    java -cp bin com.jrandrews.jsc.perf.Deadlock

# Thread contention

    java -cp bin com.jrandrews.jsc.perf.ThreadContention
