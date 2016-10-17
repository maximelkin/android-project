//import android.provider.Settings.Secure;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;

public class ServerConnection implements ServerConnectionImpl {
    private static final String host = "localhost";
    private static final int port = 8080;
    private static final String androidId = "322"; //Secure.ANDROID_ID;
    private Socket socket;
    public InputStream inputStream;//???
    private DataOutputStream dataOutputStream;

    public ServerConnection() throws IOException {
        socket = new Socket(host, port);
        socket.setTcpNoDelay(true);
        inputStream = socket.getInputStream();
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        readStr();
        if (!send("con " + androidId)) {
            throw new ConnectException();
        }
    }

    private String readStr() throws IOException {
        byte[] b = new byte[20];
        int len = inputStream.read(b);
        return new String(b, "UTF8").substring(0, len);
    }

    private boolean send(String action) {
        try {
            dataOutputStream.writeBytes(action + "#");//# - delimiter
            dataOutputStream.flush();
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


    public String search() throws IOException, IllegalAccessException {
        dataOutputStream.writeBytes("search#");
        dataOutputStream.flush();
        if (readStr().equals("1"))
            throw new IllegalAccessException();
        return readStr();
    }

    public boolean gameOver(String result) {
        return send("gameov " + result);
    }

    public boolean setWall(String coordinates) {
        return send(coordinates);
    }

    public String getWall() throws IOException {
        return readStr();
    }
}
