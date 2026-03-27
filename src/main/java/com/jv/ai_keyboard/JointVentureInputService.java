package com.jv.ai_keyboard;

import android.widget.FrameLayout;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent;
import android.widget.TextView;
import android.util.Log;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

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
        Log.d("JV_DEBUG", "Sovereign Engine Initialized");
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
        
        // Use density-independent pixels (46dp) to avoid R.dimen errors
        float density = getResources().getDisplayMetrics().density;
        int heightInPixels = (int) (46 * density); 
        
        mCandidateView.setLayoutParams(new FrameLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, heightInPixels)); 

        setCandidatesViewShown(true);
        return mCandidateView;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        setCandidatesViewShown(true);
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
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            case -2: // Symbol Toggle Logic
                if (k.getXmlLayoutResId() == R.xml.qwerty) {
                    k = new Keyboard(this, R.xml.symbols); 
                } else {
                    k = new Keyboard(this, R.xml.qwerty);
                }
                kv.setKeyboard(k);
                kv.invalidateAllKeys();
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

    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
