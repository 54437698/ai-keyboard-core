package com.jv.ai_keyboard;

import android.view.inputmethod.EditorInfo; 
import android.view.inputmethod.InputConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent; 
import android.widget.TextView;
import android.util.Log;
import android.os.Vibrator;      
import android.content.Context;   

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard qwertyKeyboard; 
    private Keyboard symbolsKeyboard; 
    private JvNativeEngine npuEngine;
    private View mCandidateView;
    private TextView suggestionText;
    private boolean isCaps = false;

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine = new JvNativeEngine();
        npuEngine.initialize(this); 
        Log.d("JV_DEBUG", "Sovereign Alpha Initialized");
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        qwertyKeyboard = new Keyboard(this, R.xml.qwerty);
        symbolsKeyboard = new Keyboard(this, R.xml.symbols);
        
        if (kv != null) {
            kv.setKeyboard(qwertyKeyboard);
            kv.setOnKeyboardActionListener(this);
        }
        return kv;
    }

    @Override
    public View onCreateCandidatesView() {
        mCandidateView = getLayoutInflater().inflate(R.layout.candidate_preview, null);
        if (mCandidateView != null) {
            suggestionText = mCandidateView.findViewById(R.id.jv_candidate_text);
        }
        return mCandidateView;
    }

    @Override
    public boolean onEvaluateCandidatesViewShown() {
        return true; 
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        setCandidatesViewShown(true); 
        
        if (suggestionText != null) {
            suggestionText.setText("Sovereign NPU: Standing By..."); 
            suggestionText.setVisibility(View.VISIBLE);
        }
    }

    private void setKeyboard(Keyboard nextKeyboard) {
        if (kv != null && nextKeyboard != null) {
            kv.setKeyboard(nextKeyboard);
            kv.invalidateAllKeys(); 
        }
    }

    // --- INTERFACE METHODS: Clean Signatures for Build #182 ---

    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case -5: // DELETE
                ic.deleteSurroundingText(1, 0);
                break;
            case -1: // SHIFT
                isCaps = !isCaps;
                if (qwertyKeyboard != null) {
                    qwertyKeyboard.setShifted(isCaps);
                }
                if (kv != null) {
                    kv.invalidateAllKeys();
                }
                break;
            case -2: // SYMBOLS
                if (kv != null) {
                    if (kv.getKeyboard() == qwertyKeyboard) {
                        setKeyboard(symbolsKeyboard);
                    } else {
                        setKeyboard(qwertyKeyboard);
                    }
                }
                break;
            case 999: // G BUTTON (NPU Trigger)
                handlePrediction(ic); 
                break;
            case 10: // ENTER
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;
            case 32: // SPACE
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
        }
    }

    public void onPress(int primaryCode) {
        if (kv != null) {
            if (primaryCode == -2 || primaryCode == 999) {
                kv.setPreviewEnabled(false);
            } else {
                kv.setPreviewEnabled(true);
            }
        }
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) { v.vibrate(15); }
    }

    public void onRelease(int primaryCode) {}
    public void onText(CharSequence text) {}
    public void swipeLeft() {}
    public void swipeRight() {}
    public void swipeDown() {}
    public void swipeUp() {}

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
}
