package ru.ifmo.droid2016.lineball;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_av1, iv_av2, iv_av3;
    private TextView text_av1, text_av2, text_av3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().hide();
        iv_av1 = (ImageView)findViewById(R.id.iv_av1);
        iv_av2 = (ImageView)findViewById(R.id.iv_av2);
        iv_av3 = (ImageView)findViewById(R.id.iv_av3);
        text_av1 = (TextView)findViewById(R.id.text_av1);
        text_av2 = (TextView)findViewById(R.id.text_av2);
        text_av3 = (TextView)findViewById(R.id.text_av3);

        iv_av1.setOnClickListener(this);
        iv_av2.setOnClickListener(this);
        iv_av3.setOnClickListener(this);
        text_av1.setOnClickListener(this);
        text_av2.setOnClickListener(this);
        text_av3.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String nick = null;
        if (v == iv_av1 || v == text_av1) {
            nick = getString(R.string.author1);
        } else if (v == iv_av2 || v == text_av2) {
            nick = getString(R.string.author2);
        }
        else if (v == iv_av3 || v == text_av3) {
            nick = getString(R.string.author3);
        }
        if (nick != null) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/" + nick));
            startActivity(browserIntent);
        }
    }
}
