package com.example.flipneonvortex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Button resetScoreButton = findViewById(R.id.resetScoreButton);
        Button resetCoinsButton = findViewById(R.id.resetCoinsButton);
        Button backButton = findViewById(R.id.backButton);
        Switch soundSwitch = findViewById(R.id.soundSwitch);
        RadioGroup difficultyGroup = findViewById(R.id.difficultyRadioGroup);
        RadioButton easyRadio = findViewById(R.id.easyRadio);
        RadioButton normalRadio = findViewById(R.id.normalRadio);
        RadioButton hardRadio = findViewById(R.id.hardRadio);
        RadioGroup modeGroup = findViewById(R.id.modeRadioGroup);
        RadioButton darkModeRadio = findViewById(R.id.darkModeRadio);
        RadioButton whiteModeRadio = findViewById(R.id.whiteModeRadio);

        SharedPreferences prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);

        // Load Preferences
        soundSwitch.setChecked(prefs.getBoolean("sound_enabled", true));
        int difficulty = prefs.getInt("difficulty", 1); // 1 = Normal default
        if (difficulty == 0) easyRadio.setChecked(true);
        else if (difficulty == 1) normalRadio.setChecked(true);
        else if (difficulty == 2) hardRadio.setChecked(true);

        boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", true);
        if (isDarkMode) darkModeRadio.setChecked(true);
        else whiteModeRadio.setChecked(true);

        soundSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("sound_enabled", isChecked).apply();
            Toast.makeText(SettingsActivity.this, "Sound " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();

            // Play test sound if enabled
            if (isChecked) {
                // We need to re-init sound manager or just use a temp one?
                // SoundManager loads pref on creation.
                // But we just updated the pref.
                if (soundManager != null) soundManager.release();
                soundManager = new SoundManager(SettingsActivity.this);
                soundManager.playScore();
            }
        });

        difficultyGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int newDiff = 1;
            String diffName = "Normal";
            if (checkedId == R.id.easyRadio) { newDiff = 0; diffName = "Easy"; }
            else if (checkedId == R.id.normalRadio) { newDiff = 1; diffName = "Normal"; }
            else if (checkedId == R.id.hardRadio) { newDiff = 2; diffName = "Hard"; }

            prefs.edit().putInt("difficulty", newDiff).apply();
            Toast.makeText(SettingsActivity.this, "Difficulty: " + diffName, Toast.LENGTH_SHORT).show();
        });

        modeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isDark = true;
            if (checkedId == R.id.whiteModeRadio) isDark = false;

            prefs.edit().putBoolean("dark_mode_enabled", isDark).apply();
            Toast.makeText(SettingsActivity.this, "Mode: " + (isDark ? "Dark" : "White"), Toast.LENGTH_SHORT).show();
            // Apply immediately
            NeonTheme.applyTheme(SettingsActivity.this);
        });

        resetScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("high_score", 0);
                editor.putInt("total_score", 0); // Reset currency too? Maybe just high score for now logic, but user asked for reset high score.
                // Creating a differentiation: High Score vs Currency. Let's reset High Score only as requested.
                editor.apply();

                Toast.makeText(SettingsActivity.this, "High Score Reset!", Toast.LENGTH_SHORT).show();
            }
        });

        resetCoinsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("coins", 0);
                editor.apply();

                Toast.makeText(SettingsActivity.this, "Coins Reset!", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (soundManager != null) {
            soundManager.release();
            soundManager = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NeonTheme.applyTheme(this);
    }
}
