package com.jv.ai_keyboard;

import android.view.inputmethod.EditorInfo; // CRITICAL: Fixes onStartInputView error
import android.view.inputmethod.InputConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent; // ADDED: Required for the DONE key
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import android.util.Log;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    // --- CLASS VARIABLES ---
    private boolean isSymbols = false;
    private JvNativeEngine npuEngine;
    private KeyboardView kv;
    private Keyboard k;
    private View mCandidateView;
    private TextView suggestionText;
    private boolean isCaps = false; // Moved here for proper scope

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine = new JvNativeEngine();
        npuEngine.initialize(this); 
        Log.d("JV_DEBUG", "Sovereign LiteRT-LM Engine Initialized");
    }

    @Override
    public View onCreateInputView() {
        Log.d("JV_DEBUG", "onCreateInputView: Loading QWERTY Layout");
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        k = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(k);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d("JV_DEBUG", "onCreateCandidatesView: Initializing Ribbon");
        mCandidateView = getLayoutInflater().inflate(R.layout.candidate_preview, null);
        suggestionText = mCandidateView.findViewById(R.id.suggestion_1);
        setCandidatesViewShown(true); 
        return mCandidateView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        
        // This is the "Force Command" for the Sovereign Ribbon
        // It tells Android not to collapse the candidate area.
        setCandidatesViewShown(true); 
        
        // Ensure the NPU status is visible immediately on launch
        if (suggestionText != null) {
            suggestionText.setText("Jv-NPU Active..."); 
            suggestionText.setVisibility(View.VISIBLE);
            Log.d("JV_DEBUG", "Ribbon Status: FORCED VISIBLE");
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE: // -5
                ic.deleteSurroundingText(1, 0);
                break;

            case Keyboard.KEYCODE_SHIFT: // -1
                isCaps = !isCaps;
                k.setShifted(isCaps);
                kv.invalidateAllKeys();
                break;

            case 10: // Enter/Search (↵)
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;

          case -2: // The Symbol/ABC Toggle
    if (isSymbols) {
        // We are in symbols, go back to Letters
        k = new Keyboard(this, R.xml.qwerty);
        isSymbols = false;
        Log.d("JV_DEBUG", "Switching to QWERTY Layout");
    } else {
        // We are in Letters, go to Symbols
        k = new Keyboard(this, R.xml.symbols);
        isSymbols = true;
        Log.d("JV_DEBUG", "Switching to Symbols Layout");
    }
    kv.setKeyboard(k);
    kv.invalidateAllKeys(); // This forces the visual refresh
    break;
            case 999: // The G-Button (AI Pivot)
                Log.d("JV_DEBUG", "G-Button Pressed: Triple Checking NPU");
                // Let's make it trigger the prediction logic manually for a "Romantic" effect
                handlePrediction(ic);
                break;

            case 32: // Space
                ic.commitText(" ", 1);
                handlePrediction(ic);
                break;

            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
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
