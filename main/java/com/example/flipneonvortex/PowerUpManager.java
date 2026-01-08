package com.example.flipneonvortex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.Iterator;

public class PowerUpManager {
    private ArrayList<PowerUp> powerUps;
    private long startTime;
    private int spawnInterval = 10000; // Spawn every 10s roughly

    public PowerUpManager() {
        powerUps = new ArrayList<>();
        startTime = System.currentTimeMillis();
    }

    public void update(int screenWidth, int screenHeight, float gameSpeed) {
        if (System.currentTimeMillis() - startTime > spawnInterval) {
            startTime = System.currentTimeMillis();
            // Spawn logic
            int y = (int) (Math.random() * (screenHeight - 200)) + 100;
            powerUps.add(new PowerUp(screenWidth, y));
        }

        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();
            p.update(gameSpeed);
            if (p.x < -100) {
                iterator.remove();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (PowerUp p : powerUps) {
            p.draw(canvas);
        }
    }

    public int checkCollision(Player player) {
        Iterator<PowerUp> iterator = powerUps.iterator();
        while (iterator.hasNext()) {
            PowerUp p = iterator.next();
            // Circle collision
            double dist = Math.sqrt(Math.pow(p.x - player.getX(), 2) + Math.pow(p.y - player.getY(), 2));
            if (dist < (p.radius + player.getRadius())) {
                int type = p.type;
                iterator.remove();
                return type;
            }
        }
        return 0; // 0 = No Collision
    }

    private class PowerUp {
        public float x, y;
        public float radius = 40;
        private Paint paint;
        public int type; // 1 = Shield, 2 = SlowMo

        public PowerUp(float x, float y) {
            this.x = x;
            this.y = y;

            // Random Type: 70% Shield, 30% SlowMo
            if (Math.random() < 0.7) {
                type = 1; // Shield (Cyan)
                paint = NeonTheme.getNeonPaint(Color.CYAN);
            } else {
                type = 2; // SlowMo (Purple)
                paint = NeonTheme.getNeonPaint(Color.parseColor("#9D00FF")); // Neon Purple
            }
        }

        public void update(float speed) {
            x -= speed;
        }

        public void draw(Canvas canvas) {
            canvas.drawCircle(x, y, radius, paint);
            // Draw ring
            Paint ring = NeonTheme.getStrokePaint(Color.WHITE);
            canvas.drawCircle(x, y, radius + 5, ring);
        }
    }
}
