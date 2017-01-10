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

import static ru.ifmo.droid2016.lineball.game.Game.MSG_GAME_END;

class DrawThread extends HandlerThread implements Handler.Callback {

    private final int color;
    private Board board;
    private final SurfaceHolder surfaceHolder;
    private Handler mReceiver;
    private final Handler uiHandler;

    private Timer timer;
    private static final long REDRAW_DELAY = 60;
    private static final long BEFORE_DRAW_DELAY = 100;

    DrawThread(SurfaceHolder surfaceHolder, Handler uiHandler, int color) {
        super("DrawThread", 10);
        this.surfaceHolder = surfaceHolder;
        this.uiHandler = uiHandler;
        this.color = color;
    }

    @Override
    protected void onLooperPrepared() {
        mReceiver = new Handler(getLooper(), this);
        //start redrawing
        Rect rect = surfaceHolder.getSurfaceFrame();
        board = new Board(rect.width(), rect.height(), color);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //Log.e("redraw1", System.currentTimeMillis() + "");
                final Who checked = board.check();
                if (checked != null) {
                    Message message = Message.obtain(uiHandler, MSG_GAME_END, checked.ordinal(), 0);
                    uiHandler.sendMessage(message);
                    cancel();
                }
                final Canvas c = surfaceHolder.lockCanvas();
                if (c == null)
                    return;
                c.drawColor(Color.WHITE);
                synchronized (board) {
                    board.drawBoard(c);
                }
                surfaceHolder.unlockCanvasAndPost(c);
            }
        }, BEFORE_DRAW_DELAY, REDRAW_DELAY);
    }

    @Override
    public boolean quit() {
        timer.cancel();
        // Clear all messages before dying
        mReceiver.removeCallbacksAndMessages(null);
        return super.quit();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.e("wall in DrawThread", System.currentTimeMillis() + "");
        board.setWall((String) msg.obj, Who.values()[msg.arg1]);
        return true;
    }


    void setWall(@NonNull String coord, @NonNull Who who) {
        Message msg = Message.obtain(mReceiver, 0, who.ordinal(), 0, coord);
        mReceiver.sendMessageAtFrontOfQueue(msg);
    }
}
