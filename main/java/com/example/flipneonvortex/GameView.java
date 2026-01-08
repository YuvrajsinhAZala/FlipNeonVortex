package com.example.flipneonvortex;

import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.content.SharedPreferences;

public class GameView extends View {
    private Joystick joystick;
    private Player player;
    private ObstacleManager obstacleManager;
    private Background background;
    private ParticleSystem particleSystem;
    private PowerUpManager powerUpManager;
    private SoundManager soundManager;

    private Paint scorePaint;

    private int score = 0;
    private int highScore = 0;

    private Handler handler;
    private Runnable gameRunnable;
    private boolean isRunning = false;
    private boolean isGameOver = false;
    private long lastTime;

    // Advanced Features
    private float timeScale = 1.0f;
    private long slowMoEndTime = 0;

    // Screen Shake
    private float shakeIntensity = 0;
    private long shakeEndTime = 0;

    // Prefs
    private SharedPreferences prefs;
    private int difficulty;
    private float speedMultiplier = 1.0f;
    private float speedIncrement = 0.002f;

    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        prefs = context.getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
        highScore = prefs.getInt("high_score", 0);

        // Load Difficulty
        difficulty = prefs.getInt("difficulty", 1); // 1 = Normal
        if (difficulty == 0) { // Easy
            gameSpeed = 10f;
            speedMultiplier = 0.8f;
            speedIncrement = 0.001f;
        } else if (difficulty == 1) { // Normal
            gameSpeed = 15f;
            speedMultiplier = 1.0f;
            speedIncrement = 0.002f;
        } else if (difficulty == 2) { // Hard
            gameSpeed = 22f;
            speedMultiplier = 1.2f;
            speedIncrement = 0.004f;
        }

        boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", true);
        int textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        scorePaint = NeonTheme.getNeonPaint(textColor);
        scorePaint.setTextSize(60);

