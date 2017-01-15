package ru.ifmo.droid2016.lineball.game;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import ru.ifmo.droid2016.lineball.MessageCodes;
import ru.ifmo.droid2016.lineball.sockets.GameSocket;

import java.io.IOException;

import static ru.ifmo.droid2016.lineball.MessageCodes.*;

public class SocketThreadGame extends HandlerThread implements Handler.Callback {

    private Handler uiHandler;
    private GameSocket socket;
    private Handler mReceiver;


    private final static int CHECK_DELAY = 20;
    private final static int socketPriority = 7;
    private String androidId;

    public SocketThreadGame(String name, Handler uiHandler, String androidId) throws IOException {
        super(name, socketPriority);
        this.uiHandler = uiHandler;
        this.androidId = androidId;
        Log.e("socket thread", "new socket thread");
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        mReceiver = new Handler(Looper.myLooper(), this);
        try {
            socket = new GameSocket(androidId);
            uiHandler.sendEmptyMessage(MSG_SOCKET_READY);
        } catch (IOException e) {
            e.printStackTrace();
            uiHandler.sendEmptyMessage(MSG_ERROR);
        }
    }

    @Override
    public boolean quit() {
        // Clear all messages before dying
        Log.e("socket thread", "Quit");
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
                quit();
                break;
            case MSG_SEND_WALL_TO_RIVAL:
                result = (socket.setWall((String) message.obj));
                break;
            case MessageCodes.MSG_GET_WALL_FROM_RIVAL:
                String coordinates = socket.getWall();
                if (coordinates.equals("1")) {
                    result = false;
                    break;
                }
                if (coordinates.equals("2")) {
                    uiHandler.sendEmptyMessage(MSG_GAME_END);
                    break;
                }
                mReceiver.sendEmptyMessageDelayed(MessageCodes.MSG_GET_WALL_FROM_RIVAL, CHECK_DELAY);
                if (coordinates.equals("3")) {
                    break;
                }
                Log.e("from rival in Socket", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
                uiHandler.sendMessage(Message.obtain(uiHandler, MSG_SET_WALL_FROM_RIVAL, coordinates));
                break;
        }
        if (!result)
            uiHandler.sendEmptyMessage(MSG_ERROR);
        return true;
    }

    public void setUiHandler(Handler handler) {
        uiHandler = handler;
    }

    public void verify(@NonNull String password) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_VERIFY_USER, password));
    }

    public void registration(String password) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_REGISTRATION, password));
    }

    public void search() {
        mReceiver.sendEmptyMessage(MSG_SEARCH);
    }

    public void gameOver(@NonNull String result) {
        mReceiver.sendMessageAtFrontOfQueue(Message.obtain(mReceiver, MSG_GAME_END, result));
    }

    public void setWall(@NonNull String coordinates) {
        mReceiver.sendMessage(Message.obtain(mReceiver, MSG_SEND_WALL_TO_RIVAL, coordinates));
    }

    //only one run!
    public void getWall() {
        mReceiver.sendEmptyMessage(MessageCodes.MSG_GET_WALL_FROM_RIVAL);
    }

    @Nullable
    public static Thread getThreadByName(@NonNull String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        Log.e("GAME", "getting thread return null");
        return null;
    }

}
