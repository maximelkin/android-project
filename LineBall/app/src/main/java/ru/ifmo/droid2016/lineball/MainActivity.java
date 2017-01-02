package ru.ifmo.droid2016.lineball;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_menu_play, btn_menu_help, btn_menu_settings, btn_menu_about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        btn_menu_play = (Button)findViewById(R.id.btn_menu_play);
        btn_menu_help = (Button)findViewById(R.id.btn_menu_help);
        btn_menu_settings = (Button)findViewById(R.id.btn_menu_settings);
        btn_menu_about = (Button)findViewById(R.id.btn_menu_about);

        btn_menu_play.setOnClickListener(this);
        btn_menu_help.setOnClickListener(this);
        btn_menu_settings.setOnClickListener(this);
        btn_menu_about.setOnClickListener(this);
    }

     @Override
     public void onClick(View v) {
         switch (v.getId()) {
             case R.id.btn_menu_play:
                 startActivity(new Intent(this, GameActivity.class));
                 break;
             case R.id.btn_menu_help:
                 startActivity(new Intent(this, HelpActivity.class));
                 break;
             case R.id.btn_menu_settings:
                 startActivity(new Intent(this, SettingsActivity.class));
                 break;
             case R.id.btn_menu_about:
                 startActivity(new Intent(this, AboutActivity.class));
                 break;
         }
     }
}
