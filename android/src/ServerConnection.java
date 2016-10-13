import android.provider.Settings.Secure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class ServerConnection implements ServerConnectionImpl {
    private static final String host = "localhost";
    private static final int port = 8080;
    private static final String androidId = Secure.ANDROID_ID;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public ServerConnection() throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        if (!send("connect " + androidId)) {
            throw new ConnectException();
        }
    }

    private boolean send(String action) throws IOException {
        dataOutputStream.writeBytes(action);
        dataOutputStream.flush();
        int response = dataInputStream.readInt();
        return (response == 0);
    }

    public boolean verify(String password) throws IOException {
        return send("ver " + password);
    }

    public boolean registration(String password) throws IOException {
        return send("reg " + password);
    }

    public boolean resetUser() throws IOException {
        return send("reset");
    }


    public String search() throws Exception {
        dataOutputStream.writeBytes("search");
        dataOutputStream.flush();
        if (dataInputStream.readInt() == 1)
            throw new Exception();
        return dataInputStream.readUTF();
    }

    public boolean gameOver(String result) throws IOException {
        return send("gameov " + result);
    }
}
