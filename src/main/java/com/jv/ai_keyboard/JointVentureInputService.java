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

    private boolean isCaps = false; // Add this variable at the very top of your class (above onCreate)

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        switch(primaryCode) {
            case -1: // The SHIFT Key
                isCaps = !isCaps; // Switch between true and false
                mKeyboard.setShifted(isCaps); // Tells the UI to look "Shifted"
                kv.invalidateAllKeys(); // Forces the keyboard to redraw the labels
                break;

            case -5: // Backspace
                ic.deleteSurroundingText(1, 0);
                break;

            case 10: // Enter
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;

            case -2: // Symbols
                if (kv.getKeyboard() == mKeyboard) {
                    kv.setKeyboard(mSymbols);
                } else {
                    kv.setKeyboard(mKeyboard);
                }
                break;

            case 999: // Smiley
                ic.commitText("😂", 1);
                break;

            default:
                char code = (char) primaryCode;
                // If isCaps is true, convert the letter to Uppercase!
                if (Character.isLetter(code) && isCaps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                
                // OPTIONAL: Turn off caps after one letter (Auto-lowercase)
                /*
                if (isCaps) {
                    isCaps = false;
                    mKeyboard.setShifted(false);
                    kv.invalidateAllKeys();
                }
                */
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
