package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.TextView;
import android.util.Log;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    // --- CLASS VARIABLES ---
    private JvNativeEngine npuEngine;
    private KeyboardView kv;
    private Keyboard k;
    private View mCandidateView;
    private TextView suggestionText;

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
        kv.setFocusable(true);
        kv.setFocusableInTouchMode(true);
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
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;

            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
                
                // --- THE NPU HANDSHAKE ---
                if (suggestionText != null && npuEngine != null) {
                    CharSequence currentText = ic.getTextBeforeCursor(20, 0);
                    String inputContext = (currentText != null) ? currentText.toString() : "";

                    new Thread(() -> {
                        String prediction = npuEngine.getPrediction(inputContext);
                        suggestionText.post(() -> {
                            if (prediction != null && !prediction.isEmpty()) {
                                suggestionText.setText(prediction);
                            }
                        });
                    }).start();
                }
                break; 
        } // This closes the switch
    } // This closes the onKey method

    // --- MANDATORY OVERRIDES ---
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
