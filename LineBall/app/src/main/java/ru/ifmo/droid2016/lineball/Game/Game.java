package ru.ifmo.droid2016.lineball.Game;

import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import ru.ifmo.droid2016.lineball.Board.Board;
import ru.ifmo.droid2016.lineball.Board.MoveFrom;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super .onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        board = new Board((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE));

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                board.redraw();
            }
        }, BEFORE_DRAW_DELAY, REDRAW_DELAY);

        //run moves getter
        Bundle bundle = new Bundle();
        bundle.putInt("work_type", GETTER_ID);
        getSupportLoaderManager().initLoader(GETTER_ID, bundle, this).forceLoad();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //TODO write touch listener, what should call setWall(), when touching end
        return true;    //"true" mean, what action processed here
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
            return;
        }
        if (data.equals("1")) {
            Log.e(TAG, "game not started");
            //TODO add app error message
            return;
        }
        if (data.equals("2")) {
            Log.d(TAG, "win because rival left");
            //TODO add win message
            return;
        }
        //got some move
        board.setWall(data, MoveFrom.RIVAL);
        //reload
        Bundle bundle = new Bundle();
        bundle.putInt("work_type", GETTER_ID);
        getSupportLoaderManager().restartLoader(GETTER_ID, bundle, this);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    private void setWall(String coordinates) {
        board.setWall(coordinates, MoveFrom.THIS_USER);
        Bundle args = new Bundle();
        args.putInt("work_type", SENDER_ID);
        args.putString("move", coordinates);
        getSupportLoaderManager().initLoader(SENDER_ID, args, this).forceLoad();
    }
}
