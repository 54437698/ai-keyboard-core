package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.KeyEvent; // ADD THIS
import android.view.inputmethod.EditorInfo; // ADD THIS
import android.view.inputmethod.InputConnection;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private JvNativeEngine npuEngine = new JvNativeEngine();
    private KeyboardView kv;
    private Keyboard mKeyboard;
    private Keyboard mSymbols;

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine.initialize(this, "models/model.pte", "models/tokenizer.bin");
        // Pre-load both layouts
        mKeyboard = new Keyboard(this, R.xml.qwerty);
        mSymbols = new Keyboard(this, R.xml.symbols);
    }

    @Override
    public View onCreateInputView() {
        View mInputView = getLayoutInflater().inflate(R.layout.input, null);
        kv = mInputView.findViewById(R.id.keyboard);
        kv.setKeyboard(mKeyboard);
        kv.setOnKeyboardActionListener(this);
        return mInputView;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch(primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;

            case Keyboard.KEYCODE_DONE:
                // FIX: Send a "Hard" Enter key event. 
                // This forces browsers to 'Go' or 'Search' instead of just making a new line.
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;

            case -2: // This is the standard code for the "123" / Symbol switch
                if (kv.getKeyboard() == mKeyboard) {
                    kv.setKeyboard(mSymbols);
                } else {
                    kv.setKeyboard(mKeyboard);
                }
                break;

            case -3: // Usually the code for the Microphone/Voice key
                // For now, let's make it a 'Space' until we add Voice intent
                ic.commitText(" ", 1);
                break;

            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
        }
    }

    // Required overrides
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
