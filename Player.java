package com.example.flipneonvortex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Player {
    private double positionX;
    private double positionY;
    private double radius;
    private Paint paint;
    private double velocityX;
    private double velocityY;
    private static final double MAX_SPEED = 20.0;
    private double gravity = 0.5;
    private boolean isGravityInverted = false;
    private boolean hasShield = false;

    public Player(double positionX, double positionY, double radius, int skinColor) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;

        // Neon visuals
        this.paint = NeonTheme.getNeonPaint(skinColor);
    }

    public void setShield(boolean shield) {
        this.hasShield = shield;
    }

    public boolean hasShield() {
        return hasShield;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float) positionX, (float) positionY, (float) radius, paint);

        if (hasShield) {
            Paint shieldPaint = NeonTheme.getStrokePaint(Color.CYAN);
            shieldPaint.setStrokeWidth(5);
            // Pulsing effect?
            canvas.drawCircle((float) positionX, (float) positionY, (float) radius + 15, shieldPaint);
        }
    }

    public void update(Joystick joystick, int screenWidth, int screenHeight, float timeScale) {
        // Apply Time Scale to Max Speed
        velocityX = joystick.getActuatorX() * MAX_SPEED * timeScale;

        // Apply to Gravity
        double effectiveGravity = gravity * timeScale * timeScale; // Gravity accel scales with time^2 roughly, or simple vel scaling
        // Actually for simple Euler steps:
        // vel += accel * dt
        // pos += vel * dt
        // If dt scales by 0.5:
        // vel += accel * 0.5
        // pos += vel * 0.5
        // So we just scale inputs.

        if (isGravityInverted) {
            velocityY -= gravity * timeScale;
            // paint.setColor(Color.parseColor("#FF00FF")); // REMOVED: Overrides Skin
        } else {
            velocityY += gravity * timeScale;
            // paint.setColor(Color.parseColor("#00FFFF")); // REMOVED: Overrides Skin
        }

        // Re-apply glow logic removed (handled in constructor/theme)

        velocityY += (joystick.getActuatorY() * 2.0) * timeScale;

        positionX += velocityX; // Already scaled
        positionY += velocityY; // Already accumulated scale

        // Bounds Check with Velocity Reset
        if (positionY < radius) {
            positionY = radius;
            velocityY = 0;
        }
        if (positionY > screenHeight - radius) {
            positionY = screenHeight - radius;
            velocityY = 0;
        }

        // Horizontal Constraint
        if (positionX < radius) positionX = radius;
        if (positionX > screenWidth - radius) positionX = screenWidth - radius;
    }

    public void flipGravity() {
        isGravityInverted = !isGravityInverted;
    }

    public int getColor() {
        return paint.getColor();
    }

    public void setPosition(double x, double y) {
        this.positionX = x;
        this.positionY = y;
    }

    public double getX() { return positionX; }
    public double getY() { return positionY; }
    public double getRadius() { return radius; }
}
