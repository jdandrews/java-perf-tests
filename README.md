# java-perf-tests
Demos designed to show java metrics under stress and tuning conditions

# Sieve of Eratosthenes
build:

    javac -d bin -s src/main/java `find src -name "*.java"`

Enough RAM:

    java -cp bin -Xmx128m -XX:+UseParallelGC com.jrandrews.jsc.perf.Main
    java -cp bin -Xmx128m -XX:+UseG1GC com.jrandrews.jsc.perf.Main

Not quite enough RAM:

    java -cp bin -Xmx50m -XX:+UseParallelGC -XX:FlightRecorderOptions=stackdepth=128 com.jrandrews.jsc.perf.Main
    java -cp bin -Xmx50m -XX:+UseG1GC       com.jrandrews.jsc.perf.Main

Overhead from JFR might require and extra 1m of heap if you want to make recordings in addition to monitoring JMX.

Leak:

    java -cp bin -Xmx128m -XX:+UseG1GC       -XX:FlightRecorderOptions=stackdepth=128 com.jrandrews.jsc.perf.Main
    java -cp bin -Xmx128m -XX:+UseParallelGC -XX:FlightRecorderOptions=stackdepth=128 com.jrandrews.jsc.perf.Main

GC logging:

...

# Deadlock

    java -cp bin com.jrandrews.jsc.perf.Deadlock
