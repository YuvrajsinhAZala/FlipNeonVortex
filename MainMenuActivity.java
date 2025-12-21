package com.example.flipneonvortex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView highScoreText = findViewById(R.id.highScoreText);
        Button playButton = findViewById(R.id.playButton);
        Button tutorialButton = findViewById(R.id.tutorialButton);
        Button shopButton = findViewById(R.id.shopButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button quitButton = findViewById(R.id.quitButton);

        // Load High Score
        SharedPreferences prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("high_score", 0);
        highScoreText.setText("High Score: " + highScore);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        tutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, TutorialActivity.class);
                startActivity(intent);
            }
        });

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ShopActivity.class);
                startActivity(intent);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Theme
        NeonTheme.applyTheme(this);

        // Refresh score if we returned from game
        SharedPreferences prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("high_score", 0);
        ((TextView)findViewById(R.id.highScoreText)).setText("High Score: " + highScore);
    }
}
