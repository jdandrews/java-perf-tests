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

    public static void main(String[] args) throws IOException {
        listNetworkInterfaces();
        JMXConnectorServer cs = configureJmx(args);
        System.out.println("Hello, world.");
        readLine("press return to exit:");
        cs.stop();
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
        for (Enumeration<InetAddress> addressEnum = i.getInetAddresses(); addressEnum.hasMoreElements(); ) {
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
        HashMap<String,Object> env = new HashMap<String,Object>();

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
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost:" + 3001  + "/jndi/rmi://localhost:" + 3000 + "/jmxrmi");
        JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(url, env, mbs);

        // Start the RMI connector server.
        //
        System.out.println("Start the RMI connector server");
        cs.start();

        return cs;
    }
}
