package ru.ifmo.droid2016.lineball.Socket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

//return true - all good

public class ServerConnection {
    private static final String host = "arcueid.ru";
    private static final int port = 8080;
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    ServerConnection(String androidId) throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        if (!send("con " + androidId)) {
            throw new ConnectException();
        }
    }

    @NonNull
    private String readStr() throws IOException {
        byte[] b = new byte[100];
        int len = inputStream.read(b);
        if (len == -1)
            throw new IOException("no input");
        return new String(b, "UTF8").substring(0, len);
    }

    private boolean writeStr(String message) {
        try {
            outputStream.write((message + "#").getBytes("UTF8"));
            outputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean send(String action) {
        try {
            return writeStr(action) && readStr().equals("0");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean verify(String password) {
        return send("ver " + password);
    }

    boolean registration(String password_username) {
        return send("reg " + password_username);
    }

    String search() {
        try {
            writeStr("search");
            String read = readStr();
            return (read.equals("1")? null: read);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    boolean gameOver(String result) {
        return send("gameov " + result);
    }

    boolean setWall(String coordinates) {
        return writeStr("wall " + coordinates);
    }

    @Nullable
    String getWall() throws IOException {
        if (inputStream.available() != 0)
            return readStr();
        else
            return null;
    }
}