

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import android.provider.Settings.Secure;

public class ServerConnection {
    private static final String host = "localhost";
    private static final int port = 8080;
    private static final String androidId = Secure.ANDROID_ID;
    private Socket socket;
    public DataInputStream dataInputStream;
    public DataOutputStream dataOutputStream;

    ServerConnection(String pass) throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataOutputStream.writeChars("connect " + androidId);
    }
}
