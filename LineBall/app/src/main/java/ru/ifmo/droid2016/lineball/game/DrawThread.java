package ru.ifmo.droid2016.lineball.game;

import android.graphics.Canvas;
import android.graphics.Color;
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

    private final Board board;
    private final SurfaceHolder surfaceHolder;
    private static final int MSG_SET_NEW_WALL = 100;
    private Handler mReceiver;
    private final Handler uiHandler;

    private Timer timer;
    private static final long REDRAW_DELAY = 70;
    private static final long BEFORE_DRAW_DELAY = 10;

    DrawThread(SurfaceHolder surfaceHolder, Handler uiHandler, int maxX, int maxY, int color) {
        super("DrawThread", 10);
        this.surfaceHolder = surfaceHolder;
        this.board = new Board(maxX, maxY, color);
        this.uiHandler = uiHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mReceiver = new Handler(getLooper(), this);
        //start redrawing
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("redraw1", System.currentTimeMillis() + "");
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
                //   Log.e("redraw3", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
                surfaceHolder.unlockCanvasAndPost(c);
                //  Log.e("redraw4", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
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
        Log.e("wall in DrawThread", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
        board.setWall((String) msg.obj, Who.values()[msg.arg1]);
        return true;
    }


    void setWall(String coord, @NonNull Who who) {
        Message msg = Message.obtain(mReceiver, MSG_SET_NEW_WALL, who.ordinal(), 0, coord);
        mReceiver.sendMessageAtFrontOfQueue(msg);
    }
}
