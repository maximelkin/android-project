package ru.ifmo.droid2016.lineball;

import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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

            return null;
        }
    }
}
