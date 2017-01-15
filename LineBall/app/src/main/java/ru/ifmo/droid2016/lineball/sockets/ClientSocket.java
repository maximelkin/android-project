package ru.ifmo.droid2016.lineball.sockets;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

//return true - all good

abstract public class ClientSocket {
    private static final String host = "arcueid.ru";
    private static final int port = 8080;
    private final Socket socket;
    protected final InputStream inputStream;
    private final OutputStream outputStream;

    public ClientSocket() throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    @NonNull
    protected String readStr() {
        byte[] b = new byte[200];
        try {
            int len = inputStream.read(b);
            if (len == -1)
                return "1";
            return new String(b, "UTF8").substring(0, len);
        } catch (IOException e) {
            e.printStackTrace();
            return "1";
        }
    }

    protected boolean writeStr(@NonNull String message) {
        try {
            outputStream.write(message.getBytes("UTF8"));
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    protected boolean send(@NonNull String action) {
        return writeStr(action) && readStr().equals("0");
    }

}