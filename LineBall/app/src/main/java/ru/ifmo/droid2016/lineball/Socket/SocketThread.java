package ru.ifmo.droid2016.lineball.Socket;

import android.os.*;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import static ru.ifmo.droid2016.lineball.Game.Game.MSG_END;
import static ru.ifmo.droid2016.lineball.Game.Game.MSG_WALL;
import static ru.ifmo.droid2016.lineball.Game.Game.REDRAW_DELAY;

public class SocketThread extends HandlerThread implements Handler.Callback {

    private Handler uiHandler;
    private ServerConnection socket;
    private Handler mReceiver;
    private final static int MSG_VERIFY = 201;
    private final static int MSG_REGISTRATION = 202;
    private final static int MSG_SEARCH = 204;
    public final static int MSG_ERROR = 205;
    private final static int MSG_SET = 206;
    private final static int MSG_GET = 207;
    public final static int MSG_START = 208;
    public final static int MSG_READY = 209;
    public final static int MSG_REG_ERR = 502;
    private String androidId;

    public SocketThread(String name, Handler uiHandler, String androidId) throws IOException {
        super(name, Process.THREAD_PRIORITY_MORE_FAVORABLE);
        this.uiHandler = uiHandler;
        this.androidId = androidId;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mReceiver = new Handler(Looper.myLooper(), this);
        try {
            socket = new ServerConnection(androidId);
            uiHandler.sendEmptyMessage(MSG_READY);
        } catch (IOException e) {
            uiHandler.sendEmptyMessage(MSG_ERROR);
            e.printStackTrace();
        }
    }

    @Override
    public boolean quit() {
        // Clear all messages before dying
        mReceiver.removeCallbacksAndMessages(null);
        return super.quit();
    }

    @Override
    public boolean handleMessage(Message message) {
        boolean result = true;
        switch (message.what) {
            case MSG_VERIFY:
                if (!socket.verify((String) message.obj)) {
                    uiHandler.sendEmptyMessage(MSG_REG_ERR);
                }
                break;
            case MSG_REGISTRATION:
                result = socket.registration((String) message.obj);
                break;

            //WARNING! IT FREEZE THIS THREAD
            case MSG_SEARCH:
                String name = socket.search();
                result = (name != null);
                if (result) {
                    uiHandler.sendEmptyMessage(MSG_START);
                    uiHandler.sendMessage(Message.obtain(uiHandler, MSG_START, name));
                }
                break;
            case MSG_END:
                socket.gameOver((String) message.obj);
                break;
            case MSG_SET:
                result = (!socket.setWall((String) message.obj));
                break;
            case MSG_GET:
                try {
                    String coordinates = socket.getWall();
                    if (coordinates != null)
                        uiHandler.sendMessage(Message.obtain(uiHandler, MSG_WALL, coordinates));
                    mReceiver.sendEmptyMessageDelayed(MSG_GET, REDRAW_DELAY);
                } catch (IOException e) {
                    result = false;
                    e.printStackTrace();
                }
                break;
        }
        if (!result)
            uiHandler.sendEmptyMessage(MSG_ERROR);
        return true;
    }

    public void setUiHandler(Handler handler) {
        uiHandler = handler;
    }

    public void verify(String password) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_VERIFY, password));
    }

    public void registration(String password) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_REGISTRATION, password));
    }

    public void search() {
        mReceiver.sendEmptyMessage(MSG_SEARCH);
    }

    public void gameOver(String result) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_END, result));
    }

    public void setWall(String coordinates) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_SET, coordinates));
    }

    //only one run!
    public void getWall() {
        mReceiver.sendEmptyMessage(MSG_GET);
    }

    @Nullable
    public static Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        Log.e("GAME", "getting thread return null");
        return null;
    }

}
