package ru.ifmo.droid2016.lineball;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import ru.ifmo.droid2016.lineball.Game.Game;
import ru.ifmo.droid2016.lineball.Socket.SocketThread;

import java.io.IOException;
import java.security.SecureRandom;

import static ru.ifmo.droid2016.lineball.Socket.SocketThread.MSG_ERROR;
import static ru.ifmo.droid2016.lineball.Socket.SocketThread.MSG_READY;
import static ru.ifmo.droid2016.lineball.Socket.SocketThread.MSG_START;

public class GameActivity extends AppCompatActivity implements Handler.Callback {
    private SocketThread socket;
    private String password;
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
       // getSupportActionBar().hide();
        password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        try {
            socket = new SocketThread("socket", new Handler(Looper.getMainLooper(), this));
            socket.start();
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    private String generateRandomString() {
        //TODO make more random string
        return randomString(10);
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
            case MSG_READY:
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
                break;
        }
        return false;
    }

    String randomString( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
