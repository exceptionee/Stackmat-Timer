package com.example.stackmat_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    View timer;
    View settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        timer = findViewById(R.id.timer);
        settings = findViewById(R.id.settings);

        timer.setOnClickListener(this);
        settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == timer) startActivity(new Intent(this, TimerActivity.class));
        else if (view == settings) startActivity(new Intent(this, SettingsActivity.class));
    }
}