        handler = new Handler();
        gameRunnable = new Runnable() {
            @Override
            public void run() {
                invalidate(); // Forces onDraw
            }
        };
    }

    // We use onSizeChanged instead of surfaceChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Portrait optimized positions
        if (joystick == null) {
            // JOYSTICK CENTERED: X = w / 2
            joystick = new Joystick(w / 2, h - 300, 150, 70);

            // Load Skin Logic
            String skin = prefs.getString("equipped_skin", "default");
            int skinColor = Color.parseColor("#39FF14"); // Default Lime
            if (skin.equals("gold")) skinColor = Color.parseColor("#FFD700");
            if (skin.equals("red")) skinColor = Color.parseColor("#FF0000");
            if (skin.equals("matrix")) skinColor = Color.BLUE;

            player = new Player(w / 2.0, h / 4.0, 30, skinColor);

            obstacleManager = new ObstacleManager(200, 350, 100, Color.MAGENTA);
            boolean isDarkMode = prefs.getBoolean("dark_mode_enabled", true);
            background = new Background(w, h, isDarkMode);
            particleSystem = new ParticleSystem();
            powerUpManager = new PowerUpManager();
            soundManager = new SoundManager(getContext());
        }

        if (!isRunning) {
            isRunning = true;
            lastTime = System.currentTimeMillis();
            gameLoop();
        }
    }

    private void gameLoop() {
        if (!isRunning) return;
        invalidate(); // Trigger drawing
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);

            // Screen Shake Effect
            if (System.currentTimeMillis() < shakeEndTime) {
                float dx = (float) ((Math.random() - 0.5f) * shakeIntensity);
                float dy = (float) ((Math.random() - 0.5f) * shakeIntensity);
                canvas.save();
                canvas.translate(dx, dy);
            } else {
                shakeIntensity = 0;
            }

            // Update Logic (Delta time roughly 16ms)
            update();

            // Draw Logic
            // canvas.drawColor(Color.BLACK); // Handled by background
            if (background != null) background.draw(canvas);
            if (particleSystem != null) particleSystem.draw(canvas);
            if (powerUpManager != null) powerUpManager.draw(canvas);

            if (player != null) player.draw(canvas);
            if (obstacleManager != null) obstacleManager.draw(canvas);
            if (joystick != null) joystick.draw(canvas);

            drawScore(canvas);

            // Restore shake
            if (shakeIntensity > 0) {
                canvas.restore();
            }

            // Loop
            if (isRunning && !isGameOver) {
                handler.postDelayed(gameRunnable, 16);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Try to recover next frame if game is still running
            if (isRunning && !isGameOver) {
                handler.postDelayed(gameRunnable, 16);
            }
        }
    }

    // Difficulty Scaling
    private float gameSpeed = 15f;

    private void update() {
        if (player == null || isGameOver) return; // Not ready or Game Over

        // Time Scale Logic
        if (System.currentTimeMillis() > slowMoEndTime) {
            // Smoothly return to 1.0
            if (timeScale < 1.0f) timeScale += 0.01f;
        } else {
            // Target 0.5
            if (timeScale > 0.5f) timeScale -= 0.05f;
        }

        // Increase speed slowly based on difficulty
        gameSpeed += speedIncrement;
        if (gameSpeed > 30f * speedMultiplier) gameSpeed = 30f * speedMultiplier; // Cap speed adjusted by difficulty

        if (background != null) background.update((gameSpeed / 3) * timeScale);
        if (particleSystem != null) {
            particleSystem.emit(player.getX(), player.getY(), player.getColor());
            particleSystem.update(timeScale);
        }

        if (joystick != null) joystick.update();
        player.update(joystick, getWidth(), getHeight(), timeScale);

        // Validations - handled in Player now

        if (obstacleManager != null) {
            obstacleManager.update(getWidth(), getHeight(), gameSpeed * timeScale);
            if (obstacleManager.playerCollide(player)) {
                if (player.hasShield()) {
                    player.setShield(false);
                    // Explosion Effect (Cyan for Shield)
                    particleSystem.explode(player.getX(), player.getY(), Color.CYAN);
                    triggerShake(50, 500); // Shake!
                    obstacleManager.removeAll();
                    if(soundManager != null) soundManager.playCrash(); // Shield break sound
                } else {
                    // Explosion Effect (Red for Death)
                    particleSystem.explode(player.getX(), player.getY(), Color.RED);
                    triggerShake(100, 1000); // Big Shake!
                    if(soundManager != null) soundManager.playCrash();
                    launchGameOver();
                }
            }
            score++;
            // Play score sound every 1 point? Maybe too annoying. Every 1? Yes retro style.
            if(soundManager != null) soundManager.playScore();

            // Dynamic Difficulty & Theme Switching
            if (score % 10 == 0) {
                // Change Theme every 10 points
                int phase = (score / 10) % 4; // 4 phases
                switch(phase) {
                    case 0: NeonTheme.setTheme(NeonTheme.THEME_DEFAULT); break;
                    case 1: NeonTheme.setTheme(NeonTheme.THEME_INFERNO); break;
                    case 2: NeonTheme.setTheme(NeonTheme.THEME_MATRIX); break;
                    case 3: NeonTheme.setTheme(NeonTheme.THEME_FROST); break;
                }
            }
        }

        if (powerUpManager != null) {
            powerUpManager.update(getWidth(), getHeight(), gameSpeed * timeScale);
            int collisionType = powerUpManager.checkCollision(player);

            if (collisionType == 1) { // Shield
                player.setShield(true);
                if(soundManager != null) soundManager.playPowerUp();
            } else if (collisionType == 2) { // SlowMo
                triggerSlowMo(5000);
                if(soundManager != null) soundManager.playPowerUp();
            }
        }
    }

    private void triggerSlowMo(long duration) {
        slowMoEndTime = System.currentTimeMillis() + duration;
        timeScale = 0.5f; // Instant slow
    }

    private void triggerShake(float intensity, long duration) {
        shakeIntensity = intensity;
        shakeEndTime = System.currentTimeMillis() + duration;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (joystick != null) {
                    if (isTouchOnJoystick(event.getX(), event.getY())) {
                        joystick.setIsPressed(true);
                    } else {
                        player.flipGravity();
                        if(soundManager != null) soundManager.playJump();
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (joystick != null && joystick.getIsPressed()) {
                    joystick.setActuator(event.getX(), event.getY());
                }
                return true;
            case MotionEvent.ACTION_UP:
                if (joystick != null) {
                    joystick.setIsPressed(false);
                    joystick.resetActuator();
                }
                return true;
        }
        return true;
    }

    private void launchGameOver() {
        if (isGameOver) return;
        isGameOver = true;
        isRunning = false;

        Context context = getContext();
        Intent intent = new Intent(context, GameOverActivity.class);
        intent.putExtra("SCORE", score);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private boolean isTouchOnJoystick(double x, double y) {
        if (joystick == null) return false;
        // Joystick is at (getWidth()/2, getHeight() - 300)
        double CenterX = getWidth() / 2.0;
        double CenterY = getHeight() - 300.0;

        return Math.sqrt(Math.pow(x - CenterX, 2) + Math.pow(y - CenterY, 2)) < 300; // Large touch area
    }

    private void drawScore(Canvas canvas) {
        canvas.drawText("Score: " + score, 50, 100, scorePaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
        if (handler != null) {
            handler.removeCallbacks(gameRunnable);
        }
        if (soundManager != null) {
            soundManager.release();
        }
    }
}
