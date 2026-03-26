package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputConnection; // ADD THIS for typing
import android.util.Log;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    @Override
    public View onCreateInputView() {
        Log.d("JV_DEBUG", "onCreateInputView: Loading QWERTY Layout");

        // 1. Inflate the layout from res/layout/input.xml
        // This keeps the 300dp height we fought so hard for!
        KeyboardView kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
        
        // 2. Load the actual keys from res/xml/qwerty.xml
        Keyboard k = new Keyboard(this, R.xml.qwerty);
        
        // 3. Connect them
        kv.setKeyboard(k);
        kv.setOnKeyboardActionListener(this);

        // 4. Force Focus (Keeping your victory fix)
        kv.setFocusable(true);
        kv.setFocusableInTouchMode(true);

        return kv;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;

        // This makes the keys actually TYPE on your phone
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
        }
    }

    // Keep these empty so the compiler is happy
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
