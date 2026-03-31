package com.jv.ai_keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.inputmethod.InputConnection; // CRITICAL IMPORT

public class JointVentureInputService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    private JvNativeEngine npuEngine = new JvNativeEngine();

    @Override
    public void onCreate() {
        super.onCreate();
        npuEngine.initialize(this, "models/model.pte", "models/tokenizer.bin");
    }

    @Override
    public View onCreateInputView() {
        View mInputView = getLayoutInflater().inflate(R.layout.input, null);
        KeyboardView kv = mInputView.findViewById(R.id.keyboard);
        Keyboard mKeyboard = new Keyboard(this, R.xml.qwerty);

        kv.setKeyboard(mKeyboard);
        kv.setOnKeyboardActionListener(this); // Using 'this' since we implement the interface below

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
                // Handle Enter key if needed
                break;
            default:
                char code = (char) primaryCode;
                ic.commitText(String.valueOf(code), 1);
        }
    }

    // Required empty overrides for the interface
    @Override public void onPress(int primaryCode) {}
    @Override public void onRelease(int primaryCode) {}
    @Override public void onText(CharSequence text) {}
    @Override public void swipeLeft() {}
    @Override public void swipeRight() {}
    @Override public void swipeDown() {}
    @Override public void swipeUp() {}
}
