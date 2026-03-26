package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    @Override
    public View onCreateInputView() {
        // This is the simplest possible way to show the keys
        try {
            KeyboardView kv = (KeyboardView) getLayoutInflater().inflate(R.layout.input, null);
            Keyboard k = new Keyboard(this, R.xml.qwerty);
            kv.setKeyboard(k);
            kv.setOnKeyboardActionListener(this);
            return kv;
        } catch (Exception e) {
            // If the keys fail, show the Pink Box so we know why
            LinearLayout fallback = new LinearLayout(this);
            fallback.setBackgroundColor(Color.MAGENTA);
            TextView tv = new TextView(this);
            tv.setText("RESTART APP: " + e.getMessage());
            fallback.addView(tv);
            return fallback;
        }
    }

    // Empty Mandatory Methods to make the compiler happy
    @Override public void onKey(int primaryCode, int[] keyCodes) {}
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
