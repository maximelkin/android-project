package ru.ifmo.droid2016.lineball.sockets;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.ConnectException;

public class GameSocket extends ClientSocket {

    public GameSocket(@NonNull String androidId) throws IOException {
        super();
        if (!send("con " + androidId)) {
            throw new ConnectException();
        }
    }

    public boolean verify(@NonNull String password) {
        return send("ver " + password);
    }

    public boolean registration(@NonNull String password_username) {
        return send("reg " + password_username);
    }

    @Nullable
    public String search() {
        writeStr("search");
        String read = readStr();
        return read.equals("1") ? null : read;
    }

    public boolean gameOver(@NonNull String result) {
        return send("gameov " + result);
    }

    public boolean setWall(@NonNull String coordinates) {
        return writeStr("wall " + coordinates);
    }

    @NonNull
    public String getWall() {
        try {
            if (inputStream.available() != 0)
                return readStr();
        } catch (IOException e) {
            e.printStackTrace();
            return "1";
        }
        return "3";
    }
}
