package com.marvelousbob.client.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.marvelousbob.client.network.register.Msg;
import com.marvelousbob.client.network.register.Ping;
import com.marvelousbob.client.network.register.Register;
import com.marvelousbob.client.network.test.IncrementalAverage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;


public class MyClient {

    public static final String REMOTE_SERVER = "52.60.181.140";
    public static final int PORT = 80;
    public static final int TIMEOUT = 15000;

    @Getter
    private final InetAddress addr;

    @Getter
    private final Client client;

    @Getter
    private IncrementalAverage latencyReport;


    public MyClient() {
        this(false);
    }

    @SneakyThrows
    public MyClient(boolean isRemoteServer) {
        this.client = new Client();
        Register.registerClasses(client);
        this.addr = isRemoteServer
                ? InetAddress.getByName(REMOTE_SERVER)
                : InetAddress.getLocalHost();
        this.latencyReport = new IncrementalAverage();
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package
     * and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName()
                        .substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    @SneakyThrows
    public void connect() {
        client.addListener(new Listener.ThreadedListener(new Listener() {
            @Override
            public void received(Connection connection, Object o) {
                if (o instanceof Msg) {
                    Msg m = (Msg) o;
                    System.out.println(m);
                }
                if (o instanceof Ping) {
                    Ping p = (Ping) o;
                    latencyReport.addToRunningAverage(p.getTimeStamp());
                }
            }
        }));
        client.start();
        client.connect(TIMEOUT, addr, PORT);
    }
}
