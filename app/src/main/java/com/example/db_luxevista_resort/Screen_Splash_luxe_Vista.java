package com.example.db_luxevista_resort;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class Screen_Splash_luxe_Vista extends AppCompatActivity {

    private TextView tvWelcome, tvAppName, tvQuote;
    private Handler handler = new Handler();

    // Some motivational quotes for the splash
    private String[] quotes = {
            "Relax. Refresh. Recharge.",
            "Luxury is in each detail.",
            "Escape the ordinary.",
            "Your comfort, our priority.",
            "Where elegance meets serenity."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_screen_splash_luxe_vista);

        tvWelcome = findViewById(R.id.tvWelcome);
        tvAppName = findViewById(R.id.tvAppName);
        tvQuote   = findViewById(R.id.tvQuote);

        // Fade-in animations
        fadeIn(tvWelcome, 800);
        fadeIn(tvAppName, 1200);
        fadeIn(tvQuote, 1600);

        // Change quotes every 2 seconds while loading
        handler.postDelayed(new Runnable() {
            int index = 0;
            @Override
            public void run() {
                tvQuote.setText(quotes[index]);
                fadeIn(tvQuote, 600);
                index = (index + 1) % quotes.length;
                handler.postDelayed(this, 2000);
            }
        }, 2000);

        // Move to MainActivity after 4 seconds
        handler.postDelayed(() -> {
            startActivity(new Intent(Screen_Splash_luxe_Vista.this, MainActivity.class));
            finish();
        }, 10000);
    }

    private void fadeIn(TextView view, int duration) {
        Animation fade = new AlphaAnimation(0, 1);
        fade.setDuration(duration);
        view.startAnimation(fade);
    }
}
