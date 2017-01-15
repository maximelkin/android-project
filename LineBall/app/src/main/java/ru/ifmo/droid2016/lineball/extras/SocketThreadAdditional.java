package ru.ifmo.droid2016.lineball.extras;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import ru.ifmo.droid2016.lineball.sockets.AdditionalSocket;

import java.io.IOException;

import static ru.ifmo.droid2016.lineball.MessageCodes.*;


public class SocketThreadAdditional extends HandlerThread implements Handler.Callback {

    private Handler uiHandler;
    private Handler mReceiver;
    private AdditionalSocket socket;


    public SocketThreadAdditional(String name, Handler uiHandler) {
        super(name);
        this.uiHandler = uiHandler;
    }

    @Override
    public boolean handleMessage(Message message) {
        boolean result = true;
        switch (message.what) {
            case MSG_DELETE_USER:
                String[] msg1 = ((String) message.obj).split(" ");
                result = socket.deleteUser(msg1[0], msg1[1]);
                break;
            case MSG_GET_TOP:
                String msg2 = socket.getTop(message.arg1, message.arg2);
                if (msg2.equals("1")) {
                    result = false;
                    break;
                }
                uiHandler.sendMessage(Message.obtain(uiHandler, MSG_GET_TOP, msg2));
                break;
        }
        if (!result) {
            uiHandler.sendEmptyMessage(MSG_ERROR);
        }
        return true;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mReceiver = new Handler(Looper.myLooper(), this);
        try {
            socket = new AdditionalSocket();
            uiHandler.sendEmptyMessage(MSG_SOCKET_READY);
        } catch (IOException e) {
            e.printStackTrace();
            //send error message
            uiHandler.sendEmptyMessage(MSG_ERROR);
        }
    }

    public void getTop(int skip, int limit) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_GET_TOP, skip, limit));
    }

    public void deleteUser(String password, String androidId) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_DELETE_USER, password + " " + androidId));
    }
}
