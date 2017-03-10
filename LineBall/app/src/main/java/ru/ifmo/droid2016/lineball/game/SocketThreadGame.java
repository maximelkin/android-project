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

    private Handler uiThreadReceiver, mainThreadReceiver;
    private GameSocket socket;


    private final static int CHECK_DELAY = 20;
    private final static int socketPriority = 7;
    private String androidId;

    public SocketThreadGame(String name, Handler uiThreadReceiver, String androidId) throws IOException {
        super(name, socketPriority);
        this.uiThreadReceiver = uiThreadReceiver;
        this.androidId = androidId;
        Log.e("socket thread", "new socket thread");
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        //init
        mainThreadReceiver = new Handler(Looper.myLooper(), this);
        try {
            socket = new GameSocket(androidId);
            uiThreadReceiver.sendEmptyMessage(MSG_SOCKET_READY);
        } catch (IOException e) {
            e.printStackTrace();
            uiThreadReceiver.sendEmptyMessage(MSG_ERROR);
        }
    }

    @Override
    public boolean quit() {
        // Clear all messages before dying
        Log.e("socket thread", "Quit");
        mainThreadReceiver.removeCallbacksAndMessages(null);
        new Exception("thread dead").printStackTrace();
        return super.quit();
    }

    @Override
    public boolean handleMessage(Message message) {
        boolean result = true;
        switch (message.what) {
            case MSG_VERIFY_USER:
                if (!socket.verify((String) message.obj))
                    uiThreadReceiver.sendEmptyMessage(MSG_VERIFYING_ERROR);
                else
                    uiThreadReceiver.sendEmptyMessage(MSG_USER_VERIFIED);

                break;
            case MSG_REGISTRATION:
                result = socket.registration((String) message.obj);
                if (result)
                    uiThreadReceiver.sendEmptyMessage(MSG_USER_VERIFIED);
                break;

            //WARNING! IT FREEZE THIS THREAD
            case MSG_SEARCH:
                String name = socket.search();
                result = (name != null);
                if (result) {
                    uiThreadReceiver.sendMessage(Message.obtain(uiThreadReceiver, MSG_START_GAME, name));
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
                    //internet troubles
                    result = false;
                    break;
                }
                if (coordinates.equals("2")) {
                    //rival leave
                    uiThreadReceiver.sendEmptyMessage(MSG_GAME_END);
                    break;
                }
                mainThreadReceiver.sendEmptyMessageDelayed(MessageCodes.MSG_GET_WALL_FROM_RIVAL, CHECK_DELAY);
                if (coordinates.equals("3")) {
                    //no walls
                    break;
                }
                Log.e("from rival in Socket", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
                uiThreadReceiver.sendMessage(Message.obtain(uiThreadReceiver, MSG_SET_WALL_FROM_RIVAL, coordinates));
                break;
        }
        if (!result)
            uiThreadReceiver.sendEmptyMessage(MSG_ERROR);
        return true;
    }

    void setUiThreadReceiver(Handler handler) {
        uiThreadReceiver = handler;
    }

    public void verify(@NonNull String password) {
        mainThreadReceiver.sendMessage(Message.obtain(mainThreadReceiver, MSG_VERIFY_USER, password));
    }

    public void registration(String password) {
        mainThreadReceiver.sendMessage(Message.obtain(mainThreadReceiver, MSG_REGISTRATION, password));
    }

    public void search() {
        mainThreadReceiver.sendEmptyMessage(MSG_SEARCH);
    }

    void gameOver(@NonNull String result) {
        mainThreadReceiver.sendMessageAtFrontOfQueue(Message.obtain(mainThreadReceiver, MSG_GAME_END, result));
    }

    void setWall(@NonNull String coordinates) {
        mainThreadReceiver.sendMessage(Message.obtain(mainThreadReceiver, MSG_SEND_WALL_TO_RIVAL, coordinates));
    }

    //only one run!
    void getWall() {
        mainThreadReceiver.sendEmptyMessage(MessageCodes.MSG_GET_WALL_FROM_RIVAL);
    }

    @Nullable
    static Thread getThreadByName(@NonNull String threadName) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            Log.e("thread search", thread.getName());
            if (thread.getName().equals(threadName)) return thread;
        }
        Log.e("GAME", "getting thread return null");
        return null;
    }

}
