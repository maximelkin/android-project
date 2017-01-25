package ru.ifmo.droid2016.lineball.game;

import android.graphics.Color;
import android.os.*;
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
import ru.ifmo.droid2016.lineball.R;
import ru.ifmo.droid2016.lineball.board.Who;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static ru.ifmo.droid2016.lineball.MessageCodes.MSG_ERROR;
import static ru.ifmo.droid2016.lineball.MessageCodes.MSG_GAME_END;
import static ru.ifmo.droid2016.lineball.MessageCodes.MSG_SET_WALL_FROM_RIVAL;
import static ru.ifmo.droid2016.lineball.board.Board.*;
import static ru.ifmo.droid2016.lineball.board.Who.RIVAL;
import static ru.ifmo.droid2016.lineball.board.Who.THIS_USER;
import static ru.ifmo.droid2016.lineball.game.SocketThreadGame.getThreadByName;


public class Game extends AppCompatActivity implements View.OnTouchListener, SurfaceHolder.Callback, Handler.Callback {

    private static final String TAG = "GAME";
    private String coord = "";
    private DrawThread board;
    private Handler uiHandler = new Handler(Looper.getMainLooper(), this);
    private SocketThreadGame socketThread;
    private SurfaceView surfaceView;
    private int color;
    private boolean isGameMaster;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        color = PreferenceManager.getDefaultSharedPreferences(this).getInt("color", 0);
        String[] extras = getIntent().getStringExtra("rival name").split(" ");
        String rivalName = extras[0];
        isGameMaster = extras[1].equals("1");

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceView.setOnTouchListener(this);
        surfaceView.getHolder().addCallback(this);
        surfaceView.setDrawingCacheEnabled(false);
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
        socketThread = ((SocketThreadGame) getThreadByName("socket"));

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

        //converting from local to global coord
        double x = event.getX() * (maxX / maxXLocal);
        double y = event.getY() * (maxY / maxYLocal);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //position of touch
                coord += x + " " + y + " ";
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                //position of stop touching
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
        //show toast win win/loose message
        int toastTextId = (winner == THIS_USER) ? R.string.this_user_win : R.string.this_user_loose;
        Toast.makeText(Game.this, getString(toastTextId), Toast.LENGTH_SHORT)
                .show();
        //disable touch listener
        surfaceView.setOnTouchListener(null);
        coord = null;
        //flush message queue
        uiHandler.removeCallbacksAndMessages(null);
        if (board != null) {
            board.quit();
        }
        Log.e("game finish", "end");
        //stop after 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        board = new DrawThread(surfaceHolder, uiHandler, color, isGameMaster);
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
            return false;
        switch (message.what) {
            //message from board
            case MSG_GAME_END:
                gameFinish(Who.values()[message.arg1]);
                return true;
            //messages from socket
            case MSG_ERROR:
                //cant send
                Log.e("game/Game", "SEND ERROR");
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
