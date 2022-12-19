package com.oracle.jsc.perf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.rmi.ssl.SslRMIServerSocketFactory;

public class Main {
    private static InputStreamReader r = new InputStreamReader(System.in);
    private static BufferedReader br = new BufferedReader(r);
    private static ExecutorService sieveExec = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        log("Hello, world.");
        // code for launching a local JMX server during JMC debugging:
        // listNetworkInterfaces();
        // JMXConnectorServer cs = configureJmx(args);

        log("press <enter> to exit (heh).");
        for (int i = 0, c = 0; c != 10 && i < 1000; ++i) {
            Future<List<Integer>> sieve = (sieveExec.submit(new SieveOfEratosthenes(100000000)));
            while (!sieve.isDone()) {
                if (System.in.available() > 0) c = System.in.read();
                sleep(5);
            }
            log(sieve.get().size() + " primes");
        }

        sieveExec.shutdown();

        // cs.stop();
    }

    private static void log(String s) {
        System.out.println(s);
    }

    private static void sleep(long n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
            // no-op
        }
    }

    private static void listNetworkInterfaces() throws SocketException {
        System.out.println(
                NetworkInterface.networkInterfaces().map(i -> describe(i)).collect(Collectors.toUnmodifiableList()));
    }

    private static String describe(NetworkInterface i) {
        StringBuffer result = new StringBuffer("\n\n".concat(i.getDisplayName()));
        try {
            result.append(":\n\tup: ").append(i.isUp());
        } catch (SocketException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        for (Enumeration<InetAddress> addressEnum = i.getInetAddresses(); addressEnum.hasMoreElements();) {
            InetAddress address = addressEnum.nextElement();
            result.append("\n\taddr: ").append(address);
        }
        return result.toString();
    }

    private static String readLine(String prompt) {
        String line = "";
        try {
            System.out.println(prompt);
            line = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static JMXConnectorServer configureJmx(String[] args) throws IOException {

        // Ensure cryptographically strong random number generator used
        // to choose the object number - see java.rmi.server.ObjID
        //
        System.setProperty("java.rmi.server.randomIDs", "true");

        // Start an RMI registry on port 3000.
        //
        System.out.println("Create RMI registry on port 3000");
        LocateRegistry.createRegistry(3000);

        // Retrieve the PlatformMBeanServer.
        //
        System.out.println("Get the platform's MBean server");
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        // Environment map.
        //
        System.out.println("Initialize the environment map");
        HashMap<String, Object> env = new HashMap<String, Object>();

        // Provide SSL-based RMI socket factories.
        //
        // The protocol and cipher suites to be enabled will be the ones
        // defined by the default JSSE implementation and only server
        // authentication will be required.
        //
        SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
        SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();
        env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, csf);
        env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, ssf);

        // Provide the password file used by the connector server to
        // perform user authentication. The password file is a properties
        // based text file specifying username/password pairs.
        //
        env.put("jmx.remote.x.password.file", "password.properties");

        // Provide the access level file used by the connector server to
        // perform user authorization. The access level file is a properties
        // based text file specifying username/access level pairs where
        // access level is either "readonly" or "readwrite" access to the
        // MBeanServer operations.
        //
        // env.put("jmx.remote.x.access.file", "access.properties");

        // Create an RMI connector server.
        //
        // As specified in the JMXServiceURL the RMIServer stub will be
        // registered in the RMI registry running in the local host on
        // port 3000 with the name "jmxrmi". This is the same name that the
        // ready-to-use management agent uses to register the RMIServer
        // stub.
        //
        System.out.println("Create an RMI connector server");
//        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://:3000/jmxrmi");
        JMXServiceURL url = new JMXServiceURL(
                "service:jmx:rmi://localhost:" + 3001 + "/jndi/rmi://localhost:" + 3000 + "/jmxrmi");
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);

        // Start the RMI connector server.
        //
        System.out.println("Start the RMI connector server");
        cs.start();

        return cs;
    }
}
