package ru.ifmo.droid2016.lineball.Socket;

import android.os.*;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import static ru.ifmo.droid2016.lineball.Game.Game.MSG_GAME_END;
import static ru.ifmo.droid2016.lineball.Game.Game.MSG_SET_WALL_FROM_RIVAL;
import static ru.ifmo.droid2016.lineball.Game.Game.REDRAW_DELAY;

public class SocketThread extends HandlerThread implements Handler.Callback {

    private Handler uiHandler;
    private ServerConnection socket;
    private Handler mReceiver;
    private final static int MSG_VERIFY_USER = 201;
    private final static int MSG_REGISTRATION = 202;
    private final static int MSG_SEARCH = 204;
    public final static int MSG_ERROR = 205;
    private final static int MSG_SEND_WALL_TO_RIVAL = 206;
    private final static int MSG_GET_WALL_FROM_RIVAL = 207;
    public final static int MSG_START_GAME = 208;
    public final static int MSG_SOCKET_READY = 209;
    public final static int MSG_USER_VERIFIED = 503;
    public final static int MSG_VERIFYING_ERROR = 502;
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
            uiHandler.sendEmptyMessage(MSG_SOCKET_READY);
        } catch (IOException e) {
            e.printStackTrace();
            uiHandler.sendEmptyMessage(MSG_ERROR);
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
            case MSG_VERIFY_USER:
                if (!socket.verify((String) message.obj))
                    uiHandler.sendEmptyMessage(MSG_VERIFYING_ERROR);
                else
                    uiHandler.sendEmptyMessage(MSG_USER_VERIFIED);
                break;
            case MSG_REGISTRATION:
                result = socket.registration((String) message.obj);
                if (result)
                    uiHandler.sendEmptyMessage(MSG_USER_VERIFIED);
                break;

            //WARNING! IT FREEZE THIS THREAD
            case MSG_SEARCH:
                String name = socket.search();
                result = name != null;
                if (result) {
                    uiHandler.sendMessage(Message.obtain(uiHandler, MSG_START_GAME, name));
                }
                break;
            case MSG_GAME_END:
                socket.gameOver((String) message.obj);
                break;
            case MSG_SEND_WALL_TO_RIVAL:
                result = (socket.setWall((String) message.obj));
                break;
            case MSG_GET_WALL_FROM_RIVAL:
                try {
                    String coordinates = socket.getWall();
                    if (coordinates != null)
                        uiHandler.sendMessage(Message.obtain(uiHandler, MSG_SET_WALL_FROM_RIVAL, coordinates));
                    mReceiver.sendEmptyMessageDelayed(MSG_GET_WALL_FROM_RIVAL, REDRAW_DELAY);
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
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_VERIFY_USER, password));
    }

    public void registration(String password) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_REGISTRATION, password));
    }

    public void search() {
        mReceiver.sendEmptyMessage(MSG_SEARCH);
    }

    public void gameOver(String result) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_GAME_END, result));
    }

    public void setWall(String coordinates) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_SEND_WALL_TO_RIVAL, coordinates));
    }

    //only one run!
    public void getWall() {
        mReceiver.sendEmptyMessage(MSG_GET_WALL_FROM_RIVAL);
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
