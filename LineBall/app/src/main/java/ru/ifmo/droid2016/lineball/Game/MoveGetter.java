package ru.ifmo.droid2016.lineball.Game;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import ru.ifmo.droid2016.lineball.Socket.ServerConnection;

import java.io.IOException;

class MoveGetter extends AsyncTaskLoader<String> {

    private ServerConnection connection;

    MoveGetter(Context context, ServerConnection connection) {
        super(context);
        this.connection = connection;
    }

    @NonNull
    @Override
    public String loadInBackground() {
        try {
            return connection.getWall();
        } catch (IOException e) {
            e.printStackTrace();
            return "connection fail";
        }
    }
}
