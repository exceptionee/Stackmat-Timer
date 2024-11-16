package com.example.stackmat_timer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

public class TimerActivity extends AppCompatActivity implements View.OnTouchListener, View.OnLongClickListener {
    public enum State { IDLE, INSPECTING, ARMED, SOLVING }
    public static State state = State.IDLE;
    private Timer timer;
    private ImageButton leftPad;
    private ImageButton rightPad;
    private ImageButton power;
    private ImageButton inspect;
    private ImageButton reset;
    static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        enableFullscreen();

        timer = findViewById(R.id.time);
        leftPad = findViewById(R.id.left_pad);
        rightPad = findViewById(R.id.right_pad);
        power = findViewById(R.id.power);
        inspect = findViewById(R.id.inspect);
        reset = findViewById(R.id.reset);

        leftPad.setOnTouchListener(this);
        rightPad.setOnTouchListener(this);

        power.setOnLongClickListener(this);
        inspect.setOnLongClickListener(this);
        reset.setOnLongClickListener(this);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        enableFullscreen();
    }

    private void enableFullscreen() {
        WindowInsetsControllerCompat c = WindowCompat
                .getInsetsController(getWindow(), getWindow().getDecorView());

        c.hide(WindowInsetsCompat.Type.systemBars());
        c.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            view.setPressed(true);
            if (leftPad.isPressed() && rightPad.isPressed()) {
                if (sp.getBoolean("vibrate_on_armed", false) &&
                        (state == State.IDLE || state == State.INSPECTING) &&
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ) {
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE))
                            .vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                }

                if (state == State.IDLE) state = State.ARMED;
                else if (state == State.SOLVING) timer.stop();
            }
        }
        else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
            view.setPressed(false);
            if ((leftPad.isPressed() || rightPad.isPressed()) && (state == State.ARMED || state == State.INSPECTING))
                timer.start();
        }

        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        if (view == reset) timer.reset();
        else if (view == inspect) timer.inspect();
        else if (view == power) finish();

        return true;
    }
}