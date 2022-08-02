package com.example.mock;

import java.io.IOException;
import java.net.Socket;

public class NetworkConnection {
    private static final String hostName = "localhost";
    private static final int port = 6767;

    public static Socket getConnection() {
        try {
            return new Socket(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
