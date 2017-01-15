package ru.ifmo.droid2016.lineball;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ru.ifmo.droid2016.lineball.game.Game;
import ru.ifmo.droid2016.lineball.game.SocketThreadGame;

import java.io.IOException;
import java.security.SecureRandom;

import static ru.ifmo.droid2016.lineball.MessageCodes.*;


public class GameActivity extends AppCompatActivity implements Handler.Callback {
    private SocketThreadGame socket;
    private String password;
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom random = new SecureRandom();
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // getSupportActionBar().hide();
        password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        try {

            String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            socket = new SocketThreadGame("socket", new Handler(Looper.getMainLooper(), this), android_id);
            socket.start();
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (socket != null)
            socket.quit();
    }

    private void fail() {
        if (alertDialog != null) alertDialog.dismiss();
        Toast.makeText(GameActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    @Override
    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MSG_ERROR:
                //alertDialog.dismiss();
                fail();
                break;
            case MSG_START_GAME:
                if (alertDialog != null) alertDialog.dismiss();
                Intent intent = new Intent(this, Game.class);
                intent.putExtra("rival name", (String) message.obj);
                startActivity(intent);
                finish();
                break;
            case MSG_VERIFYING_ERROR:
                password = null;
                //go to MSG_SOCKET_READY
                //for creating new account because error
            case MSG_SOCKET_READY:
                if (password == null) {
                    password = randomString(10);
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putString("password", password)
                            .apply();

                    //magic what show dialog with choosing username
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View layout = getLayoutInflater().inflate(R.layout.dialog_register, null);
                    final EditText username = (EditText) layout.findViewById(R.id.username);
                    final Context context = getApplicationContext();

                    alertDialog = builder.setTitle(R.string.app_name)
                            .setView(layout)
                            .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String usernameText = username.getText().toString().replace(' ', '_');
                                    if (usernameText.length() < 3) {
                                        usernameText += "2007";
                                    }
                                    if (usernameText.length() > 10) {
                                        usernameText = (String) usernameText.subSequence(0, 10);
                                    }
                                    PreferenceManager
                                            .getDefaultSharedPreferences(context)
                                            .edit().putString("name", usernameText)
                                            .apply();
                                    dialogInterface.dismiss();
                                    socket.registration(String.format("%s %s", password, usernameText));
                                }
                            })
                            .create();
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                    //magic end
                } else {
                    socket.verify(password);
                }
                break;
            case MSG_USER_VERIFIED:
                socket.search();
                break;
        }
        return false;
    }

    @NonNull
    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        return sb.toString();
    }

}
