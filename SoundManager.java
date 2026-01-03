package com.example.flipneonvortex;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;

public class SoundManager {
    private ToneGenerator toneGenerator;
    private boolean soundEnabled;

    public SoundManager(Context context) {
        // Load Preference
        SharedPreferences prefs = context.getSharedPreferences("FlipNeonVortex", Context.MODE_PRIVATE);
        soundEnabled = prefs.getBoolean("sound_enabled", true);

        if (soundEnabled) {
            try {
                // MUSIC stream for game audio
                toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void playJump() {
        if (toneGenerator != null && soundEnabled) {
            // Quieter, shorter blip
            try { toneGenerator.startTone(ToneGenerator.TONE_CDMA_KEYPAD_VOLUME_KEY_LITE, 50); } catch (Exception e) {}
        }
    }

    public void playScore() {
        if (toneGenerator != null && soundEnabled) {
            // High pitch ping (Proprietary Ack is usually pleasant)
            try { toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 100); } catch (Exception e) {}
        }
    }

    public void playCrash() {
        if (toneGenerator != null && soundEnabled) {
            // Soft Error (Lower buzz, less harsh)
            try { toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 500); } catch (Exception e) {}
        }
    }

    public void playPowerUp() {
        if (toneGenerator != null && soundEnabled) {
            // Prompt tone (Rising sound usually)
            try { toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 150); } catch (Exception e) {}
        }
    }

    public void release() {
        if (toneGenerator != null) {
            toneGenerator.release();
            toneGenerator = null;
        }
    }
}
