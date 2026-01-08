package com.example.flipneonvortex;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class ParticleSystem {
    private ArrayList<Particle> particles;
    private Random random;

    public ParticleSystem() {
        particles = new ArrayList<>();
        random = new Random();
    }

    public void emit(double x, double y, int color) {
        // Emit a few particles (Trail)
        for(int i=0; i<3; i++) {
            particles.add(new Particle(x, y, color, false));
        }
    }

    public void explode(double x, double y, int color) {
        // Explosion Burst
        for(int i=0; i<50; i++) {
            particles.add(new Particle(x, y, color, true));
        }
    }

    public void update(float timeScale) {
        Iterator<Particle> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle p = iterator.next();
            p.update(timeScale);
            if (p.isDead()) {
                iterator.remove();
            }
        }
    }

    public void draw(Canvas canvas) {
        for (Particle p : particles) {
            p.draw(canvas);
        }
    }

    private class Particle {
        double x, y;
        double vx, vy;
        int alpha = 255;
        Paint paint;
        int color;

        public Particle(double startX, double startY, int color, boolean isExplosion) {
            this.x = startX;
            this.y = startY;
            this.color = color;

            if (isExplosion) {
                // High velocity in all directions
                double angle = Math.random() * Math.PI * 2;
                double speed = Math.random() * 15 + 5;
                vx = Math.cos(angle) * speed;
                vy = Math.sin(angle) * speed;
                alpha = 255;
            } else {
                // Trail: Low velocity
                vx = (Math.random() - 0.5) * 5;
                vy = (Math.random() - 0.5) * 5;
                alpha = 200;
            }

            paint = new Paint();
            paint.setColor(color);
        }

        public void update(float timeScale) {
            x += vx * timeScale;
            y += vy * timeScale;
            alpha -= 10 * timeScale; // Fade out speed scales too
            if (alpha < 0) alpha = 0;
        }

        public boolean isDead() {
            return alpha <= 0;
        }

        public void draw(Canvas canvas) {
            paint.setAlpha(alpha);
            canvas.drawCircle((float)x, (float)y, 5, paint);
        }
    }
}
