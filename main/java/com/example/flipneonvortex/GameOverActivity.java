package com.example.flipneonvortex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Hide Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        TextView scoreText = findViewById(R.id.scoreText);
        TextView highScoreText = findViewById(R.id.highScoreText);
        TextView coinsEarnedText = findViewById(R.id.coinsEarnedText);
        Button restartButton = findViewById(R.id.restartButton);
        Button menuButton = findViewById(R.id.menuButton);

        // Get Data
        int score = getIntent().getIntExtra("SCORE", 0);

        // Save High Score
        // Save High Score & Accumulate Coins
        SharedPreferences prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
        int highScore = prefs.getInt("high_score", 0);
        int currentCoins = prefs.getInt("coins", 0);

        // Add coins
        int newCoins = currentCoins + score;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("coins", newCoins);

        if (score > highScore) {
            highScore = score;
            editor.putInt("high_score", highScore);
        }
        editor.apply(); // Apply both changes

        scoreText.setText("Score: " + score);
        highScoreText.setText("Best: " + highScore);
        coinsEarnedText.setText("Earned: " + score + " Coins");

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
                // Clear flags to ensuring fresh start
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, MainMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Go to menu on back press
        Intent intent = new Intent(GameOverActivity.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NeonTheme.applyTheme(this);
    }
}
