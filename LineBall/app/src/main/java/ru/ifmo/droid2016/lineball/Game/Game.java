package ru.ifmo.droid2016.lineball.Game;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import ru.ifmo.droid2016.lineball.Board.Board;
import ru.ifmo.droid2016.lineball.Board.Who;
import ru.ifmo.droid2016.lineball.MainActivity;
import ru.ifmo.droid2016.lineball.R;

import java.util.Timer;
import java.util.TimerTask;

public class Game extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {
    private Board board;
    private static final long REDRAW_DELAY = 50;
    private static final long BEFORE_DRAW_DELAY = 20;
    private static final int GETTER_ID = 1;
    private static final int SENDER_ID = 2;
    private static final String TAG = "GAME";
    String coord = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this));
        // setContentView(R.layout.game_layout);

        //prohibit rotate
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        board = new Board((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Who winner = board.redraw();
                if (winner != null) {
                    gameFinish(winner);
                }
            }
        }, BEFORE_DRAW_DELAY, REDRAW_DELAY);

        //run moves getter
        Bundle bundle = new Bundle();
        bundle.putInt("work_type", GETTER_ID);
        getSupportLoaderManager().initLoader(GETTER_ID, bundle, this).forceLoad();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        switch (bundle.getInt("work_type")) {
            case GETTER_ID:
                return new MoveGetter(this);
            case SENDER_ID:
                return new MoveSender(this, bundle.getString("move"));
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data.equals("send success")) {
            Log.d(TAG, data);
            return;
        }
        if (data.equals("send fail") || data.equals("connection fail")) {
            Log.e(TAG, data);
            //TODO add connection troubles message
            //maybe i need special notify messages class
            gameFinish(Who.RIVAL);
            return;
        }
        if (data.equals("1")) {
            Log.e(TAG, "game not started");
            //TODO add app error message
            //and go out?
            return;
        }
        if (data.equals("2")) {
            Log.d(TAG, "win because rival left");
            gameFinish(Who.THIS_USER);
            return;
        }
        //got some move
        board.setWall(data, Who.RIVAL);
        //reload
        Bundle bundle = new Bundle();
        bundle.putInt("work_type", GETTER_ID);
        getSupportLoaderManager().restartLoader(GETTER_ID, bundle, this);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void setWall(String coordinates) {
        board.setWall(coordinates, Who.THIS_USER);
        Bundle args = new Bundle();
        args.putInt("work_type", SENDER_ID);
        args.putString("move", coordinates);
        getSupportLoaderManager().initLoader(SENDER_ID, args, this).forceLoad();
    }

    private void gameFinish(Who winner) {
        //TODO notify user about win/loose

        //giving control to main activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
