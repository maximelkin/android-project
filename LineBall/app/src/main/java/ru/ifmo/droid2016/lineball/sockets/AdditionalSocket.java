package ru.ifmo.droid2016.lineball.sockets;

import java.io.IOException;

public class AdditionalSocket extends ClientSocket {
    public AdditionalSocket() throws IOException {
        super();
    }

    public String getTop(int skip, int limit) {
        writeStr("top " + skip + " " + limit);
        return readStr();
    }

    public boolean deleteUser(String androidId, String password) {
        return send("con " + androidId) && send("ver " + password) && send("del");
    }
}
