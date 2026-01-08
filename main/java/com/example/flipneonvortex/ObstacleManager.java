package com.example.flipneonvortex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.ArrayList;
import java.util.Iterator;

public class ObstacleManager {
    private ArrayList<Obstacle> obstacles;
    private int playerGap;
    private int obstacleGap;
    private int obstacleHeight;
    private int color;
    private long startTime;
    private long initTime;

    public ObstacleManager(int playerGap, int obstacleGap, int obstacleHeight, int color) {
        this.playerGap = playerGap;
        this.obstacleGap = obstacleGap;
        this.obstacleHeight = obstacleHeight;
        this.color = color;

        obstacles = new ArrayList<>();
        startTime = System.currentTimeMillis();
        initTime = startTime;

        // Populate init
        // obstacles.add(new Obstacle(...));
    }

    public void update(int screenWidth, int screenHeight, float gameSpeed) {
        // Spawn interval - Increased constant to 35000 for easier difficulty (slower spawns)
        int spawnInterval = (int) (35000 / gameSpeed);
        if (System.currentTimeMillis() - startTime > spawnInterval) {
            startTime = System.currentTimeMillis();
            // Spawn new obstacle
            int h = (int) (Math.random() * (screenHeight/3)) + 100;
            int y = (int) (Math.random() * (screenHeight - h));

            // Dynamic Color from Theme
            int newColor = NeonTheme.getThemeColor();

            // Reduced chance to 20% for Moving Obstacle
            boolean isMoving = Math.random() < 0.2;

            obstacles.add(new Obstacle(h, newColor, screenWidth, y, 100, isMoving, screenHeight));
        }

        Iterator<Obstacle> iterator = obstacles.iterator();
        while(iterator.hasNext()){
            Obstacle ob = iterator.next();
            ob.update(gameSpeed);
            if(ob.getRectangle().right < 0){
                iterator.remove();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (Obstacle ob : obstacles) {
            ob.draw(canvas);
        }
    }

    public boolean playerCollide(Player player) {
        for (Obstacle ob : obstacles) {
            if (ob.playerCollide(player)) {
                return true;
            }
        }
        return false;
    }

    public static class Obstacle {
        private Rect rectangle;
        private int color;
        private Paint paint;
        private float rotation = 0;

        // Movement properties
        private boolean isMoving;
        private float originalY;
        private float timeOffset;
        private int screenHeight;

        public Obstacle(int rectHeight, int color, int startX, int startY, int width, boolean isMoving, int screenHeight) {
            this.color = color;
            this.isMoving = isMoving;
            this.screenHeight = screenHeight;
            this.originalY = startY;
            this.timeOffset = (float) (Math.random() * 100); // Random phase

            rectangle = new Rect(startX, startY, startX + width, startY + rectHeight);
            paint = NeonTheme.getNeonPaint(color);
        }

        public Rect getRectangle() {
            return rectangle;
        }

        public void update(float speed) {
            // Horizontal Move
            rectangle.left -= speed;
            rectangle.right -= speed;

            // Rotation
            rotation += 5;

            // Vertical Move (Sine Wave)
            if (isMoving) {
                timeOffset += 0.05f; // Slower wave speed

                // Complex: Moving the Rect directly can cause height issues if strictly strictly tracking top/bottom
                // Let's shift top/bottom
                float height = rectangle.height();
                float newTop = originalY + (float)Math.sin(timeOffset) * 100; // Reduced Amplitude to 100px (was 200)

                // Clamp
                if (newTop < 0) newTop = 0;
                if (newTop + height > screenHeight) newTop = screenHeight - height;

                rectangle.top = (int) newTop;
                rectangle.bottom = (int) (newTop + height);
            }
        }

        public void draw(Canvas canvas) {
            // Rotate around center
            canvas.save();
            canvas.rotate(rotation, rectangle.centerX(), rectangle.centerY());
            canvas.drawRect(rectangle, paint);
            canvas.restore();
        }

        private Rect hitbox = new Rect();
        private Rect playerTouchRect = new Rect();

        public boolean playerCollide(Player player) {
            // Hitbox reduction
            int padding = 10;
            hitbox.set(rectangle.left + padding, rectangle.top + padding, rectangle.right - padding, rectangle.bottom - padding);

            playerTouchRect.set(
                    (int)(player.getX() - player.getRadius()),
                    (int)(player.getY() - player.getRadius()),
                    (int)(player.getX() + player.getRadius()),
                    (int)(player.getY() + player.getRadius())
            );

            return Rect.intersects(hitbox, playerTouchRect);
        }
    }

    public ArrayList<Obstacle> getObstacles() {
        return obstacles;
    }
    public void removeAll() {
        obstacles.clear();
    }
}
