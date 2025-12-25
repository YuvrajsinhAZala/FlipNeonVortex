package com.example.flipneonvortex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView coinText;
    private int coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        prefs = getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
        // We use "high_score" as currency for now, or we can use a separate "total_score"
        // User asked to use Total Score. Let's assume we accumulate score into a wallet.
        // For simplicity, let's say High Score = Wallet? No that's bad.
        // Let's implement accumulation in GameView first?
        // For now, let's treat 'high_score' as the wallet just to demonstrate,
        // OR better: Initialize 'coins' with a default value for testing if 0.
        // Actually, let's use a new key "coins".
        coins = prefs.getInt("coins", 0);

        coinText = findViewById(R.id.coinText);
        updateCoins();

        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NeonTheme.applyTheme(this);
    }

    private void updateCoins() {
        coinText.setText("COINS: " + coins);
    }

    private void setupButtons() {
        Button equipDefault = findViewById(R.id.equipDefaultButton);
        Button buyGold = findViewById(R.id.buyGoldButton);
        Button buyRed = findViewById(R.id.buyRedButton);
        Button buyMatrix = findViewById(R.id.buyMatrixButton);
        Button backButton = findViewById(R.id.backButton);

        final boolean hasGold = prefs.getBoolean("skin_gold_unlocked", false);
        final boolean hasRed = prefs.getBoolean("skin_red_unlocked", false);
        final boolean hasMatrix = prefs.getBoolean("skin_matrix_unlocked", false);

        // Update Button States
        if (hasGold) buyGold.setText("EQUIP");
        if (hasRed) buyRed.setText("EQUIP");
        if (hasMatrix) buyMatrix.setText("EQUIP");

        equipDefault.setOnClickListener(v -> equipSkin("default"));

        buyGold.setOnClickListener(v -> {
            if (hasGold || prefs.getBoolean("skin_gold_unlocked", false)) {
                equipSkin("gold");
            } else {
                if (coins >= 50000) {
                    coins -= 50000;
                    prefs.edit().putInt("coins", coins).putBoolean("skin_gold_unlocked", true).apply();
                    buyGold.setText("EQUIP");
                    updateCoins();
                    Toast.makeText(this, "Gold Skin Unlocked!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Need 50,000 coins!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyRed.setOnClickListener(v -> {
            if (hasRed || prefs.getBoolean("skin_red_unlocked", false)) {
                equipSkin("red");
            } else {
                if (coins >= 150000) {
                    coins -= 150000;
                    prefs.edit().putInt("coins", coins).putBoolean("skin_red_unlocked", true).apply();
                    buyRed.setText("EQUIP");
                    updateCoins();
                    Toast.makeText(this, "Inferno Skin Unlocked!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Need 150,000 coins!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buyMatrix.setOnClickListener(v -> {
            if (hasMatrix || prefs.getBoolean("skin_matrix_unlocked", false)) {
                equipSkin("matrix");
            } else {
                if (coins >= 300000) {
                    coins -= 300000;
                    prefs.edit().putInt("coins", coins).putBoolean("skin_matrix_unlocked", true).apply();
                    buyMatrix.setText("EQUIP");
                    updateCoins();
                    Toast.makeText(this, "Matrix Skin Unlocked!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Need 300,000 coins!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(v -> finish());
    }

    private void equipSkin(String skinId) {
        prefs.edit().putString("equipped_skin", skinId).apply();
        Toast.makeText(this, "Equipped!", Toast.LENGTH_SHORT).show();
    }
}
