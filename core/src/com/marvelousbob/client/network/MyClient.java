package com.marvelousbob.client.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.marvelousbob.client.network.register.Msg;
import com.marvelousbob.client.network.register.Ping;
import com.marvelousbob.client.network.register.Register;
import com.marvelousbob.client.network.test.IncrementalAverage;
import java.net.InetAddress;
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

    @SneakyThrows
    public void connect() {
        Listener onReceiveCallback = new Listener() {
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
        };
        client.addListener(new Listener.ThreadedListener(onReceiveCallback));
//        client.addListener(onReceiveCallback);
        client.start();
        client.connect(TIMEOUT, addr, PORT);
    }
}
