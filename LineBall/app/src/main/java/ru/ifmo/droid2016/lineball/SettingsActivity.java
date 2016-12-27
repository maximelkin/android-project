package ru.ifmo.droid2016.lineball;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private RadioButton radio_blue, radio_red;
    private Button btn_clear_nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().hide();

        radio_blue = (RadioButton)findViewById(R.id.radio_blue);
        radio_red = (RadioButton)findViewById(R.id.radio_red);
        btn_clear_nick = (Button)findViewById(R.id.btn_clear_nick);

        radio_blue.setOnClickListener(this);
        radio_red.setOnClickListener(this);
        btn_clear_nick.setOnClickListener(this);

        int color = PreferenceManager.getDefaultSharedPreferences(this).getInt("color", 0);
        switch (color) {
            case 0:
                radio_blue.setChecked(true);
                break;
            case 1:
                radio_red.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_blue:
                if (((RadioButton)v).isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("color", 0).apply();
                }
                break;
            case R.id.radio_red:
                if (((RadioButton)v).isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("color", 1).apply();
                }
                break;
            case R.id.btn_clear_nick:
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("username", null).apply();
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString("password", null).apply();
                break;
        }
    }
}
