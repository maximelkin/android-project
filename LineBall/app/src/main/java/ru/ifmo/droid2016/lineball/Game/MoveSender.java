package ru.ifmo.droid2016.lineball.Game;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import ru.ifmo.droid2016.lineball.Socket.ServerConnection;

import java.io.IOException;

public class MoveSender extends AsyncTaskLoader<String> {

    private final ServerConnection connection;
    private final String move;
    
    public MoveSender(Context context, ServerConnection connection, String move) {
        super(context);
        this.connection = connection;
        this.move = move;
    }

    @Override
    public String loadInBackground() {
        try {
            connection.setWall(move);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
