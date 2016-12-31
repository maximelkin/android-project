package ru.ifmo.droid2016.lineball.Game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import ru.ifmo.droid2016.lineball.Board.Board;
import ru.ifmo.droid2016.lineball.Board.Who;

import java.util.Timer;
import java.util.TimerTask;

import static ru.ifmo.droid2016.lineball.Game.Game.MSG_GAME_END;

class DrawThread extends HandlerThread implements Handler.Callback {

    private Board board;
    private SurfaceHolder surfaceHolder;
    private static final int MSG_SET_NEW_WALL = 100;
    private static final int MSG_REDRAW_BOARD = 101;
    private Handler mReceiver;
    private Handler uiHandler;

    private Timer timer;
    private static final long REDRAW_DELAY = 70;
    private static final long BEFORE_DRAW_DELAY = 10;

    DrawThread(SurfaceHolder surfaceHolder, Handler uiHandler, int maxX, int maxY, int color) {
        super("DrawThread", Process.THREAD_PRIORITY_DISPLAY);
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
                mReceiver.sendEmptyMessage(MSG_REDRAW_BOARD);
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
        switch (msg.what) {
            //add wall
            case MSG_SET_NEW_WALL:
                Log.e("wall in DrawThread", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
                board.setWall((String) msg.obj, Who.values()[msg.arg1]);
                break;
            //next tick
            case MSG_REDRAW_BOARD:
                Who checked = board.check();
                Canvas c = surfaceHolder.lockCanvas();
                if (c == null)
                    break;
                c.drawColor(Color.WHITE);
                board.drawBoard(c);
                surfaceHolder.unlockCanvasAndPost(c);
                if (checked != null) {
                    Message message = Message.obtain(uiHandler, MSG_GAME_END, checked.ordinal(), 0);
                    uiHandler.sendMessage(message);
                }
                break;
        }
        return true;
    }


    void setWall(String coord, @NonNull Who who) {
        Message msg = Message.obtain(mReceiver, MSG_SET_NEW_WALL, who.ordinal(), 0, coord);
        mReceiver.sendMessageAtFrontOfQueue(msg);
    }
}
