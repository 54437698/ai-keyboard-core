package com.jv.npu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Joint Venture Sovereign Clipboard
 * Logic: Overwrites clipboard with a single "." after 20 minutes of inactivity.
 */
public class ClipboardGuard {
    private static final String TAG = "JV_Clipboard";
    private final ClipboardManager clipboard;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final long FLUSH_DELAY = 20 * 60 * 1000; // 20 Minutes

    private final Runnable flushTask = () -> {
        try {
            // The "Mastermind" Flush: Overwrite with a single period
            ClipData clip = ClipData.newPlainText("cleared", ".");
            clipboard.setPrimaryClip(clip);
            Log.d(TAG, "Clipboard flushed to '.' after 20m timeout.");
        } catch (Exception e) {
            Log.e(TAG, "Flush failed: " + e.getMessage());
        }
    };

    public ClipboardGuard(Context context) {
        this.clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    /**
     * Resets the 20-minute countdown. 
     * Call this when the keyboard opens or when a new copy is detected.
     */
    public void resetTimer() {
        handler.removeCallbacks(flushTask);
        handler.postDelayed(flushTask, FLUSH_DELAY);
        Log.d(TAG, "Clipboard timer reset (20m remaining)");
    }

    /**
     * Immediate flush if the user manually triggers a "Clear" from the UI.
     */
    public void manualFlush() {
        handler.removeCallbacks(flushTask);
        flushTask.run();
    }
}
