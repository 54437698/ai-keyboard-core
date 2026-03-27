package com.jv.ai_keyboard;

import android.view.inputmethod.EditorInfo; // CRITICAL: Fixes onStartInputView error
import android.view.inputmethod.InputConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
@@ -46,64 +48,75 @@
        return mCandidateView;
    }

    @Override
public void onStartInputView(EditorInfo info, boolean restarting) {
    super.onStartInputView(info, restarting);
    
    // This is the "Magic Command" that forces the prediction bar to show up.
    // Without this, Android might keep the bar hidden to save screen space.
    setCandidatesViewShown(true);
    
    Log.d("JV_DEBUG", "onStartInputView: Toolbar visibility forced.");
}

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;

            case Keyboard.KEYCODE_SHIFT:
                isCaps = !isCaps;
                k.setShifted(isCaps);
                kv.invalidateAllKeys(); // Redraws keys to show CAPS status
                break;

            case Keyboard.KEYCODE_DONE:
                // This makes the 'DONE/Enter' key actually work
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;

            case -2: // The Symbol Toggle (?123)
                Log.d("JV_DEBUG", "Symbol Mode Toggled");
                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);

                // Trigger your Semantic Pivot Logic
                handlePrediction(ic);
                break;
        }
    }

    private void handlePrediction(InputConnection ic) {
        if (suggestionText != null && npuEngine != null) {
            CharSequence currentText = ic.getTextBeforeCursor(20, 0);
            String inputContext = (currentText != null) ? currentText.toString() : "";

            new Thread(() -> {
                String prediction = npuEngine.getPrediction(inputContext);
                if (prediction != null && !prediction.isEmpty()) {
                    suggestionText.post(() -> suggestionText.setText(prediction));
                }
            }).start();
        }
    }

    // --- MANDATORY OVERRIDES ---
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
