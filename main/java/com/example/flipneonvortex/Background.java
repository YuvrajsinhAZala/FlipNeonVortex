package com.example.flipneonvortex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Background {
    private Paint linePaint;
    private float scrollY = 0;
    private int width;
    private int height;
    private int backgroundColor;
    private static final int GRID_SIZE = 100;
    private static final float SCROLL_SPEED = 5f;

    public Background(int width, int height, boolean isDarkMode) {
        this.width = width;
        this.height = height;

        linePaint = new Paint();
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);

        if (isDarkMode) {
            backgroundColor = Color.BLACK;
            linePaint.setColor(Color.parseColor("#22003366")); // Dark Blue/Purple
        } else {
            backgroundColor = Color.WHITE;
            linePaint.setColor(Color.parseColor("#20000000")); // Faint Black lines
        }
    }

    public void update(float speed) {
        scrollY += speed;
        if (scrollY > GRID_SIZE) {
            scrollY = 0;
        }
    }

    public void draw(Canvas canvas) {
        // Draw background color
        canvas.drawColor(backgroundColor);

        // Draw Vertical Lines (Perspective illusion - keeping them straight for retro style for now)
        for (int i = 0; i < width; i += GRID_SIZE) {
            canvas.drawLine(i, 0, i, height, linePaint);
        }

        // Draw Horizontal Lines (Moving down)
        for (float i = scrollY; i < height; i += GRID_SIZE) {
            // Fade out near top?
            // Simple grid for now
            canvas.drawLine(0, i, width, i, linePaint);
        }

        // Add a "Horizon" glow at the top?
        // Maybe later.
    }
}
