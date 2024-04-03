package com.example.stackmat_timer;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class App extends AppCompatActivity implements View.OnTouchListener, View.OnLongClickListener {
    public enum State {
        IDLE, INSPECTING, ARMED, SOLVING
    }
    public static State state = State.IDLE;
    private Timer timer;
    private ImageButton leftPad;
    private ImageButton rightPad;
    private ImageButton inspect;
    private ImageButton reset;
    private ImageView light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timer = findViewById(R.id.time);
        leftPad = findViewById(R.id.left_pad);
        rightPad = findViewById(R.id.right_pad);
        inspect = findViewById(R.id.inspect);
        reset = findViewById(R.id.reset);
        light = findViewById(R.id.light);
        leftPad.setOnTouchListener(this);
        rightPad.setOnTouchListener(this);
        inspect.setOnLongClickListener(this);
        reset.setOnLongClickListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == motionEvent.ACTION_DOWN) {
            view.setPressed(true);
            if (leftPad.isPressed() && rightPad.isPressed()) {
                light.setImageResource(R.drawable.light_on);
                if (state == State.IDLE) state = State.ARMED;
                else if (state == State.SOLVING) timer.stop();
            }
        }
        else if (motionEvent.getAction() == motionEvent.ACTION_UP) {
            light.setImageResource(R.drawable.light_off);
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

        return true;
    }
}