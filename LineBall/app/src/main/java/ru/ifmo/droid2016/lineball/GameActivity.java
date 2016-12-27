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
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import ru.ifmo.droid2016.lineball.Game.Game;
import ru.ifmo.droid2016.lineball.Socket.SocketThread;

import java.io.IOException;
import java.security.SecureRandom;

import static ru.ifmo.droid2016.lineball.Socket.SocketThread.*;

public class GameActivity extends AppCompatActivity implements Handler.Callback {
    private SocketThread socket;
    private String password;
    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // getSupportActionBar().hide();
        //PreferenceManager.getDefaultSharedPreferences(this).edit().putString("password", null).apply();
        password = PreferenceManager.getDefaultSharedPreferences(this).getString("password", null);
        try {

            String android_id = Settings.Secure.getString(getBaseContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);

            socket = new SocketThread("socket", new Handler(Looper.getMainLooper(), this), android_id);
            socket.start();
        } catch (IOException e) {
            fail();
            e.printStackTrace();
        }
    }

    private void fail() {
        alertDialog.dismiss();
        Toast.makeText(GameActivity.this, "Connection error", Toast.LENGTH_SHORT).show();
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
            case MSG_START:
                if (alertDialog != null) alertDialog.dismiss();
                Intent intent = new Intent(this, Game.class);
                intent.putExtra("rival name", (String) message.obj);
                startActivity(intent);
                finish();
                break;
            case MSG_REG_ERR:
                password = null;
                //go to MSG_READY
                //for creating new account if error
            case MSG_READY:
                if (password == null) {
                    password = randomString(10);
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putString("password", password)
                            .apply();

                    //magic what show dialog with choosing username
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    final Context context = getApplicationContext();
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    final EditText username = new EditText(layout.getContext());
                    layout.addView(username);

                    alertDialog = builder.setMessage(R.string.input_your_nick)
                            .setView(layout)
                            .setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String usernameText = username.getText().toString();
                                    //TODO check in logs
                                    Log.e("username", usernameText);
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
                                    socket.search();
                                }
                            })
                            .create();
                   // alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                    //magic end
                } else {
                    socket.verify(password);
                }
                break;
            case MSG_VERIFIED:
                socket.search();
                break;
        }
        return false;
    }

    @NonNull
    public static String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

}
