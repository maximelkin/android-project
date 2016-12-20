package ru.ifmo.droid2016.lineball;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ru.ifmo.droid2016.lineball.Socket.ServerConnection;
import ru.ifmo.droid2016.lineball.Socket.ServerConnectionHandler;

import java.io.IOException;
import java.util.Random;
import java.util.prefs.PreferenceChangeListener;

public class GameActivity extends AppCompatActivity {
    // TODO
    private String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", "");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();
    }

    private String generateRandomString() {
        return "";
    }

    class Search extends AsyncTask<Void, Void, Void> {

        //TODO all
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (password.equals("")) {
                    //TODO generate password
                    //TODO save password
                    ServerConnectionHandler.getInstance().registration((new Random().nextLong()) + "lol");
                } else {
                    ServerConnectionHandler.getInstance().verify(password);
                }
            } catch (IOException e) {
                //dangerous
                e.printStackTrace();
            }
            return null;
        }
    }
}
