package ru.ifmo.droid2016.lineball;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HighscoresActivity extends AppCompatActivity {
    // TODO
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscores);
        getSupportActionBar().hide();
    }
}
