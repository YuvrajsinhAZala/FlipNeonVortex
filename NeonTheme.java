package com.example.flipneonvortex;

import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.Paint;

public class NeonTheme {
    // Palettes
    public static final int[] THEME_DEFAULT = { Color.parseColor("#FF00FF"), Color.parseColor("#00FFFF"), Color.parseColor("#39FF14") }; // Magenta, Cyan, Lime
    public static final int[] THEME_INFERNO = { Color.parseColor("#FF0000"), Color.parseColor("#FF4500"), Color.parseColor("#FFD700") }; // Red, Orange, Gold
    public static final int[] THEME_MATRIX = { Color.parseColor("#00FF00"), Color.parseColor("#008F11"), Color.parseColor("#003B00") }; // Green shades
    public static final int[] THEME_FROST = { Color.parseColor("#00FFFF"), Color.parseColor("#E0FFFF"), Color.parseColor("#1E90FF") }; // Cyan, Light, DodgerBlue

    private static int[] currentTheme = THEME_DEFAULT;

    public static void setTheme(int[] theme) {
        currentTheme = theme;
    }

    public static int getThemeColor() {
        // Return a random color from the current theme
        return currentTheme[(int)(Math.random() * currentTheme.length)];
    }

    public static Paint getNeonPaint(int colorHex) {
        Paint paint = new Paint();
        paint.setColor(colorHex);
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        // Add glow effect
        paint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.SOLID));
        return paint;
    }

    public static Paint getStrokePaint(int colorHex) {
        Paint paint = new Paint();
        paint.setColor(colorHex);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        // Add glow effect
        paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.SOLID));
        return paint;
    }
    public static void applyTheme(android.app.Activity activity) {
        android.content.SharedPreferences prefs = activity.getSharedPreferences("FlipNeonVortex", android.content.Context.MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode_enabled", true);

        // System content container
        android.view.ViewGroup contentContainer = activity.findViewById(android.R.id.content);
        if (contentContainer != null) {
            // Set container background just in case
            contentContainer.setBackgroundColor(isDark ? Color.BLACK : Color.WHITE);

            // Check for the inflated layout (root of XML)
            if (contentContainer.getChildCount() > 0) {
                android.view.View xmlRoot = contentContainer.getChildAt(0);
                xmlRoot.setBackgroundColor(isDark ? Color.BLACK : Color.WHITE);
                applyRecursively(xmlRoot, isDark);
            } else {
                applyRecursively(contentContainer, isDark);
            }
        }
    }

    private static void applyRecursively(android.view.View view, boolean isDark) {
        if (view instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyRecursively(group.getChildAt(i), isDark);
            }
        } else if (view instanceof android.widget.TextView) {
            // Don't change button text color usually, they have backgrounds
            if (!(view instanceof android.widget.Button)) {
                ((android.widget.TextView) view).setTextColor(isDark ? Color.WHITE : Color.BLACK);
            }
            // If it is a Switch or RadioButton, text needs to update too
            if (view instanceof android.widget.CompoundButton) { // Switch, RadioButton, CheckBox
                ((android.widget.TextView) view).setTextColor(isDark ? Color.WHITE : Color.BLACK);
            }
        }
    }
}
