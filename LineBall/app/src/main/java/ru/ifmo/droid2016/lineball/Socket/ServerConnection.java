package ru.ifmo.droid2016.lineball.Socket;
import android.provider.Settings;
import android.support.annotation.NonNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Date;

public class ServerConnection {
    private static final String host = "localhost";
    private static final int port = 8080;
    private final String androidId;
    private static Socket socket;
    private static InputStream inputStream;
    private static DataOutputStream dataOutputStream;

    public ServerConnection() throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = socket.getInputStream();
        androidId = Settings.Secure.ANDROID_ID;
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

    private void writeStr(String message) throws IOException {
        dataOutputStream.writeBytes(message + "#");
        dataOutputStream.flush();
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

    public boolean verify(String password) {
        return send("ver " + password);
    }

    public boolean registration(String password) {
        return send("reg " + password);
    }

    public boolean resetUser() {
        return send("reset");
    }


    public boolean search() throws IOException, IllegalAccessException {
        writeStr("search");
        if (readStr().equals("1"))
            throw new IllegalAccessException();
        return true;//remake!!!
    }

    public boolean gameOver(String result) {
        return send("gameov " + result);
    }

    public void setWall(String coordinates) throws IOException {
        writeStr("wall " + coordinates);
    }

    @NonNull
    public String getWall() throws IOException {
        return readStr();
    }

    public long getTimeDelta() throws IOException {
        writeStr("p");
        long time1 = (new Date()).getTime();
        long time_server = Long.parseLong(readStr());
        long time2 = (new Date()).getTime();
        return (time1 + time2) / 2 - time_server;//add comments
    }
}
