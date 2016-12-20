package ru.ifmo.droid2016.lineball.Game;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import ru.ifmo.droid2016.lineball.Board.Who;
import ru.ifmo.droid2016.lineball.MainActivity;
import ru.ifmo.droid2016.lineball.Socket.SocketThread;

import java.util.Timer;
import java.util.TimerTask;

import static ru.ifmo.droid2016.lineball.Board.Who.RIVAL;
import static ru.ifmo.droid2016.lineball.Board.Who.THIS_USER;


public class Game extends AppCompatActivity implements View.OnTouchListener, SurfaceHolder.Callback, Handler.Callback {
    public static final long REDRAW_DELAY = 50;
    private static final long BEFORE_DRAW_DELAY = 20;
    public static final int MSG_ERROR = 301;
    public static final int MSG_END = 302;
    private static final String TAG = "GAME";
    public static final int MSG_WALL = 300;
    private String coord = "";
    private DrawThread board;
    private Handler uiHandler = new Handler(Looper.getMainLooper(), this);
    private SocketThread socketThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setOnTouchListener(this);
        surfaceView.getHolder().addCallback(this);
        setContentView(surfaceView);

        //prohibit rotate
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();

        //socket should be created before!!!
        socketThread = ((SocketThread) getThreadByName("socket"));

        assert socketThread != null;
        socketThread.setUiHandler(uiHandler);

        //start redrawing
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                board.redraw();
            }
        }, REDRAW_DELAY, BEFORE_DRAW_DELAY);
        //start getting walls
        socketThread.getWall();

    }

    public boolean onTouch(View view, MotionEvent event) {

        //TODO convert coordinates
        double x = event.getX();
        double y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                coord += x + " " + y + " ";
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                coord += x + " " + y;
                setWall(coord);
                coord = "";
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }


    private void setWall(String coordinates) {
        board.setWall(coordinates, THIS_USER);
        socketThread.setWall(coordinates);
    }

    private void gameFinish(Who winner) {
        //TODO notify user about win/loose

        socketThread.gameOver((winner == THIS_USER) ? "win" : "loose");
        //giving control to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        board = new DrawThread(surfaceHolder, uiHandler);
        board.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //rotate screen? seriously?
        Log.e(TAG, "SURFACE CHANGED");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        board.quit();
        board = null;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            //message from board
            case MSG_END:
                gameFinish((message.arg1 == 0) ? THIS_USER : RIVAL);
                return true;
            //messages from socket
            case MSG_ERROR:
                //cant send
                gameFinish(RIVAL);
                return true;
            case MSG_WALL:
                board.setWall((String) message.obj, RIVAL);
                return true;
        }
        return false;
    }

    private Thread getThreadByName(String threadName) {
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            if (t.getName().equals(threadName)) return t;
        }
        return null;
    }

}
