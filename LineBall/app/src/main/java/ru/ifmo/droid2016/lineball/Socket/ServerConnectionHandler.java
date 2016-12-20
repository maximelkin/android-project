package ru.ifmo.droid2016.lineball.Socket;

import java.io.IOException;

public class ServerConnectionHandler {
    private static ServerConnection serverConnection;

    public static ServerConnection getInstance() throws IOException {
        if (serverConnection == null) {
            serverConnection = new ServerConnection();
        }
        return serverConnection;
    }
}
