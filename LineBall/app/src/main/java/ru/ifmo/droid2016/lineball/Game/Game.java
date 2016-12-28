package ru.ifmo.droid2016.lineball.Game;

import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import ru.ifmo.droid2016.lineball.Board.Who;
import ru.ifmo.droid2016.lineball.R;
import ru.ifmo.droid2016.lineball.Socket.SocketThread;

import java.util.Timer;
import java.util.TimerTask;

import static ru.ifmo.droid2016.lineball.Board.Board.*;
import static ru.ifmo.droid2016.lineball.Board.Who.RIVAL;
import static ru.ifmo.droid2016.lineball.Board.Who.THIS_USER;
import static ru.ifmo.droid2016.lineball.Socket.SocketThread.MSG_ERROR;
import static ru.ifmo.droid2016.lineball.Socket.SocketThread.getThreadByName;


public class Game extends AppCompatActivity implements View.OnTouchListener, SurfaceHolder.Callback, Handler.Callback {
    public static final long REDRAW_DELAY = 50;
    private static final long BEFORE_DRAW_DELAY = 50;
    public static final int MSG_GAME_END = 302;
    private static final String TAG = "GAME";
    public static final int MSG_SET_WALL_FROM_RIVAL = 300;
    private String coord = "";
    private DrawThread board;
    private Handler uiHandler = new Handler(Looper.getMainLooper(), this);
    private SocketThread socketThread;
    private Timer timer;
    private int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        color = PreferenceManager.getDefaultSharedPreferences(this).getInt("color", 0);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(this);
        surfaceView.getHolder().addCallback(this);
        String rivalName = getIntent().getStringExtra("rival name");
        String thisUserName = PreferenceManager.getDefaultSharedPreferences(this).getString("name", "anonymous");

        TextView leftTextField = (TextView) findViewById(R.id.left_field);
        TextView rightTextField = (TextView) findViewById(R.id.right_field);
        leftTextField.setText(thisUserName);
        rightTextField.setText(rivalName);

        leftTextField.setTextColor((color == 0) ? Color.BLUE : Color.RED);
        rightTextField.setTextColor((color == 0) ? Color.RED : Color.BLUE);
        //prohibit rotate
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        //socket should be created before!!!
        socketThread = ((SocketThread) getThreadByName("socket"));

        if (socketThread == null){
            gameFinish(RIVAL);
        }

        socketThread.setUiHandler(uiHandler);

        //start getting walls
        socketThread.getWall();

    }

    public boolean onTouch(View view, MotionEvent event) {

        double x = event.getX() * (maxX / maxXLocal);
        double y = event.getY() * (maxY / maxYLocal);

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
        timer.cancel();
        int toastTextId = (winner == THIS_USER) ? R.string.this_user_win : R.string.this_user_loose;
        Toast.makeText(Game.this, getString(toastTextId), Toast.LENGTH_SHORT)
                .show();
        socketThread.gameOver((winner == THIS_USER) ? "win" : "loose");
        board.quit();
        if (socketThread != null) {
            socketThread.quit();
            socketThread = null;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Canvas canvas = surfaceHolder.lockCanvas();
        board = new DrawThread(surfaceHolder, uiHandler, canvas.getWidth(), canvas.getHeight(), color);
        surfaceHolder.unlockCanvasAndPost(canvas);
        board.start();
        //start redrawing
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (board != null) board.redraw();
            }
        }, BEFORE_DRAW_DELAY, REDRAW_DELAY);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG, "SURFACE CHANGED");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        board.quit();
        uiHandler.removeCallbacksAndMessages(null);
        board = null;
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            //message from board
            case MSG_GAME_END:
                gameFinish(Who.values()[message.arg1]);
                return true;
            //messages from socket
            case MSG_ERROR:
                //cant send
                Log.e("GAME/GAME", "SEND ERROR");
                gameFinish(RIVAL);
                return true;
            case MSG_SET_WALL_FROM_RIVAL:
                board.setWall((String) message.obj, RIVAL);
                return true;
        }
        return false;
    }


}
