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
            case -5: // Matches your XML: <Key android:codes="-5" android:keyLabel="⌫" />
                ic.deleteSurroundingText(1, 0);
                break;

            case 10: // Matches your XML: <Key android:codes="10" android:keyLabel="↵" />
                // This sends the "Go/Search" command to Firefox
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER));
                break;

            case -2: // Matches your XML: <Key android:codes="-2" android:keyLabel="\?123" />
                if (kv.getKeyboard() == mKeyboard) {
                    kv.setKeyboard(mSymbols);
                } else {
                    kv.setKeyboard(mKeyboard);
                }
                break;

            case 999: // Matches your XML: <Key android:codes="999" android:keyLabel="G" />
                // Let's make 'G' the Smiley button for now!
                ic.commitText("😂", 1);
                break;

            default:
                // Types everything else (a, b, c, 1, 2, 3...)
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
