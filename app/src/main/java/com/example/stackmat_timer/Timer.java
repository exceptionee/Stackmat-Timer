package com.example.stackmat_timer;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.AttributeSet;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Timer extends androidx.appcompat.widget.AppCompatTextView {
    public enum Penalty { NONE, PLUS_2, DNF }
    public Penalty penalty = Penalty.NONE;
    ScheduledExecutorService executor;
    Handler handler = new Handler();
    long start;

    public Timer(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
    }

    public void inspect() {
        TimerActivity.state = TimerActivity.State.INSPECTING;
        start = System.nanoTime();

        if (executor != null) executor.shutdownNow();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> handler.post(() -> {
            if (executor.isShutdown()) return;

            long seconds = TimeUnit.SECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);

            if (TimerActivity.sp.getBoolean("inspection_sounds", false) && seconds == 8)
                MediaPlayer.create(this.getContext(), R.raw.eight_seconds).start();
            else if (TimerActivity.sp.getBoolean("inspection_sounds", false) && seconds == 12)
                MediaPlayer.create(this.getContext(), R.raw.twelve_seconds).start();

            if (seconds < 15) this.setText(String.valueOf(15 - seconds));
            else if (seconds < 17) {
                penalty = Penalty.PLUS_2;
                this.setText("+2");
            }
            else {
                penalty = Penalty.DNF;
                TimerActivity.state = TimerActivity.State.IDLE;
                this.setText("DNF");
                executor.shutdownNow();
            }
        }), 0, 1, TimeUnit.SECONDS);
    }

    public void start() {
        TimerActivity.state = TimerActivity.State.SOLVING;
        start = System.nanoTime();

        if (executor != null) executor.shutdownNow();

        executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> handler.post(() -> {
            if (executor.isShutdown()) return;

            long elapsed = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
            long cs = elapsed % 1000 / 10;
            long seconds = elapsed % 60000 / 1000;
            long minutes = elapsed / 60000;
            if (minutes == 10) {
                TimerActivity.state = TimerActivity.State.IDLE;
                executor.shutdownNow();
                this.setText("0.00");
            }
            else if (minutes > 0 && TimerActivity.state == TimerActivity.State.SOLVING) {
                this.setText(String.format(Locale.US, "%d:%02d.%02d", minutes, seconds, cs));
            }
            else {
                this.setText(String.format(Locale.US, "%2d.%02d", seconds, cs));
            }
        }), 0, 10, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        TimerActivity.state = TimerActivity.State.IDLE;
        executor.shutdownNow();

        if (penalty == Penalty.DNF) {
            this.setText("DNF");
            penalty = Penalty.NONE;
            return;
        }

        long elapsed = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        long cs = elapsed % 1000 / 10;
        long seconds = elapsed % 60000 / 1000;
        long minutes = elapsed / 60000;

        if (penalty == Penalty.PLUS_2) seconds += 2;

        if (minutes > 0) {
            this.setText(String.format(Locale.US, "%d:%02d.%02d", minutes, seconds, cs));
        }
        else {
            this.setText(String.format(Locale.US, "%2d.%02d", seconds, cs));
        }

        penalty = Penalty.NONE;
    }

    public void reset() {
        if (executor != null) executor.shutdownNow();

        TimerActivity.state = TimerActivity.State.IDLE;
        this.setText("0.00");
    }
}