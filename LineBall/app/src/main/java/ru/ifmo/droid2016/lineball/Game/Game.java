package ru.ifmo.droid2016.lineball.Game;

import android.support.v4.app.LoaderManager;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import ru.ifmo.droid2016.lineball.Board.BoardInterface;
import ru.ifmo.droid2016.lineball.Board.MoveFrom;
import ru.ifmo.droid2016.lineball.Socket.ServerConnection;

import java.util.Timer;
import java.util.TimerTask;

public class Game implements LoaderManager.LoaderCallbacks<String>, View.OnTouchListener {
    private BoardInterface board;
    private static final long REDRAW_DELAY = 50;
    private final ServerConnection serverConnection;
    private final AppCompatActivity context;

    public Game(ServerConnection serverConnection, AppCompatActivity context){
        this.serverConnection = serverConnection;
        this.context = context;
        //board init
    }

    //write moves getter from user

    public void start() {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                board.redraw();
            }
        }, 20L, REDRAW_DELAY);
        //run moves getter
        context.getSupportLoaderManager().initLoader(1, null, this).forceLoad();
    }

    @Override
    public Loader<String> onCreateLoader(int i, Bundle bundle) {
        switch (i){
            case 1:
                return new MoveGetter(context, serverConnection);
            case 2:
                return new MoveSender(context, serverConnection, bundle.getString("move"));
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        switch (loader.getId()){
            case 1:
                //got some move
                board.setWall(data, MoveFrom.THIS_USER);
                //rerun
                context.getSupportLoaderManager().restartLoader(1, null, this);
                break;
            case 2:
                //move sent, ok
                //nothing to do
                //maybe
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //lol wtf
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        //TODO:
        return false;
    }
}