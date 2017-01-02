package ru.ifmo.droid2016.lineball.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
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
import ru.ifmo.droid2016.lineball.board.Who;
import ru.ifmo.droid2016.lineball.R;
import ru.ifmo.droid2016.lineball.socket.SocketThread;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static ru.ifmo.droid2016.lineball.board.Board.*;
import static ru.ifmo.droid2016.lineball.board.Who.RIVAL;
import static ru.ifmo.droid2016.lineball.board.Who.THIS_USER;
import static ru.ifmo.droid2016.lineball.socket.SocketThread.MSG_ERROR;
import static ru.ifmo.droid2016.lineball.socket.SocketThread.getThreadByName;


public class Game extends AppCompatActivity implements View.OnTouchListener, SurfaceHolder.Callback, Handler.Callback {

    public static final int MSG_GAME_END = 302;
    private static final String TAG = "GAME";
    public static final int MSG_SET_WALL_FROM_RIVAL = 300;
    private String coord = "";
    private DrawThread board;
    private Handler uiHandler = new Handler(Looper.getMainLooper(), this);
    private SocketThread socketThread;
    private SurfaceView surfaceView;
    private int color;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        color = PreferenceManager.getDefaultSharedPreferences(this).getInt("color", 0);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
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
        setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        //socket should be created before!!!
        socketThread = ((SocketThread) getThreadByName("socket"));

        if (socketThread == null) {
            gameFinish(RIVAL);
        }

        socketThread.setUiHandler(uiHandler);

        //start getting walls
        socketThread.getWall();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (socketThread != null)
            socketThread.quit();
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
        Log.e("wall draw by user", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
        socketThread.setWall(coordinates);
        board.setWall(coordinates, THIS_USER);
    }

    private void gameFinish(Who winner) {
        Log.e("game finish", "start");
        if (socketThread != null) {
            socketThread.gameOver((winner == THIS_USER) ? "win" : "loose");
        }
        int toastTextId = (winner == THIS_USER) ? R.string.this_user_win : R.string.this_user_loose;
        Toast.makeText(Game.this, getString(toastTextId), Toast.LENGTH_SHORT)
                .show();
        surfaceView.setOnTouchListener(null);
        coord = null;
        uiHandler.removeCallbacksAndMessages(null);
        board.quit();
        Log.e("game finish", "end");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfaceHolder.setFormat(PixelFormat.RGB_565);
        Canvas canvas = surfaceHolder.lockCanvas();
        board = new DrawThread(surfaceHolder, uiHandler, canvas.getWidth(), canvas.getHeight(), color);
        surfaceHolder.unlockCanvasAndPost(canvas);
        board.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.e(TAG, "SURFACE CHANGED");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        board.quit();
        uiHandler.removeCallbacksAndMessages(null);
        surfaceView.setOnTouchListener(null);
    }

    @Override
    public boolean handleMessage(Message message) {
        if (board == null)
            return true;
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
                Log.e("wall from rival in Game", String.format("%.3f", (double) System.currentTimeMillis() / 1000));
                board.setWall((String) message.obj, RIVAL);
                return true;
        }
        return false;
    }


}
