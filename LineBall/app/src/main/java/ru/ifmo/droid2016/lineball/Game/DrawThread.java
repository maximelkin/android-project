package ru.ifmo.droid2016.lineball.Game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.SurfaceHolder;
import ru.ifmo.droid2016.lineball.Board.Board;
import ru.ifmo.droid2016.lineball.Board.Who;

import static ru.ifmo.droid2016.lineball.Game.Game.MSG_END;

class DrawThread extends HandlerThread implements Handler.Callback {

    private Board board;
    private SurfaceHolder surfaceHolder;
    private static final int MSG_ADD = 100;
    private static final int MSG_UPD = 101;
    private Handler mReceiver;
    private Handler uiHandler;


    DrawThread(SurfaceHolder surfaceHolder, Handler uiHandler) {
        super("DrawThread");
        this.surfaceHolder = surfaceHolder;
        this.board = new Board();
        this.uiHandler = uiHandler;
    }

    @Override
    protected void onLooperPrepared() {
        mReceiver = new Handler(getLooper(), this);
        mReceiver.sendEmptyMessage(MSG_UPD);
    }

    @Override
    public boolean quit() {
        // Clear all messages before dying
        mReceiver.removeCallbacksAndMessages(null);
        return super.quit();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ADD:

                board.setWall((String) msg.obj, Who.values()[msg.arg1]);
                break;
            case MSG_UPD:
                Who checked = board.check();
                Canvas c = surfaceHolder.lockCanvas();
                if (c == null)
                    break;
                c.drawColor(Color.WHITE);
                board.drawBoard(c);
                surfaceHolder.unlockCanvasAndPost(c);
                if (checked != null) {
                    Message message = Message.obtain(uiHandler, MSG_END, checked.ordinal(), 0);
                    uiHandler.sendMessage(message);
                }
                Log.e("draw thread", "update");
                break;
        }
        return true;
    }


    void setWall(String coord, @NonNull Who who) {
        Message msg = Message.obtain(mReceiver, MSG_ADD, who.ordinal(), 0, coord);
        mReceiver.sendMessageAtFrontOfQueue(msg);
    }

    void redraw() {
        mReceiver.sendEmptyMessage(MSG_UPD);
    }
}
