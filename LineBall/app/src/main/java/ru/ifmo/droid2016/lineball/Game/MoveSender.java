package ru.ifmo.droid2016.lineball.Game;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import ru.ifmo.droid2016.lineball.Socket.ServerConnectionHandler;

import java.io.IOException;

class MoveSender extends AsyncTaskLoader<String> {

    private final String move;

    MoveSender(Context context, String move) {
        super(context);
        this.move = move;
    }

    @NonNull
    @Override
    public String loadInBackground() {
        try {
            ServerConnectionHandler.getInstance().setWall(move);
        } catch (IOException e) {
            e.printStackTrace();
            return "send fail";
        }
        return "send success";
    }
}
