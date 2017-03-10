package ru.ifmo.droid2016.lineball.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import ru.ifmo.droid2016.lineball.board.Board;
import ru.ifmo.droid2016.lineball.board.Who;

import java.util.Timer;
import java.util.TimerTask;

import static ru.ifmo.droid2016.lineball.MessageCodes.MSG_GAME_END;

class DrawThread extends HandlerThread implements Handler.Callback {

    private final int color;
    private final boolean isGameMaster;
    private Board board;
    private final SurfaceHolder surfaceHolder;
    private Handler mainThreadReceiver;
    private final Handler uiThreadReceiver;

    private Timer timer;
    private static final long REDRAW_DELAY = 20;
    private static final long BEFORE_DRAW_DELAY = 100;
    private static final int drawThreadPriority = 10;

    DrawThread(SurfaceHolder surfaceHolder, Handler uiThreadReceiver, int color, boolean isGameMaster) {
        super("DrawThread", drawThreadPriority);
        this.surfaceHolder = surfaceHolder;
        this.uiThreadReceiver = uiThreadReceiver;
        this.color = color;
        this.isGameMaster = isGameMaster;
    }

    @Override
    protected void onLooperPrepared() {
        mainThreadReceiver = new Handler(getLooper(), this);
        //start redrawing
        Rect rect = surfaceHolder.getSurfaceFrame();
        board = new Board(rect.width(), rect.height(), color, isGameMaster);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Log.e("redraw1", System.currentTimeMillis() + "");
                final Who checked = board.check();
                if (checked != null) {
                    //someone won
                    Message message = Message.obtain(uiThreadReceiver, MSG_GAME_END, checked.ordinal(), 0);
                    uiThreadReceiver.sendMessage(message);
                    cancel();
                }
                final Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null)
                    return;
                canvas.drawColor(Color.WHITE);
                board.drawBoard(canvas);
                //Log.e("redraw2", System.currentTimeMillis() + "");
                surfaceHolder.unlockCanvasAndPost(canvas);
                //Log.e("redraw3", System.currentTimeMillis() + "");
            }
        }, BEFORE_DRAW_DELAY, REDRAW_DELAY);
    }

    @Override
    public boolean quit() {
        timer.cancel();
        // Clear all messages before dying
        mainThreadReceiver.removeCallbacksAndMessages(null);
        return super.quit();
    }

    @Override
    public boolean handleMessage(Message msg) {
        //get wall
        Log.e("wall in DrawThread", System.currentTimeMillis() + "");
        board.setWall((String) msg.obj, Who.values()[msg.arg1]);
        return true;
    }


    void setWall(@NonNull String coord, @NonNull Who who) {
        Message msg = Message.obtain(mainThreadReceiver, 0, who.ordinal(), 0, coord);
        mainThreadReceiver.sendMessageAtFrontOfQueue(msg);
    }
}
