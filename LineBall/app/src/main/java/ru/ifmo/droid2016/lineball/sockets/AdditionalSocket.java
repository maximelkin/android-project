package ru.ifmo.droid2016.lineball.sockets;

import java.io.IOException;

import static ru.ifmo.droid2016.lineball.sockets.ProtocolMessages.*;

public class AdditionalSocket extends ClientSocket {
    public AdditionalSocket() throws IOException {
        super();
    }

    public String getTop(int skip, int limit) {
        writeStr(getTopCode + " " + skip + " " + limit);
        return readStr();
    }

    public boolean deleteUser(String androidId, String password) {
        return send(connectionCode + " " + androidId)
                && send(verificationCode + " " + password)
                && send(deleteUserCode);
    }
}
