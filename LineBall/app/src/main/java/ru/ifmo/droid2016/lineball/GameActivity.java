package ru.ifmo.droid2016.lineball;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import ru.ifmo.droid2016.lineball.Game.Game;
import ru.ifmo.droid2016.lineball.Socket.SocketThread;

import java.io.IOException;
import java.util.Random;

import static ru.ifmo.droid2016.lineball.Socket.SocketThread.MSG_ERROR;
import static ru.ifmo.droid2016.lineball.Socket.SocketThread.MSG_START;

public class GameActivity extends AppCompatActivity implements Handler.Callback {
    private SocketThread socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();
        String password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        try {
            socket = new SocketThread("socket", new Handler(Looper.getMainLooper(), this));
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
        if (password == null) {
            password = generateRandomString();
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString("password", password)
                    .apply();
            socket.registration(password);
        } else {
            socket.verify(password);
        }
        //start search
        socket.search();
    }

    private String generateRandomString() {
        //TODO make more random string
        return new Random().nextInt() + "super gen";
    }

    private void fail(){
        Toast.makeText(GameActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }, 5000);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what){
            case MSG_ERROR:
                fail();
                break;
            case MSG_START:
                startActivity(new Intent(this, Game.class));
                break;
        }
        return false;
    }


}
