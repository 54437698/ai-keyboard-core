package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.TextView; // ADDED for the Candidate Bar
import android.util.Log;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    // --- CLASS VARIABLES (The Brain's Memory) ---
    private KeyboardView kv;
    private Keyboard k;
    private View mCandidateView;
    private TextView suggestionText; // This is what the NPU will talk to

    @Override
    public View onCreateInputView() {
        Log.d("JV_DEBUG", "onCreateInputView: Loading QWERTY Layout");

        // 1. Inflate and assign to our class variable 'kv'
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        
        // 2. Load the keys into 'k'
        k = new Keyboard(this, R.xml.qwerty);
        
        // 3. Connect them
        kv.setKeyboard(k);
        kv.setOnKeyboardActionListener(this);

        // 4. Force Focus (Android 16 Fix)
        kv.setFocusable(true);
        kv.setFocusableInTouchMode(true);

        return kv;
    }

    @Override
    public View onCreateCandidatesView() {
        Log.d("JV_DEBUG", "onCreateCandidatesView: Initializing Ribbon");
        
        // 1. Inflate the ribbon and store it in 'mCandidateView'
        mCandidateView = getLayoutInflater().inflate(R.layout.candidate_preview, null);
        
        // 2. Grab the TextView inside so we can change the text later
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
                
                // --- NPU HANDSHAKE PREP ---
                // Every time you type, we could update the bar:
                if (suggestionText != null) {
                    suggestionText.setText("NPU Thinking: " + code);
                }
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
