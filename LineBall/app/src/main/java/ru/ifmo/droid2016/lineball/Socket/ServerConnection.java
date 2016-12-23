package ru.ifmo.droid2016.lineball.Socket;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;

//return true - all good

public class ServerConnection {
    private static final String host = "localhost";
    private static final int port = 8080;
    private final Socket socket;
    private static InputStream inputStream;
    private static DataOutputStream dataOutputStream;

    ServerConnection() throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = socket.getInputStream();
        String androidId = Settings.Secure.ANDROID_ID;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        if (!send("con " + androidId)) {
            throw new ConnectException();
        }
    }

    @NonNull
    private String readStr() throws IOException {
        byte[] b = new byte[20];
        int len = inputStream.read(b);
        return new String(b, "UTF8").substring(0, len);
    }

    private boolean writeStr(String message) {
        try {
            dataOutputStream.writeBytes(message + "#");
            dataOutputStream.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean send(String action) {
        try {
            writeStr(action);
            String response = readStr();
            return Integer.parseInt(response) == 0;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean verify(String password) {
        return send("ver " + password);
    }

    boolean registration(String password) {
        return send("reg " + password);
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