package com.example.db_luxevista_resort;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    // Header navigation
    private TextView navPackages, navResort, navSignIn, navAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        // Link header navigation views
        navPackages = findViewById(R.id.navPackages);
        navResort = findViewById(R.id.navResort);
        navSignIn = findViewById(R.id.navSignIn);
        navAbout = findViewById(R.id.navAbout);

        // Click events
        navPackages.setOnClickListener(v -> {
            startActivity(new Intent(AboutUsActivity.this, ViewRoomsActivity.class));
        });

        navResort.setOnClickListener(v -> {
            startActivity(new Intent(AboutUsActivity.this, ResortActivity.class));
        });

        navSignIn.setOnClickListener(v -> {
            startActivity(new Intent(AboutUsActivity.this, MainActivity.class));
        });

        navAbout.setOnClickListener(v -> {
            Toast.makeText(this, "Already on About Us page", Toast.LENGTH_SHORT).show();
        });
    }
}
