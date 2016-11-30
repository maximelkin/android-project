package ru.ifmo.droid2016.lineball.Game;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import ru.ifmo.droid2016.lineball.Socket.ServerConnectionHandler;

import java.io.IOException;

class MoveGetter extends AsyncTaskLoader<String> {


    MoveGetter(Context context) {
        super(context);
    }

    @NonNull
    @Override
    public String loadInBackground() {
        try {
            return ServerConnectionHandler.getInstance().getWall();
        } catch (IOException e) {
            e.printStackTrace();
            return "connection fail";
        }
    }
}
