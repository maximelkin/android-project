package ru.ifmo.droid2016.lineball.sockets;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.ConnectException;

import static ru.ifmo.droid2016.lineball.sockets.ProtocolMessages.*;

public class GameSocket extends ClientSocket {

    public GameSocket(@NonNull String androidId) throws IOException {
        super();
        if (!send(connectionCode + " " + androidId)) {
            throw new ConnectException();
        }
    }

    public boolean verify(@NonNull String password) {
        return send(verificationCode + " " + password);
    }

    public boolean registration(@NonNull String password_username) {
        return send(registrationCode + " " + password_username);
    }

    @Nullable
    public String search() {
        writeStr(searchCode);
        String read = readStr();
        return read.equals("1") ? null : read;
    }

    public boolean gameOver(@NonNull String result) {
        return send(gameOverCode + " " + result);
    }

    public boolean setWall(@NonNull String coordinates) {
        return writeStr(sendWallCode + " " + coordinates);
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
