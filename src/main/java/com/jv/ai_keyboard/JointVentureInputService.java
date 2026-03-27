package com.jv.ai_keyboard;

import com.jv.ai_keyboard.BuildConfig;
import android.view.inputmethod.EditorInfo; 
import android.view.inputmethod.InputConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent; 
import android.widget.TextView;
import android.util.Log;
import android.os.Vibrator;      // Moved to top
import android.content.Context;   // Moved to top

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private boolean isSymbols = false;
    private JvNativeEngine npuEngine;
    private KeyboardView kv;
    private Keyboard k;
    private View mCandidateView;
    private TextView suggestionText;
    private boolean isCaps = false;

    @Override
public void onCreate() {
    super.onCreate();
    npuEngine = new JvNativeEngine();
    npuEngine.initialize(this); 

    // This pulls the "1.1.155-alpha" string we just set up in Gradle
    String alphaVersion = BuildConfig.BUILD_VERSION;
    
    Log.d("JV_DEBUG", "Sovereign Alpha " + alphaVersion + " Initialized");

    // Let's make it show up on the prediction bar when it starts
    if (suggestionText != null) {
        suggestionText.setText("Sovereign " + alphaVersion);
    }
}
    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        k = new Keyboard(this, R.xml.qwerty);
        kv.setKeyboard(k);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = getLayoutInflater().inflate(R.layout.candidate_preview, null);
        suggestionText = mCandidateView.findViewById(R.id.suggestion_1);
        setCandidatesViewShown(true); 
        return mCandidateView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        setCandidatesViewShown(true); 
        if (suggestionText != null) {
           // This keeps the Version Number visible on start!
        suggestionText.setText("Sovereign " + BuildConfig.BUILD_VERSION); 
        suggestionText.setVisibility(View.VISIBLE);
        }
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
                kv.invalidateAllKeys();
                break;
            case 10: 
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            case -2: 
                if (isSymbols) {
                    k = new Keyboard(this, R.xml.qwerty);
                    isSymbols = false;
                } else {
                    k = new Keyboard(this, R.xml.symbols);
                    isSymbols = true;
                }
                kv.setKeyboard(k);
                kv.invalidateAllKeys();
                break;
            case 999: 
                handlePrediction(ic);
                break;
            case 32: 
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

    @Override 
    public void onPress(int primaryCode) {
        // 1. Silent Ghost Fix
        if (primaryCode == 999 || primaryCode == -2) {
            kv.setPreviewEnabled(false);
        } else {
            kv.setPreviewEnabled(true);
        }

        // 2. The S25 Ultra Haptic "Snap"
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(15); 
        }
    }

    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
