# java-perf-tests
Demos designed to show java metrics under stress and tuning conditions

# Sieve of Eratosthenes
    javac -d bin -s src/main/java \*\*/\*.java

Enough RAM:

    java -cp bin -Xmx767m -XX:+UseParallelGC com.oracle.jsc.perf.Main

Not quite enough RAM:

    java -cp bin -Xmx256m -XX:+UseG1GC com.oracle.jsc.perf.Main
    java -cp bin -Xmx384m -XX:+UseParallelGC com.oracle.jsc.perf.Main
